package ext.psc;

import ext.psc.files.FileLoader;
import ext.psc.files.FolderBasedLoader;
import ext.psc.files.pack.DataPack;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String basePath = "E:/kab";
        String backupPath = "E:/kab2";
        FileLoader loader = new FolderBasedLoader(".*?RDY$", new File(basePath));
        List<DataPack> datas = loader.getMatchingEntries();
        List<DataPack> allDatas = loader.getAllEntries();
        for (DataPack data : datas) {

        }
    }
}
