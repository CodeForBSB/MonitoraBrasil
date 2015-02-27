/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 *
 * 
 */
package com.gamfig.monitorabrasil.fragments.listviews;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gamfig.monitorabrasil.DAO.PoliticoDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.YouTubeItemAdapter;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends ListFragment implements OnScrollListener{


    private List<Video> list;

	public VideosFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// buscar as infos do deputado
		Bundle bundle = this.getArguments();
		try {
			int idPolitico = bundle.getInt("idPolitico");
            //busca politico
            Politico p = new PoliticoDAO(AppController.getInstance().getDbh().getConnectionSource()).getPolitico(idPolitico);
            getVideos(p.getNome());


		} catch (Exception e) {

		}
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = list.get(position);
                abrirVideo(video.getId());
            }
        });

		//getListView().setOnScrollListener(this);

	}

    private void getVideos(String nome) {

        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/carga_videos_politicos.php?nome="+ URLEncoder.encode(nome),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        list = gson.fromJson(response, new TypeToken<ArrayList<Video>>() {}.getType());
                        YouTubeItemAdapter adapter = new YouTubeItemAdapter(getActivity(),R.layout.listview_item_youtube,list);
                        setListAdapter(adapter);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // pb.setVisibility(View.GONE);
                    }
                });
        AppController.getInstance().addToRequestQueue(request,"tag");
    }

    public void abrirVideo(String id){
        Uri uri = Uri.parse("http://www.youtube.com/watch?v="+id);
        uri = Uri.parse("vnd.youtube:"+uri.getQueryParameter("v"));

        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }



	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView v, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (getListView().getLastVisiblePosition() >= getListView().getCount() - 3) {


			}
		}

	}



}
