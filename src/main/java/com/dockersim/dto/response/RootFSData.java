package com.dockersim.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public class RootFSData {
	public String Type = "layers";
	public List<String> Layers;
}
