import java.util.ArrayList;

public class SubjectData {
	private String subjectName;
	private ArrayList<Integer> homeworkScores;
	private ArrayList<Integer> classworkScores;
	private ArrayList<Integer> testScores;

	public SubjectData(String subjectName) {
		this.subjectName = subjectName;
		this.homeworkScores = new ArrayList<>();
		this.classworkScores = new ArrayList<>();
		this.testScores = new ArrayList<>();
	}

	public SubjectData(String subjectName, ArrayList<Integer> homeworkScores, ArrayList<Integer> classworkScores,
			ArrayList<Integer> testScores) {
		this.subjectName = subjectName;
		this.homeworkScores = new ArrayList<>(homeworkScores);
		this.classworkScores = new ArrayList<>(classworkScores);
		this.testScores = new ArrayList<>(testScores);
	}

	public void addHomeworkScore(int score) {
		this.homeworkScores.add(score);
	}

	public void addClassworkScore(int score) {
		this.classworkScores.add(score);
	}

	public void addTestScore(int score) {
		this.testScores.add(score);
	}

	// getters
	public String getSubjectName() {
		return this.subjectName;
	}

	public ArrayList<Integer> getHomeworkScores() {
		return new ArrayList<>(this.homeworkScores);
	}

	public ArrayList<Integer> getClassworkScores() {
		return new ArrayList<>(this.classworkScores);
	}

	public ArrayList<Integer> getTestScores() {
		return new ArrayList<>(this.testScores);
	}

	public void copySubject(SubjectData subject) {
		if (subject == null) {
			return;
		}

		this.homeworkScores.clear();
		this.classworkScores.clear();
		this.testScores.clear();

		this.homeworkScores.addAll(subject.getHomeworkScores());
		this.classworkScores.addAll(subject.getClassworkScores());
		this.testScores.addAll(subject.getTestScores());
	}

	// Total score calculation
	public int getHomeworkTotalScore() {
		return this.homeworkScores.stream().mapToInt(Integer::intValue).sum();
	}

	public int getClassworkTotalScore() {
		return this.classworkScores.stream().mapToInt(Integer::intValue).sum();
	}

	public int getTestTotalScore() {
		return this.testScores.stream().mapToInt(Integer::intValue).sum();
	}

	// Average score calculation
	public double getHomeworkAverageScore() {
		// orEelse(0.0) : if array is empty, return 0.0
		return this.homeworkScores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
	}

	public double getClassworkAverageScore() {
		// orEelse(0.0) : if array is empty, return 0.0
		return this.classworkScores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
	}

	public double getTestAverageScore() {
		// orEelse(0.0) : if array is empty, return 0.0
		return this.testScores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
	}

	public double getPercentGrade() {
		double homeworkWeight = 0.25;
		double classworkWeight = 0.25;
		double testWeight = 0.50;

		return homeworkWeight * getHomeworkAverageScore() + classworkWeight * getClassworkAverageScore()
				+ testWeight * getTestAverageScore();
	}

	public String getLetterGrade() {

		double averageScore = getPercentGrade();

		if (averageScore >= 97.0) {
			return "A+";
		} else if (averageScore >= 93.0) {
			return "A";
		} else if (averageScore >= 90.0) {
			return "A-";
		} else if (averageScore >= 87.0) {
			return "B+";
		} else if (averageScore >= 83.0) {
			return "B";
		} else if (averageScore >= 80.0) {
			return "B-";
		} else if (averageScore >= 77.0) {
			return "C+";
		} else if (averageScore >= 73.0) {
			return "C";
		} else if (averageScore >= 70.0) {
			return "C-";
		} else if (averageScore >= 67.0) {
			return "D+";
		} else if (averageScore >= 65.0) {
			return "D";
		} else {
			return "E/F";
		}
	}

	// Utility
	public static SubjectData findSubjectByName(ArrayList<SubjectData> subjects, String subjectName) {
		if (subjects == null || subjects.isEmpty()) {
			return null;
		}

		for (SubjectData subject : subjects) {
			if (subject.getSubjectName().equalsIgnoreCase(subjectName)) {
				return subject;
			}
		}

		return null; // Subject not found
	}
}
