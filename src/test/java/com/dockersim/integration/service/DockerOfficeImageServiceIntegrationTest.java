package com.dockersim.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.integration.IntegrationTestSupport;
import com.dockersim.service.image.DockerOfficeImageService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("DockerOfficeImageService 통합 테스트")
class DockerOfficeImageServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private DockerOfficeImageService dockerOfficeImageService;

    @BeforeEach
    void setUp() {
        dockerOfficeImageService.loadAllFromJson();
    }

    @Test
    @DisplayName("1) JSON 파일을 읽어 DB에 저장한다.")
    void loadAndSave() {
        List<DockerOfficeImageResponse> images = dockerOfficeImageService.getAllImages();
        assertThat(images).isNotEmpty();
    }

    @Test
    @DisplayName("2-1) 이름으로 도커 이미지 조회")
    void findByName() {
        List<DockerOfficeImageResponse> images = dockerOfficeImageService.findAllByName("nginx");
        assertThat(images).isNotEmpty();
        assertThat(images.get(0).getName()).isEqualTo("nginx");
    }

    @Test
    @DisplayName("2-2) 이름으로 조회 - 존재하지 않으면 예외 발생")
    void findByName_notFound() {
        assertThatThrownBy(() -> dockerOfficeImageService.findAllByName("non-existent-image"))
            .isInstanceOf(BusinessException.class);
    }

//    @Test
//    @DisplayName("3-1) 전체 조회 - 기본 페이징(offset=0, limit=20)")
//    void getAllImages_defaultPaging() {
//        List<DockerOfficeImageResponse> images = dockerOfficeImageService.getAllImages(0, 20);
//        assertThat(images).hasSize(20);
//    }

//    @Test
//    @DisplayName("3-2) 전체 조회 - offset 범위 벗어나면 빈 리스트")
//    void getAllImages_offsetOutOfBounds() {
//        long totalImages = dockerOfficeImageService.getAllImages(0, Integer.MAX_VALUE).size();
//        List<DockerOfficeImageResponse> images = dockerOfficeImageService.getAllImages(
//            (int) totalImages, 10);
//        assertThat(images).isEmpty();
//    }
}
