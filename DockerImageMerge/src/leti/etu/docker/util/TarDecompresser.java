package leti.etu.docker.util;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;

/**
 * Created by lightwave on 17.12.15.
 */
public class TarDecompresser {

    String inputFile = null;
    String basicDir = null;
    String outputName = null;
    List<File> files = null;

    public List<File> getFiles() {
        return files;
    }

    public String getOutputName() {
        return outputName;
    }

    public TarDecompresser(String basicDir, String inputFile) {
        this.basicDir  = basicDir;
        this.inputFile = inputFile;
    }

    public void decompress() {
        File input = new File(basicDir, inputFile);
        outputName = String.valueOf((new Date()).getTime());
        File output = new File(basicDir + "/" + outputName);
        output.mkdir();
        try {
            files = unTar(input, output);
        } catch (Exception e) {
            System.out.println("Decompessing error: " + e.toString());
        }
    }

    private static List<File> unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

        //System.out.println(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile);
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null;
        while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
               // System.out.println(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                if (!outputFile.exists()) {
                    //System.out.println(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } else {
                //System.out.println(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
                final OutputStream outputFileStream = new FileOutputStream(outputFile);
                IOUtils.copy(debInputStream, outputFileStream);
                outputFileStream.close();
            }
            if(entry.getName().indexOf('/') + 1 == entry.getName().length() || entry.getName().indexOf('/') == -1 )
                untaredFiles.add(outputFile);
        }
        debInputStream.close();

        return untaredFiles;
    }
}
