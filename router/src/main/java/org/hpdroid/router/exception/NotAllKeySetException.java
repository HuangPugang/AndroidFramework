package org.hpdroid.router.exception;

/**
 * Created by kris on 16/3/11.
 */
public class NotAllKeySetException extends Exception {
    public NotAllKeySetException(String path){
        super(String.format("Not all the key set in %s", path));
    }
}
