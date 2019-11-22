package com.xiaomawang.commonlib.widget.router;

import android.util.Log;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaomawang.commonlib.base.XMFragment;

import java.util.ArrayList;
import java.util.List;

public class TabPageSwitcher {
    private static final String TAG = "TabPageSwitcher";

    private List<String> fragmentTags = new ArrayList<>();
    private String currTag;


    public void switchPage(@IdRes int fragmentContentlayoutId, FragmentManager fragmentManager, String path) {
        if (currTag != null) {
            Fragment fragment = fragmentManager.findFragmentByTag(currTag);
            if (fragment.isAdded() && !fragment.isHidden()) {
                Log.d(TAG, "switchPage hide");
                fragmentManager.beginTransaction().hide(fragment).commit();
            }
        }

        if (!fragmentTags.contains(path)) {
            fragmentTags.add(path);
        }
        currTag = path;

        Fragment fragment = fragmentManager.findFragmentByTag(path);
        if (fragment == null) {
            XMFragment baseFragment = (XMFragment) ARouter.getInstance().build(path).navigation();
            fragmentManager.beginTransaction().add(fragmentContentlayoutId, baseFragment, path).commitAllowingStateLoss();
        } else {
            if (fragment.isAdded()) {
                if (fragment.isHidden()) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.show(fragment).commit();
                }
            } else {
                fragmentManager.beginTransaction().add(fragmentContentlayoutId, fragment, path).commitAllowingStateLoss();
            }
        }
    }

    public List<String> getFragmentTags() {
        return fragmentTags;
    }

    public Fragment getFragment(FragmentManager fragmentManager, String fragmentTag){
        return fragmentManager.findFragmentByTag(fragmentTag);
    }

    public Fragment getFragment(FragmentManager fragmentManager, int index){
        return fragmentManager.findFragmentByTag(fragmentTags.get(index));
    }
}
