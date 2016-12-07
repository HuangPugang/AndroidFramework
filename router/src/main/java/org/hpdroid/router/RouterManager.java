package org.hpdroid.router;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.hpdroid.router.route.IRoute;
import org.hpdroid.router.router.ActivityRouter;
import org.hpdroid.router.router.BrowserRouter;
import org.hpdroid.router.router.CallbackRouter;
import org.hpdroid.router.router.IActivityRouteTableInitializer;
import org.hpdroid.router.router.ICallbackRouteTableInitializer;
import org.hpdroid.router.router.IRouter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by kris on 16/3/17.
 *
 * router 应该是个单例
 */
public class RouterManager {
    private static final RouterManager singleton = new RouterManager();

    //注意这是个list是有顺序的，所以排在前面的优先级会比较高
    List<IRouter> mRouters = new LinkedList<>();
    ActivityRouter mActivityRouter = new ActivityRouter();   //Activity
    BrowserRouter mBrowserRouter = new BrowserRouter();  //浏览器
    CallbackRouter mCallbackRouter = new CallbackRouter();

    private RouterManager(){}

    static RouterManager getSingleton(){
        return singleton;
    }

    public synchronized void addRouter(IRouter router){
        if(router != null){
            //first remove all the duplicate routers
            List<IRouter> duplicateRouters = new ArrayList<>();
            for(IRouter r : mRouters){
                if(r.getClass().equals(router.getClass())){
                    duplicateRouters.add(r);
                }
            }
            mRouters.removeAll(duplicateRouters);
            mRouters.add(router);
        } else {
            Log.e(new NullPointerException("The Router" +
                    "is null" +
                    "").getMessage(), "");
        }
    }

    public synchronized void initBrowserRouter(Context context, Class<? extends Activity> webviewActivity){
        mBrowserRouter.init(context, webviewActivity);
        addRouter(mBrowserRouter);
    }

    public synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer){
        initActivityRouter(context, initializer, null);
    }

    public synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer, String scheme){
        mActivityRouter.init(context, initializer);
        if(!TextUtils.isEmpty(scheme)) {
            mActivityRouter.setMatchScheme(scheme);
        }
        addRouter(mActivityRouter);
    }

    public synchronized void initCallbackRouter(Context context, ICallbackRouteTableInitializer initializer){
        initCallbackRouter(context, initializer, null);
    }

    public synchronized void initCallbackRouter(Context context, ICallbackRouteTableInitializer initializer, String scheme){
        mCallbackRouter.init(context, initializer);
        if(!TextUtils.isEmpty(scheme)) {
            mCallbackRouter.setMatchScheme(scheme);
        }
        addRouter(mCallbackRouter);
    }

    public List<IRouter> getRouters(){
        return mRouters;
    }

    public boolean open(String url){
        for(IRouter router : mRouters){
            if(router.canOpenTheUrl(url)){
                return router.open(url);
            }
        }

        return false;
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     * @param url
     * @return
     */
    @Nullable
    public IRoute getRoute(String url){
        for(IRouter router : mRouters){
            if(router.canOpenTheUrl(url)){
                return router.getRoute(url);
            }
        }
        return null;
    }


    public boolean openRoute(IRoute route){
        for(IRouter router : mRouters){
            if(router.canOpenTheRoute(route)){
                return router.open(route);
            }
        }

        return false;
    }

    /**
     * set your own activity router
     */
    public void setActivityRouter(ActivityRouter router){
        addRouter(router);
    }

    /**
     * set your own BrowserRouter
     */
    public void setBrowserRouter(BrowserRouter router){
        addRouter(router);
    }

    /**
     * set your own CallbackRouter
     */
    public void setCallbackRouter(CallbackRouter router){
        addRouter(router);
    }
}
