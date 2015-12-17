package leti.etu.docker.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

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
}
