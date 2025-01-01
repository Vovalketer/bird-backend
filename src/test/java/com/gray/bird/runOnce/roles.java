package com.gray.bird.runOnce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleRepository;
import com.gray.bird.role.RoleType;

@SpringBootTest
public class roles {
	@Autowired
	private RoleRepository roleRepository;

	@Test
	void loadRoles() {
		var userRole = RoleEntity.builder().type(RoleType.USER).build();
		roleRepository.save(userRole);

		var adminRole = RoleEntity.builder().type(RoleType.ADMIN).build();
		roleRepository.save(adminRole);

		var superAdminRole = RoleEntity.builder().type(RoleType.SUPER_ADMIN).build();
		roleRepository.save(superAdminRole);
	}
}
