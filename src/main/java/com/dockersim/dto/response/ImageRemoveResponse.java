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
public class ImageRemoveResponse {

    private List<String> console;

    public static ImageRemoveResponse from(List<String> console) {
        return ImageRemoveResponse.builder()
            .console(console)
            .build();
    }
}
