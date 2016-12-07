package org.hpdroid.router.router;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Created by chsasaw on 16/4/14.
 */
public interface RouterCallback {
    public abstract void run(RouteContext context);

    /**
     * The class supplied to custom callbacks to describe the route route
     */
    public class RouteContext {
        Map<String, String> _params;
        Bundle _extras;
        Context _context;

        public RouteContext(Map<String, String> params, Bundle extras, Context context) {
            _params = params;
            _extras = extras;
            _context = context;
        }

        /**
         * Returns the route parameters as specified by the configured route
         */
        public Map<String, String> getParams() { return _params; }

        /**
         * Returns the extras supplied with the route
         */
        public Bundle getExtras() { return _extras; }

        /**
         * Returns the Android Context that should be used to open the route
         */
        public Context getContext() { return _context; }
    }
}
