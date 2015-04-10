package View;

import Model.Faculty;
import Model.Student;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import Exception.*;

public class MainView extends JFrame implements ClientView {

    private JPanel JP_Filter = new JPanel();
    private JPanel JP_Create = new JPanel();
    private JSplitPane JSP_Create = new JSplitPane();
    private JLabel JL_Faculty = new JLabel("Faculty:");
    private JComboBox<Faculty> JCB_Faculty = new JComboBox<>();
    private JLabel JL_Group = new JLabel("Group:");
    private JComboBox JCB_Group = new JComboBox();
    private JButton JB_AddFaculty = new JButton("+");
    private JButton JB_DeleteFaculty = new JButton("-");
    private JButton JB_AddGroup = new JButton("+");
    private JButton JB_DeleteGroup = new JButton("-");
    private JButton JB_Update = new JButton("OK");
    private JButton JB_Search = new JButton("OK");
    private JButton JB_Reset = new JButton("Reset");
    private JTextField JTF_Search = new JTextField("Insert the Last Name...");
    private JButton JB_DeleteStudent = new JButton("DELETE");
    private JTextField JTF_Name = new JTextField("Insert the Name...");
    private JButton JB_AddNewFaculty = new JButton("OK");
    private JButton JB_CancelCreate = new JButton("CANCEL");
    private JTable JT_Students;
    DefaultTableModel model;
    private List<Student> students = new ArrayList<>();
    private boolean addButtonClick;
    private JLabel JL_FirstName = new JLabel("First Name:");
    private JTextField JTF_FirstName = new JTextField("");
    private JLabel JL_LastName = new JLabel("Last Name:");
    private JTextField JTF_LastName = new JTextField("");
    private JLabel JL_Enrolled = new JLabel("Enrolled Year:");
    private JTextField JTF_Enrolled = new JTextField("");
    private JButton JB_AddStudent = new JButton("ADD");
    private JButton JB_UpdateStudent = new JButton("UPDATE");
    private JPanel JP_CreateStudent = new JPanel(new GridBagLayout());
    private JPanel JP_South = new JPanel(new BorderLayout());
    private static final Logger logger = Logger.getLogger(MainView.class);

    public MainView(){
        super("Information System for students.");
        BuildMainView();
    }

    private void BuildMainView(){
        setSize(800, 800);
        setLocation(500,150);
        setResizable(false);
        ConfigureMainWindow();
        setVisible(true);
    }

    @Override
    public void close() {

    }

