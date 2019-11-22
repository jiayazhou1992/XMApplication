package com.xiaomawang.commonlib.ui.web.bean;

import com.xiaomawang.commonlib.data.bean.BaseBeen;

import java.util.Map;

public class ArgJson extends BaseBeen {
    private boolean show;
    private boolean hide;
    private boolean formData;
    private boolean share;
    private String shareTitle;
    private String shareDesc;
    private String shareLink;
    private String shareImg;
    private String pagePath;
    private Map<String, Object> pageArgs;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getShareImg() {
        return shareImg;
    }

    public void setShareImg(String shareImg) {
        this.shareImg = shareImg;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public boolean isFormData() {
        return formData;
    }

    public void setFormData(boolean formData) {
        this.formData = formData;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDesc() {
        return shareDesc;
    }

    public void setShareDesc(String shareDesc) {
        this.shareDesc = shareDesc;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public Map<String, Object> getPageArgs() {
        return pageArgs;
    }

    public void setPageArgs(Map<String, Object> pageArgs) {
        this.pageArgs = pageArgs;
    }
}
