/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Fragmente que busca as informacoes dos cards
 * 
 */
package com.gamfig.monitorabrasil.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.activitys.CotaActivity;
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.activitys.PoliticosActivity;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.activitys.ProjetosActivity;
import com.gamfig.monitorabrasil.adapter.ImageAdapter;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.cards.CardFactory;

public class ResumoInicialFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	FragmentManager mFragmentManager;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment newInstance(int sectionNumber) {
		ResumoInicialFragment fragment = new ResumoInicialFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

		return fragment;
	}

	public ResumoInicialFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(PrincipalActivity.TAG, "Fragment = onCreateView");

		View rootView = inflater.inflate(R.layout.resumo_principal, container, false);
		if (savedInstanceState != null) {
			Log.i(PrincipalActivity.TAG, "Fragment = savedInstanceState is not null");
			// monta view offline
		} else {
			montaFlippers(rootView);
		}

		// brn lista de deputados
		Button btnDeputados = (Button) rootView.findViewById(R.id.btnDeputados);
		btnDeputados.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent;
				intent = new Intent();
				intent.putExtra("casa", "camara");
				intent.setClass(getActivity(), PoliticosActivity.class);
				startActivity(intent);

			}
		});

		// btn lista de senadores
		Button btnSenadores = (Button) rootView.findViewById(R.id.btnSenadores);
		btnSenadores.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent;
				intent = new Intent();
				intent.putExtra("casa", "senado");
				intent.setClass(getActivity(), PoliticosActivity.class);
				startActivity(intent);

			}
		});

		// btn lista de proposicao
		Button btnProp = (Button) rootView.findViewById(R.id.btnProp);
		btnProp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreProposicoes(v);
			}
		});

		// btn lista de cotas
		Button btnCotas = (Button) rootView.findViewById(R.id.btnCotas);
		btnCotas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreCotas(v);
			}

		});

		// btn lista de gastam mais
		Button btnGastamMais = (Button) rootView.findViewById(R.id.btnMaisGastam);
		btnGastamMais.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreCotas(v);
			}

		});

		Button btnMaisComentados = (Button) rootView.findViewById(R.id.btnMaisComentados);
		btnMaisComentados.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreProposicoes(v);
			}
		});

		Button btnMaisVotados = (Button) rootView.findViewById(R.id.btnMaisVotados);
		btnMaisVotados.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreProposicoes(v);
			}
		});
		Button btnRecentes = (Button) rootView.findViewById(R.id.btnRecentes);
		btnRecentes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreProposicoes(v);
			}
		});

		Button btnMonitorados = (Button) rootView.findViewById(R.id.btnMonitorados);
		btnMonitorados.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment2open = HomeFragment.newInstance(1);
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.container, fragment2open);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});

		
		return rootView;
	}

	private void abreProposicoes(View v) {
		Intent intent;
		intent = new Intent();
		intent.putExtra("casa", "c");

		Button btn = (Button) v;
		switch (btn.getId()) {
		case R.id.btnProp:
			break;
		case R.id.btnMaisComentados:
			intent.putExtra("filtro", "Projetos mais Comentados");
			break;
		case R.id.btnMaisVotados:
			intent.putExtra("filtro", "Projetos mais Votados");
			break;
		case R.id.btnRecentes:
			break;

		default:
			break;
		}
		intent.setClass(getActivity(), ProjetosActivity.class);
		startActivity(intent);
	}

	private void abreCotas(View v) {

		Intent intent;
		intent = new Intent();
		intent.setClass(getActivity(), CotaActivity.class);
		startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Gson gson = new Gson();
		Log.i(PrincipalActivity.TAG, "Fragment - onSaveInstanceState");

	}

	private void montaFlippers(View rootView) {
		// buscar ultimas atividades

		CardFactory cardFactory = new CardFactory(getActivity().getApplicationContext(), rootView, getFragmentManager());
		cardFactory.makeCard("eventos");
		cardFactory.makeCard("hashtags");
		cardFactory.makeCard("+comentados");
		cardFactory.makeCard("+votados");
		// novos projetos
		cardFactory.makeCard("projetosNovos");
//		// os que mais gastam
		cardFactory.makeCard("+gastam");

		// monitorados
		final ArrayList<Politico> politicoFavoritos = new ArrayList<Politico>(new UserDAO(getActivity()).getPoliticosFavoritos().values());
		Collections.shuffle(politicoFavoritos);
		GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(getActivity(), politicoFavoritos));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				int ultimaFoto = politicoFavoritos.size() - 1;
				// ir para o fragment para add politicoMonitorado
				if (position == ultimaFoto) {
					// mostrar procura
//					getActivity().invalidateOptionsMenu();
					ListaPoliticoMonitoraFragment listaPoliticoMonitora = new ListaPoliticoMonitoraFragment();
					FragmentManager fragmentManager = getActivity().getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.container, listaPoliticoMonitora, "listaPoliticoMonitora");
					fragmentTransaction.addToBackStack("1");
					fragmentTransaction.commit();

				} else {
					// go to congressman�s profile

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

						if (politicoSelecionado.getTipoParlamentar() != null) {
							intent.putExtra("casa", politicoSelecionado.getTipoParlamentar());
						}
						startActivity(intent);
					}

				}
				// execute transaction now
				// TODO ERRO
				// getFragmentManager().executePendingTransactions();
			}
		});
		// TODO o que estao falando

	}

	public void abreEvento() {

	}

}
