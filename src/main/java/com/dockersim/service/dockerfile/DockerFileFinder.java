package com.dockersim.service.dockerfile;

import java.util.List;

import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;

public interface DockerFileFinder {

	DockerFile findByIdAndUser(Long id, User user);

	List<DockerFile> findAllByUser(User user);

	DockerFile findByPathAndUser(String path, User user);

}
