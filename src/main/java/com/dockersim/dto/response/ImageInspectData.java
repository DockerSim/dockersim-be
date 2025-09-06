package com.dockersim.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public class ImageInspectData {
	public String Id;
	public List<String> RepoTags;
	public String Created;
	public RootFSData RootFS;
}


