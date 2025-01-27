package com.gray.bird.common.json;

import lombok.Getter;

@Getter
public class RelationshipToOne {
	private ResourceIdentifier data;
	private ResourceLinks links;
	private ResourceMetadata metadata;

	public RelationshipToOne(ResourceIdentifier data, ResourceLinks links, ResourceMetadata metadata) {
		this.data = data;
		this.links = links;
		this.metadata = metadata;
	}

	public RelationshipToOne(ResourceIdentifier data) {
		this.data = data;
		this.links = new ResourceLinks();
		this.metadata = new ResourceMetadata();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationshipToOne other = (RelationshipToOne) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
}
