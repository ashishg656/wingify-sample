package com.wingify.ashishgoel.wingifysample.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.utils.NutraBaseImageDecoder;

import java.io.File;

/**
 * Created by Ashish Goel on 11/27/2015.
 */
public class ZApplication extends Application {


    static ZApplication sInstance;
    public static File cacheDir;
    public static final String IMAGE_DIRECTORY_NAME = "Instirepo";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(),
                "instirepo");
        initImageLoader(getApplicationContext());

    }

    public static void initImageLoader(Context context) {
        BitmapFactory.Options decodingOptions = new BitmapFactory.Options();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.symphony)
                .decodingOptions(decodingOptions)

                .bitmapConfig(Bitmap.Config.ARGB_8888).build();

        final int memClass = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;

        System.out.println("Memory cache size" + cacheSize);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(5).memoryCacheSize(cacheSize)
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(300)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDecoder(new NutraBaseImageDecoder(true))
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(options)
                .tasksProcessingOrder(QueueProcessingType.FIFO).build();

        ImageLoader.getInstance().init(config);
    }

    public static ZApplication getInstance() {
        if (sInstance == null)
            sInstance = new ZApplication();
        return sInstance;
    }
}
