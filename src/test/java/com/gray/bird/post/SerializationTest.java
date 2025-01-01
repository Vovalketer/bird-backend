package com.gray.bird.post;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.postAggregate.PostAggregate;
import com.gray.bird.postAggregate.PostResourceConverter;
import com.gray.bird.utils.TestUtils;

import lombok.extern.slf4j.Slf4j;

@JsonTest
@Slf4j
public class SerializationTest {
	@Autowired
	private TestUtils testUtils;
	@Autowired
	private PostResourceConverter postResourceConverter;
	@Autowired
	private JacksonTester<ResourceSingleAggregate> jacksonTester;

	@Test
	void testSerialization() throws IOException {
		PostAggregate postAggregate = testUtils.createPostAggregate();
		ResourceSingleAggregate aggregate = postResourceConverter.toAggregate(postAggregate);

		JsonContent<ResourceSingleAggregate> json = jacksonTester.write(aggregate);

		log.info(json.toString());
	}

	@Test
	void contextLoads() {
		Assertions.assertThat(jacksonTester).isNotNull();
	}
}
