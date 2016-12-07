package org.hpdroid.router.router;

import org.hpdroid.router.route.IRoute;

/**
 * Created by kris on 16/3/10.
 */
public interface IRouter {

    boolean open(IRoute route);

    boolean open(String url);

    /**
     * build the route according to the url, if not match, return null
     * @param url
     * @return
     */
    IRoute getRoute(String url);

    /**
     * decide if the route can be opened
     * @param route
     * @return
     */
    boolean canOpenTheRoute(IRoute route);

    /**
     * decide if the url can be opened
     * @param url
     * @return
     */
    boolean canOpenTheUrl(String url);

    /**
     * return the list that the IRoute list this router can open
     * @return
     */
    Class<? extends IRoute> getCanOpenRoute();
}
