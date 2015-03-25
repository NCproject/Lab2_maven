package View;

import Model.Client;
import Model.Faculty;
import Model.Group;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


import Exception.*;

import static java.awt.BorderLayout.CENTER;

public class MainView extends JFrame implements ClientView {

    private JPanel JP_Filter = new JPanel();
    private JPanel JP_Create = new JPanel();
    private JSplitPane JSP_Create = new JSplitPane();
    private JScrollPane JS_Table = new JScrollPane();
    private JLabel JL_Faculty = new JLabel("Faculty:");
    private JComboBox<Faculty> JCB_Faculty = new JComboBox<>();
    private JLabel JL_Group = new JLabel("Group:");
    private JComboBox JCB_Group = new JComboBox();
    private JButton JB_AddFaculty = new JButton("+");
    private JButton JB_DeleteFaculty = new JButton("-");
    private JButton JB_AddGroup = new JButton("+");
    private JButton JB_DeleteGroup = new JButton("-");

    private LoginView loginView;
    private JButton JB_Update = new JButton("OK");

    private JButton JB_Search = new JButton("OK");
    private JButton JB_Reset = new JButton("Reset");
    private JTextField JTF_Search = new JTextField("Name of student...");
    private JButton JB_SignIn = new JButton("Sign in");

    private JLabel JLName = new JLabel("Name:");
    private JTextField JTF_Name = new JTextField("Name ...");
    private JButton JB_AddNewFaculty = new JButton("OK");
    private JButton JB_CancelCreate = new JButton("CANCEL");

    private Object[] headers = {"First Name", "Last Name", "Faculty", "Group", "Enrolled"};
    private JTable JT_Students;
   /* private Object[][] data = {
            { "John", "Smith", "Electronic", "112", "2002" }

    };*/

    private  String[] ist;

    private boolean addButtonClick;

    /** The logger. */
    private static final Logger logger = Logger.getLogger(MainView.class);

    private List<Faculty> facultyList;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // JT_Students = new JTable(data, headers);

        JP_Filter.setLayout(new GridBagLayout());
        JP_Filter.setBorder(BorderFactory.createTitledBorder("Filter"));

        addComponentToMainPanel(JL_Faculty, 0, 0, 1, 5, 0, 0, 5);
        addComponentToMainPanel(JCB_Faculty, 1, 0, 1, 5, 0, 0, 0);
        addComponentToMainPanel(JB_AddFaculty, 3, 0, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_DeleteFaculty, 4, 0, 1, 5, 0, 0, 0 );
        setEnabledForComponents(false);
        addComponentToMainPanel(JL_Group, 0, 1, 1, 5, 0, 0, 5 );
        addComponentToMainPanel(JCB_Group, 1, 1, 2, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_AddGroup, 3, 1, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_DeleteGroup, 4, 1, 1, 5, 0, 0, 0 );
        addComponentToMainPanel(JB_Update, 0, 3, 5, 5, 0, 10, 0 );
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

        getContentPane().add(JSP_Create, BorderLayout.NORTH);
        getContentPane().add(new JPanel(new BorderLayout()).add(JB_SignIn), BorderLayout.SOUTH);

    }

    private void addComponentToMainPanel (JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy ){
        JP_Filter.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, wy, hx, hy), 0, 0));
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message );
    }

    private void setEnabledForComponents(boolean flag){
        JB_AddFaculty.setEnabled(flag);
        JB_DeleteFaculty.setEnabled(flag);
        JB_AddGroup.setEnabled(flag);
        JB_DeleteGroup.setEnabled(flag);
    }

    public LoginView getLoginView(){
        return loginView;
    }

    public JButton getButtonSignIn(){
        return JB_SignIn;
    }

    public JComboBox getJCB_Faculty(){
        return JCB_Faculty;
    }

    public JComboBox getJCB_Group(){
        return JCB_Group;
    }

    public void setEnabledComponents(boolean flag){
        JB_AddFaculty.setEnabled(flag);
        JB_DeleteFaculty.setEnabled(flag);
        JB_AddGroup.setEnabled(flag);
        JB_DeleteGroup.setEnabled(flag);
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
                if (e.getSource().equals(JB_AddFaculty)){
                    addButtonClick = true;

                } else {
                    addButtonClick = false;
                }
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

    public boolean getButtonAddClick(){
        return addButtonClick;
    }

    public JButton getJB_DeleteGroup(){
        return JB_DeleteGroup;
    }

    public void setModelToTableOfStudents(DefaultTableModel dm){
        JT_Students = new JTable(dm);
        JS_Table = new JScrollPane(JT_Students);
        getContentPane().add(JS_Table, BorderLayout.CENTER);
        JS_Table.setVisible(true);
    }

}
