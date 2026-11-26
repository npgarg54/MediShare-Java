package com.medishare.exception;

/** Signals that the user cancelled the current form or operation. */
public class OperationCancelledException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public OperationCancelledException() {
        super("Operation cancelled by user.");
    }
}
