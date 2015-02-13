package com.gamfig.monitorabrasil.classes.twitter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.Dispatcher;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Twitter;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.pojo.Util;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geral_000 on 10/02/2015.
 */
public class TwitterProxy extends TwitterFabric{

    public View getTweetTelaInicial(final LinearLayout myLayout, final Activity activity){
        //inflate
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ll = inflater.inflate(R.layout.listview_item_twitter, null, false);
        //new BuscaTweet().execute();
        Twitter t = getTwitterHashtag();
        TextView txtNome = (TextView)ll.findViewById(R.id.txtTwitterNome);
        TextView txtMsg = (TextView) ll.findViewById(R.id.txtTwitterMsg);
        TextView txtTempo = (TextView) ll.findViewById(R.id.txtTwitterTempo);
        TextView txtId = (TextView) ll.findViewById(R.id.txtTwitterId);
        ImageView foto = (ImageView) ll.findViewById(R.id.imgTwitter);
        ImageView imagem = (ImageView) ll.findViewById(R.id.imageView);
        imagem.setVisibility(View.GONE);
        if(t != null) {
            txtNome.setText(t.getNome());
            txtMsg.setText(t.getTexto());
            Linkify.addLinks(txtMsg,Linkify.WEB_URLS);
            txtId.setText(Html.fromHtml("<a href='http://twitter.com/#!/" + t.getScreenName() + "'>@" + t.getScreenName()
                    + "</a>"));
            txtId.setMovementMethod(LinkMovementMethod.getInstance());
            txtTempo.setText(" . " + t.getData());
            try {
                foto.setImageBitmap(Imagens.getImageBitmap(t.getUrlFoto()));
                foto.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }

        //TODO
        //implementar o click para abrir no twitter ou no browser

       return ll;

    }

    public Twitter getTwitterHashtag(){
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        String result = new Dispatcher(DeputadoDAO.url + "proxytwitter/gethashtag.php", params).getInformacaoPOST();
        try {
            Gson gson = new Gson();
            return gson.fromJson(result, Twitter.class);
        } catch (JsonParseException e) {

        }
        return null;
    }

    public List<Twitter> getTwitterLista() {
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        String result = new Dispatcher(DeputadoDAO.url + "proxytwitter/getlistatwitter.php", params).getInformacaoPOST();
        try {
            Gson gson = new Gson();
            return gson.fromJson(result,  new TypeToken<ArrayList<Twitter>>() {
            }.getType());
        } catch (JsonParseException e) {

        }
        return null;
    }
    public List<Twitter> getTimeline(String twitter) {
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);

        params.add(new BasicNameValuePair("id", twitter));
        String result = new Dispatcher(DeputadoDAO.url + "proxytwitter/gettimeline.php", params).getInformacaoPOST();
        try {
            Gson gson = new Gson();
            return gson.fromJson(result,  new TypeToken<ArrayList<Twitter>>() {
            }.getType());
        } catch (JsonParseException e) {

        }
        return null;
    }



    public class BuscaTweet extends AsyncTask<Void, Void, Twitter> {

        public BuscaTweet() {
        }

        @Override
        protected Twitter doInBackground(Void... params) {

            return getTwitterHashtag();
        }

        protected void onPostExecute(Usuario usuario) {
            try {


            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
