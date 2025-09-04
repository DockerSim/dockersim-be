package com.dockersim.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.DockerImageJson;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.dockersim.service.image.DockerOfficeImageServiceImpl;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DockerOfficeImageServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class) // FIX: Use MockitoExtension for automatic lifecycle management
public class DockerOfficeImageServiceImplTest {

    @Mock
    private DockerOfficeImageRepository repo;

    @InjectMocks
    private DockerOfficeImageServiceImpl service;

    private DockerOfficeImage sampleImage;

    @BeforeEach
    void setUp() {
        // FIX: Manual initialization is no longer needed with MockitoExtension
        // MockitoAnnotations.openMocks(this);

        // Sample Entity
        sampleImage = DockerOfficeImage.builder()
            .name("test")
            .namespace("lib")
            .description("desc")
            .starCount(1)
            .pullCount(2L)
            .lastUpdated(LocalDateTime.now())
            .dateRegistered(LocalDateTime.now().minusDays(1))
            .logoUrl("url")
            .build();
    }

    @Test
    @DisplayName("findByName - 성공: 모든 필드가 올바르게 매핑되야 한다.")
    void findByName_success() {
        given(repo.findAllByName("test")).willReturn(List.of(sampleImage));

        DockerOfficeImageResponse response = service.findAllByName("test").get(0);

        assertThat(response.getName()).as("name").isEqualTo("test");
        assertThat(response.getNamespace()).as("namespace").isEqualTo("lib");
        assertThat(response.getDescription()).as("description").isEqualTo("desc");
        assertThat(response.getStarCount()).as("starCount").isPositive();
        assertThat(response.getPullCount()).as("pullCount").isPositive();
        assertThat(response.getLogoUrl()).as("logoUrl").isEqualTo("url");
    }

    @Test
    @DisplayName("findByName - 실패: 존재하지 않는 이름의 이미지 BusinessException 발생")
    void findByName_notFound() {
        given(repo.findAllByName("missing")).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.findAllByName("missing"))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("getAllImages - offset,limit 적용: 부분 리스트 반환")
    void getAllImages_paging() {
        // repo.findAll 목업
        DockerOfficeImage img1 = sampleImage;
        DockerOfficeImage img2 = DockerOfficeImage.from(
            new DockerImageJson("n2", "ns", "d", 0, 0, "2020-01-01", "2020-01-01", "u",
                List.of("x")), "x");
        DockerOfficeImage img3 = DockerOfficeImage.from(
            new DockerImageJson("n3", "ns", "d", 0, 0, "2020-01-01", "2020-01-01", "u",
                List.of("y")), "y");
        given(repo.findAll()).willReturn(List.of(img1, img2, img3));

        List<DockerOfficeImageResponse> page = service.getAllImages(1, 2);

        assertThat(page).hasSize(2)
            .extracting(DockerOfficeImageResponse::getName)
            .containsExactly("n2", "n3");
    }

    @Test
    @DisplayName("getAllImages - offset 범위 밖이면 빈 리스트 반환")
    void getAllImages_outOfBounds() {
        given(repo.findAll()).willReturn(List.of(sampleImage));

        List<DockerOfficeImageResponse> page = service.getAllImages(1000, 10);

        assertThat(page).isEmpty();
    }
}
