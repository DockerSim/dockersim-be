package com.dockersim.util;

import java.util.HashMap;
import java.util.Map;

import com.dockersim.domain.User;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;

public class ImageUtil {

	/**
	 * 이미지 이름을 통해 네임스페이스, 리포지토리, 태그를 유추합니다.
	 * registry:port가 있다면 제거합니다.
	 *
	 * @param fullName registry:port/namespace/repository:tag
	 * @return 'fullName', 'namespace', 'repository', 'tag' Key 를 가진 Map을 반환합니다.
	 */
	public static Map<String, String> parserFullName(String fullName) {
        /*
            1. 태그(Tag) 파싱(태그 누락시 기본값 latest 사용)
            registry:port/namespace '/' repository ':' tag
         */
		String name = fullName;

		String tag;
        int lastColon = name.lastIndexOf(":");
		int lastSlash = name.lastIndexOf("/");
		if (lastSlash < lastColon) { // check valid tag
			tag = name.substring(lastColon + 1);
			// registry:port/namespace/repository
			name = name.substring(0, lastColon);
		} else {
            tag = "latest";
        }

        /*
            2. 레지스트리(Registry) 식별 및 제거
            registry:port '/' namespace/repository
         */
		String[] parts = name.split("/", 2);
		if (parts.length > 1 && (parts[0].contains(".") || parts[0].contains(":"))) {
			name = parts[1]; // registry가 제거된 Image name
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
			repository = name; // 네임스페이스가 있다면 그대로 할당.
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

	public static void checkInvalidImageInfo(Map<String, String> map, User user, boolean isOwner) {
		if (isOwner && ImageUtil.namespaceSameUserName(map.get("namespace"), user)) {
			throw new BusinessException(DockerImageErrorCode.INVALID_NAMESPACE, map.get("namespace"));
		}
	}

	public static boolean namespaceSameUserName(String namespace, User user) {
		return !namespace.equals(user.getName());
	}
}
