package com.xiaomawang.commonlib.ui.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.ui.widget.photoview.MyPhotoView;
import com.xiaomawang.commonlib.ui.widget.photoview.PhotoViewAttacher;
import com.xiaomawang.commonlib.utils.dev.app.BarUtils;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.widget.glide.GlideApp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShowPicsDialog extends Activity {
    private static final String TAG = "ShowPicsDialog";

    public static final String EXTRA_NAME = "pics";
    public static final String EXTRA_INDEX = "index";

    private ViewPager viewPager;
    private PicPagerAdapter picPagerAdapter;

    public static void start(final Context context, String picUrl) {
        ArrayList<String> pics = new ArrayList<>();
        pics.add(picUrl);
        Intent starter = new Intent(context, ShowPicsDialog.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putStringArrayListExtra(EXTRA_NAME,pics);
        context.startActivity(starter);
        ((Activity)context).overridePendingTransition(R.anim.window_enter_transparent_anim, R.anim.window_exit_transparent_anim);
    }

    public static void start(final Context context, ArrayList<String> picUrls, int index) {
        Intent starter = new Intent(context, ShowPicsDialog.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putStringArrayListExtra(EXTRA_NAME,picUrls);
        starter.putExtra(EXTRA_INDEX, index);
        context.startActivity(starter);
        ((Activity)context).overridePendingTransition(R.anim.window_enter_transparent_anim, R.anim.window_exit_transparent_anim);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(ResourceUtils.getColor(R.color.black_tran87)));

        setContentView(R.layout.dialog_showpics);

        BarUtils.setStatusBarAlpha(this);

        viewPager = findViewById(R.id.viewPager);

        Intent intent = getIntent();
        ArrayList<String> pics = intent.getStringArrayListExtra(EXTRA_NAME);
        int index = intent.getIntExtra(EXTRA_INDEX, 0);
        picPagerAdapter = new PicPagerAdapter(this, pics);
        viewPager.setAdapter(picPagerAdapter);
        viewPager.setCurrentItem(index);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.window_enter_transparent_anim, R.anim.window_exit_transparent_anim);
    }


    public static class PicPagerAdapter extends PagerAdapter {

        WeakReference<Activity> weakReference;
        List<String> pics;

        public PicPagerAdapter(Activity activity, List<String> pics) {
            this.weakReference = new WeakReference<>(activity);
            this.pics = pics;
        }

        @Override
        public int getCount() {
            return pics == null ? 0 : pics.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            MyPhotoView photoView = new MyPhotoView(container.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(params);
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (weakReference != null && weakReference.get() != null) {
                        weakReference.get().finish();
                    }
                }
            });
            if (weakReference != null && weakReference.get() != null) {
                GlideApp.with(weakReference.get()).load(pics.get(position)).into(photoView);
            }
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}
