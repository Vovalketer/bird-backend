package com.gray.bird.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.gray.bird.like.LikeEntity;
import com.gray.bird.like.LikeRepository;
import com.gray.bird.repost.RepostEntity;
import com.gray.bird.repost.RepostRepository;
import com.gray.bird.user.UserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InteractionsService {
	private final LikeRepository likesRepo;
	private final RepostRepository repostRepo;

	public void likePost(UserEntity user, PostEntity post) {
		LikeEntity like = new LikeEntity(user, post);
		log.info(like.toString());
		LikeEntity save = likesRepo.save(like);
		log.info(save.toString());
	}

	public void repost(UserEntity user, PostEntity post) {
		RepostEntity repost = new RepostEntity(user, post);
		repostRepo.save(repost);
	}
}
