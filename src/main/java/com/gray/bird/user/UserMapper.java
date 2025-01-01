package com.gray.bird.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gray.bird.user.dto.CredentialsDto;
import com.gray.bird.user.dto.UserDataDto;
import com.gray.bird.user.dto.UserProfileDto;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.view.UserView;

@Mapper
public interface UserMapper {
	@Mapping(target = "role", source = "user.role.type")
	UserDataDto toUserDto(UserEntity user);

	UserProfileDto toUserProfile(UserEntity user);

	@Mapping(target = "password", expression = "java(credentials.getPassword().toCharArray())")
	CredentialsDto toCredentialsDto(CredentialsEntity credentials);

	@Mapping(target = "roleType", source = "user.role.type")
	UserView toUserView(UserEntity user);

	@Mapping(target = "roleType", source = "user.role.type")
	UserProjection toUserProjection(UserEntity user);
}
