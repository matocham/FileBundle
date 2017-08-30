package ext.psc.files.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public class DirectoryFilter implements FileFilter {
    boolean getDirs = true;

    public DirectoryFilter() {

    }

    public DirectoryFilter(boolean getDirs) {
        this.getDirs = getDirs;
    }

    @Override
    public List<File> filter(List<File> inputFiles) {
        List<File> resultList = new ArrayList<>();
        for (File file : inputFiles) {
            if (file.isDirectory() == getDirs) {
                resultList.add(file);
            }
        }
        return resultList;
    }
}
