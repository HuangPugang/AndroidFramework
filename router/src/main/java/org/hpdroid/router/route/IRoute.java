package org.hpdroid.router.route;

import org.hpdroid.router.router.IRouter;

import java.util.List;
import java.util.Map;

/**
 * Created by kris on 16/3/16.
 */
public interface IRoute {

    /**
     * get the Router to process the Route
     *
     * @return
     */
    IRouter getRouter();

    String getUrl();

    String getScheme();

    String getHost();

    int getPort();

    List<String> getPath();

    Map<String, String> getParameters();

    //Route can open itself
    boolean open();
}
