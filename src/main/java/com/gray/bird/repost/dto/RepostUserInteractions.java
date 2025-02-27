package com.gray.bird.repost.dto;

import java.time.LocalDateTime;

public record RepostUserInteractions(Boolean isReposted, LocalDateTime repostedAt) {
}
