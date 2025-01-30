package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RelationshipToOne<ID> {
	private ResourceIdentifier<ID> data;
	private Metadata metadata = new Metadata();
	private Links links = new Links();

	public RelationshipToOne(ResourceIdentifier<ID> data) {
		this.data = data;
	}

	public RelationshipToOne(String type, ID id) {
		this.data = new ResourceIdentifier<ID>(type, id);
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
		RelationshipToOne<?> other = (RelationshipToOne<?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
}
