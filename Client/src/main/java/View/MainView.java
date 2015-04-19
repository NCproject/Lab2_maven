package View;

import Model.Faculty;
import Model.Group;
import Model.Student;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import Exception.*;

public class MainView extends JFrame implements ClientView {

    /** The model */
    DefaultTableModel model;

    /** The list of students */
    private List<Student> students = new ArrayList<>();

    /** The table of students */
    private JTable JT_Students;

    /** The SplitPane for creating search and choosing the Faculty, the Group */
    private JSplitPane JSP_Create = new JSplitPane();

    /** The Panel for choosing Faculty and Group */
    private JPanel JP_Filter = new JPanel();

    /** Components by JP_Filter*/
    private JLabel JL_Faculty = new JLabel("Faculty:");
    private JComboBox<Faculty> JCB_Faculty = new JComboBox<>();
    private JLabel JL_Group = new JLabel("Group:");
    private JComboBox<Group> JCB_Group = new JComboBox<>();
    private JButton JB_UpdateTable = new JButton("OK");
    private JButton JB_AddFaculty = new JButton("+");
    private JButton JB_RemoveFaculty = new JButton("-");
    private JButton JB_AddGroup = new JButton("+");
    private JButton JB_RemoveGroup = new JButton("-");
    private boolean addButtonClick;

    /** The Panel for searching Student */
    private JPanel JP_Search = new JPanel();
    private JTextField JTF_Search = new JTextField("Insert the Last Name...");
    private JButton JB_Search = new JButton("OK");
    private JButton JB_Reset = new JButton("Reset");

    /** Components for creating a new Faculty or Group */
    private JTextField JTF_Name = new JTextField("Insert the Name...");
    private JButton JB_AddFacultyOrGroup = new JButton("OK");
    private JButton JB_CancelCreate = new JButton("CANCEL");

    /** The Panel for creating a new Student */
    private JPanel JP_CreateStudent = new JPanel(new GridBagLayout());

    /** Components by JP_CreateStudent*/
    private JLabel JL_FirstName = new JLabel("First Name:");
    private JTextField JTF_FirstName = new JTextField("");
    private JLabel JL_LastName = new JLabel("Last Name:");
    private JTextField JTF_LastName = new JTextField("");
    private JLabel JL_Enrolled = new JLabel("Enrolled Year:");
    private JTextField JTF_Enrolled = new JTextField("");
    private JButton JB_AddStudent = new JButton("ADD");
    private JButton JB_UpdateStudent = new JButton("UPDATE");
    private JButton JB_DeleteStudent = new JButton("DELETE");

    private JPanel JP_South = new JPanel(new BorderLayout());

    /** The logger */
    private static final Logger logger = Logger.getLogger(MainView.class);

    /**
     * Instantiates a new MainView
     */
    public MainView(){
        super("Information System for students.");
        configureMainView();
        buildMainPanel();
    }

    /**
     * Specifies the configuration MainView.
     */
    private void configureMainView(){
        setSize(800, 800);
        setLocation(500,150);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

    }

    @Override
    public void close() {

    }

    /**
     * Creates on the MainView all components
     */
    private void buildMainPanel(){

        /** JP_Filter configuration */
        JP_Filter.setLayout(new GridBagLayout());
        JP_Filter.setBorder(BorderFactory.createTitledBorder("Filter"));
        JB_AddFaculty.addActionListener(setActionToButtonNew());
        JB_AddGroup.addActionListener(setActionToButtonNew());
        buildFilterPanel();

        /** JP_Search configuration */
        JP_Search.setLayout(new BorderLayout());
        JP_Search.setBorder(BorderFactory.createTitledBorder("Search"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(JB_Search);
        buttonPanel.add(JB_Reset);
        JTF_Search.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTF_Search.setText(null);
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        JP_Search.add(buttonPanel, BorderLayout.SOUTH);
        JP_Search.add(JTF_Search, BorderLayout.CENTER);

        /** JSP_Create configuration */
        JSP_Create.setDividerLocation(400);
        JSP_Create.setDividerSize(0);
        JSP_Create.setLeftComponent(JP_Filter);
        JSP_Create.setRightComponent(JP_Search);

        JB_AddStudent.setEnabled(false);
        JB_UpdateStudent.setEnabled(false);

        /** JS_Table configuration */
        configureTableStudents();
        JScrollPane JS_Table = new JScrollPane(JT_Students);

        /** JP_CreateStudent configuration */
        buildCreateStudentPanel();
        JB_DeleteStudent.setEnabled(false);
        JP_South.add(JB_DeleteStudent, BorderLayout.NORTH);
        JP_CreateStudent.setBorder(BorderFactory.createTitledBorder("Edit"));
        JP_South.add(JP_CreateStudent, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(JSP_Create, BorderLayout.NORTH);
        getContentPane().add(JS_Table, BorderLayout.CENTER);
        getContentPane().add(JP_South, BorderLayout.SOUTH);
    }

    /**
     * Builds the table
     *
     * @param students The list of the students
     */
    private void buildTable(final List<Student> students){
        try {
            DefaultTableModel tModel = getObjectModel(students);
            JT_Students.setModel(tModel);
            JT_Students.updateUI();
        } catch (ClientException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            showMessage("Non students!");
        }
    }

    /**
     * JT_Students configuration
     */
    private void configureTableStudents(){
        try {
            JT_Students = new JTable(getObjectModel(students)){
                @Override
                public boolean isCellEditable ( int row, int column )
                {
                    return false;
                }
            };
        } catch (ClientException e) {
            logger.error("Can't send data to the server", e);
        }

        JT_Students.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JB_DeleteStudent.setEnabled(true);
                JB_UpdateStudent.setEnabled(true);
                Student student = students.get(JT_Students.getSelectedRow());
                JTF_FirstName.setText(student.getFirstName());
                JTF_LastName.setText(student.getLastName());
                JTF_Enrolled.setText(student.getEnrolled());
                JP_CreateStudent.updateUI();
            }
        });
    }

