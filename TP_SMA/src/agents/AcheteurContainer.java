package agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurContainer extends Application {
    protected AcheteurAgent acheteurAgent;
    private ObservableList<String> observableList;

    public void setAcheteurAgent(AcheteurAgent acheteurAgent) {
        this.acheteurAgent = acheteurAgent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Acheteur", "agents.AcheteurAgent", new Object[]{this});
        agentController.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Acheteur");
        stage.show();
    }

    public void logMessage(ACLMessage aclMessage) {
        Platform.runLater(() -> {
            String message = aclMessage.getContent();
            observableList.add(message);
        });
    }
}
