package leti.etu.docker.merge;

import leti.etu.docker.util.FileUtils;
import leti.etu.docker.util.TarDecompresser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lightwave on 17.12.15.
 */
public class Layer {

    File layerDir;
    File json;
    File layer;
    File version;
    JSONObject metainfo;
    List<File> changes = null;
    String id;
    String parent = null;



    public Layer(File layerDir) {
        this.layerDir = layerDir;
        if(layerDir.isDirectory()) {
            File[] files = layerDir.listFiles();
            json = null;
            layer = null;
            version = null;
            for(int i=0; i< files.length; i++) {
                if (files[i].getName().equals("json"))
                    json = files[i];
                if (files[i].getName().equals("layer.tar"))
                    layer = files[i];
                if (files[i].getName().equals("VERSION"))
                    version = files[i];

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

    public Layer copyLayerTo(String path, String newID, String parent) {
        path = path + "/" + newID;
        List<File> layerFiles = new ArrayList<File>();
        File newLayerDir = new File(path);
        newLayerDir.mkdir();
        System.out.println("New layer ID: " + newID );
        JSONObject newMeta = (JSONObject) metainfo.clone();
        newMeta.put("id", newID);
        newMeta.put("parent", parent);
        File newJson = new File(path + "/json" );
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newJson)));
            writer.write(newMeta.toJSONString());
            writer.close();
        } catch (Exception e) {
            System.out.println("New layer json creation error: " + e.toString());
        }
        File newTar = new File(path + "/layer.tar");
        try {
            FileUtils.copy(layer, newTar);
        } catch (Exception e) {
            System.out.println("New layer file creation error: " + e.toString());
        }
        File newVers = new File(path + "/VERSION");
        try {
            FileUtils.copy(version, newVers);
        } catch (Exception e) {
            System.out.println("New layer version creation error: " + e.toString());
        }
        return null;
    }

    public File getJson() {
        return json;
    }

    public void setJson(File json) {
        this.json = json;
    }

    public File getLayer() {
        return layer;
    }

    public void setLayer(File layer) {
        this.layer = layer;
    }

    public File getVersion() {
        return version;
    }

    public void setVersion(File version) {
        this.version = version;
    }

    public File getLayerDir() {
        return layerDir;
    }

    public void setLayerDir(File layerDir) {
        this.layerDir = layerDir;
    }

    public void setMetainfo(JSONObject metainfo) {
        this.metainfo = metainfo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParent(String parent) {
        this.parent = parent;
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

    public List<File> getChanges() {
        if(changes == null) {
            processTarFile(layer);
        }
        return changes;
    }

    public void setChanges(List<File> changes) {
        this.changes = changes;
    }
}
