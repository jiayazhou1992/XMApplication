/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;
import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;


public class EasySidebar extends View {
    private static final String TAG = "EasySidebar";

    private Paint paint;

    private TextView header;

    //单个字母高度
    private float height;

    private RecyclerView mRecyclerView;

    private Context context;

    private DevSectionIndexer sectionIndex = null;

    private String[] sections;

    private int mToPosition;

    private boolean mShouldScroll = false;




    public EasySidebar(Context context) {
        super(context);
        init();
    }

    public EasySidebar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EasySidebar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EasySidebar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        //sections = new String[]{"热", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ResourceUtils.getColor(R.color.theme_color1));
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(SizeUtils.dipConvertPx(10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sections == null || sections.length == 0){
            return;
        }

        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndscroll(MotionEvent event) {
        if (mRecyclerView == null || sections==null || sections.length == 0) {
            //check the mListView to avoid NPE. but the mListView shouldn't be null
            //need to check the call stack later
            return;
        }
        int sectionPos = sectionForPoint(event.getY());

        String headerString = sections[sectionPos];

        if (header != null) {
            header.setText(headerString);
        }

        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

        if (sectionIndex == null) {
            if (adapter instanceof DevSectionIndexer) {
                sectionIndex = (DevSectionIndexer) adapter;
            } else {
                throw new RuntimeException("列表的适配器没有继承SectionIndexer接口 adapter does not implement SectionIndexer interface");
            }
        }

        if (headerString.equals(sections[0])){
            smoothMoveToPosition(mRecyclerView,0);
        }else {
            smoothMoveToPosition(mRecyclerView, sectionIndex.getPositionForSection(headerString));
        }
    }

    private float lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastY = event.getY();

                if (header == null) {
                    header = (TextView) ((View) getParent()).findViewWithTag("floating_header");
                }
                setHeaderTextAndscroll(event);
                if (header != null) {
                    header.setVisibility(View.VISIBLE);
                }
                //setBackgroundResource(R.drawable.chat_sidebar_background_pressed);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float currY = event.getY();

                if (Math.abs(currY-lastY)>height) {
                    setHeaderTextAndscroll(event);
                    lastY = currY;
                }

                return true;
            }
            case MotionEvent.ACTION_UP:
                if (header != null) {
                    header.setVisibility(View.INVISIBLE);
                }
                //setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (header != null) {
                    header.setVisibility(View.INVISIBLE);
                }
                //setBackgroundColor(Color.TRANSPARENT);
                return true;
        }
        return super.onTouchEvent(event);
    }


    public void setSections(String[] sections) {
        this.sections = sections;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setListView(RecyclerView listView) {
        mRecyclerView = listView;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRecyclerView, mToPosition);
                }*/
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRecyclerView, mToPosition);
                }
            }
        });
    }

    //recyclerview滑动到指定位置
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        if (position == -1) return;

        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position <= firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            DevLogger.iTag(TAG,"第一种可能:跳转位置在第一个可见位置之前");
            //mRecyclerView.smoothScrollToPosition(position);
            mRecyclerView.scrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            DevLogger.iTag(TAG,"第二种可能:跳转位置在第一个可见位置之后");
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                //mRecyclerView.smoothScrollBy(0, top);
                mRecyclerView.scrollBy(0,top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            DevLogger.iTag(TAG,"第三种可能:跳转位置在最后可见项之后");
            //mRecyclerView.smoothScrollToPosition(position);
            mRecyclerView.scrollToPosition(position);

            mToPosition = position;
            mShouldScroll = true;
        }
    }
}
