package com.app.imc.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import com.app.imc.R;
import com.app.imc.callback.NavigationDrawerCallbacks;
import com.app.imc.fragment.HistoryFragment;
import com.app.imc.fragment.NavigationDrawerFragment;
import com.app.imc.fragment.QuizFragment;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
        		getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
	public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }
    
    @Override
    public void onNavigationDrawerItemSelected(int position) {
    	
    	if (position < 2) {
    		getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new QuizFragment(position))
                .commit();
    	} else if (position == 2) {
    		getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new HistoryFragment())
            .commit();
    	}
        restoreActionBar();
        
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

}
