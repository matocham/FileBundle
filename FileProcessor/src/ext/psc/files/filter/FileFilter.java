package ext.psc.files.filter;

import java.io.File;
import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public interface FileFilter {
    List<File> filter(List<File> inputFiles);
}
