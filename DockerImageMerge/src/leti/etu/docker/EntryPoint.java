package leti.etu.docker;

import leti.etu.docker.util.TarDecompresser;

/**
 * Created by lightwave on 17.12.15.
 */
public class EntryPoint {

    public static void main(String[] args) {
        TarDecompresser decompresser = new TarDecompresser("/home/lightwave/ideaTest/", "test.tar");
        decompresser.decompress();
    }
}
