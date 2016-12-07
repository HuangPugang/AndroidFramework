package org.hpdroid.base.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by paul on 16/10/19.
 */

public class FragmentUtil {

    /**
     * 根据类名创建fragment
     * @param className
     * @param bundle
     * @return
     */
    public static Fragment newFragment(String className, Bundle bundle) {
        try {
            Class<Fragment> fragmentClass = (Class<Fragment>) Class.forName(className);
            Fragment fragment = fragmentClass.newInstance();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
