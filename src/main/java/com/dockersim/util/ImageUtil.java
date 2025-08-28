package com.dockersim.util;

import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    /**
     * 이미지 이름을 통해 네임스페이스, 리포지토리, 태그를 유추합니다.
     *
     * @param fullName registry:port/namespace/repository:tag
     * @return 'fullName', 'namespace', 'repository', 'tag' Key 를 가진 Map을 반환합니다.
     */
    public static Map<String, String> parserFullName(String fullName) {
        /*
            1. 태그(Tag) 분리
            registry:port/namespace '/' repository ':' tag
         */
        String name = fullName;
        String tag = "latest";
        int lastColon = name.lastIndexOf(":");
        int lastSlash = name.lastIndexOf("/");
        if (lastColon > lastSlash) {
            tag = name.substring(lastColon + 1);

            // registry:port/namespace/repository
            name = name.substring(0, lastColon);
        }

        /*
            2. 레지스트리(Registry) 식별 및 제거
            registry:port '/' namespace/repository
         */
        String[] parts = name.split("/", 2);
        if (parts.length > 1 && (parts[0].contains(".") || parts[0].contains(":"))) {
            name = parts[1];
        }

        /*
            3. 네임스페이스(namespace)/리포지토리(repository) 분리
            namespace '/' repository
         */
        String namespace = "";
        String repository;
        lastSlash = name.lastIndexOf("/");
        if (lastSlash > -1) {
            namespace = name.substring(0, lastSlash);
            repository = name.substring(lastSlash + 1);
        } else {
            repository = name;
        }
        Map<String, String> components = new HashMap<>();
        components.put("fullName", fullName);
        components.put("namespace", namespace);
        components.put("repository", repository);
        components.put("tag", tag);
        return components;
    }
}
