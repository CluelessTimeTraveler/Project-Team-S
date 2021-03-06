package edu.wpi.cs3733.c20.teamS.twoFactor;
import edu.wpi.cs3733.c20.teamS.Editing.MapEditingScreen;
import edu.wpi.cs3733.c20.teamS.database.DatabaseController;
import edu.wpi.cs3733.c20.teamS.database.EmployeeData;
import edu.wpi.cs3733.c20.teamS.serviceRequests.Employee;
import edu.wpi.cs3733.c20.teamS.utilities.TFAThread;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class TwoFactorScreen {
    private TwoFactorScreenController tfa;
    private Stage toPass;
    private Scene scene;
    private Stage stage;


    String carrier;
    int tfaCode;
    private Employee loggedIn;

    public TwoFactorScreen(Stage passedStage, Employee employee) {
        this.loggedIn = employee;
        toPass = passedStage;
        DatabaseController dbc = new DatabaseController();
        this.stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/TwoFactorScreen.fxml"));
        loader.setControllerFactory(c -> {
            this.tfa = new TwoFactorScreenController(stage, loggedIn);
            tfa.tfaScreen = this;
            return this.tfa;
        });
        try {
            Parent root = loader.load();

            this.scene = new Scene(root);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.show();
        //stage.setFullScreen(false);
        stage.setResizable(false);
        stage.setTitle("Two Factor Authentication");

        //stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();
    }

    public void show() {
        stage.setScene(scene);
        //stage.setMaximized(f);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }


    public void passedTFA(){

        MapEditingScreen mes = new MapEditingScreen(toPass, loggedIn);
        this.stage.close();
    }

    public void sendTFA(){
        DatabaseController dbc = new DatabaseController();
        EmployeeData empData = dbc.getEmployeeFromID(loggedIn.id());

        int code = TwoFactorAuthenticator.generateCode();
        TwoFactorAuthenticator tfa = new TwoFactorAuthenticator(empData, carrier, code);
        TFAThread et = new TFAThread(tfa);
        tfaCode = code;
        et.start();
        //int code = et.tfa.runTextTFA(empData,carrier);
        System.out.println("this code: "+ code);
    }

    public void setCarrier(String carrier){
        this.carrier = carrier;
    }



}
