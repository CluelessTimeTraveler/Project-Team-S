package edu.wpi.cs3733.c20.teamS.app.serviceRequests;

import edu.wpi.cs3733.c20.teamS.app.DialogEvent;
import edu.wpi.cs3733.c20.teamS.serviceRequests.JanitorServiceRequest;
import edu.wpi.cs3733.c20.teamS.serviceRequests.LaundryServiceRequest;
import edu.wpi.cs3733.c20.teamS.serviceRequests.Employee;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class JanitorRequestController {

    private final PublishSubject<DialogEvent<JanitorServiceRequest>> dialogCompleted_ = PublishSubject.create();
    private JanitorServiceRequest request = new JanitorServiceRequest();
    private Employee loggedIn;

    @FXML
    private TextField laundryField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField commentsField;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    @FXML void onCancelClicked(ActionEvent event){
        dialogCompleted_.onNext(DialogEvent.cancel());
    }

    @FXML void onOKClicked(){
        if(!locationField.getText().equals("")) {
            request.setLocation(locationField.getText());
            request.setMessage(commentsField.getText());
            request.assignTo(loggedIn);

            dialogCompleted_.onNext(DialogEvent.ok(request));
        }
    }

    public JanitorRequestController(Employee employee){
        this.loggedIn = employee;
    }

    public Observable<DialogEvent<JanitorServiceRequest>> dialogCompleted() {
        return dialogCompleted_;
    }


}
