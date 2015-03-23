package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    public static String userName = "Anonymous";
    public static String userPassword = "";
    private JLabel JL_UserName = new JLabel("Login:");
    private JTextField TF_UserName = new JTextField("");
    private JLabel JL_UserPassword = new JLabel("Password:");
    private JTextField TF_UserPassword = new JTextField("");
    private JButton B_Ok = new JButton("Sign in");
    private JPanel P_Login;

    public LoginView(){
        super("Login");
        BuildLoginView();
    }

    private void BuildLoginView(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,100);
        setLocationRelativeTo(null);
        setResizable(false);

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

    public static void main(String[] args)  {
        LoginView view = new LoginView();
    }

    private void addComponentToMainPanel(JComponent comp, int x, int y, int col, int wx, int wy, int hx, int hy) {
        P_Login.add(comp, new GridBagConstraints(x, y, col, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(wx, hx, wy, hy), 0, 0));
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message );
    }

    public String getUserName(){
        return TF_UserName.getText();
    }

    public String getUserPassword(){
        return TF_UserPassword.getText();
    }

    public void setNullToTF(){
        TF_UserPassword.setText(null);
        TF_UserName.setText(null);
    }

    public JButton getButtonOK(){
        return B_Ok;
    }
}
