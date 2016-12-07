package org.hpdroid.router.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.hpdroid.router.route.BrowserRoute;
import org.hpdroid.router.route.IRoute;
import org.hpdroid.router.utils.UrlUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kris on 16/3/17.
 */
public class BrowserRouter extends BaseRouter {
    private static final Set<String> SCHEMES_CAN_OPEN = new LinkedHashSet<>();

    private Context mBaseContext;

    private Class<? extends Activity> mWebviewClass;

    static {
        SCHEMES_CAN_OPEN.add("https");
        SCHEMES_CAN_OPEN.add("http");
    }

    public void init(Context context, Class<? extends Activity> webviewClass){
        mBaseContext = context;
        mWebviewClass = webviewClass;
    }

    @Override
    public boolean open(IRoute route) {
        Uri uri = Uri.parse(route.getUrl());

        Intent intent;
        if (mWebviewClass == null) {
            intent = new Intent(Intent.ACTION_VIEW, uri);
        }else {
            intent = new Intent(mBaseContext, mWebviewClass);
            Bundle bundle = new Bundle();
            bundle.putString("url", route.getUrl());
            intent.putExtras(bundle);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mBaseContext.startActivity(intent);

        return true;
    }

    @Override
    public boolean open(String url) {
        return open(getRoute(url));
    }

    @Override
    public IRoute getRoute(String url) {
        return new BrowserRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    @Override
    public boolean canOpenTheRoute(IRoute route) {
        return getCanOpenRoute().equals(route.getClass());
    }

    @Override
    public boolean canOpenTheUrl(String url) {
        return SCHEMES_CAN_OPEN.contains(UrlUtils.getScheme(url));
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return BrowserRoute.class;
    }
}
