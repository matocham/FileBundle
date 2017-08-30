package ext.psc.files;

import ext.psc.files.exception.FileOperationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by matocham on 27.06.2017.
 */
public class Utils {
    public static final String EXTENSION_SEPERATOR = ".";

    public static boolean clearDirectory(File directory) {
        if (!directory.isDirectory()) {
            return false;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                clearDirectory(file);
            }
            remove(file);
        }
        return true;
    }

    public static void copyFiles(File destination, File[] filesToCopy) {
        try {
            Path dstPath = destination.toPath();
            for (File file : filesToCopy) {
                Path sourcePath = file.toPath();
                Files.copy(sourcePath, dstPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                if (file.isDirectory()) {
                    copyFiles(new File(destination, file.getName()), file.listFiles());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Could not copy selected files!", e);
        }
    }

    public static void moveFiles(File destination, File[] filesToMove) {
        try {
            if (!exists(destination)) {
                destination.mkdirs();
            }
            Path dstPath = destination.toPath();
            for (File file : filesToMove) {
                Path sourcePath = file.toPath();
                if (file.isDirectory()) {
                    moveFiles(new File(destination, file.getName()), file.listFiles());
                }
                Files.move(sourcePath, dstPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Could not move selected files!", e);
        }
    }

    public static boolean exists(File directory) {
        return Files.exists(directory.toPath(), LinkOption.NOFOLLOW_LINKS);
    }

    public static String getExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(EXTENSION_SEPERATOR) == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(EXTENSION_SEPERATOR) + 1);
    }

    public static String removeExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(EXTENSION_SEPERATOR) == -1) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf(EXTENSION_SEPERATOR));
    }

    public static boolean remove(File file) {
        try {
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Could not remove file " + file.getAbsolutePath(), e);
        }
    }

    public static File copy(File file, File destDirectory) {
        try {
            if (!exists(destDirectory)) {
                destDirectory.mkdirs();
            }
            Path sourcePath = file.toPath();
            Path dstPath = destDirectory.toPath();
            Path result = Files.copy(sourcePath, dstPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            return result.toFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Could not copy " + file.getAbsolutePath() + " to " + destDirectory.getAbsolutePath(), e);
        }
    }

    public static File move(File file, File destDirectory) {
        try {
            if (!exists(destDirectory)) {
                destDirectory.mkdirs();
            }
            Path sourcePath = file.toPath();
            Path dstPath = destDirectory.toPath();
            Path result = Files.move(sourcePath, dstPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            return result.toFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Could not move " + file.getAbsolutePath() + " to " + destDirectory.getAbsolutePath(), e);
        }
    }
}
