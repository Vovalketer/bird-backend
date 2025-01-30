package com.gray.bird.media.dto;

import com.gray.bird.common.json.ResourceData;

public class MediaResource extends ResourceData<Long, MediaAttributes, MediaRelationships> {
	private static final String MEDIA_TYPE = "media";

	public MediaResource(Long id, MediaAttributes attributes, MediaRelationships relationships) {
		super(MEDIA_TYPE, id, attributes, relationships);
	}

	public static String type() {
		return MEDIA_TYPE;
	}
}
