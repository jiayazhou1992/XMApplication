package com.xiaomawang.commonlib.utils.dev.app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

/**
 * detail: Shape 工具类
 * Created by Ttt
 * https://blog.csdn.net/tanghongchang123/article/details/80283686
 * https://www.cnblogs.com/popfisher/p/5606690.html
 * https://www.cnblogs.com/dongdong230/p/4183079.html
 * https://www.cnblogs.com/zhongle/archive/2012/08/28/2659902.html
 * https://www.2cto.com/kf/201601/456024.html
 */
public final class ShapeUtils {

    // 日志TAG
    private static final String TAG = ShapeUtils.class.getSimpleName();

    private final GradientDrawable drawable;

    private ShapeUtils(Builder builder) {
        drawable = builder.gradientDrawable;
    }

    /**
     * 获取 GradientDrawable
     * @return
     */
    public GradientDrawable getDrawable() {
        return drawable;
    }

    /**
     * 设置 Drawable 背景
     * @param view
     */
    public void setDrawable(View view){
        if (view != null){
            view.setBackground(drawable);
        }
    }

    // ==

    /**
     * detail: 构造者模式
     * Created by Ttt
     */
    public static final class Builder {

        private GradientDrawable gradientDrawable = new GradientDrawable();

        public Builder() {
        }

        public Builder(GradientDrawable drawable){
            if (drawable != null){
                this.gradientDrawable = drawable;
            }
        }

        /**
         * 获取 Shape 工具类
         * @return
         */
        public ShapeUtils build(){
            return new ShapeUtils(this);
        }

        // ====

        // = 设置圆角 =

        /**
         * 设置圆角
         * @param radius
         * @return
         */
        public Builder setRadius(float radius){
            if (gradientDrawable != null){
                gradientDrawable.setCornerRadius(radius);
            }
            return this;
        }

        // == 设置左边 ==

        /**
         * 设置圆角
         * @param left
         * @return
         */
        public Builder setRadiusLeft(float left){
            setCornerRadii(left, 0, 0, left);
            return this;
        }

        /**
         * 设置圆角
         * @param leftTop
         * @param leftBottom
         * @return
         */
        public Builder setRadiusLeft(float leftTop, float leftBottom){
            setCornerRadii(leftTop, 0, 0, leftBottom);
            return this;
        }

        // == 设置右边 ==

        /**
         * 设置圆角
         * @param right
         * @return
         */
        public Builder setRadiusRight(float right){
            setCornerRadii(0, right, right, 0);
            return this;
        }

        /**
         * 设置圆角
         * @param rightTop
         * @param rightBottom
         * @return
         */
        public Builder setRadiusRight(float rightTop, float rightBottom){
            setCornerRadii(0, rightTop, rightBottom, 0);
            return this;
        }

        // == 圆角内部处理 ==

        /**
         * 内部处理方法
         * @param leftTop
         * @param rightTop
         * @param rightBottom
         * @param leftBottom
         */
        public Builder setCornerRadii(float leftTop, float rightTop, float rightBottom, float leftBottom) {
//        <corners
//            android:bottomLeftRadius="8dp"
//            android:bottomRightRadius="8dp"
//            android:topLeftRadius="8dp"
//            android:topRightRadius="8dp" />

            if (gradientDrawable != null){
                // radii 数组分别指定四个圆角的半径，每个角可以指定[X_Radius,Y_Radius]，四个圆角的顺序为左上，右上，右下，左下。如果X_Radius,Y_Radius为0表示还是直角。
                gradientDrawable.setCornerRadii(new float[] { leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom });
            }
            return this;
        }

        // == 设置背景色(填充) ==

//    <solid android:color="#DFDFE0" />

        /**
         * 设置背景色(填充铺满)
         * @param color
         * @return
         */
        public Builder setColor(String color){
            if (gradientDrawable != null && !TextUtils.isEmpty(color)){
                try {
                    gradientDrawable.setColor(Color.parseColor(color));
                } catch (Exception e){
                    LogPrintUtils.eTag(TAG, e, "setColor");
                }
            }
            return this;
        }

        /**
         * 设置背景色(填充铺满)
         * @param color
         * @return
         */
        public Builder setColor(@ColorRes int color){
            if (gradientDrawable != null){
                try {
                    gradientDrawable.setColor(ContextCompat.getColor(DevUtils.getContext(), color));
                } catch (Exception e){
                    LogPrintUtils.eTag(TAG, e, "setColor");
                }
            }
            return this;
        }

        // == 设置边框颜色 ==

//        <!-- 描边
//            android:width      整型 描边的宽度
//            android:color      颜色值 描边的颜色
//            android:dashWidth  整型 表示描边的样式是虚线的宽度， 值为0时，表示为实线。值大于0则为虚线。
//            android:dashGap    整型 表示描边为虚线时，虚线之间的间隔 即“ - - - - ”
//        ->

        /**
         * 设置边框颜色
         * @param width
         * @param color
         * @return
         */
        public Builder setStroke(int width, String color){
            if (gradientDrawable != null){
                try {
                    gradientDrawable.setStroke(width, Color.parseColor(color));
                } catch (Exception e){
                    LogPrintUtils.eTag(TAG, e, "setStroke");
                }
            }
            return this;
        }

        /**
         * 设置边框颜色
         * @param width
         * @param color
         * @return
         */
        public Builder setStroke(int width, @ColorRes int color){
            if (gradientDrawable != null){
                try {
                    gradientDrawable.setStroke(width, ContextCompat.getColor(DevUtils.getContext(), color));
                } catch (Exception e){
                    LogPrintUtils.eTag(TAG, e, "setStroke");
                }
            }
            return this;
        }

