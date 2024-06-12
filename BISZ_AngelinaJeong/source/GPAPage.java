
//// REF. 
//// https://github.com/aterai/java-swing-tips/blob/master/MultipleButtonsInTableCell/src/java/example/MainPanel.java

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public final class GPAPage extends JPanel {

	static final int ACTION_BUTTONS_COLUMN = 3; // edit, delete buttons are located at column 3

	private SubjectPage subjectPage;
	private ArrayList<SubjectData> globalSubjects;
	private JTable table;
	private JLabel gpaLabel;
	
	
	public GPAPage(PageNavigator navigator,ArrayList<SubjectData> globalSubjects, SubjectPage subjectPage) {

		super(new BorderLayout());

		// It helps to navigate two pages (this GPA page and subject page)
		this.subjectPage = subjectPage;
		this.globalSubjects = globalSubjects;

		TableModel model = makeTableModel();

		GPAPage masterPage = this;

		// Create table to hold the each subject information
		JTable table = new JTable(model) {
//	        private static final long serialVersionUID = -8705783453138884300L;

			@Override
			public void updateUI() {
				super.updateUI();
				setRowHeight(36);
				TableColumn column = getColumnModel().getColumn(ACTION_BUTTONS_COLUMN);

				column.setCellRenderer(new ButtonsRenderer());
				column.setCellEditor(new ButtonsEditor(this, navigator, globalSubjects, masterPage, subjectPage));
			}
		};

		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER); // Center
																														// headers

		// locate each cell content at center
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for (int i = 0; i < ACTION_BUTTONS_COLUMN; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Header Label and Button Panel
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel headerLabel = new JLabel("GPA Calculator");
		headerLabel.setFont(headerLabel.getFont().deriveFont(24.0f));
		headerLabel.setHorizontalAlignment(JLabel.CENTER);
		headerPanel.add(headerLabel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Right-aligned buttons
		JButton addNewSubjectButton = new JButton("Add New Subject");

		addNewSubjectButton.addActionListener(e -> {

			// Get new subject name from user
			String subjectName = (String) JOptionPane.showInputDialog(this, "New subject name", "Create New Subject",
					JOptionPane.PLAIN_MESSAGE, null, null, "");

			System.out.println("User input: " + subjectName);

			if (subjectName == null) {
				// invalid subject name
				return;
			}

			// Check if new subject is available
			SubjectData subject = SubjectData.findSubjectByName(this.globalSubjects, subjectName);

			if (subject != null) {
				// Already existing subject.
				JOptionPane.showMessageDialog(null, "Already existing subject.\nInput another subject name.",
						"Invalid Subject Name", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Create new subject
			SubjectData newSubject = new SubjectData(subjectName);

			// Jump to the Subject Page
			this.subjectPage.initializeWithSubject(newSubject);

			navigator.navigate(PageNavigateType.GPAPAGE_TO_SUBJECTPAGE);

		});

		addNewSubjectButton.setPreferredSize(new Dimension(160, 30));
		buttonPanel.add(addNewSubjectButton);

		headerPanel.add(buttonPanel, BorderLayout.EAST);

		// Create Add Subject Button
		JButton addSubjectButton = new JButton("Add Subject");

		addSubjectButton.addActionListener(e -> {

			// Get new subject name from user
			String subjectName = (String) JOptionPane.showInputDialog(this, "New subject name", "Create New Subject", // Dialog
																														// title
					JOptionPane.PLAIN_MESSAGE, null, null, "");

			System.out.println("User input: " + subjectName);

			if (subjectName == null) {
				// invalid subject name
				return;
			}


			navigator.navigate(PageNavigateType.GPAPAGE_TO_SUBJECTPAGE);

		});

		addSubjectButton.setPreferredSize(new Dimension(120, 30));

		// Create GPA panel
		JPanel gpaPanel = new JPanel();
		gpaPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add vertical padding
		JLabel gpaLabel = new JLabel("GPA: 4.0");
		gpaLabel.setFont(gpaLabel.getFont().deriveFont(36.0f)); // Increase font size
		gpaLabel.setHorizontalAlignment(JLabel.CENTER);
		gpaPanel.add(gpaLabel);

		this.gpaLabel = gpaLabel;
		
		// Create an information panel to hold the button and GPA label
		JPanel informationPanel = new JPanel(new BorderLayout());
		informationPanel.add(gpaPanel, BorderLayout.CENTER); // Add the GPA panel to the center

		// Put all components together
		add(headerPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(informationPanel, BorderLayout.SOUTH);

		this.table = table;
		
		updateGPALabel();

	}

	@Override
	public Dimension getPreferredSize() {
		// Return the size of the parent frame
		return ((JFrame) SwingUtilities.getWindowAncestor(this)).getSize();
	}

	private TableModel makeTableModel() {

		String[] columnNames = { "Subject", "Percent Grade", "Letter Grade", "Actions" };

		Object[][] data = new Object[globalSubjects.size()][4];
		int row = 0;

		for (SubjectData subject : globalSubjects) {

			data[row][0] = subject.getSubjectName();
			data[row][1] = Math.round(subject.getPercentGrade() * 100) / 100.0;
			data[row][2] = subject.getLetterGrade();
			data[row][3] = ""; // Empty string for the "Actions" column
			row++;
		}

		return new DefaultTableModel(data, columnNames) {

			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == ACTION_BUTTONS_COLUMN) {
					// Only Actions column is editable so that user can press button
					return true;
				} else {
					// The others are all read only cells
					return false;
				}
			}
		};

	}


	private double calculateGPA() {
	    double totalGradePoints = 0.0;
	    int totalCredits = 0;

	    for (SubjectData subject : globalSubjects) {
	        double averageScore = subject.getPercentGrade();
	        double gradePoint = getGradePoint(averageScore);

	        // Assuming each subject has equal credits (e.g., 3 credits per subject)
	        int credits = 3;

	        totalGradePoints += gradePoint * credits;
	        totalCredits += credits;
	    }

	    if (totalCredits > 0) {
	        return totalGradePoints / totalCredits;
	    } else {
	        return 0.0;
	    }
	}

	private double getGradePoint(double averageScore) {
		
		// GPA conversion reference. 
		// https://bigfuture.collegeboard.org/plan-for-college/get-started/how-to-convert-gpa-4.0-scale#:~:text=To%20calculate%20your%20high%20school,a%20%22weighted%20GPA%20system.%22
		
	    if (averageScore >= 97.0) {
	        return 4.0;
	    } else if (averageScore >= 93.0) {
	        return 4.0;
	    } else if (averageScore >= 90.0) {
	        return 3.7;
	    } else if (averageScore >= 87.0) {
	        return 3.3;
	    } else if (averageScore >= 83.0) {
	        return 3.0;
	    } else if (averageScore >= 80.0) {
	        return 2.7;
	    } else if (averageScore >= 77.0) {
	        return 2.3;
	    } else if (averageScore >= 73.0) {
	        return 2.0;
	    } else if (averageScore >= 70.0) {
	        return 1.7;
	    } else if (averageScore >= 67.0) {
	        return 1.3;
	    } else if (averageScore >= 65.0) {
	        return 1.0;
	    } else {
	        return 0.0;
	    }
	}

	public void reloadTable() {

		System.out.println("reloadTable()");

		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		model.setRowCount(0); // Clear existing rows

		for (SubjectData subject : globalSubjects) {
			Object[] rowData = new Object[4];
			rowData[0] = subject.getSubjectName();
			rowData[1] = Math.round(subject.getPercentGrade() * 100) / 100.0;
			rowData[2] = subject.getLetterGrade();
			rowData[3] = ""; // Empty string for the "Actions" column
			model.addRow(rowData);
		}
		
		updateGPALabel();
	}
	
	
	private void updateGPALabel() {
	    double gpa = calculateGPA();
	    String gpaText = String.format("GPA: %.2f / 4.0", gpa);
	    gpaLabel.setText(gpaText);
	}
}

@SuppressWarnings("serial")
class ButtonsPanel extends JPanel {
	
	private final List<JButton> buttons = Arrays.asList(new JButton("Edit"), new JButton("Delete"));

	protected ButtonsPanel() {
		super();
		for (JButton b : buttons) {
			b.setFocusable(false);
			b.setRolloverEnabled(false);
			add(b);
		}
	}

	@Override
	public final Component add(Component comp) {
		return super.add(comp);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		setOpaque(true);
	}

	protected List<JButton> getButtons() {
		return buttons;
	}
}

class ButtonsRenderer implements TableCellRenderer {
	private final ButtonsPanel panel = new ButtonsPanel() {

		private static final long serialVersionUID = 6256064010930163302L;

		@Override
		public void updateUI() {
			super.updateUI();
			setName("Table.cellRenderer");
		}
	};

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		return panel;
	}
}

/**
 * Action for the Edit button
 */
class EditSubjectAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2717072839423661203L;
	private final JTable table;

	private PageNavigator navigator; // navigate to SubjectPage
	private SubjectPage subjectPage;
	private ArrayList<SubjectData> sharedSubject;

	protected EditSubjectAction(JTable table, JButton viewButton, PageNavigator navigator,
			ArrayList<SubjectData> sharedSubject, SubjectPage subjectPage, GPAPage masterPage) {
		super("Edit");
		this.table = table;
		this.sharedSubject = sharedSubject;
		this.subjectPage = subjectPage;

		// Print the button's label in the console
		System.out.println("Button label: " + viewButton.getText());

		this.navigator = navigator;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int selectedRow = table.getSelectedRow();

		if (selectedRow != -1) {
			String subjectTitle = (String) table.getValueAt(selectedRow, 0); // Get the subject title from the first
																				// column
			System.out.println("Edit button clicked for subject: " + subjectTitle);

			// find subject with subjectTitle
			SubjectData subject = SubjectData.findSubjectByName(sharedSubject, subjectTitle);

			if (subject == null) {
				// do nothing
				return;
			}

			// initialize subject page with found subject
			subjectPage.initializeWithSubject(subject);

			// navigate to subject page
			this.navigator.navigate(PageNavigateType.GPAPAGE_TO_SUBJECTPAGE);

		}
	}
}

