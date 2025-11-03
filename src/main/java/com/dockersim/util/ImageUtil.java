package com.dockersim.util;

import com.dockersim.domain.User;
import com.dockersim.dto.util.ImageMeta;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;

public class ImageUtil {

	/**
	 * 이미지 이름을 통해 네임스페이스, 리포지토리, 태그를 유추합니다.
	 * registry:port가 있다면 제거합니다.
	 *
	 * @param target registry:port/namespace/name:tag
	 * @return 'fullName', 'namespace', 'name', 'tag' Key 를 가진 Map을 반환합니다.
	 */
	public static ImageMeta parserFullName(String target) {
		/*
		target이 hex ID이면
		fullName: target
		namespace: ""
		name: target
		tag: "latest"

		target이 네임스페이스가 명시되지 않은 name이면
		fullName: target
		namespace: ""
		name: name
		tag: tag or "latest"

		target이 네임스페이스가 명시된 name이면
		fullName: target
		namespace: namespace
		name: namespace/name
		tag: tag or "latest"

		 */
		String fullName = target;
        /*
            1. 태그(Tag) 파싱(태그 누락시 기본값 latest 사용)
            registry:port/namespace '/' repository ':' tag
         */

		String tag;
		int lastColon = target.lastIndexOf(":");
		int lastSlash = target.lastIndexOf("/");
		if (lastSlash < lastColon) { // check valid tag
			/*
			lastSlash = -1 이면 : name:tag or name
			lastSlash = -1, lastColon = -1 이면 : name
			 */
			tag = target.substring(lastColon + 1);
			// registry:port/namespace/name
			target = target.substring(0, lastColon);
		} else {
			tag = "latest";
		}

        /*
            2. 레지스트리(Registry) 식별 및 제거
            registry:port '/' namespace/name
         */
		String[] parts = target.split("/", 2);
		if (parts.length > 1 && (parts[0].contains(".") || parts[0].contains(":"))) {
			/*
			parts.length <= 1이면: name or namespace/name

			registry에 '.'이 있거나, ':'뒤에 port가 존재할 수 있다.
			만약 해당 조건 분기에 속하지 않는다면, registry:port는 존재하지 않은 상태이다.
			 */
			target = parts[1]; // name or namespace/name
		}

        /*
            3. 네임스페이스(namespace)/name 분리
            namespace '/' name
         */
		String namespace = "";
		String name = target;
		lastSlash = target.lastIndexOf("/");
		if (lastSlash > -1) {
			namespace = target.substring(0, lastSlash);
		}
		return new ImageMeta(fullName, namespace, name, tag);
	}

	public static void checkInvalidImageInfo(ImageMeta info, User user, boolean isOwner) {
		if (isOwner && !info.getNamespace().isEmpty() && ImageUtil.namespaceSameUserName(info.getNamespace(), user)) {
			throw new BusinessException(DockerImageErrorCode.INVALID_NAMESPACE, info.getNamespace());
		}
	}

	public static boolean namespaceSameUserName(String namespace, User user) {
		return !namespace.equals(user.getName());
	}
}
