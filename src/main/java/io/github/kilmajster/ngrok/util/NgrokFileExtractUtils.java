package io.github.kilmajster.ngrok.util;

//import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * https://gist.github.com/cassiuscai/cec0c8e418ce265d4227fef56d874aa6
 * <p>
 * FileExtractUtils
 */
public class NgrokFileExtractUtils {

    private static final Logger log = LoggerFactory.getLogger(NgrokFileExtractUtils.class);

    /**
     * The constant BUFFER_SIZE.
     */
    private static final int BUFFER_SIZE = 4096;

    public static void extractArchive(String archiveFile, String outdirPath) {
        log.info("Extracting {} to {}", archiveFile, outdirPath);

        File archive = new File(archiveFile);
        File outdir = new File(outdirPath);

        if(StringUtils.endsWithIgnoreCase(archive.getName(), "zip")) {
            extractZip(archive, outdir);
            deleteArchive(archiveFile);
        }
    }

    private static void deleteArchive(String downloadedFilePath) {
        try {
            Files.delete(Paths.get(downloadedFilePath));
        } catch (IOException e) {
            log.warn("Error while deleting {}", downloadedFilePath, e);
        }
    }

    /**
     * Extract zip.
     *
     * @param zipfile the zipfile
     * @param outdir  the outdir
     */
    public static void extractZip(File zipfile, File outdir) {
        try {
            ZipInputStream is = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipfile)));
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    mkDirs(outdir, name);
                } else {
                    String dir = directoryPart(name);
                    if (dir != null) {
                        mkDirs(outdir, dir);
                    }
                    extractFile(is, outdir, name);
                }
            }
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract file.
     *
     * @param inputStream the input stream
     * @param outDir      the out dir
     * @param name        the name
     * @throws IOException the io exception
     */
    private static void extractFile(InputStream inputStream, File outDir,
                                    String name) throws IOException {
        int count = -1;
        byte buffer[] = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(new File(outDir, name)), BUFFER_SIZE);
        while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            out.write(buffer, 0, count);
        }
        out.close();
    }

    /**
     * Mk dirs.
     *
     * @param outdir the outdir
     * @param path   the path
     */
    private static void mkDirs(File outdir, String path) {
        File d = new File(outdir, path);
        if (!d.exists()) {
            d.mkdirs();
        }
    }

    /**
     * Directory part string.
     *
     * @param name the name
     * @return the string
     */
    private static String directoryPart(String name) {
        int s = name.lastIndexOf(File.separatorChar);
        return s == -1 ? null : name.substring(0, s);
    }
}
