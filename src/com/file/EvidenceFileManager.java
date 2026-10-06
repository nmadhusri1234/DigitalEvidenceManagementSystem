package com.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class EvidenceFileManager {

    private static final String EVIDENCE_DIRECTORY = "evidence";

    private EvidenceFileManager() {
    }

    // Creates the main evidence directory
    public static void createEvidenceDirectory() throws IOException {

        Path directory = Path.of(EVIDENCE_DIRECTORY);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    // Creates a separate folder for each case
    public static String createCaseDirectory(int caseId)
            throws IOException {

        createEvidenceDirectory();

        Path caseDirectory =
                Path.of(EVIDENCE_DIRECTORY, "case_" + caseId);

        if (!Files.exists(caseDirectory)) {
            Files.createDirectories(caseDirectory);
        }

        return caseDirectory.toString();
    }

    // Copies the original evidence file into the DEMS system
    public static String copyEvidenceFile(
            String sourcePath,
            int caseId)
            throws IOException {

        File sourceFile = new File(sourcePath);

        // Check whether source exists
        if (!sourceFile.exists()) {
            throw new IOException(
                    "Evidence file does not exist: "
                    + sourcePath
            );
        }

        // Check whether it is actually a file
        if (!sourceFile.isFile()) {
            throw new IOException(
                    "The selected path is not a valid file."
            );
        }

        String caseDirectory =
                createCaseDirectory(caseId);

        Path source =
                sourceFile.toPath();

        Path destination =
                Path.of(
                        caseDirectory,
                        sourceFile.getName()
                );

        Files.copy(
                source,
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        return destination.toString();
    }

    // Reads a file using FileInputStream
    public static String readFile(String filePath)
            throws IOException {

        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException(
                    "File does not exist: " + filePath
            );
        }

        StringBuilder content =
                new StringBuilder();

        try (FileInputStream inputStream =
                     new FileInputStream(file)) {

            byte[] buffer = new byte[1024];

            int bytesRead;

            while ((bytesRead =
                    inputStream.read(buffer)) != -1) {

                content.append(
                        new String(
                                buffer,
                                0,
                                bytesRead
                        )
                );
            }
        }

        return content.toString();
    }

    // Copies a file using FileInputStream and FileOutputStream
    public static void copyUsingStreams(
            String sourcePath,
            String destinationPath)
            throws IOException {

        try (
            FileInputStream inputStream =
                    new FileInputStream(sourcePath);

            FileOutputStream outputStream =
                    new FileOutputStream(destinationPath)
        ) {

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead =
                    inputStream.read(buffer)) != -1) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }
        }
    }
    public static boolean deleteEvidenceFile(
            String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            return false;
        }

        // Delete the actual evidence file
        boolean fileDeleted = file.delete();

        if (!fileDeleted) {
            return false;
        }

        // Get the parent case directory
        File parentDirectory = file.getParentFile();

        if (parentDirectory != null
                && parentDirectory.isDirectory()) {

            // Check whether the directory is empty
            String[] remainingFiles =
                    parentDirectory.list();

            if (remainingFiles != null
                    && remainingFiles.length == 0) {

                // Delete empty case directory
                parentDirectory.delete();
            }
        }

        return true;
    }

    // Checks whether a file exists
    public static boolean fileExists(
            String filePath) {

        return Files.exists(
                Path.of(filePath)
        );
    }

    // Returns file size
    public static long getFileSize(
            String filePath)
            throws IOException {

        if (!fileExists(filePath)) {
            throw new IOException(
                    "File does not exist: "
                    + filePath
            );
        }

        return Files.size(
                Path.of(filePath)
        );
    }

    // Returns the file name
    public static String getFileName(
            String filePath) {

        return Path.of(filePath)
                .getFileName()
                .toString();
    }

    // Returns the file extension
    public static String getFileExtension(
            String filePath) {

        String fileName =
                getFileName(filePath);

        int dotIndex =
                fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            return "";
        }

        return fileName.substring(
                dotIndex + 1
        );
    }
}