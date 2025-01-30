package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Links {
	private String self;
	private String related;
	private String first;
	private String last;
	private String prev;
	private String next;
}
