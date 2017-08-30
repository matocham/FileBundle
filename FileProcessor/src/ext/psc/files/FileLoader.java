package ext.psc.files;

import ext.psc.files.pack.DataPack;

import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public interface FileLoader {
    List<DataPack> getAllEntries();

    List<DataPack> getMatchingEntries();

    void cleanup();
}