        /**
         * 设置边框颜色
         * @param width
         * @param color
         * @return
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public Builder setStroke(int width, ColorStateList color){
            if (gradientDrawable != null && color != null){
                try {
                    gradientDrawable.setStroke(width, color);
                } catch (Exception e){
                    LogPrintUtils.eTag(TAG, e, "setStroke");
                }
            }
            return this;
        }

        // == 设置大小 ==

//        <!-- 宽度和高度
//                android:width   整型 宽度
//                android:height  整型 高度
//            -->

        /**
         * 设置大小
         * @param width
         * @param height
         * @return
         */
        public Builder setSize(int width, int height){
            if (gradientDrawable != null){
                try {
                    gradientDrawable.setSize(width, height);
                } catch (Exception e){
                    LogPrintUtils.eTag(TAG, e, "setSize");
                }
            }
            return this;
        }

        // == 设置边距 ==

//        /**
//         * 设置边距
//         * @param padding
//         * @return
//         */
//        public Builder setPadding(int padding){
//            if (gradientDrawable != null){
//                try {
//                    Rect rect = new Rect();
//                    rect.left = padding;
//                    rect.top = padding;
//                    rect.right = padding;
//                    rect.bottom = padding;
//                    gradientDrawable.getPadding(rect);
//                } catch (Exception e){
//                    LogPrintUtils.eTag(TAG, e, "setPadding");
//                }
//            }
//            return this;
//        }

        // == 设置渐变 ==

//        <!-- 渐变，这个设置之后一般就不要设置solid填充色了
//            android:startColor  颜色值 起始颜色
//            android:endColor    颜色值 结束颜色
//            android:centerColor 整型 渐变中间颜色，即开始颜色与结束颜色之间的颜色
//            android:angle       整型 渐变角度(PS：当angle=0时，渐变色是从左向右。 然后逆时针方向转，当angle=90时为从下往上。angle必须为45的整数倍)
//
//            android:type ["linear" | "radial" | "sweep"] 渐变类型(取值：linear、radial、sweep)
//            linear 线性渐变，这是默认设置
//            radial 放射性渐变，以开始色为中心。
//            sweep 扫描线式的渐变。
//
//            android:useLevel ["true" | "false"] 如果要使用LevelListDrawable对象，就要设置为true。设置为true无渐变。false有渐变色
//
//            android:gradientRadius 整型
//            渐变色半径.当 android:type="radial" 时才使用。单独使用 android:type="radial"会报错。
//
//            android:centerX 整型 渐变中心X点坐标的相对位置
//            android:centerY 整型 渐变中心Y点坐标的相对位置
//        -->
//        <gradient
//            android:startColor="@android:color/white"
//            android:centerColor="@android:color/black"
//            android:endColor="@android:color/black"
//            android:useLevel="true"
//            android:angle="45"
//            android:type="radial"
//            android:centerX="0"
//            android:centerY="0"
//            android:gradientRadius="90"/>

//            switch (angle) {
//                case 0:
//                    st.mOrientation = Orientation.LEFT_RIGHT;
//                    break;
//                case 45:
//                    st.mOrientation = Orientation.BL_TR;
//                    break;
//                case 90:
//                    st.mOrientation = Orientation.BOTTOM_TOP;
//                    break;
//                case 135:
//                    st.mOrientation = Orientation.BR_TL;
//                    break;
//                case 180:
//                    st.mOrientation = Orientation.RIGHT_LEFT;
//                    break;
//                case 225:
//                    st.mOrientation = Orientation.TR_BL;
//                    break;
//                case 270:
//                    st.mOrientation = Orientation.TOP_BOTTOM;
//                    break;
//                case 315:
//                    st.mOrientation = Orientation.TL_BR;
//                    break;
//            }

        /**
         * 设置渐变颜色
         * @param colors
         */
        public Builder(@ColorInt int[] colors){
            this(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors));
        }

        /**
         * 设置渐变颜色
         * @param orientation
         * @param colors
         */
        public Builder(GradientDrawable.Orientation orientation, @ColorInt int[] colors){
            this(new GradientDrawable(orientation, colors));
        }
    }

    // == 快捷方法 ==

    /**
     * 创建新的 Shape Builder 对象
     * @param radius
     * @param color
     * @return
     */
    public static Builder newBuilder(float radius, @ColorRes int color){
        return new Builder().setRadius(radius).setColor(color);
    }

    /**
     * 创建新的 Shape Builder 对象
     * @param left 通用左上, 左下
     * @param color
     * @return
     */
    public static Builder newBuilderToLeft(float left, @ColorRes int color){
        return new Builder().setRadiusLeft(left).setColor(color);
    }

    /**
     * 创建新的 Shape Builder 对象
     * @param right 通用右上, 左下
     * @param color
     * @return
     */
    public static Builder newBuilderToRight(float right, @ColorRes int color){
        return new Builder().setRadiusRight(right).setColor(color);
    }

    /**
     * 创建渐变的 Shape Builder 对象
     * @param colors
     * @return
     */
    public static Builder newBuilderToGradient(@ColorInt int[] colors){
        return new Builder(colors);
    }

    /**
     * 创建渐变的 Shape Builder 对象
     * @param orientation
     * @param colors
     * @return
     */
    public static Builder newBuilderToGradient(GradientDrawable.Orientation orientation, @ColorInt int[] colors){
        return new Builder(orientation, colors);
    }
}
