import Controller.ClientController;
import Model.*;
import View.ClientView;
import View.MainView;

public class MainClass {

    public static void main(String[] args){
        Client client = new Client();
        MainView mainView = new MainView();
        ClientController controller = new ClientController(client, mainView);
    }



    // mainView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //mainView.pack();
    //mainView.setVisible(true);
}
