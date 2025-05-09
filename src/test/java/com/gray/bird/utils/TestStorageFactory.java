package com.gray.bird.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class TestStorageFactory {
	public static MultipartFile imageFile(String filename, String extension, Color color) throws IOException {
		// create a 50×50 BufferedImage
		BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 50, 50);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, extension, baos);
		byte[] imageBytes = baos.toByteArray();

		MultipartFile file =
			new MockMultipartFile(filename, filename + "." + extension, "image/" + extension, imageBytes);

		return file;
	}

	public static Path writeFileToDisk(MultipartFile file, String filename, Path path) throws IOException {
		try (InputStream inputStream = file.getInputStream()) {
			Path res = path.resolve(filename).normalize();
			Files.copy(inputStream, res, StandardCopyOption.REPLACE_EXISTING);
			return res;
		}
	}
}
