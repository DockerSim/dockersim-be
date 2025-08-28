package com.dockersim.service.dockerfile;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;
import com.dockersim.dto.request.DockerFileRequest;
import com.dockersim.dto.response.DockerFileResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerFileErrorCode;
import com.dockersim.repository.DockerFileRepository;
import com.dockersim.service.user.UserFinder;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DockerFileServiceImpl implements DockerFileService {

    private final DockerFileRepository dockerFileRepository;
    private final UserFinder userFinder;
    private final DockerFileFinder dockerFileFinder;

    @Override
    public DockerFileResponse create(SimulationUserPrincipal principal, DockerFileRequest request) {
        checkValidRequest(request);
        User user = userFinder.findUserById(principal.getUserId());
        DockerFile dockerFile = DockerFile.from(request, user);
        return DockerFileResponse.from(dockerFileRepository.save(dockerFile));
    }

    @Override
    @Transactional(readOnly = true)
    public DockerFileResponse get(SimulationUserPrincipal principal, Long id) {
        User user = userFinder.findUserById(principal.getUserId());
        DockerFile dockerFile = dockerFileFinder.findByIdAndUser(id, user);
        return DockerFileResponse.from(dockerFile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DockerFileResponse> getAll(SimulationUserPrincipal principal) {
        User user = userFinder.findUserById(principal.getUserId());
        return dockerFileFinder.findAllByUser(user).stream()
            .map(DockerFileResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public DockerFileResponse update(SimulationUserPrincipal principal, Long id,
        DockerFileRequest request
    ) {
        checkValidRequest(request);

        User user = userFinder.findUserById(principal.getUserId());
        DockerFile dockerFile = dockerFileFinder.findByIdAndUser(id, user);

        return DockerFileResponse.from(dockerFileRepository.save(dockerFile));
    }

    @Override
    public void delete(SimulationUserPrincipal principal, Long id) {
        User user = userFinder.findUserById(principal.getUserId());
        DockerFile dockerFile = dockerFileFinder.findByIdAndUser(id, user);
        dockerFileRepository.delete(dockerFile);
    }

    private void checkValidRequest(DockerFileRequest request) {
        if (request.getName().isEmpty()) {
            throw new BusinessException(DockerFileErrorCode.INVALID_DOCKER_FILE_NAME,
                request.getName());
        }
    }
}