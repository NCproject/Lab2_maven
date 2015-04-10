import Controller.AccessController;
import Controller.ClientController;
import Model.*;
import View.ClientView;
import View.LoginView;
import View.MainView;

public class MainClass {

    public static void main(String[] args){
        Client client = new Client();
        LoginView loginView= new LoginView();
        AccessController controller = new AccessController(client, loginView);
    }

}
