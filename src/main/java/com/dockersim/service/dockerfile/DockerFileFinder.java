package com.dockersim.service.dockerfile;

import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;
import java.util.List;

public interface DockerFileFinder {

    DockerFile findByIdAndUser(Long id, User user);

    DockerFile findByPathAndUser(String path, User user);

    List<DockerFile> findAllByUser(User user);
}
