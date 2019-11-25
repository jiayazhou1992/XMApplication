package com.xiaomawang.commonlib.data.remote;

import com.xiaomawang.commonlib.data.remote.exception.ApiException;
import com.xiaomawang.commonlib.data.remote.exception.ExceptionEngine;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class HttpSubscriber<T> implements Subscriber<BaseDto<T>> {

    @Override
    public void onSubscribe(Subscription s) {
        // 观察者接收事件 = 1个
        s.request(1);
    }

    @Override
    public void onNext(BaseDto<T> t) {
        if (t.getCode() == HttpsConstant.RespCode.R000) {

        } else if(t.getCode() == HttpsConstant.RespCode.R001 || //登录超时，重新登录
                t.getCode() == HttpsConstant.RespCode.R002){ //令牌无效

        } else{
            ExceptionEngine.handleException(new ServerException(t.getCode(), t.getMessage()));
        }
    }

    @Override
    public void onError(Throwable t) {
        ExceptionEngine.handleException(t);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T t);

    public abstract void onFailed(ApiException e);
}
