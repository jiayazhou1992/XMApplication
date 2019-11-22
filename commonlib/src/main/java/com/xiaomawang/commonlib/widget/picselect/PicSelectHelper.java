package com.xiaomawang.commonlib.widget.picselect;


import com.xiaomawang.commonlib.utils.dev.common.FileUtils;

public class PicSelectHelper {
    private static final String TAG = "PicSelectHelper";

    private static PicSelectInterface picSelectInterface;


    public static PicSelectInterface getInstance() {

        if (picSelectInterface == null){
            synchronized (PicSelectHelper.class){
                if (picSelectInterface == null){
                    picSelectInterface = new PicSelectInterfaceImpl();
                }
            }
        }

        return picSelectInterface;
    }

    public static String getCropPath(String originalPath){
        //Log.i(TAG, "originalPath "+originalPath);
        String cropPath = null;
        if (originalPath != null) {
            String name = FileUtils.getName(originalPath);
            cropPath = String.valueOf(originalPath).replace(name, "crop_" + name);
        }
        //Log.i(TAG, "cropPath "+cropPath);
        return cropPath;
    }

    public static String originalPath(String cropPath){
        //Log.i(TAG, "cropPath "+cropPath);
        String originalPath = null;
        if (cropPath != null) {
            originalPath = String.valueOf(cropPath).replace("crop_", "");
        }
        //Log.i(TAG, "originalPath "+originalPath);
        return originalPath;
    }
}
