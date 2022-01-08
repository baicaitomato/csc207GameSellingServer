package src.tests.observer_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.main.observer.AbstractObservable;
import src.main.observer.UserObserver;


/**
 * A test suite for the AbstractObservable class used mainly for keeping track of changes to Users.
 */
public class TestAbstractObservable {
    static ObservableForTest obsOne, obsTwo;

    /**
     * Sets up the static concrete Observables for test cases.
     */
    @BeforeEach
    public void setUp() {
        obsOne = new ObservableForTest();
        obsTwo = new ObservableForTest();
    }


    /**
     * Test cases for AbstractObservable.getObserver
     */
    @Test
    public void testGetObserver() {
        assertNotNull(obsOne.getObserver());
        assertNotNull(obsTwo.getObserver());
        assertNotEquals(obsOne.getObserver(), obsTwo.getObserver());
    }

    /**
     * Test cases for AbstractObservable.detach
     */
    @Test
    public void testDetach() {
        obsOne.detach();
        assertNull(obsOne.getObserver());
        obsTwo.detach();
        assertNull(obsTwo.getObserver());
    }

    /**
     * Test cases for AbstractObservable..notifyObserver; mainly that it doesn't throw an exception when the
     * observer is null.
     */
    @Test
    public void testNotifyObserver() {
        obsOne.notifyObserver("");
        obsOne.detach();
        obsOne.notifyObserver("");  // Fail Silently
    }

    /**
     * Test cases for AbstractObservable.attach
     */
    @Test
    public void testAttach() {
        UserObserver oldObserver = obsOne.getObserver();
        obsOne.attach(new UserObserver());
        assertNotEquals(oldObserver, obsOne.getObserver());
    }

}

/**
 * A concrete Observable to be used in this test suite.
 */
class ObservableForTest extends AbstractObservable {

    public ObservableForTest() {
        super();
    }
}
