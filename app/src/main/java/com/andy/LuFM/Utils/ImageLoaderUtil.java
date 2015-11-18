package com.andy.LuFM.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import com.andy.LuFM.LuFmApplication;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * 图片加载类
 */
public class ImageLoaderUtil {
    private static final int maxMemory = 1024 * 1024 * ((ActivityManager) LuFmApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 8;
    private static final int maxDiskSize = 50 * 1024 * 1024;
    private static final int maxDiskCount = 8;

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(getLruDiskCache(context))
                .memoryCache(getMemoryCache())
                .tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoaderConfiguration config = builder.build();
        // Initialize ImageLoader w     ith configuration.
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

    private static LruDiskCache getLruDiskCache(Context context) {
        LruDiskCache lruDiskCache = null;
        try {
            File file = getDiskCacheDir(context, "newsItemImages");
            lruDiskCache = new LruDiskCache(file, null
                    , new Md5FileNameGenerator(), maxDiskSize, maxDiskCount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lruDiskCache;
    }

    private static MemoryCache getMemoryCache() {
        MemoryCache memoryCache;
        memoryCache = new LruMemoryCache(maxMemory);
        return memoryCache;
    }

    public static File getDiskCacheDir(Context context, String folderName) {
        String cachePath;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            cachePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getAbsolutePath();
        }
        return new File(cachePath + File.separator + folderName);
    }
}
