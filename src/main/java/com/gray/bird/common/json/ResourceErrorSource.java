package com.gray.bird.common.json;

public class ResourceErrorSource {
	private String pointer;
	private String parameter;
	private String header;

	public ResourceErrorSource(String pointer, String parameter, String header) {
		this.pointer = pointer;
		this.parameter = parameter;
		this.header = header;
	}

	public ResourceErrorSource() {
	}

	public String getPointer() {
		return pointer;
	}

	public String getParameter() {
		return parameter;
	}

	public String getHeader() {
		return header;
	}
}
