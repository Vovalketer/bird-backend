package com.gray.bird.common.jsonApi;

import java.util.List;

public interface RelationshipToMany extends ResourceLinks, ResourceMetadata {
	List<ResourceIdentifier> getData();
	void addData(ResourceIdentifier data);
	Boolean isPresent(ResourceIdentifier resourceIdentifier);
}
