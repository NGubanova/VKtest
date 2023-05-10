package com.example.vktest;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileHasher {
    public static byte[] createHash(File file) throws Exception {
        if (file.isDirectory()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            for (File subFile : file.listFiles()) {
//                byte[] subFileHash = createHash(subFile);
//                digest.update(subFileHash);
//            }
            while (file.isDirectory()){
                file.listFiles();
            }
            return digest.digest();
        } else {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[256];
            int count;
            while ((count = inputStream.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
            inputStream.close();
            return digest.digest();
        }
    }

}

