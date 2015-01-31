package com.gamfig.monitorabrasil.fragments.ficha;

import android.app.Activity;
import android.os.Bundle;

public class TabFactory {

	private Activity activity;
	private Bundle bundle;
	private TabFactory tab;

	public TabFactory() {
	};

	public TabFactory(Activity activity2, Bundle arguments) {
		this.activity = activity2;
		this.bundle = arguments;
	}

	public void criaTab(TabFactory tab) {
		this.tab = tab;
		this.tab.setActivity(getActivity());
		this.tab.setBundle(getBundle());
		this.tab.montaLayout();

	}

	public void montaLayout() {

	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
}
