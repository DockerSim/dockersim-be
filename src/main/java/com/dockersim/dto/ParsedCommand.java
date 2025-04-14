package com.dockersim.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class ParsedCommand {
    private String group;         // e.g. container, image, volume
    private String command;       // e.g. run, exec, stop
    private List<String> flags = new ArrayList<>();         // e.g. -it, --rm
    private Map<String, String> options = new HashMap<>(); // --name=myapp OR --name myapp
    private List<String> args = new ArrayList<>();          // 나머지 이미지명, 대상 등
}
