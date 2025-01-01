package com.gray.bird.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.gray.bird.common.entity.TimestampedEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credentials")
public class CredentialsEntity extends TimestampedEntity {
	// TODO: rework this entity, bring all the security related stuff here
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	// TODO: convert to weak reference?
	private UserEntity user;
	private String password;

	public CredentialsEntity(UserEntity user, String password) {
		this.password = password;
		this.user = user;
	}
}
