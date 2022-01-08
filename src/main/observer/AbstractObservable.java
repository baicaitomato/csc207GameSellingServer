package src.main.observer;

/**
 * An abstract class that contains the functionality for being an Observable such as attaching and detaching observers
 * as well as notifying observers of any change.
 */
public abstract class AbstractObservable {
    private UserObserver observer;

    /**
     * Initializes an Observable
     */
    public AbstractObservable() {
        this.attach(new UserObserver());
    }

    /**
     * Notifies the observer if any, that a change has occurred to this Observable
     * @param msg a description of what change has occurred.
     */
    public void notifyObserver(String msg) {
        if (this.observer != null) {
            this.observer.update();
            System.out.println(msg);
        }
    }

    /**
     * @return the observer that is attached to this Observable
     */
    public UserObserver getObserver() {
        return this.observer;
    }

    /**
     * Attaches the given observer to this Observable
     * @param userObserver the observer to observe this observable
     */
    public void attach(UserObserver userObserver) {
        this.observer = userObserver;
    }

    /**
     * Removes the observer from observing this Observable
     */
    public void detach() {
        this.observer = null;
    }
}