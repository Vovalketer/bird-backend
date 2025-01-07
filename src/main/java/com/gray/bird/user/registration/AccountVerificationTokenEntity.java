package com.gray.bird.user.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.common.entity.TimestampedEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_verification_tokens")
public class AccountVerificationTokenEntity extends TimestampedEntity {
	@Id
	@GeneratedValue
	private Long id;
	private String token;
	@Column(nullable = false)
	private Long userId;
	private LocalDateTime expiresAt;

	public AccountVerificationTokenEntity(Long userId, LocalDateTime expiresAt) {
		this.userId = userId;
		this.expiresAt = expiresAt;
		this.token = UUID.randomUUID().toString();
	}
}
