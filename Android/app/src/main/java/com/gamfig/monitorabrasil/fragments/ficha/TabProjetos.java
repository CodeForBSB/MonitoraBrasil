package com.gamfig.monitorabrasil.fragments.ficha;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.fragments.listviews.ProjetosFragment;


public class TabProjetos extends TabFactory {

	public TabProjetos() {

	}

	public void montaLayout() {
		//carregar o fragment
		ProjetosFragment fragment2open = new ProjetosFragment();
		fragment2open.setArguments(getBundle());
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.listviewProjetos, fragment2open);
		//fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		

	}

	

}
