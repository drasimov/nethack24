import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements PageNavigator {

	CurrentPage currentPage;
	ArrayList<SubjectData> globalSubjects;
	CardLayout cardLayout;
	Container container;
	GPAPage gpaPage;

	enum CurrentPage {
		GPA_PAGE, SUBJECT_PAGE
	};



	public MainFrame() {

		this.globalSubjects = new ArrayList<SubjectData>();
		
		loadSampleSubjects();
		
		currentPage = CurrentPage.GPA_PAGE;

		container = getContentPane();
		cardLayout = new CardLayout();
		container.setLayout(cardLayout);

		SubjectPage subjectPage = new SubjectPage(this, this.globalSubjects);
		GPAPage gpaPage = new GPAPage(this, this.globalSubjects, subjectPage);

		container.add(gpaPage);
		container.add(subjectPage);

		// Minimized size
		setSize(800, 500);

		// This APP is not relative any other windows at all
		setLocationRelativeTo(null);

		// When move window, keep some size
		pack();

		// Make the frame full screen
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		this.gpaPage = gpaPage;

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame frame = new MainFrame();
				frame.setVisible(true);
			}
		});
	}

	private void loadSampleSubjects() {

		// Randomly create score for each subjects

		List<String> subjectNames = Arrays.asList("AP Calculus BC", "Honors Biology", "AP US History", "Chinese",
				"Physics", "AP Euro History");

		for (String subjectName : subjectNames) {
			this.globalSubjects.add(new SubjectData(subjectName));
		}

		int MAX_SCORE = 100;
		int MIN_SCORE = 60; // Minimum score for each assessment

		for (SubjectData subject : this.globalSubjects) {
			Random random = new Random();
			int numHomeworkScores = random.nextInt(6) + 5; // 5 to 10 homework scores
			int numClassworkScores = random.nextInt(6) + 5; // 5 to 10 classwork scores
			int numTestScores = random.nextInt(6) + 5; // 5 to 10 test scores

			for (int i = 0; i < numHomeworkScores; i++) {
				subject.addHomeworkScore(generateRandomScoreBetween(random, MIN_SCORE, MAX_SCORE));
			}

			for (int i = 0; i < numClassworkScores; i++) {
				subject.addClassworkScore(generateRandomScoreBetween(random, MIN_SCORE, MAX_SCORE));
			}

			for (int i = 0; i < numTestScores; i++) {
				subject.addTestScore(generateRandomScoreBetween(random, MIN_SCORE, MAX_SCORE));
			}
		}
	}
	
	private int generateRandomScoreBetween(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	@Override
	public void navigate(PageNavigateType type) {

		if (type == PageNavigateType.SUBJECTPAGE_TO_GPAPAGE_BY_SAVE_BUTTON) {
			this.gpaPage.reloadTable();
		}

		cardLayout.next(container);
	}
}