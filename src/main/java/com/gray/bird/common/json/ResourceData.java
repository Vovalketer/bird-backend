package com.gray.bird.common.json;

import lombok.Getter;

@Getter
public class ResourceData<ID, ATT, REL> extends ResourceIdentifier<ID> {
	private ATT attributes;
	private REL relationships;
	private Metadata metadata = new Metadata();
	private Links links = new Links();

	public ResourceData(String type, ID id, ATT attributes, REL relationships) {
		super(type, id);
		this.attributes = attributes;
		this.relationships = relationships;
	}

	public void addMetadata(String key, Object metadata) {
		this.metadata.addMetadata(key, metadata);
	}

	public Object getMetadata(String key) {
		return this.metadata.getMetadata(key);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
