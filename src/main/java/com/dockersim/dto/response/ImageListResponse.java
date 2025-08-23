package com.dockersim.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageListResponse {

    private List<String> console;

    public static ImageListResponse from(List<String> console) {
        return ImageListResponse.builder()
            .console(console)
            .build();
    }
}
