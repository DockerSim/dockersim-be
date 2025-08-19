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
    public DockerImageResponse pullImage(String name) {
        Map<String, String> image = parserImageName(name);

        Optional<DockerImage> userImage = repo.findByNameAndTag(
            image.get("repository"),
            image.get("tag")
        );

        if (userImage.isPresent()) {
            return DockerImageResponse.from(userImage.get(),
                List.of(SuccessCode.COMMAND_EXECUTE_READ.getMessage("image", name)));
        }

        DockerOfficeImage officeImage = officeImageService.findByNameAndTag(
            image.get("repository"),
            image.get("tag")
        );
        if (officeImage != null) {
            repo.save(DockerImage.from(officeImage));
            return DockerImageResponse.from(officeImage,
                List.of(
                    DockerImageErrorCode.USER_IMAGE_NOT_FOUND.getMessage(name),
                    SuccessCode.COMMAND_EXECUTE_PULL_IMAGE.getMessage(name)
                )
            );
        }

        // Step 4. 존재하지 않는 이미지, 예외 처리
        throw new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, name);
    }

    @Override
    public List<DockerImageResponse> listImages(String name, boolean all, boolean quiet) {
        return null;
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
            map.put("repository", parts[0]);
            map.put("tag", parts[1]);
        } else {
            map.put("repository", name);
            map.put("tag", "latest");
        }
        return map;
    }
}
