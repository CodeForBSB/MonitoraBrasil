package com.gamfig.monitorabrasil.classes.twitter;

import android.app.Activity;
import android.widget.LinearLayout;


/**
 * Created by geral_000 on 10/02/2015.
 */
public class TwitterFabric {

    public TwitterFabric(){};

    public TwitterFabric(String tipo){
        if(tipo.equals("api")){
            new TwitterAPI();
        }else{
            if(tipo.equals("proxy")){
                new TwitterProxy();
            }
        }
    }

    public android.view.View getTweetTelaInicial(final LinearLayout myLayout, final Activity activity){


        return null;
    }

}
