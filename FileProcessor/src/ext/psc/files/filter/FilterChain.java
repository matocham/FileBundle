package ext.psc.files.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public class FilterChain {
    private ArrayList<FileFilter> filterChain;

    public FilterChain() {
        filterChain = new ArrayList<>();
    }

    public FilterChain(FileFilter filter) {
        filterChain.add(filter);
    }

    public List<File> filter(List<File> files) {
        for (FileFilter filter : filterChain) {
            files = filter.filter(files);
        }
        return files;
    }

    public FilterChain addFilter(FileFilter filter){
        filterChain.add(filter);
        return this;
    }
}
