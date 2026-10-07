package util;

import model.User;
import model.PlayerState;

/**
 * Thread-safe User Session Singleton.
 */
public class Session {

    private static Session instance;
    private User currentUser;
    private PlayerState currentPlayerState;

    private Session() {}

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public synchronized void startSession(User user, PlayerState playerState) {
        this.currentUser = user;
        this.currentPlayerState = playerState;
    }

    public synchronized void clearSession() {
        this.currentUser = null;
        this.currentPlayerState = null;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public synchronized PlayerState getCurrentPlayerState() {
        return currentPlayerState;
    }

    public synchronized void setCurrentPlayerState(PlayerState currentPlayerState) {
        this.currentPlayerState = currentPlayerState;
    }
}
