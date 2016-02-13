/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics;


/**
 * A basic exception for all fatal graphics problems which occur during execution.
 * These generally should not occur in a well written program.  Examples: Cannot open image file, shader doesn't compile, etc.
 */
@SuppressWarnings("serial")
public class FatalGraphicsException extends Exception {
    public FatalGraphicsException(String message) {
        super(message);
    }

    public FatalGraphicsException(String message, Throwable t) {
        super(message, t);
    }

    public FatalGraphicsException(Throwable t) {
        super(t);
    }
}
