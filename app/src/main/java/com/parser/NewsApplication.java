package com.parser;

import android.app.Application;
import android.content.Context;

import com.parser.loader.ImageLoader;
import com.parser.os.VKExecutor;

import java.util.HashMap;
import java.util.Map;

public class NewsApplication extends Application {
    private static Map<String, Object> sAppItemsMap;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppItemsMap = new HashMap<>();
        ImageLoader imageLoader = new ImageLoader(this);
        VKExecutor executor = new VKExecutor();
        sAppItemsMap.put(ImageLoader.KEY, imageLoader);
        sAppItemsMap.put(VKExecutor.KEY, executor);
    }

    public static Object get(Context context, String key) {
        return sAppItemsMap.get(key);
    }

}
