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
import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.PlayListItemAdapter;
import com.gamfig.monitorabrasil.adapter.YouTubeItemAdapter;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.youtube.PlayList;
import com.gamfig.monitorabrasil.classes.youtube.Video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class TvSenadoFragment extends ListFragment implements OnScrollListener{


    private List<Video> list;

	public TvSenadoFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

//        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PlayList playList = list.get(position);
//
//                VideosFragment videosFragment = new VideosFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("idPlaylist",playList.getId());
//                videosFragment.setArguments(bundle);
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.container, videosFragment)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });
        getPlaylists();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = list.get(position);
                abrirVideo(video.getId());
            }
        });
    }



    public void abrirVideo(String id){
        Uri uri = Uri.parse("http://www.youtube.com/watch?v="+id);
        uri = Uri.parse("vnd.youtube:"+uri.getQueryParameter("v"));

        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private void getPlaylists() {

        StringRequest request = new StringRequest(Request.Method.GET , AppController.URL + "rest/youtube/getvideos.php",
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
                        Crashlytics.log(error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(request,"tag");
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
