package leti.etu.docker.util;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Created by lightwave on 22.12.15.
 */
public class TarCompresser {

    public static void compressFiles(Collection<File> files, File output)
            throws IOException {
        // Create the output stream for the output file
        FileOutputStream fos = new FileOutputStream(output);
        // Wrap the output file stream in streams that will tar and gzip everything
        TarArchiveOutputStream taos = new TarArchiveOutputStream(
                new BufferedOutputStream(fos));
        // TAR originally didn't support long file names, so enable the support for it
        taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        // Get to putting all the files in the compressed output file
        for (File f : files) {
            addFilesToCompression(taos, f, "");
        }

        // Close everything up
        taos.close();
        fos.close();
    }

    private static void addFilesToCompression(TarArchiveOutputStream taos, File file, String dir)
            throws IOException {
        // Create an entry for the file
        taos.putArchiveEntry(new TarArchiveEntry(file, dir + File.separator + file.getName()));
        if (file.isFile()) {
            // Add the file to the archive
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            IOUtils.copy(bis, taos);
            taos.closeArchiveEntry();
            bis.close();
        }
        else if (file.isDirectory()) {
            // close the archive entry
            taos.closeArchiveEntry();
            // go through all the files in the directory and using recursion, add them to the archive
            for (File childFile : file.listFiles()) {
                addFilesToCompression(taos, childFile, dir + File.separator + file.getName());
            }
        }
    }
}
