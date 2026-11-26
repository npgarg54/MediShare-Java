package com.medishare.exception;

/** Signals a request to leave the current role menu and return to login. */
public class LogoutRequestedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public LogoutRequestedException() {
        super("Logout requested by user.");
    }
}
