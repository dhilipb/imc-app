package com.app.imc.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.app.imc.App;
import com.app.imc.Database;
import com.app.imc.dao.CourseDAO;
import com.app.imc.dao.OptionDAO;
import com.app.imc.dao.QuestionDAO;
import com.app.imc.model.Course;
import com.app.imc.model.Option;
import com.app.imc.model.Question;

public class Parser {
	
	private final List<Course> mCourses;
	private final int MAX_COURSES = 2;
	
	private static Parser instance;
	private Parser() {
		mCourses = new ArrayList<Course>();
	}
	
	public static List<Course> getCourses() {
		return Parser.getInstance().mCourses;
	}
	
	public static Parser getInstance() {
		if (instance == null) {
			instance = new Parser();
		}
		return instance;
	}
	
	public void setup() {
		if (mCourses.size() == MAX_COURSES) {
			return;
		}
		
		addCourse("Unit 1", "Investment Environment");
		addCourse("Unit 2", "Investment Practice");

		Database.closeDatabase();
		
	}
	
	private void addCourse(final String name, final String longName) {

		Course course = new Course(name, longName);
		course.setQuestions(populate(course));
		mCourses.add(course);
		
		/*
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			}
		});
		*/
	}
	
	/*
	 * Helper function to populate courses
	 */
	private List<Question> populate(Course course) {
		
		Log.d("Parser", "Populating course " + course.getCourseName());
		
		List<Question> questions = new ArrayList<Question>();
		List<Course> courses = new ArrayList<Course>();
		Database repo = Database.getInstance();
		
		CourseDAO courseDAO = new CourseDAO();
		try {
			courses = courseDAO.retrieveAll();
			
			if (courses.size() == MAX_COURSES) {
				// Perfect!
				questions.addAll(populateFromDB(course, courseDAO));
			}
		} catch (Exception e) {
			// Corrupted database
			Log.w("Parser", "Corrupted database! Copying database from assets");
			repo.copyDatabase();
		}
		
		if (questions.size() == 0) {
			Log.e("Parser", "NO questions found!!! Trying again");
			repo.copyDatabase();
			populate(course);
		}
		
		// If course is not present, parse from HTML
//		if (!courses.contains(course)) {
//			courseDAO.insert(course);
//			String filename = course.getSlug() + ".html";
//			questions = parseFromHTML(course, filename);
//		}
		
		return questions;
	}
	
	/*
	 * Populate from database
	 */
	private List<Question> populateFromDB(Course courseToFind, CourseDAO courseDAO) {
		
		Log.d("Parser", "Populating " + courseToFind.getCourseName() + " from Database");
		
		Course course = null;
		
		List<Course> dbCourses = courseDAO.retrieveAll();
		for (Course dbCourse : dbCourses)  {
			if (dbCourse.getSlug().equals(courseToFind.getSlug())) {
				course = dbCourse;
				break;
			}
		}
		
		// This shouldn't happen!
		if (course == null) {
			Log.e("Parser", "Course is null?! Uh-oh");
			return null;
		}
		
		courseToFind.setId(course.getId());
		
		// Populate questions and options from database
		QuestionDAO questionDAO = new QuestionDAO();
		OptionDAO optionDAO = new OptionDAO();
		
		List<Question> questions = questionDAO.retrieveByColumn("course", Long.toString(course.getId()));
		for (Question question : questions) {
			List<Option> options = optionDAO.retrieveByColumn("question", Long.toString(question.getId()));
			question.setOptions(options);
		}
		
		course.setQuestions(questions);
		return questions;
	}
	
	/*
	 * Parse from HTML
	 */
	private List<Question> parseFromHTML(Course course, String filename) {
		List<Question> questions = new ArrayList<Question>();
		InputStream is = null;
		
		Log.d("Parser", "Populating from HTML " + course.getCourseName());
		long lStartTime = System.currentTimeMillis();
		try {
			is = App.getAssetManager().open(filename);
			Document doc = Jsoup.parse(is, "UTF-8", "http://example.com");
			Elements eQuestions = doc.select(".test-question");
			
			
			for (Element eQuestion : eQuestions) {
				// DAO
				QuestionDAO questionDAO = new QuestionDAO();
				questionDAO.setCourse(course);
				
				// Question text
				String questionText = eQuestion.getElementsByClass("test-question-text").get(0).text();
				
				// Questions which contain numerals - I, II etc
				if (eQuestion.getElementsByClass("test-response-options").size() > 0) {
					Elements eRespOptions = eQuestion.getElementsByClass("test-response-options");
					questionText += "\n";
					for (Element eRespOption : eRespOptions) {
						String numeral = eRespOption.getElementsByClass("test-response-numeral").get(0).text();
						String responseOptionText = eRespOption.getElementsByClass("test-response-option").get(0).text();
						questionText += "\n" + numeral + ": " + responseOptionText;
					}
				}
				
				Question question = new Question(questionText);

				// Explanation
				String explanation = eQuestion.getElementsByClass("test-answer-actual").get(0).text().trim();
				explanation += "\n\n" + eQuestion.getElementsByClass("test-answer-justification").get(0).text().trim();
				explanation = explanation.replaceAll("Explanation ", "Explanation: ");
				question.setExplanation(explanation);
				
				// Correct Answer
				String correctAnswer = eQuestion.getElementsByClass("test-answer-actual").get(0).text().trim();
				Integer index = correctAnswer.indexOf("is:");
				correctAnswer = correctAnswer.substring(index + 4, index + 5);
				question.setCorrectAnswer(correctAnswer);
				
				// Insert to DB
				questionDAO.insert(question);

				// Options
				OptionDAO optionDAO = new OptionDAO();
				optionDAO.setQuestion(question);
				for (Element eAnswer : eQuestion.getElementsByClass("test-response-answer")) {
					Option option = new Option(eAnswer.text());
					
					optionDAO.insert(option);
					question.addOption(option);
				}
				
				questions.add(question);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long lEndTime = System.currentTimeMillis();
	 
		Log.d("Parser", "Time taken to parse " + Long.toString(lEndTime - lStartTime));
		
		return questions;
	}
	
}
