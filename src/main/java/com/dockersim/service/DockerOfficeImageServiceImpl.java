package com.dockersim.service;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.domain.DockerOfficeTag;
import com.dockersim.dto.DockerImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DockerOfficeImageServiceImpl implements DockerOfficeImageService {
    private final DockerOfficeImageRepository repo;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void loadAllFromJson() {
        String path = "static/data/docker_images.json";
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            List<DockerImageJson> list = mapper.readValue(is, new TypeReference<List<DockerImageJson>>() {});
            for (DockerImageJson jsonImage : list) {
                repo.save(DockerOfficeImage.fromJson(jsonImage));
            }
        } catch (Exception e) {
            throw new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_DATA_LOAD_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DockerImageResponse findByName(String name) {
        DockerOfficeImage img = repo.findByName(name)
                .orElseThrow(() ->
                        new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND, name)
                );

        List<String> tags = img.getTags().stream()
                .map(DockerOfficeTag::getTag).toList();

        return DockerImageResponse.builder()
                .name(img.getName())
                .namespace(img.getNamespace())
                .description(img.getDescription())
                .starCount(img.getStarCount())
                .pullCount(img.getPullCount())
                .lastUpdated(img.getLastUpdated())
                .dateRegistered(img.getDateRegistered())
                .logoUrl(img.getLogoUrl())
                .tags(tags)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DockerImageResponse> getAllImages(int offset, int limit) {
        List<DockerImageResponse> all = repo.findAll().stream()
                .map(img -> DockerImageResponse.builder()
                        .name(img.getName())
                        .namespace(img.getNamespace())
                        .description(img.getDescription())
                        .starCount(img.getStarCount())
                        .pullCount(img.getPullCount())
                        .lastUpdated(img.getLastUpdated())
                        .dateRegistered(img.getDateRegistered())
                        .logoUrl(img.getLogoUrl())
                        .tags(img.getTags().stream()
                                .map(DockerOfficeTag::getTag)
                                .collect(Collectors.toList()))
                        .build()
                )
                .collect(Collectors.toList());

        int start = Math.max(0, offset);
        int end   = Math.min(all.size(), offset + limit);
        if (start >= all.size()) {
            return Collections.emptyList();
        }
        return all.subList(start, end);
    }
}
