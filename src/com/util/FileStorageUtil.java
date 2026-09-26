package com.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileStorageUtil {

    // ============================================
    // SAVE LIST TO FILE
    // ============================================

    public static <T> void saveList(
            List<T> list,
            String filePath) throws IOException {

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             new FileOutputStream(filePath))) {

            outputStream.writeObject(list);
        }
    }


    // ============================================
    // LOAD LIST FROM FILE
    // ============================================

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadList(
            String filePath) throws IOException {

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             new FileInputStream(filePath))) {

            return (List<T>) inputStream.readObject();

        } catch (ClassNotFoundException e) {

            throw new IOException(
                    "Unable to read stored data.",
                    e
            );
        }
    }


    // ============================================
    // CHECK FILE EXISTENCE
    // ============================================

    public static boolean fileExists(
            String filePath) {

        return new java.io.File(filePath).exists();
    }
}