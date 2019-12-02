package com.liwei.viewbinder;

import android.app.Activity;

public class ViewBinder {

    public static void bind(Activity activity) {
        try {
            Class clazz = Class.forName(activity.getClass().getCanonicalName() + "$$ViewBinder");
            IViewBinder<Activity> iViewBinder = (IViewBinder<Activity>) clazz.newInstance();
            iViewBinder.bind(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
