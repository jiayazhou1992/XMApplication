package com.xiaomawang.commonlib.ui.widget.recyclerview.dragsortrecyclerView;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;

/**
 * Created by jiayazhou on 2017/10/28.
 * Changed by huangshihong on 2018/2/8
 */

public class DragSwipeHelper {

    /*绑定拖拽事件*/
    public static ItemTouchHelper attachToRecyclerView(@NonNull RecyclerView recyclerView, @NonNull ItemTouchHelperImpl dragSortCutInstance) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragSortCutInstance);
        //new ListenerInterceptor(itemTouchHelper);//尝试拦截监听器替换成短按监听器
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return itemTouchHelper;
    }

    /**
     * 长按拖动监听器拦截,替换成短按监听器
     */
    public static class ListenerInterceptor {
        private GestureDetector.OnGestureListener mListener2BeIntercept;
        private InterceptListener mListener2Intercept;

        private boolean mDoDrag = true;

        public ListenerInterceptor(ItemTouchHelper helper) {
            mListener2Intercept = new InterceptListener();

            try {
                Field fGesDetector = ItemTouchHelper.class.getDeclaredField("mGestureDetector");
                fGesDetector.setAccessible(true);
                Object objGesDetector = fGesDetector.get(helper);

                Field fImpl = GestureDetectorCompat.class.getDeclaredField("mImpl");
                fImpl.setAccessible(true);

                Object objImpl = fImpl.get(objGesDetector);


                Field fLis = null;
                try {
                    fLis = objImpl.getClass().getDeclaredField("mListener");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Object oDet = null;
                if (fLis == null) {
                    Field fDet = objImpl.getClass().getDeclaredField("mDetector");
                    fDet.setAccessible(true);

                    oDet = fDet.get(objImpl);
                    fLis = oDet.getClass().getDeclaredField("mListener");
                }
                fLis.setAccessible(true);

                mListener2BeIntercept = (GestureDetector.OnGestureListener) fLis.get(oDet);

                fLis.set(oDet, mListener2Intercept);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setDoDrag(boolean drag) {
            mDoDrag = drag;
        }

        public class InterceptListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public void onShowPress(MotionEvent e) {
                if (mDoDrag)
                    mListener2BeIntercept.onLongPress(e);
            }
        }
    }
}
