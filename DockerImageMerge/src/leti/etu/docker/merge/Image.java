package leti.etu.docker.merge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lightwave on 17.12.15.
 */
public class Image {

    List<File> files;
    List<Layer> layers;

    public Image(List<File> files) {
        this.files = files;
        layers = new ArrayList<Layer>();
        for(int i = 0; i < files.size(); i++) {
            if(files.get(i).isDirectory()) {
                Layer temp = new Layer(files.get(i));
                layers.add(temp);
                System.out.println("Layer ID:" + temp.getId());
                System.out.println("Layer Parent:" + temp.getParent());
            }
        }
    }

    public Layer getTopLayer() {
        int layerCounter = layers.size();
        String curParent = null;
        for(int i =0; i < layers.size(); i++) {
            if(layers.get(i).getParent() == null)
                curParent = layers.get(i).getId();
        }
        layerCounter--;
        Layer lastLayer = null;
        for(; layerCounter!=0; layerCounter--) {
            for(int i = 0; i< layers.size(); i++) {
                if(layers.get(i).getParent() != null && layers.get(i).getParent().equals(curParent) ) {
                    curParent = layers.get(i).getId();
                    lastLayer = layers.get(i);
                    break;
                }
            }
        }

        return lastLayer;

    }
}