    /**
     * Adds components to the FilterPanel
     *
     * @param comp Component
     * @param x gridx
     * @param y gridy
     * @param col gridwidth
     * @param wx top
     * @param wy bottom
     * @param hx left
     * @param hy rigth
     */
    private void addComponentToFilterPanel (JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy ){
        JP_Filter.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, wy, hx, hy), 0, 0));
    }

    /**
     * Adds components to the CreateStudentPanel
     *
     * @param comp Component
     * @param x gridx
     * @param y gridy
     * @param col gridwidth
     * @param wx top
     * @param wy bottom
     * @param hx left
     * @param hy rigth
     */
    private void addComponentToCreateStudentPanel (JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy ){
        JP_CreateStudent.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, wy, hx, hy), 0, 0));
    }

    /**
     * Creates a FilterPanel on the MainView with all components
     */
    private void buildFilterPanel(){
        addComponentToFilterPanel(JL_Faculty, 0, 0, 1, 5, 0, 0, 5);
        addComponentToFilterPanel(JCB_Faculty, 1, 0, 1, 5, 0, 0, 0);

        /** Adds ActionListener to the JCB_Faculty */
        JCB_Faculty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                Faculty faculty = (Faculty) comboBox.getSelectedItem();
                if (faculty != null) {
                    JCB_Group.removeAllItems();
                    List<Group> groups = faculty.getGroups();
                    for (Group g : groups) {
                        JCB_Group.addItem(g);
                    }
                }
            }
        });

        addComponentToFilterPanel(JB_AddFaculty, 3, 0, 1, 5, 0, 0, 0);
        addComponentToFilterPanel(JB_RemoveFaculty, 4, 0, 1, 5, 0, 0, 0);
        addComponentToFilterPanel(JL_Group, 0, 1, 1, 5, 0, 0, 5);
        addComponentToFilterPanel(JCB_Group, 1, 1, 2, 5, 0, 0, 0);
        addComponentToFilterPanel(JB_AddGroup, 3, 1, 1, 5, 0, 0, 0);
        addComponentToFilterPanel(JB_RemoveGroup, 4, 1, 1, 5, 0, 0, 0);
        addComponentToFilterPanel(JB_UpdateTable, 0, 3, 5, 5, 0, 10, 0);
    }

    /**
     * Creates a CreateStudentPanel on the MainView with all components
     */
    private void buildCreateStudentPanel(){
        addComponentToCreateStudentPanel(JL_FirstName, 0, 0, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JTF_FirstName, 1, 0, 2, 10, 0, 10, 20);
        addComponentToCreateStudentPanel(JL_LastName, 0, 1, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JTF_LastName, 1, 1, 2, 10, 0, 10, 20);
        addComponentToCreateStudentPanel(JL_Enrolled, 0, 2, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JTF_Enrolled, 1, 2, 2, 10, 0, 10, 20);
        addComponentToCreateStudentPanel(JB_AddStudent, 1, 3, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JB_UpdateStudent, 2, 3, 1, 10, 0, 10, 20);
    }

    /**
     * Gets the TableModel for JT_Students
     *
     * @param students The list of the students
     * @return model
     * @throws ClientException
     */
    private DefaultTableModel getObjectModel(List<Student> students)  throws ClientException  {
        this.students = students;
        String[][] data = new String[students.size()][3];
        Object[] columnNames = new Object[3];
        columnNames[0] = "First Name";
        columnNames[1] = "Last Name";
        columnNames[2] = "Enrolled";
        int i = 0;
        while (i < students.size()) {
            for (Student st : students) {
                data[i][0] = st.getFirstName();
                data[i][1] = st.getLastName();
                data[i][2] = st.getEnrolled();
                i++;
            }
        }
        model = new DefaultTableModel(data, columnNames);
        return model;
    }

    /**
     * @return ActionListener for the ButtonNew
     */
    private ActionListener setActionToButtonNew(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSP_Create.setRightComponent(null);
                JPanel JP_AddFaculty = new JPanel();
                JP_AddFaculty.setLayout(new GridBagLayout());
                JP_AddFaculty.setBorder(BorderFactory.createTitledBorder("Create a new:"));
                JP_AddFaculty.setLayout(new BorderLayout());
                JP_AddFaculty.add(JTF_Name, BorderLayout.CENTER);
                JSP_Create.setDividerLocation(400);
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(JB_AddFacultyOrGroup);
                buttonPanel.add(JB_CancelCreate);

                JB_CancelCreate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JSP_Create.setRightComponent(null);
                        JSP_Create.setDividerLocation(400);
                        JSP_Create.setRightComponent(JP_Search);
                    }
                });

                JP_AddFaculty.add(buttonPanel, BorderLayout.SOUTH);
                JSP_Create.setRightComponent(JP_AddFaculty);
                addButtonClick = e.getSource().equals(JB_AddFaculty);
                JTF_Name.setText("Name...");
                JTF_Name.setDisabledTextColor(Color.GRAY);
                JTF_Name.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        JTF_Name.setText(null);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {

                    }
                });

            }
        };
    }

    /**
     * Sets to the ComboBoxes facultyList
     *
     * @param facultyList List of Faculties
     */
    public void setFilters(List<Faculty> facultyList){
        if (facultyList != null){
            JCB_Faculty.removeAllItems();
            JCB_Faculty.repaint();
            for (Faculty f : facultyList){
                JCB_Faculty.addItem(f);
                if (JCB_Faculty.getSelectedItem().equals(f)){
                    JCB_Group.removeAllItems();
                    List<Group> groups = f.getGroups();
                    for (Group g : groups){
                        JCB_Group.addItem(g);
                    }
                }
            }
        }
    }


    /**
     * Creates table for the studentList
     *
     * @param studentList List of the students
     */
    public void showStudents(List<Student> studentList){
        if (JCB_Faculty.getItemCount()!= 0 && JCB_Group.getItemCount() != 0){

            Group g = (Group) JCB_Group.getSelectedItem();
            if (studentList.size() == 0) {
                showMessage("Group " + g.getNumber() + " doesn't have a single student!");
            }
            this.students = studentList;
            buildTable(students);
        } else {
            showMessage("Please, create Faculty or Group.");
        }
    }

    /**
     * Shows the MessageDialog
     *
     * @param message message
     */
    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message );
    }

    /**
     * @return JCB_Faculty
     */
    public JComboBox getJCB_Faculty(){
        return JCB_Faculty;
    }

    /**
     * @return JCB_Group
     */
    public JComboBox getJCB_Group(){
        return JCB_Group;
    }

    /**
     * @return JB_UpdateTable
     */
    public JButton getJB_UpdateTable(){
        return JB_UpdateTable;
    }

    /**
     * @return The text of JTF_Name
     */
    public String getNameOfNewFacultyOrGroup(){
        return JTF_Name.getText();
    }

    /**
     * @return JB_AddFacultyOrGroup
     */
    public JButton getJB_AddFacultyOrGroup(){
        return JB_AddFacultyOrGroup;
    }

    /**
     * @return B_RemoveFaculty
     */
    public JButton getJB_RemoveFaculty(){
        return JB_RemoveFaculty;
    }

    /**
     * @return addButtonClick
     */
    public boolean getButtonAddClick(){
        return addButtonClick;
    }

    /**
     * @return JB_RemoveGroup
     */
    public JButton getJB_RemoveGroup(){
        return JB_RemoveGroup;
    }

    /**
     * @return The text of the JTF_FirstName
     */
    public String getTextFromJTF_FirstName(){
        return JTF_FirstName.getText();
    }

    /**
     * @return The text of the JTF_LastName
     */
    public String getTextFromJTF_LastName(){
        return JTF_LastName.getText();
    }

    /**
     * @return The text of the JTF_Enrolled
     */
    public String getTextFromJTF_Enrolled(){
        return JTF_Enrolled.getText();
    }

    /**
     * @return JB_AddStudent
     */
    public JButton getJB_AddStudent(){
        return JB_AddStudent;
    }

    /**
     * @return  JT_Students
     */
    public JTable getJT_Students(){
        return JT_Students;
    }

    /**
     * @return List of the students
     */
    public List<Student> getStudents(){
        return students;
    }

    /**
     * @return JB_Search
     */
    public JButton getJB_SearchStudent(){
        return JB_Search;
    }

    /**
     * @return The text of the JTF_Search
     */
    public String getTextFromJTF_Search(){
        return JTF_Search.getText();
    }

    /**
     * @return JB_DeleteStudent
     */
    public JButton getJB_RemoveStudent(){ return JB_DeleteStudent; }

    /**
     * @return JB_UpdateStudent
     */
    public JButton getJB_UpdateStudent() {return JB_UpdateStudent; }

    /**
     * @return JB_Reset
     */
    public JButton getJB_ResetTable() { return JB_Reset; }

}
