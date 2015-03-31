package com.app.imc.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.jsoup.helper.StringUtil;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.imc.App;
import com.app.imc.R;
import com.app.imc.dao.HistoryDAO;
import com.app.imc.model.Course;
import com.app.imc.model.History;
import com.app.imc.model.Option;
import com.app.imc.model.Question;
import com.app.imc.parser.Parser;

public class QuizFragment extends Fragment {

	private View view;
	
	private final Course mCourse;
	private final Integer mCourseNumber;
	private final List<Question> mQuestionsHistory;
	private Integer mQuestionsPointer = -1;
	private Integer mQuestionsCorrect = 0;
	
	private Integer MAX_QUESTIONS;
	
	 /**
     * Returns a new instance of this fragment for the given section
     * number.
	 * @throws Exception
     */
    public QuizFragment(int courseNumber) {
    	mQuestionsHistory = new ArrayList<Question>();
    	mCourseNumber = courseNumber;
    	
    	List<Course> courses = Parser.getCourses();
    	if (courseNumber > courses.size()) {
    		Log.e("QuizFragment", "Course Number invalid");
    		throw new RuntimeException("Course number invalid");
    	} else {
    		mCourse = courses.get(courseNumber);
    	}
    }
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        this.view = view;
        
        MAX_QUESTIONS = Integer.parseInt(getActivity().getString(R.string.max_questions));
        
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(mCourse.getCourseName() + " - " + mCourse.getLongName());
        
        displayQuestion(getNextQuestion());
        setupListeners();
        
