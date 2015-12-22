package leti.etu.docker.util;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.InsertDelta;
import difflib.Patch;

import java.io.*;
import java.nio.file.Files;
import java.util.*;



/**
 * Created by lightwave on 17.12.15.
 */
public class FileUtils {

    public static void copy(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    public static String[] splitFilePath(String path) {
        return path.split("/");
    }

    private static List<String> fileToLines(File file) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                lines.add(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void writeLinesToFile(List<String> strings, File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for(int i=0; i < strings.size(); i++) {
                bw.write(strings.get(i));
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        for(int i =0; i < files.length && files != null; i++) {
            if(files[i].isFile())
                files[i].delete();
            if(files[i].isDirectory())
                deleteDirectory(files[i].getAbsolutePath());
        }
        dir.delete();
    }

    public static void compareAndEdit(Collection<File> col1, Collection<File> col2, String base1, String base2) {

        Iterator<File> i1 = col1.iterator();
        while(i1.hasNext()) {
            Iterator<File> i2 = col2.iterator();
            File fstFile = i1.next();
            while(i2.hasNext()) {
                File secFile = i2.next();
                if(isRelativePathSame(fstFile, secFile, base1, base2)) {
                    if(fstFile.isDirectory() && secFile.isDirectory()) {
                        if(fstFile.listFiles() == null || fstFile.listFiles().length == 0) {
                            secFile.delete();
                        }
                        else if(fstFile.listFiles() == null || fstFile.listFiles().length == 0) {
                            continue;
                        } else {
                            compareAndEdit(java.util.Arrays.asList(fstFile.listFiles()), java.util.Arrays.asList(secFile.listFiles()), base1, base2 );
                        }
                    }
                    if(fstFile.isFile() && secFile.isFile()) {
                        if(fstFile.length() > 100000 || secFile.length() > 100000) {
                            System.out.println("Large file conflict: " + fstFile.getAbsolutePath().substring(base1.length()));
                            if(fstFile.length() == secFile.length()) {

                                secFile.delete();
                            }
                        } else {
                            System.out.println("Small file conflict: " + fstFile.getAbsolutePath().substring(base1.length()));
                            if(compareFiles(secFile, fstFile, null)) {
                                secFile.delete();
                            }
                            else {
                                secFile.delete();
                            }
                        }
                    }
                    continue;
                }
            }
        }
    }

    public static boolean isRelativePathSame(File file1, File file2, String base1, String base2) {
        String rel1 = file1.getAbsolutePath().substring(base1.length()),
                rel2 = file2.getAbsolutePath().substring(base2.length());
        return  rel1.equals(rel2);
    }

    public static boolean compareFiles(File file1, File file2, File resFile) {
        List<String> first = fileToLines(file1);
        List<String> second  = fileToLines(file2);
        Patch patch = DiffUtils.diff(first, second);
        if(resFile != null) {
            try {
                Patch pt = new Patch();
                for (Delta delta: patch.getDeltas()) {
                    if(delta.getType() == Delta.TYPE.CHANGE)
                        pt.addDelta(new InsertDelta(delta.getOriginal(), delta.getRevised()));
                    else if(delta.getType() == Delta.TYPE.INSERT)
                        pt.addDelta(delta);
                }
                List res = DiffUtils.patch(first, pt);

                writeLinesToFile(res, resFile);
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }

        for (Delta delta: patch.getDeltas()) {
            System.out.println("Delta: " + delta.toString());
        }

        return patch.getDeltas().size() == 0;
    }

}
