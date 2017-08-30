package ext.psc.files.filter;

import ext.psc.files.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public class ExtensionFilter implements FileFilter {
    private List<String> extensions;

    public ExtensionFilter(String... extensions) {
        this.extensions = new ArrayList<>(Arrays.asList(extensions));
    }

    @Override
    public List<File> filter(List<File> inputFiles) {
        List<File> resultList = new ArrayList<>();
        for (File file : inputFiles) {
            if (file.isDirectory()) {
                continue;
            }
            String ext = Utils.getExtension(file);
            if (extensions.contains(ext)) {
                resultList.add(file);
            }
        }
        return resultList;
    }
}
