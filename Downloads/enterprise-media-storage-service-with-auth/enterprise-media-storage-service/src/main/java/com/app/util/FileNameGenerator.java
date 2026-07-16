package com.app.util;

import java.util.UUID;

public class FileNameGenerator {

	private FileNameGenerator() {
	}

	public static String generate(String originalName) {

		String extension = "";

		int index = originalName.lastIndexOf(".");

		if (index != -1) {
			extension = originalName.substring(index);
		}

		return UUID.randomUUID() + extension;
	}
}