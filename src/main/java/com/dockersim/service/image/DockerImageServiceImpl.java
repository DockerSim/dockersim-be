package com.dockersim.service.image;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.DockerFile;
import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.domain.User;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.dto.response.ImageInspectData;
import com.dockersim.dto.response.ImageInspectRootFSData;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.service.dockerfile.DockerFileFinder;
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.user.UserFinder;
import com.dockersim.util.ImageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DockerImageServiceImpl implements DockerImageService {

	private final DockerImageRepository dockerImageRepository;

	private final SimulationFinder simulationFinder;
	private final DockerImageFinder dockerImageFinder;
	private final UserFinder userFinder;
	private final DockerFileFinder dockerFileFinder;

    /*


    private boolean isPotentialHexId(String str) {
        if (str == null || str.isEmpty() || str.contains("/") || str.contains(":")) {
            return false;
        }
        return str.matches("^[0-9a-fA-F]+$");
    }

    private String getConsoleHeader() {
        return
    }
     */

	// ----------------------------------------------

	@Override
	@Transactional
	public DockerImageResponse build(SimulationUserPrincipal principal, String dockerFilePath, String tag) {
		User user = userFinder.findUserById(principal.getUserId());
		DockerFile dockerFile = dockerFileFinder.findByPathAndUser(dockerFilePath, user);
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());

		Map<String, String> imageInfo = ImageUtil.parserFullName(tag);
		ImageUtil.checkInvalidImageInfo(imageInfo, user, true);

		DockerImage image = DockerImage.from(simulation, dockerFile, imageInfo);

		DockerImage prevImage = dockerImageFinder.findSameImage(
			simulation,
			image.getNamespace(),
			image.getName(),
			image.getTag(),
			ImageLocation.LOCAL
		);
		if (prevImage != null) {
			prevImage.convertToDangling();
			dockerImageRepository.save(prevImage);
		}

		Stream<String> headerStream = Stream.of(
			dockerFilePath + "에 위치한 Dockerfile에 의해 Image" + image.getName() + "을 생성했습니다.");
		Stream<String> bodyStream = Stream.empty();

		if (prevImage != null) {
			bodyStream = Stream.of(
				"Local에 존재하는 기존의 Image는 댕글링이미지로 변환됩니다.:",
				prevImage.getShortHexId()
			);
		}

		return DockerImageResponse.from(dockerImageRepository.save(image),
			Stream.concat(headerStream, bodyStream).toList()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> history(SimulationUserPrincipal principal, String nameOrHexId) {
		Map<String, String> imageInfo = ImageUtil.parserFullName(nameOrHexId);

		// User user = userFinder.findUserById(principal.getUserId());
		// ImageUtil.checkInvalidImageInfo(imageInfo, user, false);
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());

		DockerImage image = dockerImageFinder.findImageByNameOrId(simulation, imageInfo, ImageLocation.LOCAL,
			nameOrHexId);
		return image.getLayers();
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> inspect(SimulationUserPrincipal principal, String nameOrHexId) {

		Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		// User user = userFinder.findUserById(principal.getUserId());
		// ImageUtil.checkInvalidImageInfo(imageInfo, user, false);

		Map<String, String> imageInfo = ImageUtil.parserFullName(nameOrHexId);
		DockerImage image = dockerImageFinder.findImageByNameOrId(simulation, imageInfo, ImageLocation.LOCAL,
			nameOrHexId);

		ImageInspectData inspectData = ImageInspectData.builder()
			.Id(image.getHexId())
			.RepoTags(List.of(image.getFullNameWithTag()))
			.Created(image.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
			.RootFS(ImageInspectRootFSData.builder().Layers(image.getLayers()).build())
			.build();

		try {
			return Collections.singletonList(
				(new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(List.of(inspectData)));
		} catch (JsonProcessingException e) {
			throw new BusinessException(DockerImageErrorCode.FAIL_CONVERT_INSPECT);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> ls(SimulationUserPrincipal principal, boolean all, boolean quiet) {
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		List<DockerImage> images = dockerImageFinder.findBySimulationInLocal(simulation, all);

		Stream<String> headerStream, bodyStream;
		if (quiet) {
			headerStream = Stream.of("REPOSITORY");
			bodyStream = images.stream().map(DockerImage::getHexId);
		} else {
			headerStream = String.format("%-25s %-20s %-15s %-20s", "REPOSITORY", "TAG", "IMAGE ID", "CREATED").lines();
			bodyStream = images.stream().map(
				image ->
					String.format(
						"%-25s %-20s %-15s %-20s",
						image.getName(),
						image.getTag(),
						image.getShortHexId(),
						formatDuration(image.getCreatedAt())
					)
			);
		}
		return Stream.concat(headerStream, bodyStream).toList();
	}

	private String formatDuration(LocalDateTime time) {

		Duration duration = Duration.between(time, LocalDateTime.now());
		long days = duration.toDays();
		if (days > 0) {
			return days + (days == 1 ? " day ago" : " days ago");
		}
		long hours = duration.toHours();
		if (hours > 0) {
			return hours + (hours == 1 ? " hour ago" : " hours ago");
		}
		long minutes = duration.toMinutes();
		if (minutes > 0) {
			return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
		}
		return "just now";
	}

	@Override
	public List<DockerImageResponse> prune(SimulationUserPrincipal principal, boolean all) {
		return List.of();
	}

	@Override
	public List<DockerImageResponse> pull(SimulationUserPrincipal principal, String name,
		boolean all) {
		return List.of();
	}

	@Override
	public List<DockerImageResponse> push(SimulationUserPrincipal principal, String name, boolean allTags) {
		return null;
	}

	@Override
	public DockerImageResponse rm(SimulationUserPrincipal principal, String nameOrId,
		boolean force) {
		return null;
	}
}
