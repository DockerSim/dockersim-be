package com.dockersim.service.dockerfile;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;
import com.dockersim.dto.request.DockerFileRequest;
import com.dockersim.dto.response.DockerFileResponse;
import com.dockersim.repository.DockerFileRepository;
import com.dockersim.service.user.UserFinder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DockerFileServiceImpl implements DockerFileService {

	private final DockerFileRepository dockerFileRepository;
	private final UserFinder userFinder;
	private final DockerFileFinder dockerFileFinder;

	@Override
	public DockerFileResponse createDockerFile(SimulationUserPrincipal principal, DockerFileRequest request) {
		User user = userFinder.findUserById(principal.getUserId());

		DockerFile dockerFile = DockerFile.from(request, user);
		dockerFile.addUser(user);

		return DockerFileResponse.from(dockerFileRepository.save(dockerFile));
	}

	@Override
	public DockerFileResponse getDockerFileInfo(SimulationUserPrincipal principal, Long id) {
		User user = userFinder.findUserById(principal.getUserId());

		DockerFile dockerFile = dockerFileFinder.findByIdAndUser(id, user);

		return DockerFileResponse.from(dockerFile);
	}

	@Override
	public List<DockerFileResponse> getDockerFileSummary(SimulationUserPrincipal principal) {
		User user = userFinder.findUserById(principal.getUserId());

		return dockerFileFinder.findAllByUser(user).stream()
			.map(DockerFileResponse::from)
			.collect(Collectors.toList());
	}

	@Override
	public DockerFileResponse updateDockerFile(SimulationUserPrincipal principal, Long id, DockerFileRequest request) {
		User user = userFinder.findUserById(principal.getUserId());

		DockerFile dockerFile = dockerFileFinder.findByIdAndUser(id, user);

		return DockerFileResponse.from(dockerFileRepository.save(dockerFile));
	}

	@Override
	public void deleteDockerfile(SimulationUserPrincipal principal, Long id) {
		User user = userFinder.findUserById(principal.getUserId());

		DockerFile dockerFile = dockerFileFinder.findByIdAndUser(id, user);
		dockerFile.removeUser();

		dockerFileRepository.delete(dockerFile);
	}
}