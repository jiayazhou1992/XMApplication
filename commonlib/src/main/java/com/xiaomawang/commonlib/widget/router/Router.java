package com.xiaomawang.commonlib.widget.router;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.xiaomawang.commonlib.base.XMFragment;
import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Router {
    private static final String TAG = "Router";

    public static final String connection_1 = "?";
    public static final String connection_2 = "&";
    public static final String connection_3 = "=";

    private static RouterOptions mOptions;

    public static void init(RouterOptions options){
        mOptions = options;
    }

    public static Page with(@NonNull AppCompatActivity activity){
        Page page = new Page(activity);
        return page;
    }

    /**
     * page
     */
    public static class Page{

        WeakReference<AppCompatActivity> context;
        Class<? extends RouteActivity> containerClass = mOptions.mContainerClass;
        String pagePath;
        boolean anim = true;
        boolean replace;
        int requestCode = -1;//尽量不要重复 比如 以及页面 100+ 二级页面 200+
        Bundle data;

        Page(AppCompatActivity context) {
            this.context = new WeakReference<>(context);
        }

        public Page setContainerClass(Class<? extends RouteActivity> containerClass) {
            this.containerClass = containerClass;
            return this;
        }

        public void setPath(String path) {
            this.pagePath = path;
        }

        public Page setAnim(boolean anim) {
            this.anim = anim;
            return this;
        }

        public Page setReplace(boolean replace) {
            this.replace = replace;
            return this;
        }

        public Page setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Page setData(Bundle data) {
            if (this.data == null) {
                this.data = data;
            }else {
                this.data.putAll(data);
            }
            return this;
        }

        public Page setData(String json) {
            if (StringUtils.isEmpty(json)) return this;
            if (this.data == null) {
                this.data = new Bundle();
            }
            parseJsonData(json);
            return this;
        }

        public Page setData(Map<String, Object> map) {
            if (map != null && map.size() > 0) {
                if (this.data == null) {
                    this.data = new Bundle();
                }
                for (Map.Entry<String, Object> entrySet:map.entrySet()) {
                    String k = entrySet.getKey();
                    Object v = entrySet.getValue();
                    if (v instanceof String) {
                        this.data.putString(k, (String) v);
                    }else if (v instanceof Float) {
                        this.data.putFloat(k, (Float) v);
                    }else if (v instanceof Integer) {
                        this.data.putInt(k, (Integer) v);
                    }else if (v instanceof Boolean) {
                        this.data.putBoolean(k, (Boolean) v);
                    }
                }
            }
            return this;
        }

        private Uri parsePath(String path){
            if (StringUtils.isEmpty(path)) return null;

            Uri uri = Uri.parse(path);
            Set<String> keys = uri.getQueryParameterNames();
            if (keys != null && keys.size() > 0) {
                for (String k : keys) {
                    if (data == null){
                        data = new Bundle();
                    }
                    //Boolean
                    String isBooleanStr = uri.getQueryParameter(k);
                    if (isBooleanStr != null) {
                        if ("false".equals(isBooleanStr)) {
                            data.putBoolean(k, false);
                            break;
                        } else if ("true".equals(isBooleanStr)) {
                            data.putBoolean(k, true);
                            break;
                        }
                    }
                    //String
                    List<String> vs = uri.getQueryParameters(k);
                    if (vs != null && vs.size() > 0) {
                        if (vs.size() > 1) {
                            data.putStringArrayList(k, new ArrayList<>(vs));
                        }else {
                            data.putString(k, vs.get(0));
                        }
                    }
                }
            }
            this.pagePath = uri.getPath();
//            if (this.pagePath != null && this.pagePath.startsWith("/")) {
//                this.pagePath = this.pagePath.substring(1);
//            }
            //Log.i(TAG, " scheme " + uri.getScheme() + " host " + uri.getHost() + " path " + uri.getPath());
            return uri;
        }

        private void parseJsonData(String json){
            if (StringUtils.isEmpty(json)) return;
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet =  jsonObject.entrySet();
                if (entrySet != null && entrySet.size() > 0) {
                    for (Map.Entry<String, JsonElement> entry: entrySet) {
                        if (data == null){
                            data = new Bundle();
                        }
                        data.putString(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            } catch (JsonSyntaxException e){

            }catch (IllegalStateException e) {

            }finally {

            }
        }

        public void pageGo(String path){
            pageGo(path, null);
        }

        public void pageGo(String path, CallBack callBack){
            this.pagePath = String.valueOf(path);
            // 解析 path
            Uri uri = parsePath(this.pagePath);
            if (uri != null && !TextUtils.equals(uri.getScheme(), mOptions.scheme) && ("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))) {
                //跳转html
//                Intent intent = new Intent(this.context.get(), mOptions.webContainerActivityClass);
                if (data == null) {
                    data = new Bundle();
                }
//                Bundle bundle = WebContract.getBundle(path, "", false, true, true);
//                bundle.putAll(data);
//                intent.putExtras(bundle);
//                RouterFragment routerFragment = getRouterFragment(this.context.get());
//                if (requestCode == -1) {
//                    routerFragment.startActivity(intent);
//                } else {
//                    routerFragment.startActivityForResult(intent, requestCode, callBack);
//                }
            }else {
                //跳转原生
                if (TextUtils.equals(this.context.get().getClass().getName(), containerClass.getName()) && (this.context.get() instanceof MultipleFragmentActivity)) {
                    //添加fragment
                    RouterFragment routerFragment = getRouterFragment(this.context.get());
                    routerFragment.startFragmentForResult(pagePath, replace, anim, requestCode, data, callBack);
                } else {
                    //打开新的activity
                    RouterFragment routerFragment = getRouterFragment(this.context.get());
                    if (this.context.get() instanceof SingleFragmentActivity && !routerFragment.haveTags()){
                        //如果当前已处于SingleFragmentActivity且该activity里还没有fragment
                        routerFragment.startFragmentForResult(pagePath, replace, anim, requestCode, data, callBack);
                    }else {
                        Intent intent = new Intent(this.context.get(), containerClass);
                        if (data == null) {
                            data = new Bundle();
                        }
                        data.putString(BaseRouteConfig.PAGE_PATH, pagePath);
                        intent.putExtras(data);
                        if (requestCode == -1) {
                            routerFragment.startActivity(intent);
                        } else {
                            routerFragment.startActivityForResult(intent, requestCode, callBack);
                        }
                    }
                }
            }
        }

        public void pageGo(Intent intent){
            pageGo(intent, null);
        }

        public void pageGo(Intent intent, CallBack callBack){
            RouterFragment routerFragment = getRouterFragment(this.context.get());
            if (requestCode == -1){
                routerFragment.startActivity(intent);
            }else {
                routerFragment.startActivityForResult(intent, requestCode, callBack);
            }
        }

        public void pageGo(Intent[] intents){
            this.context.get().startActivities(intents);
        }

        public void pageBack() {
            pageBack(null);
        }

        public void pageBack(Bundle bundle) {
            RouterFragment routerFragment = getRouterFragment(this.context.get());
            routerFragment.fragmentBack(bundle);
        }
    }


    public static RouterFragment getRouterFragment(AppCompatActivity activity) {
        RouterFragment routerFragment = (RouterFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (routerFragment == null) {
            routerFragment = RouterFragment.newInstance();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(routerFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return routerFragment;
    }

    public interface CallBack{
        void onPageBack(int requestCode, int resultCode, Intent data);
    }

    /**
     * 配置
     */
    public static class RouterOptions {
        //fragment容器
        public Class<? extends RouteActivity> mContainerClass;

        //fragment html容器路径(fragment 的 router path)
        public String webContainerPath;

        //Activity html容器
//        public Class<? extends BaseWebActivity> webContainerActivityClass;

        //
        public String scheme;
        public String host;
        public String pathPrefix;


        public RouterOptions(Builder builder) {
            this.mContainerClass = builder.mContainerClass;
            this.webContainerPath = builder.webContainerPath;
//            this.webContainerActivityClass = builder.webContainerActivityClass;
            this.scheme = builder.scheme;
            this.host = builder.host;
            this.pathPrefix = builder.pathPrefix;
        }

        public static class Builder{
            Class<? extends RouteActivity> mContainerClass = SingleFragmentActivity.class;
            String webContainerPath;
//            Class<? extends BaseWebActivity> webContainerActivityClass;
            String scheme;
            String host;
            String pathPrefix;

            public Builder() {

            }

            public Builder setContainerClass(Class<? extends RouteActivity> containerClass) {
                this.mContainerClass = containerClass;
                return this;
            }

            public Builder setWebContainerPath(String webContainerPath) {
                this.webContainerPath = webContainerPath;
                return this;
            }

//            public Builder setWebContainerActivityClass(Class<? extends BaseWebActivity> webContainerActivityClass) {
//                this.webContainerActivityClass = webContainerActivityClass;
//                return this;
//            }

            public Builder setScheme(String scheme) {
                this.scheme = scheme;
                return this;
            }

            public Builder setHost(String host) {
                this.host = host;
                return this;
            }

            public Builder setPathPrefix(String pathPrefix) {
                this.pathPrefix = pathPrefix;
                return this;
            }

            public RouterOptions build() {
                return new RouterOptions(this);
            }
        }
    }

}
