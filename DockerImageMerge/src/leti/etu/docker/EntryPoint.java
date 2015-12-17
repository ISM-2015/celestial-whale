package leti.etu.docker;

import leti.etu.docker.merge.Image;
import leti.etu.docker.merge.Layer;
import leti.etu.docker.util.TarDecompresser;

/**
 * Created by lightwave on 17.12.15.
 */
public class EntryPoint {

    public static void main(String[] args) {
        Image img = new Image("/home/lightwave/ideaTest", "testtest.tar");
        Image img2 = new Image("/home/lightwave/ideaTest", "happ.tar");
        img2.addLayer(img.getTopLayer());
    }
}
