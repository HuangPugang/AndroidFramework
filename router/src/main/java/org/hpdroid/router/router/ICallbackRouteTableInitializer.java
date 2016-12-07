package org.hpdroid.router.router;

import java.util.Map;

/**
 * Created by chsasaw on 16/4/14.
 */
public interface ICallbackRouteTableInitializer {
    /**
     * init the router table
     * @param router the router map to
     */
    void initRouterTable(Map<String, RouterCallback> router);
}
