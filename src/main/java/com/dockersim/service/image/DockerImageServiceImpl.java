package com.dockersim.service.image;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.exception.code.SuccessCode;
import com.dockersim.repository.DockerImageRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DockerImageServiceImpl implements DockerImageService {

    private final DockerOfficeImageService officeImageService;
    private final DockerImageRepository repo;


    @Override
    public DockerImageResponse pullImage(String imageFullName) {
        Map<String, String> image = parserImageName(imageFullName);

        Optional<DockerImage> userImage = repo.findByName(image.get("name"));

        // Step 1. 사용자 이미지 조회 및 반환
        if (userImage.isPresent()) {
            return DockerImageResponse.fromDockerImage(userImage.get(),
                List.of(SuccessCode.COMMAND_EXECUTE_READ.getMessage("image", imageFullName)));
        }

        // Step 2. 사용자 이미지가 없는 경우 공식 이미지 조회
        DockerOfficeImage officeImage = officeImageService.findByNameForImageService(
            image.get("name"));

        // Step 3. 공식 이미지 반환
        if (officeImage != null) {
            return DockerImageResponse.fromDockerImage(
                DockerImage.fromDockerOfficeImage(officeImage),
                List.of(
                    DockerImageErrorCode.USER_IMAGE_NOT_FOUND.getMessage(imageFullName),
                    SuccessCode.COMMAND_EXECUTE_PULL_IMAGE.getMessage(imageFullName)
                )
            );
        }

        // Step 4. 존재하지 않는 이미지, 예외 처리
        throw new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, imageFullName);
    }

    @Override
    public List<DockerImageResponse> listImages() {
        return List.of();
    }

    @Override
    public String removeImage(String imageName) {
        return "";
    }

    @Override
    public String buildImage(String tag, String path) {
        return "";
    }

    @Override
    public List<String> pruneImages(boolean all) {
        return List.of();
    }

    @Override
    public String inspectImage(String imageName) {
        return "";
    }

    @Override
    public String showImageHistory(String imageName) {
        return "";
    }


    private Map<String, String> parserImageName(String name) {
        Map<String, String> map = new HashMap<>();
        if (name.contains(":")) {
            String[] parts = name.split(":");
            map.put("name", parts[0]);
            map.put("tag", parts[1]);
        } else {
            map.put("name", name);
            map.put("tag", "latest");
        }
        return map;
    }
}
