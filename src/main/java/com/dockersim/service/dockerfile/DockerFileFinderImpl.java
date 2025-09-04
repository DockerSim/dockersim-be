package com.dockersim.service.dockerfile;


import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerFileErrorCode;
import com.dockersim.repository.DockerFileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DockerFileFinderImpl implements DockerFileFinder {

    private final DockerFileRepository repo;

    @Override
    public DockerFile findByIdAndUser(Long id, User user) {
        return repo.findByIdAndUser(id, user).orElseThrow(
            () -> new BusinessException(DockerFileErrorCode.DOCKER_FILE_NOT_FOUND));
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