    private void ConfigureMainWindow(){

        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JP_Filter.setLayout(new GridBagLayout());
        JP_Filter.setBorder(BorderFactory.createTitledBorder("Filter"));
        addComponentToMainPanel(JL_Faculty, 0, 0, 1, 5, 0, 0, 5);
        addComponentToMainPanel(JCB_Faculty, 1, 0, 1, 5, 0, 0, 0);
        addComponentToMainPanel(JB_AddFaculty, 3, 0, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_DeleteFaculty, 4, 0, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JL_Group, 0, 1, 1, 5, 0, 0, 5);
        addComponentToMainPanel(JCB_Group, 1, 1, 2, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_AddGroup, 3, 1, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_DeleteGroup, 4, 1, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_Update, 0, 3, 5, 5, 0, 10, 0);
        JSP_Create.setDividerLocation(400);
        JSP_Create.setDividerSize(0);
        JB_AddFaculty.addActionListener(setActionToButtonNew());
        JB_AddGroup.addActionListener(setActionToButtonNew());
        JSP_Create.setLeftComponent(JP_Filter);
        JP_Create.setLayout(new BorderLayout());
        JP_Create.setBorder(BorderFactory.createTitledBorder("Search"));
        JP_Create.add(JTF_Search, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(JB_Search);
        buttonPanel.add(JB_Reset);
        JP_Create.add(buttonPanel, BorderLayout.SOUTH);
        JSP_Create.setRightComponent(JP_Create);

        try {
            JT_Students = new JTable(getObjectModel(students)){

                    @Override
                    public boolean isCellEditable ( int row, int column )
                    {
                        return false;
                    }

            };
        } catch (ClientException e) {
            e.printStackTrace();
        }

        JTF_Search.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTF_Search.setText(null);
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        JB_CancelCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSP_Create.setRightComponent(null);
                JSP_Create.setDividerLocation(400);
                JSP_Create.setRightComponent(JP_Create);
            }
        });

        JScrollPane JS_Table = new JScrollPane(JT_Students);
        getContentPane().add(JS_Table, BorderLayout.CENTER);
        getContentPane().add(JSP_Create, BorderLayout.NORTH);
        buildCreateStudentPanel();
        JB_DeleteStudent.setEnabled(false);
        JP_South.add(JB_DeleteStudent, BorderLayout.NORTH);
        JP_CreateStudent.setBorder(BorderFactory.createTitledBorder("Edit"));
        JP_South.add(JP_CreateStudent, BorderLayout.CENTER);
        getContentPane().add(JP_South, BorderLayout.SOUTH);
    }

    public void buildTable(final List<Student> students){
        try {

            DefaultTableModel tModel = getObjectModel(students);
            JT_Students.setModel(tModel);
            JT_Students.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JB_DeleteStudent.setEnabled(true);
                    Student student = students.get(JT_Students.getSelectedRow());
                    JTF_FirstName.setText(student.getFirstName());
                    JTF_LastName.setText(student.getLastName());
                    JTF_Enrolled.setText(student.getEnrolled());
                    JP_CreateStudent.updateUI();
                }
            });
        JT_Students.updateUI();
        } catch (ClientException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            showMessage("Non students!");
        }
    }
    private void addComponentToMainPanel (JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy ){
        JP_Filter.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, wy, hx, hy), 0, 0));
    }
    private void addComponentToCreateStudentPanel (JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy ){
        JP_CreateStudent.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, wy, hx, hy), 0, 0));
    }

    public void buildCreateStudentPanel(){
        addComponentToCreateStudentPanel(JL_FirstName, 0, 0, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JTF_FirstName, 1, 0, 2, 10, 0, 10, 20);
        addComponentToCreateStudentPanel(JL_LastName, 0, 1, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JTF_LastName, 1, 1, 2, 10, 0, 10, 20);
        addComponentToCreateStudentPanel(JL_Enrolled, 0, 2, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JTF_Enrolled, 1, 2, 2, 10, 0, 10, 20);
        addComponentToCreateStudentPanel(JB_AddStudent, 1, 3, 1, 10, 20, 10, 5);
        addComponentToCreateStudentPanel(JB_UpdateStudent, 2, 3, 1, 10, 0, 10, 20);
    }

    public DefaultTableModel getObjectModel(List<Student> students)  throws ClientException  {
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get object model");
        }

        this.students = students;
        String[][] data = new String[students.size()][3];
        Object[] columnNames = new Object[3];
        columnNames[0] = "First Name";
        columnNames[1] = "Last Name";
        columnNames[2] = "Enrolled";

        int i = 0;
        while(i < students.size()){
            for (Student st : students) {
                data[i][0] = st.getFirstName();
                data[i][1] = st.getLastName();
                data[i][2] = st.getEnrolled();
                i++;
            }}

        model = new DefaultTableModel(data, columnNames);
        return model;
    }

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
                buttonPanel.add(JB_AddNewFaculty);
                buttonPanel.add(JB_CancelCreate);
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

    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message );
    }

    public JComboBox getJCB_Faculty(){
        return JCB_Faculty;
    }

    public JComboBox getJCB_Group(){
        return JCB_Group;
    }

    public JButton getJB_Update(){
        return JB_Update;
    }

    public String getNew(){
        return JTF_Name.getText();
    }

    public JButton getJButtonAddNEw(){
        return JB_AddNewFaculty;
    }

    public JButton getJB_DeleteFaculty(){
        return JB_DeleteFaculty;
    }

    public boolean getButtonAddClick(){
        return addButtonClick;
    }

    public JButton getJB_DeleteGroup(){
        return JB_DeleteGroup;
    }

    public String getTextFromJTF_FirstName(){
        return JTF_FirstName.getText();
    }

    public String getTextFromJTF_LastName(){
        return JTF_LastName.getText();
    }

    public String getTextFromJTF_Enrolled(){
        return JTF_Enrolled.getText();
    }

    public JButton getJB_AddStudent(){
        return JB_AddStudent;
    }

    public JTable getJT_Students(){
        return JT_Students;
    }

    public List<Student> getStudents(){
        return students;
    }

    public JButton getJB_Search(){
        return JB_Search;
    }

    public String getTextFromJTF_Search(){
        return JTF_Search.getText();
    }

    public JButton getJB_DeleteStudent(){ return JB_DeleteStudent; }

    public JButton getJB_UpdateStudent() {return JB_UpdateStudent; }

    public JButton getJB_Reset() { return JB_Reset; }



}
