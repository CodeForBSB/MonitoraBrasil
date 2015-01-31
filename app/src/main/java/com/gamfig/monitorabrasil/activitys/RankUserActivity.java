package com.gamfig.monitorabrasil.activitys;

import java.util.RandomAccess;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.fragments.listviews.RankUsersFragment;

public class RankUserActivity extends Activity {

	private RankUsersFragment mListFragment; // fragment que carrega a lista de politicos
	private FragmentManager mFragmentManager;
	boolean hideMenu = false;
	boolean showMenuPolitico = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_politicos);
		setTitle("Ranking Monitora, Brasil!");
		if (!isInTwoPaneMode()) {

			mListFragment = new RankUsersFragment();

			// TODO 1 - add the FriendsFragment to the fragment_container
			mFragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.fragment_container, mListFragment);
			fragmentTransaction.commit();

		} else {

			// Otherwise, save a reference to the FeedFragment for later use

			// mFichaFragment = (PoliticoDetalheFragment) getFragmentManager().findFragmentById(R.id.politico_frag);
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		Log.i(PrincipalActivity.TAG, "Activity - onResume");
	}

	private boolean isInTwoPaneMode() {

		return findViewById(R.id.fragment_container) == null;

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// getMenuInflater().inflate(R.menu.lista_politicos, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);

	}

}
