package com.dockersim.service.container;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.repository.DockerContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContainerFinderImpl implements ContainerFinder {

    private final DockerContainerRepository containerRepository;

    @Override
    public boolean existsByImageIdAndStatus(String imageId, ContainerStatus status) {
        return containerRepository.existsByImageIdAndStatus(imageId, status);
    }
}
