package org.hpdroid.router.router;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.hpdroid.router.exception.InvalidRoutePathException;
import org.hpdroid.router.route.CallbackRoute;
import org.hpdroid.router.route.IRoute;
import org.hpdroid.router.tools.ActivityRouteRuleBuilder;
import org.hpdroid.router.utils.UrlUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chsasaw on 16/4/14.
 */
public class CallbackRouter extends BaseRouter {
    private static final String TAG = "CallbackRouter";
    private static String MATCH_SCHEME = "hxstore";

    private static final Set<String> HOSTS_CAN_OPEN = new LinkedHashSet<>();

    static CallbackRouter mSharedCallbackRouter = new CallbackRouter();

    Context mBaseContext;

    Map<String, RouterCallback> mRouteTable = new HashMap<>();

    static {
        CAN_OPEN_ROUTE = CallbackRoute.class;
    }

    public static CallbackRouter getSharedRouter(){
        return mSharedCallbackRouter;
    }

    public void init(Context appContext, ICallbackRouteTableInitializer initializer) {
        mBaseContext = appContext;
        initializer.initRouterTable(mRouteTable);
        for(String pathRule : mRouteTable.keySet()){
            boolean isValid = ActivityRouteRuleBuilder.isActivityRuleValid(pathRule);
            if(!isValid){
                Log.e(new InvalidRoutePathException(pathRule).getMessage(), "");
                mRouteTable.remove(pathRule);
                HOSTS_CAN_OPEN.remove(UrlUtils.getHost(pathRule));
            }else {
                HOSTS_CAN_OPEN.add(UrlUtils.getHost(pathRule));
            }
        }
    }


    @Override
    public IRoute getRoute(String url) {
        return new CallbackRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    @Override
    public boolean canOpenTheRoute(IRoute route) {

        return CAN_OPEN_ROUTE.equals(route.getClass());
    }


    @Override
    public boolean canOpenTheUrl(String url) {
        return TextUtils.equals(UrlUtils.getScheme(url), MATCH_SCHEME) && HOSTS_CAN_OPEN.contains(UrlUtils.getHost(url));
    }

    public void setMatchScheme(String scheme){
        MATCH_SCHEME = scheme;
    }

    public String getMatchScheme(){
        return MATCH_SCHEME;
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return CAN_OPEN_ROUTE;
    }

    @Override
    public boolean open(IRoute route) {
        if(route instanceof CallbackRoute){
            CallbackRoute aRoute = (CallbackRoute) route;
            RouterCallback callback = match(aRoute);

            RouterCallback.RouteContext context = new RouterCallback.RouteContext(UrlUtils.getParameters(aRoute.getUrl()), ((CallbackRoute) route).getExtras(), mBaseContext);
            callback.run(context);

            return true;
        }

        return false;
    }

    @Override
    public boolean open(String url) {
        return open(getRoute(url));
    }

    /**
     * host 和path匹配称之为路由匹匹配
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private String findMatchedRoute(CallbackRoute route) {
        List<String> givenPathSegs = route.getPath();
        OutLoop:
        for(String routeUrl : mRouteTable.keySet()){
            List<String> routePathSegs = UrlUtils.getPathSegments(routeUrl);
            if(!TextUtils.equals(UrlUtils.getHost(routeUrl), route.getHost())){
                continue;
            }
            if(givenPathSegs.size() != routePathSegs.size()){
                continue;
            }
            for(int i=0;i<routePathSegs.size();i++){
                if(!routePathSegs.get(i).startsWith(":")
                        &&!TextUtils.equals(routePathSegs.get(i), givenPathSegs.get(i))) {
                    continue OutLoop;
                }
            }
            //find the match route
            return routeUrl;
        }

        return null;
    }

    @Nullable
    private RouterCallback match(CallbackRoute route) {
        String matchedRoute = findMatchedRoute(route);
        if(matchedRoute == null){
            return null;
        }
        RouterCallback callback = mRouteTable.get(matchedRoute);

        return callback;
    }
}
