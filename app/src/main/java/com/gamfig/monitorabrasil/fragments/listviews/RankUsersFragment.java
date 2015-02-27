package com.gamfig.monitorabrasil.fragments.listviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.activitys.LoginRedeActivity;
import com.gamfig.monitorabrasil.adapter.RankAdapter;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.dialog.DialogPontuacao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RankUsersFragment extends ListFragment implements OnScrollListener {

	RankAdapter adapter;
	RelativeLayout rlPb;
	ListView lv;
	int idPolitico;
	int currentPage;
	boolean chegouFim = false;
    String tipo;
    ViewPager pager;

	public RankUsersFragment() {
	}
    public void setTipo (String tipo){
        this.tipo=tipo;
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        if(tipo.equals("amigos")){
            Session session = Session.getActiveSession();
            if(session==null){
                // try to restore from cache
                session = Session.openActiveSessionFromCache(getActivity());
            }
            if(session != null && ( session.isOpened())){
                //buscar a lista de amgigos
                getFriends(session);
            }else{
                //mostrar botao para conectar
                new AlertDialog.Builder(getActivity())
                        .setTitle("Faça login no Facebook")
                        .setMessage("Para ver seus amigos, faça login no Facebook")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), LoginRedeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }else{
            new buscaRank(getActivity()).execute();
            getListView().setOnScrollListener(this);
        }
        pager = (ViewPager)getActivity().findViewById(R.id.pager);


	}

    public void getFriends(Session session){
        Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> graphUsers, Response response) {
                if (graphUsers != null) {
                    List<Usuario> usuarios = new ArrayList<Usuario>();
                    Iterator itr = graphUsers.iterator();
                    List<String> ids = new ArrayList<String>();

                    while (itr.hasNext()) {
                        //buscar a pontuacao de cada um

                        GraphUser friend = (GraphUser) itr.next();
                        Usuario user = new Usuario();
                        user.setNome(friend.getName());
                        user.setIdFacebook(friend.getId());
                        usuarios.add(user);
                        ids.add(friend.getId());
                    }
                    buscaPontuacao(ids);


                } else {
                    Log.i("monitora", response.toString());
                }
            }
        }).executeAsync();
    }
    private void buscaPontuacao(final List<String> ids) {
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST , AppController.URL + "rest/get_ranking_friends.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Usuario> usuarios = gson.fromJson(response, new TypeToken<ArrayList<Usuario>>() {
                        }.getType());
                        RankAdapter adapter = new RankAdapter(getActivity(), R.layout.listview_item_usuario, usuarios);
                        setListAdapter(adapter);
                        getListView().setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Usuario userSelecionado = (Usuario)getListAdapter().getItem(arg2);
                                DialogFragment dialog = new DialogPontuacao(userSelecionado);
                                dialog.show(getActivity().getFragmentManager(), "Pontuação");



                            }
                        });
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                JSONArray jsonArray = new JSONArray(ids);
                params.put("ids",jsonArray.toString());
                return params;
            }};
        AppController.getInstance().addToRequestQueue(request,"tag");
    }



    /**
	 * BUSCA Rank
	 *
	 * 
	 */
	public class buscaRank extends AsyncTask<Void, Void, List<Usuario>> {
		Activity mActivity;
		ArrayList<Usuario> users;

		public buscaRank(Activity listaProjetosActivity) {
			this.mActivity = listaProjetosActivity;

		}

		@Override
		protected List<Usuario> doInBackground(Void... params) {

			// buscar os projetos da lista do user

			return new UserDAO().buscaRankingUsers(0);
		}

		protected void onPostExecute(List<Usuario> usuarios) {

			// VERIFICA SE ESTa NA ABA DE PROJETOS
			try {
				if (usuarios != null) {
					adapter = new RankAdapter(getActivity(), R.layout.listview_item_usuario, usuarios);
					setListAdapter(adapter);
					getListView().setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            Usuario userSelecionado = (Usuario)getListAdapter().getItem(arg2);
                            DialogFragment dialog = new DialogPontuacao(userSelecionado);
                            dialog.show(getActivity().getFragmentManager(), "Pontuação");


							
						}
					});
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	public class buscaMaisUsers extends AsyncTask<Void, Void, List<Usuario>> {
		Activity mActivity;
		private ProgressDialog mDialog;
		List<Usuario> users;
		int page;

		public buscaMaisUsers(Activity listaProjetosActivity, int currentPage2) {
			this.mActivity = listaProjetosActivity;
			page = currentPage2;
		}

		@Override
		protected List<Usuario> doInBackground(Void... params) {

			return new UserDAO().buscaRankingUsers(page);

		}

		protected void onPostExecute(List<Usuario> usuarios) {

			try {
				if (usuarios != null) {
					adapter = (RankAdapter) getListAdapter();
					for (Usuario user : usuarios) {
						adapter.add(user);
					}
					adapter.notifyDataSetChanged();

				} else {
					chegouFim = true;
					// colocar foot
					if (getListView().getFooterViewsCount() == 0) {
						View footerView = ((LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout_fim, null, false);
						lv.addFooterView(footerView);
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	private void loadElements(int currentPage2) {
		if (!chegouFim)
			new buscaMaisUsers(getActivity(), currentPage2).execute();

	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView v, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (getListView().getLastVisiblePosition() >= getListView().getCount() - 3) {
				currentPage++;
				// load more list items:
				loadElements(currentPage);
			}
		}

	}
}
