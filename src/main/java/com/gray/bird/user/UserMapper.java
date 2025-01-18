package com.gray.bird.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gray.bird.user.dto.CredentialsDto;
import com.gray.bird.user.dto.UserDataDto;
import com.gray.bird.user.dto.UserProfileDto;
import com.gray.bird.user.dto.UserProjection;

@Mapper
public interface UserMapper {
	@Mapping(target = "role", source = "role.type")
	UserDataDto toUserDto(UserEntity user);

	UserProfileDto toUserProfile(UserEntity user);

	@Mapping(target = "password", expression = "java(credentials.getPassword().toCharArray())")
	CredentialsDto toCredentialsDto(CredentialsEntity credentials);

	@Mapping(target = "roleType", source = "user.role.type")
	@Mapping(target = "userId", source = "user.uuid")
	UserProjection toUserProjection(UserEntity user);
}
