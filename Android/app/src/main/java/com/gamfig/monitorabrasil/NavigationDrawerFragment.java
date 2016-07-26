package com.gamfig.monitorabrasil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.activitys.LoginRedeActivity;
import com.gamfig.monitorabrasil.adapter.DrawerAdapter;
import com.gamfig.monitorabrasil.classes.ItemDrawer;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.dialog.DialogGostou;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer. See the <a
 * href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"> design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
@SuppressLint("NewApi")
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerListView;
	private View mFragmentContainerView;
	private View headerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mDrawerListView = (ExpandableListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

		// add o header
		headerView = montaHeader();
		mDrawerListView.addHeaderView(headerView);

		List<ItemDrawer> listDataHeader = new ArrayList<ItemDrawer>();
		HashMap<ItemDrawer, List<ItemDrawer>> listDataChild = new HashMap<ItemDrawer, List<ItemDrawer>>();

		listDataHeader.add(new ItemDrawer(getString(R.string.title_section1), R.drawable.ic_action_home));

		// menu camara
		ItemDrawer itemCamara = new ItemDrawer("Câmara", R.drawable.ic_action_politico);
		listDataHeader.add(itemCamara);

		List<ItemDrawer> itensCamara = new ArrayList<ItemDrawer>();
		itensCamara.add(new ItemDrawer("Deputados", R.drawable.urna));
		itensCamara.add(new ItemDrawer("Cota Parlamentar", R.drawable.urna));
		itensCamara.add(new ItemDrawer("Vote nos Projetos", R.drawable.urna));

		listDataChild.put(itemCamara, itensCamara);

		// menu camara
		ItemDrawer itemSenado = new ItemDrawer("Senado", R.drawable.ic_action_politico);
		listDataHeader.add(itemSenado);

		List<ItemDrawer> itensSenado = new ArrayList<ItemDrawer>();
		itensSenado.add(new ItemDrawer("Senadores", R.drawable.urna));
		// itensSenado.add(new ItemDrawer("Cota Parlamentar", R.drawable.urna));
        itensSenado.add(new ItemDrawer("Vote nos Projetos", R.drawable.urna));
        itensSenado.add(new ItemDrawer("Tv Senado", R.drawable.urna));

		listDataChild.put(itemSenado, itensSenado);

		// O que estao falando
		listDataHeader.add(new ItemDrawer(getString(R.string.title_section6), R.drawable.ic_action_falando));

		// Pontuacao
		listDataHeader.add(new ItemDrawer("Pontuação", R.drawable.ic_action_rank));

		// Ranking
		listDataHeader.add(new ItemDrawer("Ranking", R.drawable.rank));

		// Gostou?
		listDataHeader.add(new ItemDrawer(getString(R.string.title_section7), R.drawable.ic_action_gostou));

		DrawerAdapter adpater = new DrawerAdapter(getActionBar().getThemedContext(), R.layout.listview_item_drawer, listDataHeader, listDataChild);

		mDrawerListView.setAdapter(adpater);
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

		mDrawerListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				if (groupPosition == 0 || groupPosition == 3 || groupPosition == 4|| groupPosition == 5) {
					selectItem(groupPosition);
				} else {
					if (groupPosition == 6) {
						// mostra o dialog
						DialogFragment dialog = new DialogGostou();
						dialog.show(getFragmentManager(), "dialog");
					}
				}
				return false;
			}
		});

		mDrawerListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int posicaoGrupo, int posicaoChild, long arg4) {
				// 1 - eleicoes
				// 10 - presidente
				// 11 - governador
				// 12 - senador
				// 13 - dep. fed
				// 14 - dep est

				// 2 - camara
				// 20 - deputados
				// 21 - cota
				// 22 - projetos

				// 3- senado
				// 30 senadores
				// 31 - projetos
				int parametro = Integer.valueOf(String.valueOf(posicaoGrupo) + String.valueOf(posicaoChild));

				selectItem(parametro);
				return false;
			}
		});

		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), LoginRedeActivity.class);
					startActivity(intent);


				}
			}
		});

		// mDrawerListView.setGroupIndicator(d);
		return mDrawerListView;
	}

	public int GetPixelFromDips(float pixels) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);

	}

	private View montaHeader() {
		View headerView = View.inflate(this.getActivity().getApplicationContext(), R.layout.header_view, null);
		// busca usuario
		Usuario user = new UserDAO(getActivity()).getUserCompleto();
		// busca o nome
		TextView nome = (TextView) headerView.findViewById(R.id.nome);
		nome.setText(user.getNome());
		nome.setTextColor(Color.BLACK);

		// pontos
		TextView txtPontos = (TextView) headerView.findViewById(R.id.txtPontos);
		txtPontos.setText("Pontos: " + String.valueOf(user.getPontos()));

		// comentarios e votos
		TextView txtComentarios = (TextView) headerView.findViewById(R.id.txtComentarios);
		txtComentarios.setText("Comentários: " + String.valueOf(user.getNrComentarios()) + " Votos: " + String.valueOf(user.getNrVotos()));

        // busca a foto
        ImageView foto = (ImageView) headerView.findViewById(R.id.foto);
        user.carregaFoto(foto,"square");
		return headerView;
	}

	private void atualizaHeader() {
		// busca usuario
		Usuario user = new UserDAO(getActivity()).getUserCompleto();
		// busca o nome
		TextView nome = (TextView) headerView.findViewById(R.id.nome);
		nome.setText(user.getNome());
		nome.setTextColor(Color.BLACK);

		// pontos
		TextView txtPontos = (TextView) headerView.findViewById(R.id.txtPontos);
		txtPontos.setText("Pontos: " + String.valueOf(user.getPontos()));

		// comentarios e votos
		TextView txtComentarios = (TextView) headerView.findViewById(R.id.txtComentarios);
		txtComentarios.setText("Comentários: " + String.valueOf(user.getNrComentarios()) + " Votos: " + String.valueOf(user.getNrVotos()));

		// busca a foto
		ImageView foto = (ImageView) headerView.findViewById(R.id.foto);
        user.carregaFoto(foto, "square");

		return;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
		R.string.navigation_drawer_close /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				atualizaHeader();
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { // If the drawer is open, show the global app actions in the action bar. See also //
	 * showGlobalContextActionBar, which controls the top-left area of the action bar. if (mDrawerLayout != null && isDrawerOpen()) { inflater.inflate(R.menu.principal, menu);
	 * 
	 * showGlobalContextActionBar(); } super.onCreateOptionsMenu(menu, inflater);
	 * 
	 * }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// if (item.getItemId() == R.id.action_example) {
		// Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
		// return true;
		// }

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}
