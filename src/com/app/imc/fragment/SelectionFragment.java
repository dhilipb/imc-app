package com.app.imc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.imc.R;
import com.app.imc.activity.MainActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Select Unit 1
        Button bSelectUnit1 = (Button) view.findViewById(R.id.bSelectUnit1);
        bSelectUnit1.setText(Html.fromHtml(getString(R.string.unit1_button)));

        bSelectUnit1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.onNavigationDrawerItemSelected(0);
			}
		});
        
        
        // Select Unit 2
        Button bSelectUnit2 = (Button) view.findViewById(R.id.bSelectUnit2);
        bSelectUnit2.setText(Html.fromHtml(getString(R.string.unit2_button)));

        bSelectUnit2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.onNavigationDrawerItemSelected(1);
			}
		});
        
        
        return view;
    }
    
}
