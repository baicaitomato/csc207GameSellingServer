package src.main.observer;

import src.main.users.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A UserObserver that keeps track of changes to any given User object by serializing and saving all Users to a file.
 */
public class UserObserver {
    private static final String CWD = System.getProperty("user.dir");
    private static File storageFile;

    /**
     * Initializes a UserObserver
     */
    public UserObserver() {
        File dbDirectory = new File(CWD, "db");
        dbDirectory.mkdir();

        String storageDirectoryName = dbDirectory.getPath();
        storageFile = new File(storageDirectoryName, "userStorage");

        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the File that this UserObserver is storing all the Observable users in.
     */
    public File getStorageFile() {
        return storageFile;
    }

    /**
     * Serializes and saves the list of all users to a storage file database.
     */
    public void update() {
        try {
            // March 10 https://www.youtube.com/watch?v=qo9S2CeoqQE&t=6s
            FileOutputStream fos = new FileOutputStream(storageFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(User.getAllUsers());
            fos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
