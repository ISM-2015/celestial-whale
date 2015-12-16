package leti.etu.docker.merge;

import leti.etu.docker.util.TarDecompresser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by lightwave on 17.12.15.
 */
public class Layer {

    File layerDir;
    JSONObject metainfo;
    List<File> changes;
    String id;
    String parent = null;



    public Layer(File layerDir) {
        this.layerDir = layerDir;
        if(layerDir.isDirectory()) {
            File[] files = layerDir.listFiles();
            File json = null;
            File layer = null;
            for(int i=0; i< files.length; i++) {
                if (files[i].getName().equals("json"))
                    json = files[i];
                if (files[i].getName().equals("layer.tar"))
                    layer = files[i];

            }
            processJsonFile(json);


        }
    }

    protected void processJsonFile(File json) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(json)));
        } catch (Exception e) {
            System.out.println("Json file exception: " + e.toString());
            return;
        }
        StringBuilder out = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        } catch (Exception e) {
            System.out.println("Read json file exception: " + e.toString());
        }

        Object temp = JSONValue.parse(out.toString());
        metainfo = (JSONObject)temp;
        id = (String)metainfo.get("id");
        parent = (String)metainfo.get("parent");
    }

    public void processTarFile(File tar) {
        TarDecompresser decompresser = new TarDecompresser(layerDir.getAbsolutePath(), tar.getName());
        decompresser.decompress();
        changes = decompresser.getFiles();
    }

    public JSONObject getMetainfo() {
        return metainfo;
    }

    public String getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }
}
