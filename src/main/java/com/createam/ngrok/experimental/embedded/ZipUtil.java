package com.createam.ngrok.experimental.embedded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

    public static void unzipArchiveTo(String zipFileFullPath, String destinationDirFullPath) throws IOException {
        File dir = new File(destinationDirFullPath);
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        fis = new FileInputStream(zipFileFullPath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String fileName = ze.getName();
            log.info("Extracting zip file from {} to {}", zipFileFullPath, destinationDirFullPath + File.separator + fileName);
            File newFile = new File(destinationDirFullPath + File.separator + fileName);
            new File(newFile.getParent()).mkdirs();
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zis.closeEntry();
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        fis.close();
    }
}
