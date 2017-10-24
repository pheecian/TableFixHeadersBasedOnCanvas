package com.example.pheec.tablefixheaders;

import android.graphics.Bitmap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pheec on 2017/10/24.
 */

public class BitmapHelper {
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Bitmap>>> map;

    private BitmapHelper() {
        map = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Bitmap>>>();

    }

    public static BitmapHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static Bitmap getBitmap(int tag, int w, int h) {

        if (map.containsKey(tag)) {
            if (map.get(tag).containsKey(w)) {
                if (map.get(tag).get(w).containsKey(h)) {
                    return map.get(tag).get(w).get(h);
                } else {
                    map.get(tag).get(w).put(h, Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
                    return map.get(tag).get(w).get(h);
                }
            } else {
                map.get(tag).put(w, new ConcurrentHashMap<Integer, Bitmap>());
                map.get(tag).get(w).put(h, Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
                return map.get(tag).get(w).get(h);
            }
        } else {
            map.put(tag, new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Bitmap>>());
            map.get(tag).put(w, new ConcurrentHashMap<Integer, Bitmap>());
            map.get(tag).get(w).put(h, Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
            return map.get(tag).get(w).get(h);
        }
    }

    private static class LazyHolder {
        private static final BitmapHelper INSTANCE = new BitmapHelper();
    }

}


