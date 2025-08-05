package com.dockersim.integration.service;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.DockerImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.dockersim.service.DockerOfficeImageServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("DockerOfficeImageService 통합 테스트")
public class DockerOfficeImageServiceIntegrationTest {
    @Autowired
    private DockerOfficeImageServiceImpl service;

    @Autowired
    private DockerOfficeImageRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        service.loadAllFromJson();
    }

    @Test
    @DisplayName("1) JSON 파일을 읽어 DB에 저장한다.")
    void loadAllFromJson_savesToDatabase() {
        long count = repo.count();
        assertThat(count)
                .as("JSON 로드 후 레코드가 0보다 커야 한다")
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("2-1) 이름으로 도커 이미지 조회")
    void findByName_success() {
        DockerImageResponse response = service.findByName("centos");

        assertThat(response).as("centos 이미지는 존재해야 한다.").isNotNull();
        assertThat(response.getName()).as("이름 필드").isEqualTo("centos");
        assertThat(response.getTags()).as("태그 필드").isNotEmpty();
    }

    @Test
    @DisplayName("2-2) 이름으로 조회 - 존재하지 않으면 예외 발생")
    void findByName_notFound() {
        assertThatThrownBy(()->service.findByName("notfound"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("3-1) 전체 조회 - 기본 페이징(offset=0, limit=20)")
    void getAllImages_defaultPaging() {
        List<DockerImageResponse> list = service.getAllImages(0, 20);
        assertThat(list)
                .as("20개 이하의 결과 반환")
                .hasSizeLessThanOrEqualTo(20);
    }

    @Test
    @DisplayName("3-2) 전체 조회 - offset 범위 벗어나면 빈 리스트")
    void getAllImages_outOfRange() {
        List<DockerImageResponse> list = service.getAllImages(1000, 20);
        assertThat(list)
                .as("offset이 범위를 벗어나면 빈 리스트")
                .isEmpty();
    }

}