        return view;
	}
	
	private void setupListeners() {

        // Next question
        Button bNextQuestion = (Button) view.findViewById(R.id.bNextQuestion);
        bNextQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        displayQuestion(getNextQuestion());
			}
		});

        // Previous Question
        Button bPrevQuestion = (Button) view.findViewById(R.id.bPrevQuestion);
        bPrevQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Question question = getPrevQuestion();
				
				if (question != null) {
					displayQuestion(question);
				}
			}
		});
        
	}
	
	/*
	 * Display a given question
	 */
	private void displayQuestion(Question question) {
	    if (question == null) {
	    	displayFinish();
	    	return;
	    }
		
	    ActionBar actionBar = getActivity().getActionBar();
	    actionBar.setTitle(mCourse.getCourseName() + " (" + (mQuestionsPointer+1) + "/" + MAX_QUESTIONS + ")");
	    
	    String[] unit1Colors = getString(R.string.unit1Colors).split(",");
	    String[] unit2Colors = getString(R.string.unit2Colors).split(",");
	
	    TextView tQuestion = (TextView) view.findViewById(R.id.question);
	    tQuestion.setText(question.getQuestion());

	    // Options
	    LinearLayout lOptionsList = (LinearLayout) view.findViewById(R.id.lOptionsList);
	    lOptionsList.removeAllViews();
	    Collection<Option> options = question.getOptions();
	    
	    Integer optionCount = -1;
	    Context context = App.getContext();
	    for (Option option : options) {
	    	Integer DP_20 = (int) App.dpTopx(20);
	    	Integer DP_10 = (int) App.dpTopx(10);
	    	optionCount++;
	    	final String alphabet = "" + getString(R.string.alphabets).charAt(optionCount);
	    	
	    	LinearLayout row = new LinearLayout(context);
	    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    	layoutParams.setMargins(0, 0, 0, 15);
	    	row.setLayoutParams(layoutParams);
	    	row.setWeightSum(10f);
	    	
	    	OnClickListener answerListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkAnswer(alphabet, (LinearLayout) v.getParent());
				}
			};
	    	
	    	// Alphabet for the answer
	    	TextView tAlphabet = new TextView(context);
	    	tAlphabet.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 2f));
	    	tAlphabet.setText(alphabet);
	    	tAlphabet.setMinHeight(20);
	    	if (mCourse.getCourseName().equals("Unit 1")) {
	    		tAlphabet.setBackgroundColor(Color.parseColor(unit1Colors[optionCount]));
	    	} else {
	    		tAlphabet.setBackgroundColor(Color.parseColor(unit2Colors[optionCount]));
	    	}
	    	tAlphabet.setGravity(Gravity.CENTER);
	    	tAlphabet.setPadding(DP_20, DP_20, DP_20, DP_20);
	    	tAlphabet.setTextColor(Color.WHITE);
	    	tAlphabet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	    	tAlphabet.setOnClickListener(answerListener);
	    	
	    	// Option Answer text
	    	TextView tOptionText = new TextView(context);
	    	tOptionText.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 8f));
	    	tOptionText.setText(option.getText());
	    	tOptionText.setPadding(DP_10, DP_10, DP_10, 0);
	    	tOptionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
	    	tOptionText.setSingleLine(false);
	    	tOptionText.setTextColor(Color.BLACK);
	    	tOptionText.setOnClickListener(answerListener);
	    	
	    	// Highlight if answered
			String answered = question.getAnswered();
			if (StringUtil.isBlank(answered)) {
				// No answer selected
				tOptionText.setBackgroundColor(Color.parseColor("#EEEEEE"));
				displayExplanation(false);
			} else if (alphabet.equals(answered)) {
				// Answer selected
				if (question.getCorrectAnswer().equals(answered)) {
					tAlphabet.setBackgroundColor(Color.parseColor(getString(R.string.correct_dark_answer_color)));
					tOptionText.setBackgroundColor(Color.parseColor(getString(R.string.correct_answer_color)));
				} else {
					tAlphabet.setBackgroundColor(Color.parseColor(getString(R.string.incorrect_dark_answer_color)));
		        	tOptionText.setBackgroundColor(Color.parseColor(getString(R.string.incorrect_answer_color)));
				}
				displayExplanation(true);
			}

			// Add to rows
	    	row.addView(tAlphabet);
	    	row.addView(tOptionText);
	    	
	    	lOptionsList.addView(row);
	    	
	    }
	    
	    // Explanation
		TextView tExplanation = (TextView) view.findViewById(R.id.tExplanationText);
		tExplanation.setText(question.getExplanation());
		
		// Previous/Next Question button
		Button bPrevQuestion = (Button) view.findViewById(R.id.bPrevQuestion);
		Button bNextQuestion = (Button) view.findViewById(R.id.bNextQuestion);
		bNextQuestion.setText("Next Question");
		if (mQuestionsPointer == 0) {
			// Hide previous question
			bPrevQuestion.setVisibility(View.GONE);
			bNextQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 2f));
		} else if (mQuestionsPointer == MAX_QUESTIONS - 1) {
			// Hide previous question
			// Set next question = Finish
			bPrevQuestion.setVisibility(View.GONE);
			bNextQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 2f));
			bNextQuestion.setText("Finish");
		} else {
			// Normal case
			bPrevQuestion.setVisibility(View.VISIBLE);
			bNextQuestion.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
		}
		
	}
	

	/*
	 * Retrieve the previous question
	 */
	private Question getPrevQuestion() {
		if (mQuestionsPointer <= 0) {
			return null;
		}
		return mQuestionsHistory.get(--mQuestionsPointer);
	}
	
	/*
	 * Retrieve the next question
	 */
	private Question getNextQuestion() {
		if (mQuestionsPointer < mQuestionsHistory.size() - 1) {
			return mQuestionsHistory.get(++mQuestionsPointer);
		} else if (mQuestionsPointer == MAX_QUESTIONS - 1) {
			return null;
		} else {
			Question question = getRandomQuestion();
		    mQuestionsHistory.add(question);
		    mQuestionsPointer++;
		    return question;
		}
	}
	
	/*
	 * Get random question from bank
	 */
	private Question getRandomQuestion() {
		List<Question> questions = mCourse.getQuestions();
		int max = questions.size() - 1;
		int min = 0;

		Random rand = new Random();

	    Question question = null;
	    if (mQuestionsHistory.size() == questions.size()) {
	    	return null;
	    } else {
		    while (question == null) {
		    	int randomNum = rand.nextInt((max - min) + 1) + min;
		    	if (!mQuestionsHistory.contains(questions.get(randomNum))) {
		    		question = questions.get(randomNum);
		    	}
		    }
	    }

		return question;
	}
	
	/*
	 * Check answer
	 */
	private void checkAnswer(String alphabet, LinearLayout layout) {
		Question question = getCurrentQuestion();
		if (!StringUtil.isBlank(question.getAnswered())) {
			return;
		}
		question.setAnswered(alphabet);
		
		String correctAnswer = question.getCorrectAnswer();

		View alpha = layout.getChildAt(0);
		View optionText = layout.getChildAt(1);
		
		if (alphabet.equals(correctAnswer)) {
			alpha.setBackgroundColor(Color.parseColor(getString(R.string.correct_dark_answer_color)));
			optionText.setBackgroundColor(Color.parseColor(getString(R.string.correct_answer_color)));
			mQuestionsCorrect++;
		} else {
			alpha.setBackgroundColor(Color.parseColor(getString(R.string.incorrect_dark_answer_color)));
			optionText.setBackgroundColor(Color.parseColor(getString(R.string.incorrect_answer_color)));
		}
		
		displayExplanation(true);
	}
	
	private void displayExplanation(boolean show) {
		int visibility = show ? View.VISIBLE : View.GONE;
		TextView tExplanationText = (TextView) view.findViewById(R.id.tExplanationText);
		tExplanationText.setVisibility(visibility);
	}
	
	private Question getCurrentQuestion() {
		return mQuestionsHistory.get(mQuestionsPointer);
	}
	
	/*
	 * Finished Questions
	 */
	private void displayFinish() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new FinishedQuizFragment())
                .commit();
	}
	private class FinishedQuizFragment extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				ViewGroup container,
				Bundle savedInstanceState) {
	        final View view = inflater.inflate(R.layout.fragment_finished, container, false);
	        TextView correctScore = (TextView) view.findViewById(R.id.correctScore);
	        TextView incorrectScore = (TextView) view.findViewById(R.id.incorrectScore);
	        
	        correctScore.setText(Integer.toString(mQuestionsCorrect));
	        incorrectScore.setText(Integer.toString(mQuestionsPointer - mQuestionsCorrect + 1));
	        
	        Button bTryAgain = (Button) view.findViewById(R.id.bTryAgain);
	        bTryAgain.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
			        getFragmentManager().beginTransaction()
			                .replace(R.id.container, new QuizFragment(mCourseNumber))
			                .commit();
				}
			});
	        
	        // DAO
	        HistoryDAO historyDAO = new HistoryDAO();
	        historyDAO.insert(new History(mCourse.getId(), mQuestionsCorrect));
	        
			return view;
			
		}
	}

}
