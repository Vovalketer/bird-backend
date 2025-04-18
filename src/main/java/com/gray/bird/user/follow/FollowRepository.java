package com.gray.bird.user.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {
	@Query("SELECT CASE WHEN COUNT(f)>0 THEN true ELSE false END "
		+ "FROM FollowEntity f WHERE f.id.followingUser = :followingUser "
		+ "AND f.id.followedUser = :followedUser")
	Boolean
	existsByFollowingAndFollowedId(
		@Param("followingUser") UUID followingUser, @Param("followedUser") UUID followedUser);

	@Query("SELECT f.id.followedUser FROM FollowEntity f WHERE f.id.followingUser = :userId")
	List<UUID> findFollowing(@Param("userId") UUID userId);

	@Query("SELECT f.id.followingUser FROM FollowEntity f WHERE f.id.followedUser = :userId")
	List<UUID> findFollowed(@Param("userId") UUID userId);

	@Query("SELECT f FROM FollowEntity f WHERE f.id.followingUser = :userId")
	List<FollowEntity> findByFollowingUserId(@Param("userId") UUID userId);
	@Query("SELECT f FROM FollowEntity f WHERE f.id.followedUser = :userId")
	List<FollowEntity> findByFollowedUserId(@Param("userId") UUID userId);
	@Query("SELECT f FROM FollowEntity f WHERE f.id.followingUser = :userId OR f.id.followedUser = :userId")
	List<FollowEntity> findAllByUserId(@Param("userId") UUID userId);

	@Query("SELECT f FROM FollowEntity f WHERE f.id.followingUser IN :userIds")
	List<FollowEntity> findByFollowingUserIdsIn(List<UUID> userIds);
	@Query("SELECT f FROM FollowEntity f WHERE f.id.followedUser IN :userIds")
	List<FollowEntity> findByFollowedUserIdsIn(List<UUID> userIds);

	@Query("SELECT f FROM FollowEntity f WHERE f.id.followingUser = :userId OR f.id.followedUser IN :userIds")
	List<FollowEntity> findAllByUserIdsIn(@Param("userId") List<UUID> userIds);
}
