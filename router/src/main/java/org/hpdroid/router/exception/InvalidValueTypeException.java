package org.hpdroid.router.exception;

/**
 * Created by kris on 16/3/10.
 */
public class InvalidValueTypeException extends RuntimeException{
    public InvalidValueTypeException(String path, String value){
        super(String.format("The type of the value is not match witch the path, Path : %s, value: %s", path, value));
    }

}
