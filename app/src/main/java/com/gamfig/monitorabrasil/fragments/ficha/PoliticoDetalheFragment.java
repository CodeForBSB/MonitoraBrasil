package com.gamfig.monitorabrasil.fragments.ficha;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.tabs.SlidingTabLayout;

public final class PoliticoDetalheFragment extends Fragment {
	public FragmentManager fm;
	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detalhe_politico, container, false);

		return rootView;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// BEGIN_INCLUDE (setup_viewpager)
		// Get the ViewPager and set it's PagerAdapter so that it can display items
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

		Bundle bundle = this.getArguments();
		if (bundle.containsKey("casa")) {
			if (bundle.getString("casa").equals("senado") || bundle.getString("casa").equals("s")) {
				mViewPager.setAdapter(new SenadorAdapter());
			} else {
				mViewPager.setAdapter(new DeputadoAdapter());
			}
		}

		// END_INCLUDE (setup_viewpager)

		// BEGIN_INCLUDE (setup_slidingtablayout)
		// Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
		// it's PagerAdapter set.
		mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setViewPager(mViewPager);
		// END_INCLUDE (setup_slidingtablayout)
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Bundle bundle = this.getArguments();
		// fm = getActivity().getFragmentManager();
		// int idPolitico = bundle.getInt("idPolitico");
		//
		// bundle.putInt("idPolitico", idPolitico);
		//
		// ActionBar tabBar = getActivity().getActionBar();
		// tabBar.removeAllTabs();
		// tabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// if (bundle.containsKey("casa")) {
		// if (bundle.getString("casa").equals("senado") || bundle.getString("casa").equals("s")) {
		// montaFichaSenador(bundle, tabBar);
		// } else {
		// montaFichaDeputado(bundle, tabBar);
		// }
		// } else {
		// montaFichaDeputado(bundle, tabBar);
		// }

	}

	class SenadorAdapter extends PagerAdapter {

		/**
		 * @return the number of pages to display
		 */
		@Override
		public int getCount() {
			return 4;
		}

		/**
		 * @return true if the value returned from {@link #instantiateItem(android.view.ViewGroup, int)} is the same object as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
		 */
		@Override
		public boolean isViewFromObject(View view, Object o) {
			return o == view;
		}

		// BEGIN_INCLUDE (pageradapter_getpagetitle)
		/**
		 * Return the title of the item at {@code position}. This is important as what this method returns is what is displayed in the {@link SlidingTabLayout}.
		 * <p>
		 * Here we construct one using the position value, but for real application the title should refer to the item's contents.
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			String titulo = null;
			switch (position) {
			case 0:
				titulo = "Dados";
				break;
			case 1:
				titulo = "Bens";
				break;
			case 2:
				titulo = "#Hashtag";
				break;
			case 3:
				titulo = "Projetos";
				break;

			default:
				break;
			}
			return titulo;
		}

		// END_INCLUDE (pageradapter_getpagetitle)

		/**
		 * Instantiate the {@link android.view.View} which should be displayed at {@code position}. Here we inflate a layout from the apps resources and then change the text view to signify the
		 * position.
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// Inflate a new layout from our resources
			View view = getActivity().getLayoutInflater().inflate(getLayout(position), container, false);
			// Add the newly created View to the ViewPager
			container.addView(view);

			// montar o layout
			montaLayout(position);

			// Retrieve a TextView from the inflated View, and update it's text
			// TextView title = (TextView) view.findViewById(R.id.item_title);
			// title.setText(String.valueOf(position + 1));

			// Return the View
			return view;
		}

		private void montaLayout(int position) {
			getArguments().putString("casa", "s");
			TabFactory tab = new TabFactory(getActivity(), getArguments());

			switch (position) {
			case 0:
				tab.criaTab(new FichaSenador());
				break;
			case 1:
				tab.criaTab(new BensFragment());
				break;
			case 2:
				tab.criaTab(new HashtagFragment());
				break;
			case 3:
				tab.criaTab(new TabProjetos());
				break;

			default:
				break;
			}

		}

		private int getLayout(int position) {
			int layout = R.layout.item_imagem;
			switch (position) {
			case 0:
				layout = R.layout.fragment_ficha_deputado;
				break;
			case 1:
				layout = R.layout.fragment_ficha_bens;
				break;
			case 2:
				layout = R.layout.fragment_hashtag;
				break;
			case 3:
				layout = R.layout.tab_projetos;
				break;

			default:
				break;
			}
			return layout;
		}

		/**
		 * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the {@link android.view.View}.
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	class DeputadoAdapter extends PagerAdapter {

		/**
		 * @return the number of pages to display
		 */
		@Override
		public int getCount() {
			return 7;
		}

		/**
		 * @return true if the value returned from {@link #instantiateItem(android.view.ViewGroup, int)} is the same object as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
		 */
		@Override
		public boolean isViewFromObject(View view, Object o) {
			return o == view;
		}

		// BEGIN_INCLUDE (pageradapter_getpagetitle)
		/**
		 * Return the title of the item at {@code position}. This is important as what this method returns is what is displayed in the {@link SlidingTabLayout}.
		 * <p>
		 * Here we construct one using the position value, but for real application the title should refer to the item's contents.
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			String titulo = null;
			switch (position) {
			case 0:
				titulo = "Dados";
				break;
			case 1:
				titulo = "Bens";
				break;
			case 2:
				titulo = "#Hashtag";
				break;
			case 3:
				titulo = "Presen�a";
				break;
			case 4:
				titulo = "Projetos";
				break;
			case 5:
				titulo = "Cota Parlamentar";
				break;
			case 6:
				titulo = "Doa��es de Campanha";
				break;

			default:
				break;
			}
			return titulo;
		}

		// END_INCLUDE (pageradapter_getpagetitle)

		/**
		 * Instantiate the {@link android.view.View} which should be displayed at {@code position}. Here we inflate a layout from the apps resources and then change the text view to signify the
		 * position.
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// Inflate a new layout from our resources
			View view = getActivity().getLayoutInflater().inflate(getLayout(position), container, false);
			// Add the newly created View to the ViewPager
			container.addView(view);

			// montar o layout
			montaLayout(position);

			// Retrieve a TextView from the inflated View, and update it's text
			// TextView title = (TextView) view.findViewById(R.id.item_title);
			// title.setText(String.valueOf(position + 1));

			// Return the View
			return view;
		}

		private void montaLayout(int position) {
			TabFactory tab = new TabFactory(getActivity(), getArguments());

			switch (position) {
			case 0:
				tab.criaTab(new FichaDeputado());
				break;
			case 1:
				tab.criaTab(new BensFragment());
				break;
			case 2:
				tab.criaTab(new HashtagFragment());
				break;
			case 3:
				tab.criaTab(new PresencaFragment());
				break;
			case 4:
				tab.criaTab(new TabProjetos());
				break;
			case 5:
				tab.criaTab(new CotaFichaFragment());
				break;
			case 6:
				tab.criaTab(new DoacaoFragment());
				break;
			default:
				break;
			}

		}

		private int getLayout(int position) {
			int layout = R.layout.item_imagem;
			switch (position) {
			case 0:
				layout = R.layout.fragment_ficha_deputado;
				break;
			case 1:
				layout = R.layout.fragment_ficha_bens;
				break;
			case 2:
				layout = R.layout.fragment_hashtag;
				break;
			case 3:
				layout = R.layout.fragment_ficha_presenca;
				break;
			case 4:
				layout = R.layout.tab_projetos;
				break;
			case 5:
				layout = R.layout.fragment_cotas_parlamentar;
				break;
			case 6:
				layout = R.layout.fragment_doacao;
				break;

			/*
			 * case 3: layout = R.layout.fragment_ficha_deputado; break; case 4: layout = R.layout.fragment_ficha_deputado; break; case 5: layout =
			 * R.layout.fragment_ficha_deputado; break; case 6: layout = R.layout.fragment_ficha_deputado; break;
			 */

			default:
				break;
			}
			return layout;
		}

		/**
		 * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the {@link android.view.View}.
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	public void updateFeedDisplay(int position) {
		// TODO atualizar qual item foi selecionado

	}

	/*
	 * private void montaFichaSenador(Bundle bundle, ActionBar tabBar) { // Inicia a primeira tab FichaSenador fichaSenador = new FichaSenador(); fichaSenador.setArguments(bundle);
	 * 
	 * // tab de projetos ProjetosFragment projetosFragment = new ProjetosFragment(); bundle.putString("casa", "s"); projetosFragment.setArguments(bundle);
	 * 
	 * // tab dos hashes HashtagFragment hashtag = new HashtagFragment(); hashtag.setArguments(bundle);
	 * 
	 * // tab dos bens BensFragment bens = new BensFragment(); bens.setArguments(bundle);
	 * 
	 * // tab de cotas CotaFichaFragment cota = new CotaFichaFragment(); cota.setArguments(bundle);
	 * 
	 * tabBar.addTab(tabBar.newTab().setText("Dados").setTabListener(new TabListener(fichaSenador, fm))); tabBar.addTab(tabBar.newTab().setText("Bens").setTabListener(new
	 * TabListener(bens, fm))); tabBar.addTab(tabBar.newTab().setText("Projetos").setTabListener(new TabListener(projetosFragment, fm))); // hashtag tab Tab tabHashtag =
	 * tabBar.newTab(); tabHashtag.setText("#Hashtag").setTabListener(new TabListener(hashtag, fm)); tabBar.addTab(tabHashtag);
	 * 
	 * if (bundle.getString("hashtag") != null) tabBar.selectTab(tabHashtag); // Tab tab = tabBar.newTab(); // tab.setText("Cota Parlamentar").setTabListener(new TabListener(cota,
	 * fm)); // tabBar.addTab(tab); // tabBar.addTab(tabBar.newTab().setText("Doa��es de Campanha").setTabListener(new TabListener(doacao, fm))); // if (bundle.getString("cota") !=
	 * null) // tabBar.selectTab(tab); // tabBar.addTab(tabBar.newTab().setText("V�deos").setTabListener(new TabListener(fichaDeputado))); }
	 * 
	 * private void montaFichaDeputado(Bundle bundle, ActionBar tabBar) { // Inicia a primeira tab // FichaDeputado fichaDeputado = new FichaDeputado(); //
	 * fichaDeputado.setArguments(bundle);// passando o position
	 * 
	 * // tab de presencas PresencaFragment presenca = new PresencaFragment(); presenca.setArguments(bundle);
	 * 
	 * // tab de projetos ProjetosFragment projetosFragment = new ProjetosFragment(); projetosFragment.setArguments(bundle);
	 * 
	 * // tab dos hashes HashtagFragment hashtag = new HashtagFragment(); hashtag.setArguments(bundle);
	 * 
	 * // tab de cotas CotaFichaFragment cota = new CotaFichaFragment(); cota.setArguments(bundle);
	 * 
	 * // tab doacoes parecido com cotas DoacaoFragment doacao = new DoacaoFragment(); doacao.setArguments(bundle); // tab videos a fazer
	 * 
	 * // tab dos bens BensFragment bens = new BensFragment(); bundle.putString("casa", "c"); bens.setArguments(bundle);
	 * 
	 * // tabBar.addTab(tabBar.newTab().setText("Dados").setTabListener(new TabListener(fichaDeputado, fm))); tabBar.addTab(tabBar.newTab().setText("Bens").setTabListener(new
	 * TabListener(bens, fm)));
	 * 
	 * // hashtag tab Tab tabHashtag = tabBar.newTab(); tabHashtag.setText("#Hashtag").setTabListener(new TabListener(hashtag, fm)); tabBar.addTab(tabHashtag);
	 * 
	 * tabBar.addTab(tabBar.newTab().setText("Presen�a").setTabListener(new TabListener(presenca, fm))); tabBar.addTab(tabBar.newTab().setText("Projetos").setTabListener(new
	 * TabListener(projetosFragment, fm)));
	 * 
	 * // cota tab Tab tab = tabBar.newTab(); tab.setText("Cota Parlamentar").setTabListener(new TabListener(cota, fm)); tabBar.addTab(tab);
	 * 
	 * tabBar.addTab(tabBar.newTab().setText("Doa��es de Campanha").setTabListener(new TabListener(doacao, fm))); if (bundle.getString("cota") != null) tabBar.selectTab(tab); if
	 * (bundle.getString("hashtag") != null) tabBar.selectTab(tabHashtag); // tabBar.addTab(tabBar.newTab().setText("V�deos").setTabListener(new TabListener(fichaDeputado))); }
	 * 
	 * public static class TabListener implements ActionBar.TabListener { private final Fragment mFragment; private Fragment mFrangmentAnterior; FragmentManager fm;
	 * 
	 * public TabListener(Fragment fragment, FragmentManager fm) { mFragment = fragment; this.fm = fm; }
	 * 
	 * @Override public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) { if (null != mFragment) { FragmentTransaction ft = fm.beginTransaction(); //
	 * ft.replace(R.id.detalhe_politico, mFragment); // ft.addToBackStack(null); ft.commit();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * @Override public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) { if (null != mFragment) { mFrangmentAnterior = mFragment; FragmentTransaction ft =
	 * fm.beginTransaction(); ft.remove(mFragment); }
	 * 
	 * } }
	 */

}
