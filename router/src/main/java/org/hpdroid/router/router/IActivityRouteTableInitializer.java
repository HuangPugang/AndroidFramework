package org.hpdroid.router.router;

import java.util.Map;

/**
 * Created by kris on 16/3/10.
 */
public interface IActivityRouteTableInitializer {
    /**
     * init the router table
     *
     * @param router the router map to
     */
    void initRouterTable(Map<String, String> router);

}
