package org.hpdroid.router.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import org.hpdroid.router.exception.InvalidRoutePathException;
import org.hpdroid.router.exception.RouteNotFoundException;
import org.hpdroid.router.route.ActivityRoute;
import org.hpdroid.router.route.IRoute;
import org.hpdroid.router.tools.ActivityRouteRuleBuilder;
import org.hpdroid.router.utils.UrlUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kris on 16/3/10.
 */
public class ActivityRouter extends BaseRouter {
    private static final String TAG = "ActivityRouter";
    private static String MATCH_SCHEME = "hxstore";
    private static final Set<String> HOSTS_CAN_OPEN = new LinkedHashSet<>();

    static ActivityRouter mSharedActivityRouter = new ActivityRouter();
    Context mBaseContext;
    Map<String, String> mRouteTable = new HashMap<>();

    static {
        CAN_OPEN_ROUTE = ActivityRoute.class;
    }

    public static ActivityRouter getSharedRouter() {
        return mSharedActivityRouter;
    }

    public void init(Context appContext, IActivityRouteTableInitializer initializer) {
        mBaseContext = appContext;
        initializer.initRouterTable(mRouteTable);
        for (String pathRule : mRouteTable.keySet()) {
            boolean isValid = ActivityRouteRuleBuilder.isActivityRuleValid(pathRule);
            if (!isValid) {
                Log.e(new InvalidRoutePathException(pathRule).getMessage(), "");
                mRouteTable.remove(pathRule);
                HOSTS_CAN_OPEN.remove(UrlUtils.getHost(pathRule));
            } else {
                HOSTS_CAN_OPEN.add(UrlUtils.getHost(pathRule));
            }
        }
    }


    @Override
    public IRoute getRoute(String url) {
        return new ActivityRoute.Builder(this)
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

    public void setMatchScheme(String scheme) {
        MATCH_SCHEME = scheme;
    }

    public String getMatchScheme() {
        return MATCH_SCHEME;
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return CAN_OPEN_ROUTE;
    }

    @Override
    public boolean open(IRoute route) {
        if (route instanceof ActivityRoute) {
            ActivityRoute aRoute = (ActivityRoute) route;
            switch (aRoute.getOpenType()) {
                case ActivityRoute.START:
                    return open(aRoute, aRoute.getActivity());
                case ActivityRoute.FOR_RESULT_ACTIVITY:
                    return openForResult(aRoute, aRoute.getActivity(), aRoute.getRequestCode());
                case ActivityRoute.FOR_RESULT_SUPPORT_FRAGMENT:
                    return openForResult(aRoute, aRoute.getSupportFragment(), aRoute.getRequestCode());
                case ActivityRoute.FOR_RESULT_FRAGMENT:
                    return openForResult(aRoute, aRoute.getFragment(), aRoute.getRequestCode());
                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public boolean open(String url) {
        return open(getRoute(url));
    }


    protected boolean open(ActivityRoute route, Context context) {
        try {
            Intent intent = match(route);
            if (intent == null) {
                Log.e(new RouteNotFoundException(route.getUrl()).getMessage(), "");
                return false;
            }

            if (context == null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mBaseContext.startActivity(intent);
            } else {
                context.startActivity(intent);
            }

            if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }

            return true;
        } catch (Exception e) {
            Log.e(e.getMessage(), "");
        }

        return false;
    }

    protected boolean openForResult(ActivityRoute route, Activity activity, int requestCode) {

        try {
            Intent intent = match(route);
            if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
            activity.startActivityForResult(intent, requestCode);

            return true;
        } catch (Exception e) {
            Log.e(e.getMessage(), "");
        }

        return false;
    }

    protected boolean openForResult(ActivityRoute route, Fragment fragment, int requestCode) {

        try {
            Intent intent = match(route);
            if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
            fragment.startActivityForResult(intent, requestCode);

            return true;
        } catch (Exception e) {
            Log.e(e.getMessage(), "");
        }

        return false;
    }

    protected boolean openForResult(ActivityRoute route, android.app.Fragment fragment, int requestCode) {

        try {
            Intent intent = match(route);
            if (route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null) {
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
            fragment.startActivityForResult(intent, requestCode);

            return true;
        } catch (Exception e) {
            Log.e(e.getMessage(), "");
        }

        return false;
    }


    /**
     * host 和path匹配称之为路由匹匹配
     *
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private String findMatchedRoute(ActivityRoute route) {
        List<String> givenPathSegs = route.getPath();
        OutLoop:
        for (String routeUrl : mRouteTable.keySet()) {
            List<String> routePathSegs = UrlUtils.getPathSegments(routeUrl);
            if (!TextUtils.equals(UrlUtils.getHost(routeUrl), route.getHost())) {
                continue;
            }
            if (givenPathSegs.size() != routePathSegs.size()) {
                continue;
            }
            for (int i = 0; i < routePathSegs.size(); i++) {
                if (!routePathSegs.get(i).startsWith(":")
                        && !TextUtils.equals(routePathSegs.get(i), givenPathSegs.get(i))) {
                    continue OutLoop;
                }
            }
            //find the match route
            return routeUrl;
        }

        return null;
    }

    private Intent setOptionParams(String url, Intent intent) {
        Map<String, String> queryParams = UrlUtils.getParameters(url);
        for (String key : queryParams.keySet()) {
            intent.putExtra(key, queryParams.get(key));
        }

        return intent;
    }

    private Intent setExtras(Bundle bundle, Intent intent) {
        intent.putExtras(bundle);
        return intent;
    }

    @Nullable
    private Intent match(ActivityRoute route) {
        String matchedRoute = findMatchedRoute(route);
        if (matchedRoute == null) {
            return null;
        }
        String activityName = mRouteTable.get(matchedRoute);
        try {
            Class<?> aClass = Class.forName(activityName);
            Intent intent = new Intent(mBaseContext, aClass);
            intent = setOptionParams(route.getUrl(), intent);
            intent = setExtras(route.getExtras(), intent);
            return intent;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
