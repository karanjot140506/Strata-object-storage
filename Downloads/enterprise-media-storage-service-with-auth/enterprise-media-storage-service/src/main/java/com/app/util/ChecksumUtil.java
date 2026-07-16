package com.app.util;

import java.io.InputStream;
import java.security.MessageDigest;

public class ChecksumUtil {

	private ChecksumUtil() {
	}

	public static String sha256(InputStream inputStream) throws Exception {

		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		byte[] buffer = new byte[8192];

		int bytesRead;

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			digest.update(buffer, 0, bytesRead);
		}

		byte[] hash = digest.digest();

		StringBuilder sb = new StringBuilder();

		for (byte b : hash) {
			sb.append(String.format("%02x", b));
		}

		return sb.toString();
	}
}