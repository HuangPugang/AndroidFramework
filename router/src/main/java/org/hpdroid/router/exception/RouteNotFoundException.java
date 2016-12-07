package org.hpdroid.router.exception;

/**
 * Created by kris on 16/3/10.
 */
public class RouteNotFoundException extends Exception {
    public RouteNotFoundException(String routePath){
        super(String.format("The route not found: %s", routePath));
    }
}
