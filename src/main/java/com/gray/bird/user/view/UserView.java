package com.gray.bird.user.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.role.RoleType;

@Entity
@Immutable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_view")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserView {
	@Id
	@JsonIgnore
	private Long id;
	private String referenceId;
	private String username;
	private String handle;
	private String bio;
	private LocalDate dateOfBirth;
	private String location;
	@Enumerated(EnumType.STRING)
	private RoleType roleType;
	private String profileImage;
	private LocalDateTime createdAt;
}
