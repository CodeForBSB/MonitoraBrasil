package com.gamfig.monitorabrasil.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.fabric.sdk.android.Fabric;

/**
 * Created by geral_000 on 14/02/2015.
 */
public class CustomApplication extends Application{

    public final static String URL = "http://www.gamfig.com/mbrasilwsdl/";

    private ImageLoader mImagemLoader;

    private RequestQueue rq;

    private SharedPreferences sharedPref;


    public SharedPreferences getSharedPref(){return sharedPref;}
    public ImageLoader getmImagemLoader() {
        return mImagemLoader;
    }
    public RequestQueue getRq() {
        return rq;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //configurando o imageloader
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .memoryCacheSize(50*1024*1024)
                .build();
        this.mImagemLoader = ImageLoader.getInstance();
        mImagemLoader.init(conf);   
        rq = Volley.newRequestQueue(getApplicationContext());

        sharedPref = getSharedPreferences(getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);
    }


}
