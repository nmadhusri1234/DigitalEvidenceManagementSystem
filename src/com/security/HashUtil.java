package com.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    private HashUtil() {
    }

    public static String generateSHA256(
            String filePath) throws IOException {

        File file = new File(filePath);

        // Check whether file exists
        if (!file.exists()) {
            throw new IOException(
                    "File does not exist: " + filePath
            );
        }

        // Check whether it is actually a file
        if (!file.isFile()) {
            throw new IOException(
                    "Path is not a valid file: " + filePath
            );
        }

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            try (FileInputStream inputStream =
                         new FileInputStream(file)) {

                byte[] buffer = new byte[4096];

                int bytesRead;

                while ((bytesRead =
                        inputStream.read(buffer)) != -1) {

                    digest.update(
                            buffer,
                            0,
                            bytesRead
                    );
                }
            }

            byte[] hashBytes =
                    digest.digest();

            StringBuilder hash =
                    new StringBuilder();

            for (byte b : hashBytes) {

                hash.append(
                        String.format("%02x", b)
                );
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "SHA-256 algorithm not available.",
                    e
            );
        }
    }
}