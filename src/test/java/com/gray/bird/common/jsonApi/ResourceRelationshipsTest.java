package com.gray.bird.common.jsonApi;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringJUnitConfig
public class ResourceRelationshipsTest {
	ResourceRelationships relationships;
	RelationshipToManyImpl toMany1;
	RelationshipToManyImpl toMany2;
	RelationshipToOneImpl toOne1;
	RelationshipToOneImpl toOne2;

	@BeforeEach
	void setUp() throws Exception {
		relationships = new ResourceRelationshipsImpl();
		ResourceIdentifier identifier1 = new ResourceIdentifierImpl("type1", "mockId1");
		ResourceIdentifier identifier2 = new ResourceIdentifierImpl("type2", "mockId2");
		ResourceIdentifier identifier3 = new ResourceIdentifierImpl("type3", "mockId3");
		ResourceIdentifier identifier4 = new ResourceIdentifierImpl("type4", "mockId4");
		ResourceIdentifier identifier5 = new ResourceIdentifierImpl("type5", "mockId5");
		ResourceIdentifier identifier6 = new ResourceIdentifierImpl("type6", "mockId6");

		toMany1 = new RelationshipToManyImpl(List.of(identifier1, identifier2));
		toMany2 = new RelationshipToManyImpl(List.of(identifier3, identifier4));
		toOne1 = new RelationshipToOneImpl(identifier5);
		toOne2 = new RelationshipToOneImpl(identifier6);
	}

	@Test
	void testAddRelationshipToMany() {
		relationships.addRelationshipToMany("test", toMany1);
		relationships.addRelationshipToMany("test2", toMany2);
		Optional<RelationshipToMany> relationshipToMany1 = relationships.getRelationshipToMany("test");
		Optional<RelationshipToMany> relationshipToMany2 = relationships.getRelationshipToMany("test2");
		Map<String, Object> relationshipsMap = relationships.getRelationships();
		Assertions.assertThat(relationshipToMany1).isNotEmpty();
		Assertions.assertThat(relationshipToMany1.get()).isEqualTo(toMany1);
		Assertions.assertThat(relationshipToMany2).isNotEmpty();
		Assertions.assertThat(relationshipToMany2.get()).isEqualTo(toMany2);
		Assertions.assertThat(relationshipsMap).containsEntry("test", toMany1);
		Assertions.assertThat(relationshipsMap).containsEntry("test2", toMany2);
	}

	@Test
	void testAddRelationshipToOne() {
		relationships.addRelationshipToOne("test", toOne1);
		relationships.addRelationshipToOne("test2", toOne2);
		Optional<RelationshipToOne> relationshipToOne1 = relationships.getRelationshipToOne("test");
		Optional<RelationshipToOne> relationshipToOne2 = relationships.getRelationshipToOne("test2");
		Map<String, Object> relationshipsMap = relationships.getRelationships();
		Assertions.assertThat(relationshipToOne1).isNotEmpty();
		Assertions.assertThat(relationshipToOne1.get()).isEqualTo(toOne1);
		Assertions.assertThat(relationshipToOne2).isNotEmpty();
		Assertions.assertThat(relationshipToOne2.get()).isEqualTo(toOne2);
		Assertions.assertThat(relationshipsMap).containsEntry("test", toOne1);
		Assertions.assertThat(relationshipsMap).containsEntry("test2", toOne2);
	}

	@Test
	void testRemoveRelationship() {
		relationships.addRelationshipToOne("test", toOne1);
		relationships.addRelationshipToMany("test2", toMany1);
		relationships.removeRelationship("test");
		relationships.removeRelationship("test2");
		Optional<RelationshipToOne> relationshipToOne = relationships.getRelationshipToOne("test");
		Optional<RelationshipToMany> relationshipToMany = relationships.getRelationshipToMany("test2");
		Map<String, Object> relationshipsMap = relationships.getRelationships();
		Assertions.assertThat(relationshipToOne).isEmpty();
		Assertions.assertThat(relationshipToMany).isEmpty();
		Assertions.assertThat(relationshipsMap).isEmpty();
	}
}
