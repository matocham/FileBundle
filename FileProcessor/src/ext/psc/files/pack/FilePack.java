package ext.psc.files.pack;


import ext.psc.files.Utils;
import ext.psc.files.exception.FileOperationException;
import ext.psc.files.filter.FilterChain;
import ext.psc.files.filter.RegexFilter;

import java.io.File;
import java.util.*;

/**
 * Created by matocham on 27.06.2017.
 */
public class FilePack {
    public static final String DIR_KEY = "directory";
    public static final String NO_EXT_KEY = "no_ext";

    private List<File> allFiles;
    private Map<String, List<File>> extensionMap;
    private boolean skipDirectories = false;
    private boolean skipNonExistingFiles = true;
    private boolean removeNonEmptyDirs = true;
    private boolean copyDirContents = true;
    private boolean moveNonEmptyDirs = true;

    public FilePack() {
        allFiles = new ArrayList<>();
        extensionMap = new HashMap<>();
    }

    public FilePack(boolean skipDirectories) {
        this.skipDirectories = skipDirectories;
    }

    public FilePack(List<File> files) {
        this();
        addAll(files);
    }

    public FilePack(List<File> files, boolean skipDirectories) {
        this(skipDirectories);
        addAll(files);
    }

    public void addAll(List<File> files) {
        if (files != null) {
            for (File file : files) {
                addFile(file);
            }
        }
    }

    public void addAll(FilePack pack) {
        addAll(pack.getAllFiles());
    }

    public void addFile(File file) {
        if (allFiles.contains(file)) {
            return;
        }
        if (Utils.exists(file) || !skipNonExistingFiles) {
            if (file.isDirectory() && skipDirectories) {
                return;
            }
            allFiles.add(file);
            if (file.isDirectory()) {
                addEntry(DIR_KEY, file);
            } else {
                String ext = Utils.getExtension(file);
                if (ext.isEmpty()) {
                    addEntry(NO_EXT_KEY, file);
                } else {
                    addEntry(ext, file);
                }
            }
        } else {
            System.out.println("File " + file.getAbsolutePath() + " does not exist!");
        }
    }

    private void addEntry(String extensionKey, File file) {
        List<File> filesOfExtension = extensionMap.get(extensionKey);
        if (filesOfExtension == null) {
            filesOfExtension = new ArrayList<>();
            extensionMap.put(extensionKey, filesOfExtension);
        }
        filesOfExtension.add(file);
    }

    public FilePack(File[] files) {
        this(Arrays.asList(files));
    }


    public FilePack getFilesOfExtension(String extension) {
        List<File> matchedFiles = extensionMap.get(extension);
        if (matchedFiles == null) {
            matchedFiles = Collections.emptyList();
        }
        return new FilePack(matchedFiles);
    }

    public FilePack getDirectories() {
        return new FilePack(extensionMap.get(DIR_KEY));
    }

    public FilePack filter(String filterRegex) {
        List<File> matchedFiles = new RegexFilter(filterRegex).filter(allFiles);
        return new FilePack(matchedFiles);
    }

    public FilePack filter(FilterChain filterChain) {
        List<File> filteredFiles = filterChain.filter(allFiles);
        return new FilePack(filteredFiles);
    }

    public List<File> getAllFiles() {
        return allFiles;
    }

    public boolean removeFromPack(File file) {
        boolean result = allFiles.remove(file);
        String extMapKey;
        if (file.isDirectory()) {
            extMapKey = DIR_KEY;
        } else {
            String ext = Utils.getExtension(file);
            if (ext.isEmpty()) {
                extMapKey = NO_EXT_KEY;
            } else {
                extMapKey = ext;
            }
        }
        List<File> selectedFiles = extensionMap.get(extMapKey);
        if (selectedFiles != null) {
            selectedFiles.remove(file);
        }
        return result;
    }

    public void removeAllFromPack() {
        for (File file : allFiles) {
            removeFromPack(file);
        }
    }

    public void remove() {
        for (File file : allFiles) {
            removeFile(file);
        }
    }

    private boolean removeFile(File file) {
        if (!allFiles.contains(file)) {
            return false;
        }
        if (file.isDirectory() && removeNonEmptyDirs) {
            Utils.clearDirectory(file);
        }
        boolean status = Utils.remove(file);
        if (status) {
            removeFromPack(file);
        }
        return status;
    }

    public FilePack moveTo(File directory) {
        if (!Utils.exists(directory)) {
            directory.mkdirs();
        }
        if (!directory.isDirectory()) {
            throw new FileOperationException("Destination path " + directory.getAbsolutePath() + " is not a directory!");
        }
        List<File> moveResult = moveFiles(directory);
        return new FilePack(moveResult);
    }

    private List<File> moveFiles(File directory) {
        List<File> changedFiles = new ArrayList<>();
        for (File file : allFiles) {
            if (file.isDirectory() && file.listFiles().length > 0) {
                handleNonEmptyDir(directory, changedFiles, file);
                continue;
            }
            File result = Utils.move(file, directory);
            changedFiles.add(result);
        }
        return changedFiles;
    }

    private void handleNonEmptyDir(File directory, List<File> changedFiles, File file) {
        if (!moveNonEmptyDirs) {
        } else {
            Utils.moveFiles(new File(directory, file.getName()), file.listFiles());
            Utils.remove(file);
            changedFiles.add(new File(directory, file.getName()));
        }
    }

    public void moveToInPlace(File directory) {
        FilePack result = moveTo(directory);
        this.allFiles = result.allFiles;
        this.extensionMap = result.extensionMap;
    }

    public FilePack copyTo(File directory) {
        if (!Utils.exists(directory)) {
            directory.mkdirs();
        }
        if (!directory.isDirectory()) {
            throw new FileOperationException("Destination path " + directory.getAbsolutePath() + " is not a directory!");
        }
        List<File> copyResult;
        copyResult = copyFiles(directory);

        return new FilePack(copyResult);
    }

    private List<File> copyFiles(File directory) {
        List<File> changedFiles = new ArrayList<>();
        for (File file : allFiles) {
            File result = Utils.copy(file, directory);
            if (file.isDirectory() && copyDirContents) {
                Utils.copyFiles(result, file.listFiles());
            }
            changedFiles.add(result);
        }
        return changedFiles;
    }

    public void copyToInPlace(File directory) {
        FilePack result = copyTo(directory);
        this.allFiles = result.allFiles;
        this.extensionMap = result.extensionMap;
    }

    public void setSkipNonExistingFiles(boolean skipNonExistingFiles) {
        this.skipNonExistingFiles = skipNonExistingFiles;
    }

    public void setSkipDirectories(boolean skipDirectories) {
        this.skipDirectories = skipDirectories;
    }

    public void setRemoveNonEmptyDirs(boolean removeNonEmptyDirs) {
        this.removeNonEmptyDirs = removeNonEmptyDirs;
    }

    public void setCopyDirContents(boolean copyDirContents) {
        this.copyDirContents = copyDirContents;
    }

    public void setMoveNonEmptyDirs(boolean moveNonEmptyDirs) {
        this.moveNonEmptyDirs = moveNonEmptyDirs;
    }

    public int size() {
        return allFiles.size();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append("Size: ").append(allFiles.size());
        representation.append(", Skip dirs: ").append(skipDirectories);
        representation.append(", Remove not empty dirs: ").append(removeNonEmptyDirs);
        representation.append(", Copy dirs content: ").append(copyDirContents);
        representation.append(", Move non empty dirs: ").append(moveNonEmptyDirs);
        return representation.toString();
    }
}
