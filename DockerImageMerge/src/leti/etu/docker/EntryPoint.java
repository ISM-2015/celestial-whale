package leti.etu.docker;

import difflib.DiffUtils;
import leti.etu.docker.merge.Image;
import leti.etu.docker.merge.Layer;
import leti.etu.docker.util.FileUtils;
import leti.etu.docker.util.TarCompresser;
import leti.etu.docker.util.TarDecompresser;

import java.io.File;
import java.nio.file.FileSystems;

/**
 * Created by lightwave on 17.12.15.
 */
public class EntryPoint {

    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("Not enough arguments");
            return;
        }
        File i1 = new File(args[0]);
        File i2 = new File(args[1]);
        File res = new File(args[2]);
        Image img = new Image(i1.getParent(), i1.getName());
        Image img2 = new Image(i2.getParent(), i2.getName());

        img.merge(img2, res.getAbsolutePath(), args.length >=4 ? args[3] : "mergedImage");
        img.clean();
        img2.clean();

    }
}
