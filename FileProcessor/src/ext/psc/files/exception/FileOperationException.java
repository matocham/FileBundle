package ext.psc.files.exception;

/**
 * Created by matocham on 28.06.2017.
 */
public class FileOperationException extends RuntimeException {
    public FileOperationException(Exception e) {
        super(e);
    }

    public FileOperationException(String s) {
        super(s);
    }

    public FileOperationException(String message, Exception e) {
        super(message, e);
    }
}
