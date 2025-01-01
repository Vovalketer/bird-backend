package com.gray.bird.utils;

import org.mapstruct.factory.Mappers;

import com.gray.bird.post.PostMapper;
import com.gray.bird.postAggregate.PostAggregateMapper;
import com.gray.bird.user.UserMapper;

public class TestUtilsFactory {
	public static TestUtils createTestUtils() {
		UserMapper userMapper = Mappers.getMapper(UserMapper.class);
		PostMapper postMapper = Mappers.getMapper(PostMapper.class);
		PostAggregateMapper postAggregateMapper = Mappers.getMapper(PostAggregateMapper.class);

		return new TestUtils(userMapper, postMapper, postAggregateMapper);
	}
}
