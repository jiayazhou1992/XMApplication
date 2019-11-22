package com.xiaomawang.commonlib.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {
    private MutableLiveData<Throwable> throwableMutableLiveData;

    public MutableLiveData<Throwable> getThrowableMutableLiveData() {
        if (throwableMutableLiveData == null) {
            throwableMutableLiveData = new MutableLiveData<>();
        }
        return throwableMutableLiveData;
    }
}
