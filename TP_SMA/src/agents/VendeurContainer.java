package agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
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

public class VendeurContainer extends Application {
    protected VendeurAgent vendeurAgent;
    private ObservableList<String> observableList;
    private AgentContainer agentContainer;

    public void setVendeurAgent(VendeurAgent vendeurAgent) {
        this.vendeurAgent = vendeurAgent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        agentContainer = runtime.createAgentContainer(profile);
    }

    public void logMessage(ACLMessage aclMessage) {
        Platform.runLater(() -> {
            observableList.add(aclMessage.getContent());
        });
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
        HBox hBox = new HBox();
        Label label = new Label("Agent name : ");
        TextField textFieldAgentName = new TextField();
        Button buttonDeploy = new Button("Deploy");
        hBox.getChildren().addAll(label,
                textFieldAgentName,
                buttonDeploy);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        buttonDeploy.setOnAction(e -> {
            if (textFieldAgentName.getText().toString() != null) {
                AgentController agentController = null;
                try {
                    String agentName = textFieldAgentName.getText().toString();
                    agentController = agentContainer.createNewAgent(agentName, "agents.VendeurAgent", new Object[]{VendeurContainer.this});
                    agentController.start();
                } catch (StaleProxyException ex) {
                    ex.printStackTrace();
                }
            }
        });
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Vendeur");
        stage.show();
    }
}
