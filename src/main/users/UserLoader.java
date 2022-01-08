package src.main.users;

import src.main.Game;
import src.main.observer.UserObserver;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that holds the functions necessary to load users in the database from previous back-end executions
 * of the main client.
 */
public class UserLoader {
    private static final File STORAGE_FILE = new UserObserver().getStorageFile();

    /**
     * Loads all the users from the database into the static allUsers variable in the User class.
     */
    public static void loadUsers() {
        HashMap<String, User> allUsers = User.getAllUsers();
        allUsers.clear();
        try{
            HashMap<String, User> tempAllUsers = getDeserializedAllUsers();
            for (Map.Entry<String, User> entry: tempAllUsers.entrySet()) {
                User user = entry.getValue();
                user.resetDailyCreditsAdded();
                allUsers.put(entry.getKey(), user);
                // This allows games to be bought/removed the next day
                for (Map.Entry<String, Game> gameEntry: user.getGameLibrary().entrySet()) {
                    gameEntry.getValue().putOffProbation();
                }
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }

    /**
     * Deserializes and returns a hashmap of all saved users indexed by username.
     * @return a hashmap containing users from the previous session of the main client indexed by username
     */
    private static HashMap<String, User> getDeserializedAllUsers() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(STORAGE_FILE);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (HashMap<String, User>) ois.readObject();
    }
}
