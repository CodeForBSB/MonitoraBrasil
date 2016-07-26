package com.gamfig.monitorabrasil.fragments.listviews;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.gamfig.monitorabrasil.DAO.DataBaseHelper;
import com.gamfig.monitorabrasil.DAO.PoliticoDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.PoliticoAdapter;
import com.gamfig.monitorabrasil.classes.Politico;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoliticosFragment extends ListFragment {

	private static final String TAG = "MonitoraBrasil";

	List<Politico> politicos = new ArrayList<Politico>();
	String jsonPoliticos;
	PoliticoAdapter adapterPoliticos = null;
	private List<Politico> listaPoliticos = new ArrayList<Politico>();
	private Bundle data;
	private Bundle mBundle;
	int position;
    private DataBaseHelper dbh;
    private PoliticoDAO politicoDAO;

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
        dbh = new DataBaseHelper(getActivity());
        data = getArguments();
        if (mBundle == null) {
            mBundle = data;
            setListShown(false);
        }

        try {
            politicoDAO = new PoliticoDAO(dbh.getConnectionSource());
            Map<String,Object> values = new HashMap<String,Object>();
            values.put("tipo",mBundle.getString("casa"));
            if(mBundle.get("uf")!= null)
                if(!mBundle.getString("uf").equals("Brasil")){
                    values.put("uf",mBundle.getString("uf"));
                }
            if(mBundle.get("partido")!= null)
                if(!mBundle.getString("partido").equals("Todos os Partidos")){
                    values.put("siglaPartido",mBundle.getString("partido"));
                }

            politicos = politicoDAO.queryForFieldValues(values);
            adapterPoliticos = new PoliticoAdapter(getActivity(), R.layout.listview_item_politico, politicos);

            getListView().setAdapter(adapterPoliticos);
            setListShown(true);

        }
        catch (SQLException e){
			System.out.println(e.toString());
        }

        /*

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
*/
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
		if (casa.equals("s")) {

			getActivity().getActionBar().setTitle("Senadores");
		} else getActivity().getActionBar().setTitle("Deputados Federais");

		if (adapterPoliticos != null) {
			setListAdapter(adapterPoliticos);
			getListView().setSelection(this.position);
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


	public void setmBundle(Bundle mBundle) {
		this.mBundle = mBundle;
	}



}
