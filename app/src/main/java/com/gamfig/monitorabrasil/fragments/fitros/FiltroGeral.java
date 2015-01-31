/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Fragment usado para carregar o layout de um filtro especifico
 * passa como argumento qual layout vai ser carregado
 * hoje no filtro de politico e projetos
 * 
 */
package com.gamfig.monitorabrasil.fragments.fitros;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FiltroGeral extends Fragment {
	private int layout;
	public FiltroGeral(int layout) {
		this.layout=layout;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(this.layout, container, false);

		return rootView;
	}

}
