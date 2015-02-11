package com.gamfig.monitorabrasil.fragments.listviews;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.PoliticoAdapter;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.politico.PoliticoFactory;
import com.google.gson.Gson;

public class PoliticosFragment extends ListFragment {

	private static final String TAG = "MonitoraBrasil";

	List<Politico> politicos = new ArrayList<Politico>();
	String jsonPoliticos;
	PoliticoAdapter adapterPoliticos = null;
	private List<Politico> listaPoliticos = new ArrayList<Politico>();
	private Bundle data;
	private Bundle mBundle;
	int position;

	public interface SelectionListener {
		public void onItemSelected(int position, ListView l, View view);
	}

	private SelectionListener mCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
	}

	// Note: ListFragments come with a default onCreateView() method.
	// For other Fragments you'll normally implement this method.
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState)

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Check for previously saved state
		if (savedInstanceState == null) {
			Log.e("DEBUG", "onActivityCreated saved is null");
			// if (adapterPoliticos == null)
			// montar de acordo com a casa

		}
		data = getArguments();
		if (mBundle == null) {
			mBundle = data;
			new BuscaPoliticos(mBundle, this).execute();
			setListShown(false);
		} else {
			if (savedInstanceState == null) {
				new BuscaPoliticos(mBundle, this).execute();
				setListShown(false);
			}
		}

		// When using two-pane layout, configure the ListView to highlight the
		// selected list item

		if (isInTwoPaneMode()) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		} else {
			Log.i(TAG, "NAVIGATION_MODE_STANDARD");

			ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        }

	}

	@Override
	public void onResume() {
		super.onResume();
		String casa = this.data.getString("casa");
		if (casa.equals("senado")) {

			getActivity().getActionBar().setTitle("Senadores");
		} else getActivity().getActionBar().setTitle("Deputados Federais");

		if (adapterPoliticos != null) {
			setListAdapter(adapterPoliticos);
			getListView().setSelection(this.position);
			Log.e("DEBUG", "onResume adapter not null");
		}
	}

	public void atualizaAdapter(Bundle bundle) {
		this.data = bundle;
		new BuscaPoliticos(data, this).execute();

	}

	private void mudaTitulo() {
		String casa = this.data.getString("casa");
		if (casa.equals("senado")) {

			getActivity().setTitle("Senadores");
		} else {

			getActivity().setTitle("Deputados Federais");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Gson gson = new Gson();
		savedInstanceState.putString("listaPoliticos", gson.toJson(listaPoliticos));
		Log.e("DEBUG", "onSaveInstanceState");

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");
		// Make sure that the hosting Activity has implemented
		// the SelectionListener callback interface. We need this
		// because when an item in this ListFragment is selected,
		// the hosting Activity's onItemSelected() method will be called.

		try {

			mCallback = (SelectionListener) activity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SelectionListener");
		}

		Log.e("DEBUG", "onAttach");

	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		// Notify the hosting Activity that a selection has been made.
		this.position = position;
		mCallback.onItemSelected(position, l, view);

	}

	// If there is a FeedFragment, then the layout is two-pane
	private boolean isInTwoPaneMode() {

		return getFragmentManager().findFragmentById(R.id.fragment_container) == null;

	}

	public Bundle getmBundle() {
		return mBundle;
	}

	public void setmBundle(Bundle mBundle) {
		this.mBundle = mBundle;
	}

	public class BuscaPoliticos extends AsyncTask<Void, Void, List<Politico>> {

		private PoliticosFragment mActivity;
		ProgressBar pbar;
		PoliticoFactory politicoFactory;

		public BuscaPoliticos(Bundle data, PoliticosFragment politicosFragment) {
			mActivity = politicosFragment;
			politicoFactory = new PoliticoFactory(data, getActivity());
		}

		protected void onPreExecute() {
		}

		@Override
		protected List<Politico> doInBackground(Void... params) {

			return politicoFactory.buscaPoliticos();
		}

		protected void onPostExecute(List<Politico> results) {
			try {
				if (results != null) {

					setListShown(true);
					politicos = results;
					adapterPoliticos = new PoliticoAdapter(mActivity.getActivity(), R.layout.listview_item_politico, politicos);

					mActivity.getListView().setAdapter(adapterPoliticos);
					mudaTitulo();

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}
