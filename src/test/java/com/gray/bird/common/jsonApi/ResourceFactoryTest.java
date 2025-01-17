package com.gray.bird.common.jsonApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.gray.bird.config.ObjectMapperConfig;

@SpringBootTest(classes = {ResourceFactory.class, ObjectMapperConfig.class})
public class ResourceFactoryTest {
	@Autowired
	private ResourceFactory resourceFactory;

	@Test
	void testCreateIdentifier() {
		String type = "testType";
		String id = "testId";
		ResourceIdentifier identifier = resourceFactory.createIdentifier(type, id);

		Assertions.assertThat(identifier).isNotNull();
		Assertions.assertThat(identifier.getType()).isEqualTo(type);
		Assertions.assertThat(identifier.getId()).isEqualTo(id);
	}

	@Test
	void testCreateAttributes() {
		LocalDate dob = LocalDate.of(1970, 1, 1);
		LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 1);
		TestAttributes attributes = new TestAttributes("testUsername", "testHandle", dob, createdAt);

		ResourceAttributes resourceAttributes = resourceFactory.createAttributes(attributes);

		Assertions.assertThat(resourceAttributes).isNotNull();
		Assertions.assertThat(resourceAttributes.getAttributes()).isNotNull();
		Assertions.assertThat(resourceAttributes.getAttribute("username")).isEqualTo("testUsername");
		Assertions.assertThat(resourceAttributes.getAttribute("handle")).isEqualTo("testHandle");
		Assertions.assertThat(resourceAttributes.getAttribute("dateOfBirth")).isEqualTo(dob.toString());
		Assertions.assertThat(resourceAttributes.getAttribute("createdAt")).isEqualTo(createdAt.toString());
	}

	@Test
	void testCreateRelationshipToMany() {
		ResourceIdentifier id1 = resourceFactory.createIdentifier("testType1", "testId1");
		ResourceIdentifier id2 = resourceFactory.createIdentifier("testType2", "testId2");
		ResourceIdentifier id3 = resourceFactory.createIdentifier("testType3", "testId3");
		List<ResourceIdentifier> idList = List.of(id1, id2, id3);

		RelationshipToMany rel = resourceFactory.createRelationshipToMany();
		rel.addAllData(idList);

		Assertions.assertThat(rel.getData()).containsExactlyInAnyOrderElementsOf(idList);
	}

	@Test
	void testCreateRelationshipToOne() {
		ResourceIdentifier id1 = resourceFactory.createIdentifier("testType1", "testId1");

		RelationshipToOne rel = resourceFactory.createRelationshipToOne(id1);

		Assertions.assertThat(rel.getData()).isEqualTo(id1);
	}

	@Test
	void testCreateData() {
		// required to create the data object
		ResourceIdentifier id1 = resourceFactory.createIdentifier("testType1", "testId1");
		LocalDate dob = LocalDate.of(1970, 1, 1);
		LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 1);
		TestAttributes attributes = new TestAttributes("testUsername", "testHandle", dob, createdAt);
		ResourceAttributes resourceAttributes = resourceFactory.createAttributes(attributes);

		// create relationships
		ResourceIdentifier relToOneId = resourceFactory.createIdentifier("relToOneType", "relToOneId");
		RelationshipToOne relationshipToOne = resourceFactory.createRelationshipToOne(relToOneId);

		ResourceIdentifier relToManyId1 = resourceFactory.createIdentifier("relToManyType1", "relToManyId1");
		ResourceIdentifier relToManyId2 = resourceFactory.createIdentifier("relToManyType2", "relToManyId2");
		ResourceIdentifier relToManyId3 = resourceFactory.createIdentifier("relToManyType3", "relToManyId3");
		RelationshipToMany relationshipToMany = resourceFactory.createRelationshipToMany();
		List<ResourceIdentifier> relToManyIds = List.of(relToManyId1, relToManyId2, relToManyId3);
		relationshipToMany.addAllData(relToManyIds);

		ResourceData data = resourceFactory.createData(id1, resourceAttributes);
		data.addRelationshipToOne("relToOne", relationshipToOne);
		data.addRelationshipToMany("relToMany", relationshipToMany);

		Assertions.assertThat(data.getId()).isEqualTo(id1.getId());
		Assertions.assertThat(data.getType()).isEqualTo(id1.getType());
		Assertions.assertThat(data.getAttributes()).isEqualTo(resourceAttributes.getAttributes());
		Assertions.assertThat(data.getRelationshipToOne("relToOne").get().getData()).isEqualTo(relToOneId);
		Assertions.assertThat(data.getRelationshipToMany("relToMany").get().getData())
			.containsExactlyInAnyOrderElementsOf(relToManyIds);
	}

	@Test
	void testCreateLinks() {
		ResourceLinks links = resourceFactory.createLinks();
		links.addLink("self", "http://localhost:8080/test");
		links.addLink("related", "http://localhost:8080/test/related");

		Assertions.assertThat(links.getLinks()).containsEntry("self", "http://localhost:8080/test");
		Assertions.assertThat(links.getLinks())
			.containsEntry("related", "http://localhost:8080/test/related");
	}

	@Test
	void testCreateMetadata() {
		ResourceMetadata metadata = resourceFactory.createMetadata();
		TestMetadata testMetadata = new TestMetadata("testKey", "testKey2");
		TestMetadata testMetadata2 = new TestMetadata("testKey3", "testKey4");
		metadata.addMetadata("testKey", testMetadata);
		metadata.addMetadata("testKey2", testMetadata2);

		Assertions.assertThat(metadata.getMetadata()).containsEntry("testKey", testMetadata);
		Assertions.assertThat(metadata.getMetadata()).containsEntry("testKey2", testMetadata2);
	}

	private record TestAttributes(
		String username, String handle, LocalDate dateOfBirth, LocalDateTime createdAt) {
	}

	private record TestMetadata(String testKey, String testKey2) {
	}
}
