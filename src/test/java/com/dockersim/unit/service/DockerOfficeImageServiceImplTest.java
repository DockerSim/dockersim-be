package com.dockersim.unit.service;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.domain.DockerOfficeTag;
import com.dockersim.dto.DockerImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.dockersim.service.DockerImageJson;
import com.dockersim.service.DockerOfficeImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("DockerOfficeImageServiceImpl 단위 테스트")
public class DockerOfficeImageServiceImplTest {
    @Mock
    private DockerOfficeImageRepository repo;

    @InjectMocks
    private DockerOfficeImageServiceImpl service;

    private DockerOfficeImage sampleImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample Entity
        sampleImage = DockerOfficeImage.builder()
                .name("test")
                .namespace("lib")
                .description("desc")
                .starCount(1)
                .pullCount(2L)
                .lastUpdated(LocalDate.now())
                .dateRegistered(LocalDate.now().minusDays(1))
                .logoUrl("url")
                .build();
        List<DockerOfficeTag> tags = List.of("a","b","c").stream()
                .map(t -> DockerOfficeTag.builder().tag(t).image(sampleImage).build())
                .toList();
        sampleImage.getTags().addAll(tags);
    }

    @Test
    @DisplayName("findByName - 성공: 모든 필드가 올바르게 매핑되야 한다.")
    void findByName_success() {
        given(repo.findByName("test")).willReturn(Optional.of(sampleImage));

        DockerImageResponse response = service.findByName("test");

        assertThat(response.getName()).as("name").isEqualTo("test");
        assertThat(response.getNamespace()).as("namespace").isEqualTo("lib");
        assertThat(response.getDescription()).as("description").isEqualTo("desc");
        assertThat(response.getStarCount()).as("starCount").isPositive();
        assertThat(response.getPullCount()).as("pullCount").isPositive();
        assertThat(response.getLogoUrl()).as("logoUrl").isEqualTo("url");
        assertThat(response.getTags()).as("tags list").containsExactly("a","b","c");
    }

    @Test
    @DisplayName("findByName - 실패: 존재하지 않는 이름의 이미지 BusinessException 발생")
    void findByName_notFound() {
        given(repo.findByName("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByName("missing"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("getAllImages - offset,limit 적용: 부분 리스트 반환")
    void getAllImages_paging() {
        // repo.findAll 목업
        DockerOfficeImage img1 = sampleImage;
        DockerOfficeImage img2 = DockerOfficeImage.fromJson(
                new DockerImageJson("n2","ns","d",0,0,"2020-01-01","2020-01-01","u",List.of("x")));
        DockerOfficeImage img3 = DockerOfficeImage.fromJson(
                new DockerImageJson("n3","ns","d",0,0,"2020-01-01","2020-01-01","u",List.of("y")));
        given(repo.findAll()).willReturn(List.of(img1, img2, img3));

        List<DockerImageResponse> page = service.getAllImages(1, 2);

        assertThat(page).hasSize(2)
                .extracting(DockerImageResponse::getName)
                .containsExactly("n2","n3");
    }

    @Test
    @DisplayName("getAllImages - offset 범위 밖이면 빈 리스트 반환")
    void getAllImages_outOfBounds() {
        given(repo.findAll()).willReturn(List.of(sampleImage));

        List<DockerImageResponse> page = service.getAllImages(1000, 10);

        assertThat(page).isEmpty();
    }
}
