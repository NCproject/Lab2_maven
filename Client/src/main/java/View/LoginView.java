package View;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    /** User Login */
    private JLabel JL_UserName = new JLabel("Login:");
    private JTextField TF_UserName = new JTextField("");

    /** User Password */
    private JLabel JL_UserPassword = new JLabel("Password:");
    private JTextField TF_UserPassword = new JTextField("");

    private JButton B_Ok = new JButton("Sign in");
    private JPanel P_Login;


    /**
     * Instantiates a new LoginView
     */
    public LoginView(){
        super("Login");
        configureLoginView();
        buildLoginPanel();
    }

    /**
     * Specifies the configuration LoginView.
     */
    private void configureLoginView(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400,100);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Creates a MainPanel on the LoginView with all components
     */
    private void buildLoginPanel(){
        P_Login = new JPanel();
        P_Login.setLayout(new GridBagLayout());
        addComponentToMainPanel(JL_UserName, 0, 0, 1, 20, 10, 30, 10);
        addComponentToMainPanel(TF_UserName, 1, 0, 1, 20, 10, 10, 30);
        addComponentToMainPanel(JL_UserPassword, 0, 1, 1, 0, 10, 30, 10);
        addComponentToMainPanel(TF_UserPassword, 1, 1, 1, 0, 10, 10, 30);
        addComponentToMainPanel(B_Ok, 1, 2, 1, 0, 10, 10, 30);
        add(P_Login);
        pack();
    }

    /**
     * Adds components to the MainPanel
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
    private void addComponentToMainPanel(JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy) {
        P_Login.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, hx, wy, hy), 0, 0));
     }

    /**
     * Shows messageDialog
     *
     * @param message message
     */
    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message );
    }

    /**
     * @return Text from TF_UserName
     */
    public String getUserName(){
        return TF_UserName.getText();
    }

    /**
     * @return Text from TF_UserPassword
     */
    public String getUserPassword(){
        return TF_UserPassword.getText();
    }

    /**
     * Sets null to the TF_UserPassword and TF_UserName
     */
    public void setNullToTF(){
        TF_UserPassword.setText(null);
        TF_UserName.setText(null);
    }

    /**
     * @return JB_OK
     */
    public JButton getButtonOK(){
        return B_Ok;
    }
}
