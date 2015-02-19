package com.gamfig.monitorabrasil.classes.twitter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.Dispatcher;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.CustomApplication;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Twitter;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geral_000 on 10/02/2015.
 */
public class TwitterProxy extends TwitterFabric{
    private Activity activity;
    private View tl;
    private ProgressBar pb;
    private LinearLayout ll;

    public View getTweetTelaInicial(LinearLayout myLayout, final Activity activity){
        this.activity=activity;
        this.ll=myLayout;
        //inflate
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pb = (ProgressBar)activity.findViewById(R.id.pbTwitter);
        tl = inflater.inflate(R.layout.listview_item_twitter, null, false);
        getTwitterHashtag();
        //TODO
        //implementar o click para abrir no twitter ou no browser

       return ll;

    }

    public void getTwitterHashtag(){

        StringRequest request = new StringRequest(Request.Method.POST , CustomApplication.URL + "proxytwitter/gethashtag.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        montaTwitter(gson.fromJson(response, Twitter.class));
                        pb.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }

        };

        request.setTag("tag");
        ((CustomApplication) this.activity.getApplicationContext()).getRq().add(request);
    }

    private void montaTwitter(Twitter t) {
        TextView txtNome = (TextView)tl.findViewById(R.id.txtTwitterNome);
        TextView txtMsg = (TextView) tl.findViewById(R.id.txtTwitterMsg);
        TextView txtTempo = (TextView) tl.findViewById(R.id.txtTwitterTempo);
        TextView txtId = (TextView) tl.findViewById(R.id.txtTwitterId);
        ImageView foto = (ImageView) tl.findViewById(R.id.imgTwitter);
        ImageView imagem = (ImageView) tl.findViewById(R.id.imageView);
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
                ((CustomApplication) this.activity.getApplicationContext()).getmImagemLoader().displayImage(t.getUrlFoto(),foto);
                foto.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            this.ll.addView(tl);
        }
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
}
