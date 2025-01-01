package com.gray.bird.email;

public class EmailUtils {
	public String getNewAccountMessage(String name, String host, String token) {
		return "Hello " + name
			+ (", \n\nYour new account has been created. Please click on the link below to verify "
				+ "your account.\n\n")
			+ getVerificationUrl(host, token) + "\n\nThe Suport Team";
	}

	public String getResetPasswordMessage(String name, String host, String token) {
		return "Hello " + name
			+ (", \n\nThe link to reset your password has been sent. Please click on the link "
				+ "below to reset your password.\n\n")
			+ getPasswordResetUrl(host, token) + "\n\nThe Suport Team";
	}

	private String getVerificationUrl(String host, String token) {
		return host + "/verify/accouont?token=" + token;
	}

	private String getPasswordResetUrl(String host, String token) {
		return host + "/verify/password?token=" + token;
	}
}
