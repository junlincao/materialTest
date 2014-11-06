package com.caiyi.ui;

import java.lang.reflect.Method;

import android.widget.AbsListView;

/**
 * ListView辅助类
 * 
 * @author CJL
 * @since 2014年11月6日
 */
public class ListViewHelper {
    /** AbsListView 的 computeVerticalScrollOffset() 方法 **/
    private static Method mMethod;

    /** 反射失败异常 **/
    public static class NotSupportException extends Exception {

        private static final long serialVersionUID = -7519190522802824355L;

        public NotSupportException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /** Constructor **/
    private ListViewHelper() {
    }

    /**
     * 获取AbsListView当前滚动Y值
     * 
     * @param lv
     *            AbsListView
     * @return 滚动Y值
     * @throws NotSupportException
     *             反射方法失败...
     */
    public static int getScrollY(AbsListView lv) throws NotSupportException {
        try {
            if (mMethod == null) {
                mMethod = Class.forName(AbsListView.class.getName()).getDeclaredMethod(
                        "computeVerticalScrollOffset");
                mMethod.setAccessible(true);
            }
            return (int) mMethod.invoke(lv);
        } catch (Exception e) {
            throw new NotSupportException("invoke AbsListView method computeVerticalScrollOffset() failed !\n"
                    + e.getMessage(), e);
        }
    }

    /**
     * 获取AbsListView 滚动范围
     * 
     * @param lv
     *            AbsListView
     * @return 最大滚动Y值
     * @throws NotSupportException
     *             反射方法失败...
     */
    public static int getScrollRange(AbsListView lv) throws NotSupportException {
        try {
            Method m = Class.forName(AbsListView.class.getName()).getDeclaredMethod(
                    "computeVerticalScrollRange");
            m.setAccessible(true);
            return (int) m.invoke(lv);
        } catch (Exception e) {
            throw new NotSupportException("invoke AbsListView method computeVerticalScrollOffset() failed !\n"
                    + e.getMessage(), e);
        }
    }
}
