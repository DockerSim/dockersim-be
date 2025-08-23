package com.dockersim.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.DockerImageJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DockerOfficeImage 전체 파이프라인 테스트 ")
public class DockerOfficeImagePipeLineTest {

    private static List<DockerImageJson> jsonList;

    @BeforeAll
    @DisplayName("JSON 파일 로드 및 DTO 리스트 생성")
    static void setUp() throws Exception {
        // FIX: ObjectMapper를 지역 변수로 변경
        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream is = DockerOfficeImagePipeLineTest.class.getClassLoader()
            .getResourceAsStream("static/data/docker_images.json");

        assertThat(is).as("등록된 경로에서 JSON파일을 찾을 수 있어야 한다.").isNotNull();
        // FIX: 다이아몬드 연산자(<>) 사용
        jsonList = mapper.readValue(is, new TypeReference<>() {
        });
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

        DockerOfficeImage entity = DockerOfficeImage.from(dto, "latest");

        assertThat(entity.getName()).as("name").isEqualTo(dto.getName());
        assertThat(entity.getNamespace()).as("namespace").isEqualTo(dto.getNamespace());
        assertThat(entity.getDescription()).as("description").isEqualTo(dto.getDescription());
        assertThat(entity.getStarCount()).as("starCount").isEqualTo(dto.getStarCount());
        assertThat(entity.getPullCount()).as("pullCount").isEqualTo(dto.getPullCount());
        assertThat(entity.getLastUpdated()).as("lastUpdated")
            .isEqualTo(
                LocalDate.parse(dto.getLastUpdated(), DateTimeFormatter.ISO_DATE).atStartOfDay());
        assertThat(entity.getDateRegistered()).as("dateRegistered")
            .isEqualTo(LocalDate.parse(dto.getDateRegistered(), DateTimeFormatter.ISO_DATE)
                .atStartOfDay());
        assertThat(entity.getLogoUrl()).as("logoUrl").isEqualTo(dto.getLogoUrl());
        assertThat(entity.getTag()).as("tag").isNotEmpty();
    }

    @Test
    @DisplayName("태그 매핑 검증")
    void factoryMapping_tagsAndBackReference() {
        DockerImageJson dto = jsonList.get(0);
        List<String> tags = jsonList.get(0).getTags();

        List<DockerOfficeImage> entityList = tags.stream()
            .map(tag -> DockerOfficeImage.from(dto, tag))
            .toList();

        assertThat(tags)
            .as("tags 개수와 순서")
            .hasSize(entityList.size())
            .containsExactlyInAnyOrderElementsOf(entityList.stream()
                .map(DockerOfficeImage::getTag)
                .toList());
    }
}
