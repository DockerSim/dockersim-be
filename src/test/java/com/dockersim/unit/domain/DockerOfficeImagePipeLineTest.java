package com.dockersim.unit.domain;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.domain.DockerOfficeTag;
import com.dockersim.service.DockerImageJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DockerOfficeImage 전체 파이프라인 테스트 ")
public class DockerOfficeImagePipeLineTest {
    private static ObjectMapper mapper;
    private static List<DockerImageJson> jsonList;

    @BeforeAll
    @DisplayName("JSON 파일 로드 및 DTO 리스트 생성")
    static void setUp() throws Exception {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream is = DockerOfficeImagePipeLineTest.class.getClassLoader()
                .getResourceAsStream("static/data/docker_images.json");

        assertThat(is).as("등록된 경로에서 JSON파일을 찾을 수 있어야 한다.").isNotNull();
        jsonList = mapper.readValue(is, new TypeReference<List<DockerImageJson>>() {});
    }

    @Test
    @DisplayName("역직렬화: JSON → DockerImageJson 리스트")
    void jsonDeserialization_shouldLoadList() {
        assertThat(jsonList)
                .as("JSON 리스트는 비어있지 않아야 한다.")
                .isNotEmpty();
    }

    @Test
    @DisplayName("DTO 검증: 주요 필드 매핑 확인")
    void dtoBasicFields_shouldBeMapped() {
        DockerImageJson dto = jsonList.get(0);

        assertThat(dto.getName()).as("name 필드").isNotBlank();
        assertThat(dto.getNamespace()).as("namespace 필드").isNotBlank();
        assertThat(dto.getPullCount()).as("pull_count는 0보다 크다.").isPositive();
        assertThat(dto.getLastUpdated()).as("last_update 필드").isNotBlank();
        assertThat(dto.getDescription()).as("description 필드").isNotBlank();
        assertThat(dto.getTags()).as("tags 리스트").isNotNull().isNotEmpty();
        assertThat(dto.getLogoUrl()).as("logo_url필드: 기본 로고 url이 있거나, 커스텀 로고 url이 었어야 한다.")
                .satisfiesAnyOf(
                        str -> assertThat(str).isNotBlank(),
                        str -> assertThat(str).isEqualTo("https://avatars.githubusercontent.com/u/5429470")
                );
    }

    @Test
    @DisplayName("팩토리 메서드 변환 검증: DTO → Entity 기본 피드 매핑 확인")
    void factoryMapping_basicFields() {
        DockerImageJson dto = jsonList.get(0);

        DockerOfficeImage entity = DockerOfficeImage.fromJson(dto);

        assertThat(entity.getName()).as("name").isEqualTo(dto.getName());
        assertThat(entity.getNamespace()).as("namespace").isEqualTo(dto.getNamespace());
        assertThat(entity.getDescription()).as("description").isEqualTo(dto.getDescription());
        assertThat(entity.getStarCount()).as("starCount").isEqualTo(dto.getStarCount());
        assertThat(entity.getPullCount()).as("pullCount").isEqualTo(dto.getPullCount());
        assertThat(entity.getLastUpdated()).as("lastUpdated")
                .isEqualTo(LocalDate.parse(dto.getLastUpdated()));
        assertThat(entity.getDateRegistered()).as("dateRegistered")
                .isEqualTo(LocalDate.parse(dto.getDateRegistered()));
        assertThat(entity.getLogoUrl()).as("logoUrl").isEqualTo(dto.getLogoUrl());
    }

    @Test
    @DisplayName("태그 매핑 및 양방향 참조 검증")
    void factoryMapping_tagsAndBackReference() {
        DockerImageJson dto = jsonList.get(0);
        DockerOfficeImage entity = DockerOfficeImage.fromJson(dto);
        List<DockerOfficeTag> tags = entity.getTags();

        assertThat(tags)
                .as("tags 개수와 순서")
                .extracting(DockerOfficeTag::getTag)
                .containsExactlyElementsOf(dto.getTags());

        // then: 각 태그의 back-reference
        assertThat(tags)
                .as("각 태그의 image 참조")
                .allSatisfy(tag -> assertThat(tag.getImage()).isSameAs(entity));
    }
}
