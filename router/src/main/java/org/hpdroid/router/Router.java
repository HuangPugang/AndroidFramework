package org.hpdroid.router;

import android.app.Activity;
import android.content.Context;

import org.hpdroid.router.route.IRoute;
import org.hpdroid.router.router.ActivityRouter;
import org.hpdroid.router.router.BrowserRouter;
import org.hpdroid.router.router.CallbackRouter;
import org.hpdroid.router.router.IActivityRouteTableInitializer;
import org.hpdroid.router.router.ICallbackRouteTableInitializer;
import org.hpdroid.router.router.IRouter;

/**
 * Created by chensi on 16/3/17.
 * shell to the user
 */
public class Router {

    public static synchronized void addRouter(IRouter router){
        RouterManager.getSingleton().addRouter(router);
    }

    public static synchronized void initBrowserRouter(Context context, Class<? extends Activity> webActivityClass){
       RouterManager.getSingleton().initBrowserRouter(context, webActivityClass);
    }

    public static synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer){
        RouterManager.getSingleton().initActivityRouter(context, initializer);
    }

    public static synchronized void initCallbackRouter(Context context, String scheme, ICallbackRouteTableInitializer initializer){
        RouterManager.getSingleton().initCallbackRouter(context, initializer, scheme);
    }

    public static synchronized void initCallbackRouter(Context context, ICallbackRouteTableInitializer initializer){
        RouterManager.getSingleton().initCallbackRouter(context, initializer);
    }

    public static synchronized void initActivityRouter(Context context, String scheme, IActivityRouteTableInitializer initializer){
        RouterManager.getSingleton().initActivityRouter(context, initializer, scheme);
    }

    /**
     * remote open url, if there is not router to process the url, return null
     * @param url
     * @return
     */
    public static boolean open(String url){
        return RouterManager.getSingleton().open(url);
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     * @param url
     * @return
     */
    public static IRoute getRoute(String url){
        return RouterManager.getSingleton().getRoute(url);
    }

    public static void openRoute(IRoute route){
        RouterManager.getSingleton().openRoute(route);
    }

    public static void setActivityRouter(ActivityRouter router){
        RouterManager.getSingleton().setActivityRouter(router);
    }

    public static void setBrowserRouter(BrowserRouter router){
        RouterManager.getSingleton().setBrowserRouter(router);
    }

    public static void setCallbackRouter(CallbackRouter router){
        RouterManager.getSingleton().setCallbackRouter(router);
    }

}
