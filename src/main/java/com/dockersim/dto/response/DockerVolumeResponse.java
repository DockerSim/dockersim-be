package com.dockersim.dto.response;

import com.dockersim.domain.DockerVolume;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerVolumeResponse {

    private List<String> console;

    private LocalDateTime createAt;
    private String driver;
    private String mountPoint;
    private String name;

    private boolean anonymous;

    public static DockerVolumeResponse from(DockerVolume volume, List<String> console) {
        return DockerVolumeResponse.builder()
            .console(console)
            .driver("local")
            .mountPoint("/var/lib/docker/volumes/" + volume.getName() + "/_data")
            .name(volume.getName())
            .anonymous(volume.isAnonymous())
            .build();
    }

}
