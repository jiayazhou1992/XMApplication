package com.xiaomawang.commonlib.utils.dev.common;


import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxJavaUtils {

    public ObservableTransformer schedulersTransformer() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
