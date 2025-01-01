package com.gray.bird.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.gray.bird.exception.ApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements IEmailService {
	private final JavaMailSender emailSender;

	@Value("${spring.mail.verify.host}")
	private String host;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Async
	public void sendNewAccountEmail(String name, String email, String token) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setSubject(EmailConstants.SUBJECT_NEW_USER_ACCOUNT_VERIFICATION);
			message.setFrom(fromEmail);
			message.setTo(email);
			message.setText(getEmailMessage(name, host, token));
			// emailSender.send(message);
			log.info("Sending verification email to " + email);
			log.warn("EMAIL SENDER DISABLED");
			log.warn("VERIFICATION TOKEN: {}", token);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw new ApiException("Unable to send email");
		}
	}

	@Async
	public void sendPasswordResetEmail(String name, String email, String token) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setSubject(EmailConstants.SUBJECT_PASSWORD_RESET_REQUEST);
			message.setFrom(fromEmail);
			message.setTo(email);
			message.setText(getResetPasswordMessage(name, host, token));
			emailSender.send(message);
			log.info("Sending Password Reset email to " + email);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw new ApiException("Unable to send email");
		}
	}

	private String getEmailMessage(String name, String host, String token) {
		return "Hello " + name
			+ (", \n\nYour new account has been created. Please click on the link below to verify "
				+ "your account.\n\n")
			+ getVerificationUrl(host, token) + "\n\nThe Suport Team";
	}

	private String getVerificationUrl(String host, String token) {
		// TODO: the path is hardcoded, make it dynamic - maybe pass it as a parameter
		return host + "/auth/verify/account?token=" + token;
	}

	private String getResetPasswordMessage(String name, String host, String token) {
		return "Hello " + name
			+ (", \n\nThe link to reset your password has been sent. Please click on the link "
				+ "below to reset your password.\n\n")
			+ getPasswordResetUrl(host, token) + "\n\nThe Suport Team";
	}

	private String getPasswordResetUrl(String host, String token) {
		return host + "auth/verify/password?token=" + token;
	}
}
