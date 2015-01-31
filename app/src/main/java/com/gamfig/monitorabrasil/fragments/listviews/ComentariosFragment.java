package com.gamfig.monitorabrasil.fragments.listviews;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.adapter.ComentarioAdapter;
import com.gamfig.monitorabrasil.classes.Comentario;

public class ComentariosFragment extends ListFragment {

	private Bundle data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Check for previously saved state
		if (savedInstanceState == null) {
			data = getArguments();
			// busca comentarios

		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// try {
		//
		// mCallback = (SelectionListener) activity;
		//
		// } catch (ClassCastException e) {
		// throw new ClassCastException(activity.toString() + " must implement SelectionListener");
		// }
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// When using two-pane layout, configure the ListView to highlight the
		// selected list item
		new BuscaComentarios(data.getInt("idProposta")).execute();

	}

	/**
	 * 
	 * Busca os comentarios do projeto
	 * 
	 */
	public class BuscaComentarios extends AsyncTask<Void, Void, ArrayList<Comentario>> {
		int idProposta;

		public BuscaComentarios(int idProposta) {
			this.idProposta = idProposta;
		}

		@Override
		protected ArrayList<Comentario> doInBackground(Void... params) {

			return new DeputadoDAO().buscaComentarios(String.valueOf(idProposta),"proj");
		}

		protected void onPostExecute(ArrayList<Comentario> comentarios) {
			try {
				setListShown(true);
				if (comentarios != null) {

					ComentarioAdapter adapter = new ComentarioAdapter(getActivity(), R.layout.listview_item_comentario, comentarios, new UserDAO(getActivity()).buscaLikes());
					setListAdapter(adapter);
					getListView().setSelection(adapter.getCount() - 1);
					getListView().setOnTouchListener(new OnTouchListener() {
						// Setting on Touch Listener for handling the touch inside ScrollView
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// Disallow the touch request for parent scroll on touch of child view
							v.getParent().requestDisallowInterceptTouchEvent(true);
							return false;
						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
}
