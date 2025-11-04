package com.dockersim.service.dockerfile;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerFileErrorCode;
import com.dockersim.repository.DockerFileRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DockerFileFinderImpl implements DockerFileFinder {

	private final DockerFileRepository repo;

	@Override
	public DockerFile findByIdAndUser(Long id, User user) {
		return repo.findByIdAndUser(id, user).orElseThrow(
			() -> new BusinessException(DockerFileErrorCode.NOT_FOUND_DOCKER_FILE));
	}

	@Override
	public DockerFile findByPathAndUser(String path, User user) {
		return repo.findByPathAndUser(path, user).orElseThrow(
			() -> new BusinessException(DockerFileErrorCode.INVALID_DOCKER_FILE_PATH, path)
		);
	}

	@Override
	public List<DockerFile> findAllByUser(User user) {
		return repo.findAllByUser(user);
	}
}
