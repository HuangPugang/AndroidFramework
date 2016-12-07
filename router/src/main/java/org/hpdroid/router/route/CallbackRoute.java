package org.hpdroid.router.route;

import android.os.Bundle;

import org.hpdroid.router.router.IRouter;

/**
 * Created by chsasaw on 16/4/14.
 */
public class CallbackRoute extends BaseRoute {

    private Bundle mExtras;

    public CallbackRoute(IRouter router, String url) {
        super(router, url);
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public void setExtras(Bundle extras) {
        mExtras = extras;
    }

    public static class Builder {
        String mUrl;
        IRouter mRouter;
        Bundle mExtras;

        public Builder(IRouter router){
            mRouter = router;
        }

        public Builder setUrl(String url){
            mUrl = url;
            return this;
        }

        public Builder setExtras(Bundle bundle){
            mExtras = bundle;
            return this;
        }

        public CallbackRoute build(){
            CallbackRoute route = new CallbackRoute(mRouter, mUrl);
            route.setExtras(mExtras);
            return route;
        }
    }
}
