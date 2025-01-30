package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@AllArgsConstructor
@Getter
public class RelationshipToMany<ID> {
	private Collection<ResourceIdentifier<ID>> data;
	private Metadata metadata = new Metadata();
	private Links links = new Links();

	public RelationshipToMany(Collection<ResourceIdentifier<ID>> data) {
		this.data = data;
	}

	public RelationshipToMany(String type, Collection<ID> ids) {
		this.data = ids.stream().map(id -> new ResourceIdentifier<>(type, id)).toList();
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
		RelationshipToMany<?> other = (RelationshipToMany<?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
}
