package org.hpdroid.base.base;

import android.support.v4.app.Fragment;

import org.hpdroid.base.common.bean.FragmentItem;

import java.util.List;

/**
 * Created by paul on 16/12/7.
 */

public abstract class BaseFragmentFactory {

    public abstract Fragment getTabFragment(int index);

    public enum FragmentIndex {
        home,
        loan,
        insurance,
        creditCard,
        mine
    }

    public abstract List<FragmentItem> getDormFragmentList();
}
