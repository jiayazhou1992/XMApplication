package com.xiaomawang.commonlib.ui.web.bean;

public class MessageToJS {
    private String responseId;
    private DataBean responseData;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public DataBean getResponseData() {
        return responseData;
    }

    public void setResponseData(DataBean responseData) {
        this.responseData = responseData;
    }

    public static class DataBean{
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
