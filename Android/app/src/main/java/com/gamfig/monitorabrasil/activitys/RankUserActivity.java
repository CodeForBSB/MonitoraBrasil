package com.gamfig.monitorabrasil.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.fragments.listviews.RankUsersFragment;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

    public class RankUserActivity extends ActionBarActivity implements MaterialTabListener {

//	private RankUsersFragment mListFragment; // fragment que carrega a lista de politicos
	private FragmentManager mFragmentManager;
	boolean hideMenu = false;
	boolean showMenuPolitico = false;
    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rank_user);

        Toolbar toolbar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager );
// init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
// when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });
// insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
    }
    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabReselected(MaterialTab tab) {

    }
    @Override
    public void onTabUnselected(MaterialTab tab) {
    }
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public Fragment getItem(int num) {
            RankUsersFragment fragment = new RankUsersFragment();
            if(num==0){
                fragment.setTipo("todos");
            }else{
                fragment.setTipo("amigos");
            }
            return fragment;
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence titulo=null;
            switch (position){
                case 0:
                    titulo = "Ranking Geral";
                    break;
                case 1:
                    titulo = "Ranking Amigos";
                    break;
            }
            return titulo;
        }
    }
}