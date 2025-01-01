package com.gray.bird.email;

public interface IEmailService {
	void sendNewAccountEmail(String name, String email, String token);
	void sendPasswordResetEmail(String name, String email, String token);
}
