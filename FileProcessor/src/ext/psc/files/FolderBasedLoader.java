package ext.psc.files;

import ext.psc.files.exception.FileOperationException;
import ext.psc.files.pack.DataPack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matocham on 27.06.2017.
 */
public class FolderBasedLoader implements FileLoader {
    private static final String ALL_FILES = ".*";
    private String folderMask;
    private File baseDir;
    boolean removeEmptyDirectories = true;
    boolean removeNonMatchingDirectories = false;

    public FolderBasedLoader(File baseDir) {
        this.folderMask = ALL_FILES;
        this.baseDir = baseDir;
        if (!(Utils.exists(baseDir) || baseDir.isDirectory())) {
            throw new FileOperationException(baseDir.getAbsolutePath() + " does not exist or is not a directory!");
        }
    }

    public FolderBasedLoader(String folderMask, File baseDir) {
        this.folderMask = folderMask;
        this.baseDir = baseDir;
        if (!(Utils.exists(baseDir) || baseDir.isDirectory())) {
            throw new FileOperationException(baseDir.getAbsolutePath() + " does not exist or is not a directory!");
        }
    }

    public FolderBasedLoader(String folderMask, File baseDir, boolean removeEmptyDirectories, boolean removeNonMatchingDirectories) {
        this(folderMask, baseDir);
        this.removeEmptyDirectories = removeEmptyDirectories;
        this.removeNonMatchingDirectories = removeNonMatchingDirectories;
    }

    @Override
    public List<DataPack> getAllEntries() {
        return getEntries(ALL_FILES);
    }

    @Override
    public List<DataPack> getMatchingEntries() {
        return getEntries(folderMask);
    }

    private List<DataPack> getEntries(String fileMask) {
        List<DataPack> dataPacks = new ArrayList<>();
        File[] allFiles = baseDir.listFiles();

        for (File file : allFiles) {
            if (file.isDirectory()) {
                DataPack dataPack = processDir(file, fileMask);
                if (dataPack != null) {
                    dataPacks.add(dataPack);
                }
            }
        }
        return dataPacks;
    }

    private DataPack processDir(File directory, String fileMask) {
        if (directory.listFiles().length == 0 && removeEmptyDirectories) {
            Utils.remove(directory);
            return null;
        }
        if (directory.getName().matches(fileMask)) {
            DataPack dataPack = new DataPack(directory);
            return dataPack;
        } else if (removeNonMatchingDirectories) {
            cleanupDir(directory);
        }
        return null;
    }

    @Override
    public void cleanup() {
        File[] allFiles = baseDir.listFiles();
        for (File file : allFiles) {
            cleanupDir(file);
        }
        if (baseDir.listFiles().length > 0) {
            throw new FileOperationException("Could not remove all directory content!");
        }
    }

    private void cleanupDir(File file) {
        if (file.isDirectory()) {
            Utils.clearDirectory(file);
        }
        Utils.remove(file);
    }

    public void setFolderMask(String folderMask) {
        this.folderMask = folderMask;
    }
}
