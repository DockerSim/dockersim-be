package com.dockersim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedDockerCommand {
    private String command;
    private String mainCommand; // docker
    private String group; // container, image, volume, network
    private String subCommand; // run, stop, pull, build 등
    private List<String> arguments;
    private Map<String, String> options; // --name mycontainer
    private Map<String, List<String>> multiOptions; // -e KEY=VALUE -e KEY2=VALUE2
    private List<String> flags; // -d, -it

    // 파싱된 특수 값들
    private String imageName;
    private String imageTag;
    private String containerName;
    private String networkName;
    private String volumeName;

    // 유효성 검사 결과
    private boolean valid;
    private String errorMessage;

    public String getFullImageName() {
        if (imageName == null)
            return null;
        return imageTag != null ? imageName + ":" + imageTag : imageName + ":latest";
    }

    public boolean hasFlag(String flag) {
        return flags != null && flags.contains(flag);
    }

    public String getOption(String key) {
        return options != null ? options.get(key) : null;
    }

    public List<String> getMultiOption(String key) {
        return multiOptions != null ? multiOptions.get(key) : null;
    }
}