package leti.etu.docker;

import difflib.DiffUtils;
import leti.etu.docker.merge.Image;
import leti.etu.docker.merge.Layer;
import leti.etu.docker.util.FileUtils;
import leti.etu.docker.util.TarCompresser;
import leti.etu.docker.util.TarDecompresser;

import java.io.File;

/**
 * Created by lightwave on 17.12.15.
 */
public class EntryPoint {

    public static void main(String[] args) {
//        Image img = new Image("/home/lightwave/ideaTest", "test.tar");
//        Image img2 = new Image("/home/lightwave/ideaTest", "happ.tar");
        File file = new File("/home/lightwave/d.jpg");
        File file2 = new File("/home/lightwave/p.jpg");
        File res = new File("/home/lightwave/resfile.txt");
        System.out.println("Size:" + file.length());
        System.out.println("Size:" + file2.length());
        try {
            res.createNewFile();
        } catch (Exception e) {

        }

        //FileUtils.compareFiles(file, file2, null);

//        try {
//            file.createNewFile();
//            TarCompresser.compressFiles(img.getFiles(), file);
//        } catch (Exception e) {
//
//        }
        //img2.addLayer(img.getTopLayer());
    }
}
