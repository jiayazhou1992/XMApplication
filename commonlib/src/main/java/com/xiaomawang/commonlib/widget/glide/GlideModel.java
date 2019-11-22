package com.xiaomawang.commonlib.widget.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;


/**
 * Created by jiayazhou on 2018/11/17.
 */
@GlideModule
public class GlideModel extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context,glide,registry);
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return true;
    }
}
