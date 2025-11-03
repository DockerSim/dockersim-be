package com.dockersim.dto.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageMeta {
	String fullName;
	String namespace;
	String name;
	String tag;

	public void updateNamespace(String newNamespace) {
		this.namespace = newNamespace;
	}

	public void updateTag(String newTag) {
		this.tag = newTag;
	}
}
