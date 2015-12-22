package leti.etu.docker.util;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.InsertDelta;
import difflib.Patch;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
            System.out.println("write exception" + e.toString());
        }
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
                System.out.println("patch exception" + e.toString());
            }
        }

        for (Delta delta: patch.getDeltas()) {
            System.out.println("Delta: " + delta.toString());
        }

        return patch.getDeltas().size() == 0;
    }

}
