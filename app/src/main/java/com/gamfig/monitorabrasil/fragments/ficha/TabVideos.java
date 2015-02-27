package com.gamfig.monitorabrasil.fragments.ficha;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.fragments.listviews.ProjetosFragment;
import com.gamfig.monitorabrasil.fragments.listviews.VideosFragment;


public class TabVideos extends TabFactory {

	public TabVideos() {

	}

	public void montaLayout() {
		//carregar o fragment
        VideosFragment fragment2open = new VideosFragment();
		fragment2open.setArguments(getBundle());
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.listviewVideos, fragment2open);
		//fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		

	}

	

}
