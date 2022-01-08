package src.tests.observer_tests;

import org.junit.jupiter.api.Test;
import src.main.observer.UserObserver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test suite for the UserObserver class.
 */
public class TestUserObserver {

    /**
     * Checking if the storage file to save users to is exists.
     */
    @Test
    public void testStorageFile() {
        UserObserver uo = new UserObserver();

        assertTrue(uo.getStorageFile().isFile());
        assertTrue(uo.getStorageFile().exists());
        assertEquals("userStorage", uo.getStorageFile().getName());
    }

}
