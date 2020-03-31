package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {
    protected ConsumerAgent consumerAgent;
    private ObservableList<String> observableList;

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void startContainer() throws Exception
    {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Consumer","agents.ConsumerAgent",new Object[]{this});
        agentController.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Label label = new Label("Livre a acheter : ");
        TextField textFieldLivre = new TextField();
        Button buttonAcheter = new Button("OK");
        hBox.getChildren().addAll(label,
                textFieldLivre,
                buttonAcheter);
        VBox vBox = new VBox();
        ListView<String> listViewMessages = new ListView<String>();
        observableList = FXCollections.observableArrayList();
        listViewMessages.setItems(observableList);
        vBox.getChildren().add(listViewMessages);
        vBox.setPadding(new Insets(10));
        borderPane.setCenter(vBox);
        buttonAcheter.setOnAction( e -> {
            String livre = textFieldLivre.getText();
            observableList.add(livre);
            GuiEvent guiEvent = new GuiEvent(this,1);
            guiEvent.addParameter(livre);
            consumerAgent.onGuiEvent(guiEvent);
        });
        borderPane.setTop(hBox);
        Scene scene = new Scene(borderPane,600,400);
        stage.setScene(scene);
        stage.setTitle("Consommateur");
        stage.show();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater( () -> {
            observableList.add(aclMessage.getContent());
        });
    }
}