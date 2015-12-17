package leti.etu.docker.merge;

import leti.etu.docker.util.FileUtils;
import leti.etu.docker.util.ImageIdGenerator;
import leti.etu.docker.util.TarDecompresser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lightwave on 17.12.15.
 */
public class Image {

    List<File> files;
    List<Layer> layers;
    TarDecompresser decompresser;
    String basicDir;
    String fileName;

    public Image(String basicDir, String fileName) {
        this.basicDir = basicDir;
        this.fileName = fileName;
        layers = new ArrayList<Layer>();
        decompresser = new TarDecompresser(basicDir, fileName);
        decompresser.decompress();
        files = decompresser.getFiles();
        for(int i = 0; i < files.size(); i++) {
            if(files.get(i).isDirectory()) {
                Layer temp = new Layer(files.get(i));
                layers.add(temp);
     //           System.out.println("Layer ID:" + temp.getId());
     //           System.out.println("Layer Parent:" + temp.getParent());
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

    public void addLayer(Layer newLayer) {
        List<File> layerFiles = new ArrayList<File>();

        String  newPath = basicDir + "/" + decompresser.getOutputName() + "/";
        newLayer.copyLayerTo(newPath, ImageIdGenerator.generateId(), getTopLayer().getId());
    }

    public Image createCopy(String basicDir, String name) {
        File output = new File(basicDir + "/" + name);
        output.mkdir();
        for(int i=0; i < files.size(); i++) {
            File curFile = files.get(i);
            try {
                if(curFile.isDirectory()) {
                    output = new File(basicDir + "/" + name + "/" + curFile.getName());
                    output.mkdir();
                } else {
                    if(curFile.getName().equals("repositories")) {
                        output = new File(basicDir + "/" + name + "/" + curFile.getName());
                    } else {
                        String[] inputPath = FileUtils.splitFilePath(curFile.getAbsolutePath());
                        output = new File(basicDir + "/" + name + "/" + inputPath[inputPath.length-2] + "/" + curFile.getName());
                    }
                    FileUtils.copy(files.get(i), output);
                }
            } catch (Exception e) {
                System.out.println("Copying file error:" + e.toString());
            }
        }
        return null;
    }
}