/**
 * Action for the Delete Button
 */
class DeleteSubjectAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5852630089402501556L;
	private final JTable table;
	private ArrayList<SubjectData> globalSubjects;
	private GPAPage masterPage;

	protected DeleteSubjectAction(JTable table, ArrayList<SubjectData> globalSubjects, GPAPage masterPage) {
		super("Delete");
		this.table = table;
		this.globalSubjects = globalSubjects;
		this.masterPage = masterPage;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (table.isEditing()) {
			int row = table.convertRowIndexToModel(table.getEditingRow());
			table.getCellEditor().stopCellEditing();

			String subjectTitle = (String) table.getValueAt(row, 0); // Get the subject title from the first column
			System.out.println("Delete button clicked for subject: " + subjectTitle);

			globalSubjects.removeIf(subject -> subject.getSubjectName().equalsIgnoreCase(subjectTitle));

			masterPage.reloadTable();
		}
	}

}

@SuppressWarnings("serial")
class ButtonsEditor extends AbstractCellEditor implements TableCellEditor {

	private final ButtonsPanel panel = new ButtonsPanel();
	private final JTable table;

	private final class EditingStopHandler extends MouseAdapter implements ActionListener {
		@Override
		public void mousePressed(MouseEvent e) {
			Object o = e.getSource();
			if (o instanceof TableCellEditor) {
				actionPerformed(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, ""));
			} else if (o instanceof JButton) {
				// DEBUG:
				// view button click ->
				// control key down + edit button(same cell) press ->
				// remain selection color
				ButtonModel m = ((JButton) e.getComponent()).getModel();
				if (m.isPressed() && table.isRowSelected(table.getEditingRow()) && e.isControlDown()) {
					panel.setBackground(table.getBackground());
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EventQueue.invokeLater(() -> fireEditingStopped());
		}
	}

	protected ButtonsEditor(JTable table, PageNavigator navigator, ArrayList<SubjectData> globalSubjects,
			GPAPage masterPage, SubjectPage subjectPage) {
		super();
		this.table = table;
		List<JButton> list = panel.getButtons();

		// Edit button
		list.get(0).setAction(
				new EditSubjectAction(table, list.get(0), navigator, globalSubjects, subjectPage, masterPage));

		// Delete button
		list.get(1).setAction(new DeleteSubjectAction(table, globalSubjects, masterPage));

		// Buttons listen to mouse and action event when table cell editing is done
		EditingStopHandler handler = new EditingStopHandler();

		for (JButton b : list) {
			b.addMouseListener(handler);
			b.addActionListener(handler);
		}

		panel.addMouseListener(handler);
	}

	@Override
	public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
		panel.setBackground(tbl.getSelectionBackground());
		return panel;
	}

	@Override
	public Object getCellEditorValue() {
		return "";
	}

}
