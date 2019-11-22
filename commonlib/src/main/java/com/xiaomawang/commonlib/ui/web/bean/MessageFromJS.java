package com.xiaomawang.commonlib.ui.web.bean;

import com.xiaomawang.commonlib.data.bean.BaseBeen;

import java.util.Map;

public class MessageFromJS extends BaseBeen {
    private String handlerName;
    private Map<String, Object> data;
    private String callbackId;

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }
}
