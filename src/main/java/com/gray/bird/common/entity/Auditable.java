package com.gray.bird.common.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;

import com.gray.bird.exception.ApiException;

/**
 * Adds auditing metadata.
 * Creation, update timestamps and the ID of the user who performed the action.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable extends TimestampedEntity {
	@NotNull
	private Long createdBy;
	@NotNull
	private Long updatedBy;

	@PrePersist
	public void beforePersist() {
		Long userId = 0L; // RequestContext.getUserId();
		if (userId == null) {
			throw new ApiException("Cannot persist entity without user ID in Request Context");
		}
		setCreatedBy(userId);
		setUpdatedBy(userId);
	}

	@PreUpdate
	public void beforeUpdate() {
		Long userId = 0L; // RequestContext.getUserId();
		if (userId == null) {
			throw new ApiException("Cannot update entity without user ID in Request Context");
		}
		setCreatedBy(userId);
		setUpdatedBy(userId);
	}
}
