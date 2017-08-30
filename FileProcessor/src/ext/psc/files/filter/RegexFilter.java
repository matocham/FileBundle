package ext.psc.files.filter;

import ext.psc.files.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public class RegexFilter implements FileFilter {
    private String regex;

    public RegexFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public List<File> filter(List<File> inputFiles) {
        List<File> resultList = new ArrayList<>();
        for (File file : inputFiles) {
            if (Utils.removeExtension(file).matches(regex)) {
                resultList.add(file);
            }
        }
        return resultList;
    }
}
