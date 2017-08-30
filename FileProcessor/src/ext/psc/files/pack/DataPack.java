package ext.psc.files.pack;

import ext.psc.files.Utils;
import ext.psc.files.exception.FileOperationException;

import java.io.File;
import java.util.Arrays;

/**
 * Created by matocham on 27.06.2017.
 */
public class DataPack {
    private static final int MAX_DELETE_ATTEMPTS = 3;

    private File location;
    private FilePack files;
    private boolean obsolete = false;

    public DataPack(File location) {
        this.location = location;
        this.files = new FilePack(location.listFiles());
    }

    public void remove() {
        files.remove();
        Utils.remove(location);
        obsolete = true;
    }

    public void forceRemove() {
        int securityCounter = 0;
        do {
            Utils.clearDirectory(location);
            refresh();
            securityCounter++;
        } while (files.size() > 0 && securityCounter < MAX_DELETE_ATTEMPTS);
        if (securityCounter == MAX_DELETE_ATTEMPTS) {
            throw new FileOperationException("Could not clear directory " + location.getAbsolutePath());
        }
        Utils.remove(location);
        files = new FilePack();
        obsolete = true;
    }

    public void move(File newLocation) {
        File subfolderLocation = new File(newLocation, location.getName());
        if (!Utils.exists(subfolderLocation)) {
            subfolderLocation.mkdirs();
        }
        files.moveTo(subfolderLocation);
        Utils.remove(location);
        obsolete = true;
    }

    public void refresh() {
        if (Utils.exists(location)) {
            files = new FilePack(Arrays.asList(location.listFiles()));
        } else {
            files.removeAllFromPack();
            obsolete = true;
        }
    }

    public FilePack getFiles() {
        return files;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    @Override
    public String toString() {
        return "Base location: " + location.getAbsolutePath();
    }
}
