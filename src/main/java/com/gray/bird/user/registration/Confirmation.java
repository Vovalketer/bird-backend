package com.gray.bird.user.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.gray.bird.common.entity.TimestampedEntity;
import com.gray.bird.user.UserEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "confirmations")
public class Confirmation extends TimestampedEntity {
	@Id
	@GeneratedValue
	@Column(name = "confirmation_id")
	private Long id;
	private String token;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonProperty("user_id")
	private UserEntity user;
	private LocalDateTime expiresAt;

	public Confirmation(UserEntity user, LocalDateTime expiresAt) {
		this.user = user;
		this.expiresAt = expiresAt;
		this.token = UUID.randomUUID().toString();
	}
}
