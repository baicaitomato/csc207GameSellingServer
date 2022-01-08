package src.main.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class that records errors that occur in system.Client by writing them to a file.
 */
public class ErrorRecorder {
    private BufferedWriter writer;
    private final ArrayList<String> errorLogs = new ArrayList<>();

    /**
     * Initializes an ErrorRecorder
     */
    public ErrorRecorder() {
        File errorFile = new File(System.getProperty("user.dir"), "errorLogs.txt");
        try {
            if (errorFile.exists()) {
                errorFile.delete();
            }
            errorFile.createNewFile();

            this.writer = new BufferedWriter(new FileWriter(errorFile, true));
        } catch (IOException ignored) {}
    }

    /**
     * Writes the given transaction code and its corresponding error message to the error logs file.
     * @param errorMessage the message corresponding to the exception
     */
    protected void recordError(String errorMessage) {
        try {
            this.writer.write(errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes this ErrorRecorders buffered writer.
     */
    protected void closeWriter() {
        try {
            this.writer.close();
        } catch (IOException ignored) {}
    }

}
