package com.gray.bird.auth.jwt;

import java.time.LocalDateTime;

import com.gray.bird.common.entity.TimestampedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshTokenEntity extends TimestampedEntity {
	// maybe implement token rotation in a commit later
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true, columnDefinition = "text")
	private String token;
	@Column(nullable = false)
	private String username;
	@Column(nullable = false)
	private LocalDateTime expiresAt;
	// we dont want to modify the expiresAt value so we'll use revokedAt to handle logouts
	private LocalDateTime revokedAt;

	// private String ip;
	public RefreshTokenEntity(String token, String username, LocalDateTime expiresAt) {
		this.token = token;
		this.username = username;
		this.expiresAt = expiresAt;
	}

}
