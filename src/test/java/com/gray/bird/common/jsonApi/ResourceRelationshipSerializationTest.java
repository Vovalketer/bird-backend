package com.gray.bird.common.jsonApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@JsonTest
public class ResourceRelationshipSerializationTest {
	ResourceRelationships relationships;
	RelationshipToManyImpl toMany1;
	RelationshipToManyImpl toMany2;
	RelationshipToOneImpl toOne1;
	RelationshipToOneImpl toOne2;

	@Autowired
	JacksonTester<ResourceRelationships> jacksonTester;

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
	void testSerializationToOne() throws IOException {
		relationships.addRelationshipToOne("test", toOne1);
		relationships.addRelationshipToOne("test2", toOne2);

		JsonContent<ResourceRelationships> json = jacksonTester.write(relationships);

		// test root
		Assertions.assertThat(json.getJson()).isNotNull();
		Assertions.assertThat(json).hasJsonPathValue("$.relationships");
		Assertions.assertThat(json).extractingJsonPathNumberValue("$.relationships.length()").isEqualTo(2);

		// test first relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test.data.type")
			.isEqualTo("type5");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test.data.id")
			.isEqualTo("mockId5");

		// test second relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test2");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test2.data.type")
			.isEqualTo("type6");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test2.data.id")
			.isEqualTo("mockId6");
	}

	@Test
	void testSerializationToMany() throws IOException {
		relationships.addRelationshipToMany("test3", toMany1);
		relationships.addRelationshipToMany("test4", toMany2);

		JsonContent<ResourceRelationships> json = jacksonTester.write(relationships);

		// test root
		Assertions.assertThat(json.getJson()).isNotNull();
		Assertions.assertThat(json).extractingJsonPathNumberValue("$.relationships.length()").isEqualTo(2);

		// test first relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test3");
		Assertions.assertThat(json)
			.extractingJsonPathNumberValue("$.relationships.test3.data.length()")
			.isEqualTo(2);
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[0].type")
			.isEqualTo("type1");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[0].id")
			.isEqualTo("mockId1");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[1].type")
			.isEqualTo("type2");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[1].id")
			.isEqualTo("mockId2");

		// test second relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test4");
		Assertions.assertThat(json)
			.extractingJsonPathNumberValue("$.relationships.test4.data.length()")
			.isEqualTo(2);
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test4.data[0].type")
			.isEqualTo("type3");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test4.data[0].id")
			.isEqualTo("mockId3");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test4.data[1].type")
			.isEqualTo("type4");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test4.data[1].id")
			.isEqualTo("mockId4");
	}

	@Test
	void testCombinedSerialization() throws IOException {
		relationships.addRelationshipToOne("test", toOne1);
		relationships.addRelationshipToMany("test2", toMany1);
		relationships.addRelationshipToMany("test3", toMany2);
		relationships.addRelationshipToOne("test4", toOne2);

		JsonContent<ResourceRelationships> json = jacksonTester.write(relationships);

		System.out.println(json.getJson());

		// test root
		Assertions.assertThat(json.getJson()).isNotNull();
		Assertions.assertThat(json).extractingJsonPathNumberValue("$.relationships.length()").isEqualTo(4);

		// test first relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test.data.type")
			.isEqualTo("type5");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test.data.id")
			.isEqualTo("mockId5");

		// test second relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test2");
		Assertions.assertThat(json)
			.extractingJsonPathNumberValue("$.relationships.test2.data.length()")
			.isEqualTo(2);
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test2.data[0].type")
			.isEqualTo("type1");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test2.data[0].id")
			.isEqualTo("mockId1");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test2.data[1].type")
			.isEqualTo("type2");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test2.data[1].id")
			.isEqualTo("mockId2");

		// test third relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test3");
		Assertions.assertThat(json)
			.extractingJsonPathNumberValue("$.relationships.test3.data.length()")
			.isEqualTo(2);
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[0].type")
			.isEqualTo("type3");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[0].id")
			.isEqualTo("mockId3");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[1].type")
			.isEqualTo("type4");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test3.data[1].id")
			.isEqualTo("mockId4");

		// test fourth relationship
		Assertions.assertThat(json).hasJsonPathValue("$.relationships.test4");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test4.data.type")
			.isEqualTo("type6");
		Assertions.assertThat(json)
			.extractingJsonPathStringValue("$.relationships.test4.data.id")
			.isEqualTo("mockId6");
	}
}
