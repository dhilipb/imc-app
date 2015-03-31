package com.app.imc.fragment;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.imc.App;
import com.app.imc.Database;
import com.app.imc.R;
import com.app.imc.dao.HistoryDAO;
import com.app.imc.model.Course;
import com.app.imc.model.History;
import com.app.imc.parser.Parser;

public class HistoryFragment extends Fragment {


	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Set up history
        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        FragmentPagerAdapter adapterViewPager = new HistoryPagerAdapter(getFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        
		return view;
	}

	private class HistoryListFragment extends Fragment {

		private final long mCourse;
		
		private View view;
		
		public HistoryListFragment(Course course) {
			mCourse = course.getId();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	        view = inflater.inflate(R.layout.fragment_history_content, container, false);
	        
	        displayHistoryList();
	        
			Database.closeDatabase();
	        return view;
		}
		
		private void displayHistoryList() {
			Context context = App.getContext();
	    	Integer DP_10 = (int) App.dpTopx(10);
	    	Integer DP_20 = (int) App.dpTopx(20);
	    	
	    	LinearLayout list = (LinearLayout) view.findViewById(R.id.historyList);
	    	list.removeAllViews();
	    	
	    	HistoryDAO historyDAO = new HistoryDAO();
	    	List<History> history = historyDAO.retrieveByColumn("course", Long.toString(mCourse));
	    	
	    	for (History hItem : history) {
				LinearLayout row = new LinearLayout(context);
		    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		    	row.setLayoutParams(layoutParams);
		    	row.setWeightSum(16f);
				
				TextView taken = new TextView(context);
				taken.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 6f));
				taken.setText(hItem.getDate().replace(" ", "\n"));
				taken.setPadding(DP_10, DP_10, DP_10, DP_10);
				taken.setGravity(Gravity.LEFT);
				taken.setTextColor(Color.BLACK);
				taken.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				row.addView(taken);

				TextView correct = new TextView(context);
				correct.setText(Integer.toString(hItem.getCorrect()));
				correct.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 3f));
				correct.setBackgroundColor(Color.parseColor(getString(R.string.correct_dark_answer_color)));
				correct.setGravity(Gravity.CENTER);
				correct.setPadding(DP_20, DP_20, DP_20, DP_20);
				correct.setTextColor(Color.WHITE);
				correct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				row.addView(correct);
				
				Integer max_questions = Integer.parseInt(getActivity().getString(R.string.max_questions));
				TextView incorrect = new TextView(context);
				incorrect.setText(Integer.toString((max_questions - hItem.getCorrect())));
				incorrect.setBackgroundColor(Color.parseColor(getString(R.string.incorrect_dark_answer_color)));
				incorrect.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 3f));
				incorrect.setGravity(Gravity.CENTER);
				incorrect.setPadding(DP_20, DP_20, DP_20, DP_20);
				incorrect.setTextColor(Color.WHITE);
				incorrect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				row.addView(incorrect);
				
				TextView percent = new TextView(context);
				Double percentCorrect = (hItem.getCorrect() * 100.0 / max_questions);
				percent.setText(Math.round(percentCorrect) + "%");
				percent.setBackgroundColor(Color.BLACK);
				percent.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 4f));
				percent.setGravity(Gravity.CENTER);
				percent.setPadding(DP_20, DP_20, DP_20, DP_20);
				percent.setTextColor(Color.WHITE);
				percent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				row.addView(percent);
				
				list.addView(row);
			}
		}
		
	}

	private class HistoryPagerAdapter extends FragmentPagerAdapter {

		public HistoryPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return Parser.getCourses().size();
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			return new HistoryListFragment(Parser.getCourses().get(position));
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return Parser.getCourses().get(position).getCourseName().toUpperCase(new Locale("UK"));
		}

	}
}
