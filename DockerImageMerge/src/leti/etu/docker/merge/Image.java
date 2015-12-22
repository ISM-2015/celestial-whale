package leti.etu.docker.merge;

import leti.etu.docker.util.FileUtils;
import leti.etu.docker.util.ImageIdGenerator;
import leti.etu.docker.util.TarCompresser;
import leti.etu.docker.util.TarDecompresser;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
    File repositories;
    JSONObject reps;

    public List<File> getFiles() {
        return files;
    }

    public void clean() {
        decompresser.deleteOutputs();
    }

    public Image(String basicDir, String fileName) {

        this.basicDir = basicDir;
        this.fileName = fileName;
        layers = new ArrayList<Layer>();
        decompresser = new TarDecompresser(basicDir, fileName);
        decompresser.decompress();
        files = decompresser.getFiles();
        for(int i = 0; i < files.size(); i++) {
            if(files.get(i).getName().equals("repositories")) {
                repositories = files.get(i);
            }
            if(files.get(i).isDirectory()) {
                Layer temp = new Layer(files.get(i));
                temp.getChanges();
                layers.add(temp);

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
    private void createMerged(Layer newLayer, String repAddr, String name) {
        String newId = addLayer(newLayer);

        JSONObject latest = new JSONObject();
        latest.put("latest", newId);
        JSONObject rep = new JSONObject();
        rep.put(name, latest);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repositories)));
            writer.write(rep.toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File oldImage = new File(basicDir + "/" + decompresser.getOutputName());
        File mergedImage = new File(repAddr);
        try {
            mergedImage.createNewFile();
            TarCompresser.compressFiles(java.util.Arrays.asList(oldImage.listFiles()), mergedImage);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
    public String addLayer(Layer newLayer) {
        String  newPath = basicDir + "/" + decompresser.getOutputName() + "/";
        String newId = ImageIdGenerator.generateId();
        layers.add(newLayer.copyLayerTo(newPath, newId, getTopLayer().getId()));
        return newId;
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
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean merge(Image toMerge, String repName, String name) {
        Layer current = getTopLayer();
        Layer add = toMerge.getTopLayer();
        String base1 = current.getLayerDir().getAbsolutePath() + "/" + current.getChangesDir(),
                base2 = add.getLayerDir().getAbsolutePath() + "/" + add.getChangesDir();
        if(!current.getParent().equals(add.getParent())) {
            System.out.println("Images have diffrent parents");
            return false;
        }
        List<File> first = current.getChanges(), second = add.getChanges();
        FileUtils.compareAndEdit(first, second, base1, base2);
        add.makeNewTar(second);
        add.deleteUntarChanges();
        current.deleteUntarChanges();
        createMerged(add, repName, name);



        return  true;


    }
}
