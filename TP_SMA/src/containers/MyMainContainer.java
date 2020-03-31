package containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class MyMainContainer {

    public static void main(String[] args) throws Exception{
        /*Pour créer une instance de JADE*/
        Runtime runtime = Runtime.instance();
        /*Pour la configuration du container*/
        ProfileImpl profile = new ProfileImpl();
        /*Pour afficher l'interface graphique avec le démarragae*/
        profile.setParameter(ProfileImpl.GUI,"true");
        AgentContainer agentContainer = runtime.createMainContainer(profile);
        /*Pour démarrer le MainContainer*/
        agentContainer.start();
    }
}
