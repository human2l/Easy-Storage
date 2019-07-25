package com.mad.easystorage.application;

import android.app.Activity;
import android.app.Application;

import com.mad.easystorage.constants.Constants;

import java.util.LinkedList;
import java.util.List;

/**
 * responsible for managing all of the activities in application
 */
public class MyApplication extends Application {
    private List<Activity> mActivityList = new LinkedList<Activity>();
    private static MyApplication sInstance;

    private MyApplication() {
    }

    /**
     * Each time use this factory to create an new sInstance of MyApplication for use
     *
     * @return
     */
    public static MyApplication getInstance() {
        if (null == sInstance) {
            sInstance = new MyApplication();
        }
        return sInstance;
    }

    /**
     * Add an activity to the container.
     *
     * @param activity pass in an activity
     */
    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    /**
     * Search all of the activities and finish them.
     * Then exit the application
     */
    public void exit() {
        for (Activity activity : mActivityList) {
            activity.finish();
        }
        System.exit(Constants.SYSTEM_EXIT_STATUS);
    }
}