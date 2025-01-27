package com.gray.bird.common.json;

import lombok.Getter;

import java.util.List;

@Getter
public class RelationshipToMany {
	private List<ResourceIdentifier> data;
	private ResourceLinks links;
	private ResourceMetadata metadata;

	public RelationshipToMany(List<ResourceIdentifier> data, ResourceLinks links, ResourceMetadata metadata) {
		this.data = data;
		this.links = links;
		this.metadata = metadata;
	}

	public RelationshipToMany(List<ResourceIdentifier> data) {
		this.data = data;
		this.links = new ResourceLinks();
		this.metadata = new ResourceMetadata();
	}

	public void addLink(String key, String url) {
		links.addLink(key, url);
	}

	public void addMetadata(String key, Object value) {
		metadata.addMetadata(key, value);
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
		RelationshipToMany other = (RelationshipToMany) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
}
