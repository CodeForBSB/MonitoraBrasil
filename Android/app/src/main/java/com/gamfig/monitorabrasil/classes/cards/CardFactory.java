package com.gamfig.monitorabrasil.classes.cards;

import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Projeto;

import java.util.List;

public class CardFactory {
	private ViewFlipper viewFlipper;
	private Context fragmentActivity;
	public Animation slide_in_left;
	public Animation slide_out_left;
	private View rootView;
	private int tempoTransicao = 4000;
	private FragmentManager fragmentManager;

	public CardFactory(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		this.fragmentActivity = fragmentActivity;
		this.rootView = rootView;
		slide_in_left = AnimationUtils.loadAnimation(this.fragmentActivity, R.anim.abc_fade_in);
		slide_out_left = AnimationUtils.loadAnimation(this.fragmentActivity, R.anim.abc_fade_out);

	}

	public void buscaInfos() {
	}

	public void makeCard(String tpCartao) {
		if (tpCartao.equals("eventos")) {
			new CardUltimosEventos(fragmentActivity, rootView, fragmentManager).buscaInfos();
		}
		if (tpCartao.equals("hashtags")) {
			new CardHashtag(fragmentActivity, rootView, fragmentManager).buscaInfos();
		}

		if (tpCartao.equals("+gastam")) {
			new CardMaisGastam(fragmentActivity, rootView, fragmentManager).buscaInfos();
		}


	}

    public void makeCard(String tpCartao, List<Projeto> projetos) {
        if (tpCartao.equals("projetosNovos")) {
            new CardProjetosRecentes(fragmentActivity, rootView, fragmentManager,projetos).buscaInfos();
        }
        if (tpCartao.equals("projetosComentados")) {
            new CardProjetosMaisComentados(fragmentActivity, rootView, fragmentManager,projetos).buscaInfos();
        }
        if (tpCartao.equals("projetosVotados")) {
            new CardProjetosMaisVotados(fragmentActivity, rootView, fragmentManager,projetos).buscaInfos();
        }
    }

	public void montaViewFlipper(int viewflipper1) {
		viewFlipper = (ViewFlipper) rootView.findViewById(viewflipper1);
		viewFlipper.setInAnimation(slide_in_left);
		viewFlipper.setOutAnimation(slide_out_left);
		viewFlipper.setFlipInterval(this.tempoTransicao);

	}

	public CharSequence formataTempo(String tempo) {
		int hora = Integer.valueOf(tempo.substring(0, 2));
		if (hora > 0 && hora < 24) {
			return String.valueOf(hora) + "hr";
		} else {
			if (hora == 0) {
				int min = Integer.valueOf(tempo.substring(3, 5));
				if (min == 0) {
					return "agora";
				} else {
					return String.valueOf(min) + " min";
				}
			} else {
				return "+ de 1 dia";
			}
		}
	}

	public ViewFlipper getView() {
		return viewFlipper;
	}

	public void setView(ViewFlipper view) {
		this.viewFlipper = view;
	}

	public Context getFragmentActivity() {
		return fragmentActivity;
	}

	public void setFragmentActivity(Context fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
	}

	public int getTempoTransicao() {
		return tempoTransicao;
	}

	public void setTempoTransicao(int tempoTransicao) {
		this.tempoTransicao = tempoTransicao;
	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public void setFragmentManager(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}


}
