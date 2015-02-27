package com.gamfig.monitorabrasil.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.adapter.ImageAdapter;
import com.gamfig.monitorabrasil.classes.Politico;

public class HomeFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	protected static final String EXTRA_RES_ID = "POS";
	FragmentManager mFragmentManager;

	// Appnext appnext;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment newInstance(int sectionNumber) {
		HomeFragment fragment = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

		return fragment;
	}

	public HomeFragment() {
	}

	List<Politico> politicoFavoritos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(PrincipalActivity.TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		// botao compartilhar
		Button btnCompartilhar = (Button) rootView.findViewById(R.id.btnCompartilhar);
		btnCompartilhar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_compartilhe));
				sendIntent.setType("text/plain");
				startActivity(sendIntent);

			}
		});

		// buscar os deputados salvos

		politicoFavoritos = new ArrayList<Politico>(new UserDAO(getActivity()).getPoliticosFavoritos().values());
		// if (politicoFavoritos == null) {
		// politicoFavoritos = new ArrayList<Politico>();
		// }
		// final List<Politico> politicoFavoritos = tempPF;
		GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

		if (null != politicoFavoritos) {
			gridview.setAdapter(new ImageAdapter(getActivity(), politicoFavoritos));

			gridview.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {



						Politico politicoSelecionado = politicoFavoritos.get(position);
						if (null != politicoSelecionado) {
							// call activity of profile of the congressman

							// analytics

							Intent intent = new Intent();
							intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setClass(getActivity(), FichaActivity.class);
							intent.putExtra("idPolitico", politicoSelecionado.getIdCadastro());
							intent.putExtra("twitter", politicoSelecionado.getTwitter());
							intent.putExtra("nome", politicoSelecionado.getNome());

							if (politicoSelecionado.getTipo() != null) {
								intent.putExtra("casa", politicoSelecionado.getTipo());
							}
							startActivity(intent);
						}

					}
					// execute transaction now
					// TODO ERRO
					// getFragmentManager().executePendingTransactions();

			});

		}
		//
		// appnext = new Appnext(rootView.getContext());
		// // appnext.addMoreAppsLeft("6ec7bcbb-5b24-41c1-a6d9-79d442578e1d");
		// appnext.setAppID("6ec7bcbb-5b24-41c1-a6d9-79d442578e1d"); // Set your AppID
		// appnext.showBubble(); // show the interstitial

		return rootView;
	}

	// tratar o select do evento callback
	public void onItemSelected(int position) {

		Log.i(PrincipalActivity.TAG, "Entered onItemSelected(" + position + ")");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((PrincipalActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		Log.i(PrincipalActivity.TAG, "onAttach()");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(PrincipalActivity.TAG, "Entered onActivityCreated()");

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		Log.i(PrincipalActivity.TAG, "Entered home onPrepareOptionsMenu()");
		super.onPrepareOptionsMenu(menu);
	}

}
