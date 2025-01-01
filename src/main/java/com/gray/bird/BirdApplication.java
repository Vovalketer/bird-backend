package com.gray.bird;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class BirdApplication {
	public static void main(String[] args) {
		SpringApplication.run(BirdApplication.class, args);
	}

	// @Bean
	// CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
	// return args -> {
	// RequestContext.setUserId(0L);
	// var userRole = RoleEntity.builder().role(RoleType.USER).build();
	// roleRepository.save(userRole);

	// var adminRole = RoleEntity.builder().role(RoleType.ADMIN).build();
	// roleRepository.save(adminRole);

	// var superAdminRole = RoleEntity.builder().role(RoleType.SUPER_ADMIN).build();
	// roleRepository.save(superAdminRole);
	// RequestContext.reset();
	// };
	// }
}
