import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public final class SubjectPage extends JPanel {

	static final int ACTION_BUTTONS_COLUMN = 2;
	private SubjectData subjectData;
	private DefaultTableModel tableModel;
	private ArrayList<SubjectData> globalSubjects;

	JLabel subjectGradeLabel;
	JLabel headerLabel;

	public SubjectPage(PageNavigator navigator,  ArrayList<SubjectData> globalSubjects) {
		super(new BorderLayout());

		this.globalSubjects = globalSubjects;

		TableModel model = makeModel();
		this.tableModel = (DefaultTableModel) model;

		JTable table = new JTable(model) {
			@Override
			public void updateUI() {
				super.updateUI();
				setRowHeight(36);
			}
		};

		
	    // Set up the custom cell editor for editable cells
	    TableColumn homeworkColumn = table.getColumnModel().getColumn(1);
	    TableColumn classworkColumn = table.getColumnModel().getColumn(2);
	    TableColumn testColumn = table.getColumnModel().getColumn(3);

	    homeworkColumn.setCellEditor(new NumericCellEditor());
	    classworkColumn.setCellEditor(new NumericCellEditor());
	    testColumn.setCellEditor(new NumericCellEditor());
		
		
		// Update average when cell is updated
		table.getModel().addTableModelListener(e -> {
		    int row = e.getFirstRow();
		    int column = e.getColumn();

		    if (row != tableModel.getRowCount() - 1 && column != 0) {
		        updateAverageRow();
		    }
		});
		
		
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		// locate each cell content at center
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Header Label and Buttons Panel
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel headerLabel = new JLabel("Math");
		headerLabel.setFont(headerLabel.getFont().deriveFont(24.0f));
		headerLabel.setHorizontalAlignment(JLabel.CENTER);
		headerPanel.add(headerLabel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Right-aligned buttons
		JButton saveButton = new JButton("Save Data");

		saveButton.addActionListener(e -> {
			System.out.println("saveButton Pressed");

			SubjectData currentSubject = this.generateSubjectWithTableData();
			SubjectData foundSubject = SubjectData.findSubjectByName(this.globalSubjects,
					currentSubject.getSubjectName());

			if (foundSubject != null) {
				// Already existing subject. Just copy.

				foundSubject.copySubject(currentSubject);

			} else {
				// New subject
				this.globalSubjects.add(currentSubject);
			}

			navigator.navigate(PageNavigateType.SUBJECTPAGE_TO_GPAPAGE_BY_SAVE_BUTTON);
		});

		saveButton.setPreferredSize(new Dimension(120, 30));
		buttonPanel.add(saveButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			System.out.println("Cancel button Pressed");
			navigator.navigate(PageNavigateType.SUBJECTPAGE_TO_GPAPAGE_BY_CANCEL_BUTTON);
		});
		cancelButton.setPreferredSize(new Dimension(120, 30));
		buttonPanel.add(cancelButton);

		headerPanel.add(buttonPanel, BorderLayout.EAST);

		// Create GPA panel
		JPanel averageScorePanel = new JPanel();
		averageScorePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		JLabel subjectGradeLabel = new JLabel("Math Average score: 97.5 (A+)");
		subjectGradeLabel.setFont(subjectGradeLabel.getFont().deriveFont(36.0f));
		subjectGradeLabel.setHorizontalAlignment(JLabel.CENTER);
		averageScorePanel.add(subjectGradeLabel);

		// Put all components together
		add(headerPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(averageScorePanel, BorderLayout.SOUTH);

		// keep component to modify
		this.headerLabel = headerLabel;
		this.subjectGradeLabel = subjectGradeLabel;
	}

	private static TableModel makeModel() {

		// This makes tables structure.
		
		String[] columnNames = { "index", "Homework (25%)", "Classwork (25%)", "Tests (50%)" };

		Object[][] data = {};
		
		return new DefaultTableModel(data, columnNames) {

	        @Override
	        public Class<?> getColumnClass(int column) {
	            return getValueAt(0, column).getClass();
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            // Make the last row (Average) and the first column (index) read-only
	            if (row == getRowCount() - 1 || column == 0) {
	                return false;
	            }
	            return true;
	        }

	    };
	}

	public void initializeWithSubject(SubjectData subject) {
		this.subjectData = subject;

		// Change labels
		this.headerLabel.setText(subject.getSubjectName());

		String scoreString = "Subject Average: " + new DecimalFormat("#.##").format(subject.getPercentGrade());
		this.subjectGradeLabel.setText(scoreString);

		updateTableData();
		updateSubjectGradeLabel(subject.getHomeworkAverageScore(), subject.getClassworkAverageScore(), subject.getTestAverageScore());
		
	}

	private void updateAverageRow() {
	    int homeworkSum = 0;
	    int classworkSum = 0;
	    int testSum = 0;
	    int homeworkCount = 0;
	    int classworkCount = 0;
	    int testCount = 0;

	    for (int i = 0; i < tableModel.getRowCount() - 1; i++) {
	        Object homeworkValue = tableModel.getValueAt(i, 1);
	        if (homeworkValue != null && !homeworkValue.toString().isEmpty()) {
	            homeworkSum += Integer.parseInt(homeworkValue.toString());
	            homeworkCount++;
	        }

	        Object classworkValue = tableModel.getValueAt(i, 2);
	        if (classworkValue != null && !classworkValue.toString().isEmpty()) {
	            classworkSum += Integer.parseInt(classworkValue.toString());
	            classworkCount++;
	        }

	        Object testValue = tableModel.getValueAt(i, 3);
	        if (testValue != null && !testValue.toString().isEmpty()) {
	            testSum += Integer.parseInt(testValue.toString());
	            testCount++;
	        }
	    }

	    double homeworkAverage = homeworkCount > 0 ? (double) homeworkSum / homeworkCount : 0;
	    double classworkAverage = classworkCount > 0 ? (double) classworkSum / classworkCount : 0;
	    double testAverage = testCount > 0 ? (double) testSum / testCount : 0;

	    int lastRowIndex = tableModel.getRowCount() - 1;
	    if (lastRowIndex >= 0) {
	        tableModel.setValueAt(String.format("%.2f", homeworkAverage), lastRowIndex, 1);
	        tableModel.setValueAt(String.format("%.2f", classworkAverage), lastRowIndex, 2);
	        tableModel.setValueAt(String.format("%.2f", testAverage), lastRowIndex, 3);
	    }
	    
	    updateSubjectGradeLabel(homeworkAverage, classworkAverage, testAverage);
	    
	}
	
	
	private void updateSubjectGradeLabel(double homeworkAverage, double classworkAverage, double testAverage) {
	    double percentGrade = calculatePercentGrade(homeworkAverage, classworkAverage, testAverage);
	    String letterGrade = getLetterGrade(percentGrade);
	    		
	    String scoreString = "Subject Grade: " + String.format("%.2f", percentGrade) + " (" + letterGrade + ")";
	    subjectGradeLabel.setText(scoreString);
	}
	
	private double calculatePercentGrade(double homeworkAverage, double classworkAverage, double testAverage) {
	    double homeworkWeight = 0.25;
	    double classworkWeight = 0.25;
	    double testWeight = 0.50;

	    return homeworkWeight * homeworkAverage + classworkWeight * classworkAverage + testWeight * testAverage;
	}
	
	
	public String getLetterGrade(double percentGrade) {


		if (percentGrade >= 97.0) {
			return "A+";
		} else if (percentGrade >= 93.0) {
			return "A";
		} else if (percentGrade >= 90.0) {
			return "A-";
		} else if (percentGrade >= 87.0) {
			return "B+";
		} else if (percentGrade >= 83.0) {
			return "B";
		} else if (percentGrade >= 80.0) {
			return "B-";
		} else if (percentGrade >= 77.0) {
			return "C+";
		} else if (percentGrade >= 73.0) {
			return "C";
		} else if (percentGrade >= 70.0) {
			return "C-";
		} else if (percentGrade >= 67.0) {
			return "D+";
		} else if (percentGrade >= 65.0) {
			return "D";
		} else {
			return "E/F";
		}
	}

	private void updateTableData() {
		tableModel.setRowCount(0); // Clear existing data

		List<Integer> homeworkScores = subjectData.getHomeworkScores();
		List<Integer> classworkScores = subjectData.getClassworkScores();
		List<Integer> testScores = subjectData.getTestScores();

		int maxRows = Math.max(Math.max(homeworkScores.size(), classworkScores.size()), testScores.size());
		maxRows = Math.max(maxRows, 10); // Ensure at least 10 rows are displayed

		for (int i = 0; i < maxRows; i++) {
			Object[] row = new Object[4];
			row[0] = i + 1; // Index column

			if (i < homeworkScores.size()) {
				row[1] = homeworkScores.get(i);
			} else {
				row[1] = "";
			}

			if (i < classworkScores.size()) {
				row[2] = classworkScores.get(i);
			} else {
				row[2] = "";
			}

			if (i < testScores.size()) {
				row[3] = testScores.get(i);
			} else {
				row[3] = "";
			}

			tableModel.addRow(row);
		}

		// Add the "Average" row
		Object[] averageRow = new Object[4];
		averageRow[0] = "Average";
		averageRow[1] = String.format("%.2f", subjectData.getHomeworkAverageScore());
		averageRow[2] = String.format("%.2f", subjectData.getClassworkAverageScore());
		averageRow[3] = String.format("%.2f", subjectData.getTestAverageScore());
		tableModel.addRow(averageRow);
	}

	public SubjectData generateSubjectWithTableData() {
		// Generate new subject with current table data

		String subjectName = this.subjectData.getSubjectName();
		ArrayList<Integer> homeworkScores = new ArrayList<Integer>();
		ArrayList<Integer> classworkScores = new ArrayList<Integer>();
		ArrayList<Integer> testScores = new ArrayList<Integer>();

		int rowCount = tableModel.getRowCount();
		for (int i = 0; i < rowCount - 1; i++) { // Exclude the last "Average" row
			Object homeworkScore = tableModel.getValueAt(i, 1);
			if (homeworkScore != null && !homeworkScore.toString().isEmpty()) {
				homeworkScores.add(Integer.parseInt(homeworkScore.toString()));
			}

			Object classworkScore = tableModel.getValueAt(i, 2);
			if (classworkScore != null && !classworkScore.toString().isEmpty()) {
				classworkScores.add(Integer.parseInt(classworkScore.toString()));
			}

			Object testScore = tableModel.getValueAt(i, 3);
			if (testScore != null && !testScore.toString().isEmpty()) {
				testScores.add(Integer.parseInt(testScore.toString()));
			}
		}

		return new SubjectData(subjectName, homeworkScores, classworkScores, testScores);
	}
}

class NumericCellEditor extends DefaultCellEditor {
    private static final long serialVersionUID = 1L;

    public NumericCellEditor() {
        super(new JTextField());
    }

    @Override
    public boolean stopCellEditing() {
        String value = ((JTextField) getComponent()).getText();
        if (value.isEmpty()) {
            return super.stopCellEditing();
        }
        try {
            int intValue = Integer.parseInt(value);
            if (intValue >= 0 && intValue <= 100) {
                return super.stopCellEditing();
            }
        } catch (NumberFormatException ex) {
            // Invalid input, do not stop editing
        }
        return false;
    }
}
