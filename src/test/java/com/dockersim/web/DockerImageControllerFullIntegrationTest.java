package com.dockersim.web;

import com.dockersim.exception.code.DockerImageErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        properties = "spring.jpa.hibernate.ddl-auto=create",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@DisplayName("DockerImageController 전체 플로우 통합 테스트")
public class DockerImageControllerFullIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /api/images?offset=0&limit=5")
    void getAllImages() throws Exception {
        mvc.perform(get("/api/images")
                        .param("offset", "0")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @DisplayName("GET /api/images/search?name=centos")
    void findByName() throws Exception {
        mvc.perform(get("/api/images/search")
                        .param("name", "centos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("centos"))
                .andExpect(jsonPath("$.data.tags").isArray());
    }

    @Test
    @DisplayName("GET /api/images/search?name=notExist")
    void findByName_notExist() throws Exception {
        mvc.perform(get("/api/images/search")
                        .param("name", "notExist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND.getMessage(
                        "notExist"
                )));
    }
}
