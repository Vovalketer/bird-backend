package com.gray.bird.user.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {
	@Query("SELECT CASE WHEN COUNT(f)>0 THEN true ELSE false END "
		+ "FROM FollowEntity f WHERE f.id.followingUser = :followingUser "
		+ "AND f.id.followedUser = :followedUser")
	Boolean
	existsByFollowingAndFollowedId(
		@Param("followingUser") UUID followingUser, @Param("followedUser") UUID followedUser);
}
