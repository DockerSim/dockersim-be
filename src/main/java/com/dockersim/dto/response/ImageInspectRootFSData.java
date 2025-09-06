package com.dockersim.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public class ImageInspectRootFSData {
	public String Type = "layers";
	public List<String> Layers;
}