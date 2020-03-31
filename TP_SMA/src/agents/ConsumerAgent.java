package agents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/*GuiAgent hérite de Agent*/
/*Pour que l'agent intéragit correctement avec le conteneur*/
public class ConsumerAgent extends GuiAgent {
    /*transient = pas sérialisable*/
    /*Quand l'agent migre, il ne vas pas ramener le container*/
    /*Association bidirctionnelle*/
    protected transient ConsumerContainer consumerContainer;
    /*Cycle de vie*/

    /*Cette méthode est exécutée une fois l'agent est déployé dans un conteneur*/
    @Override
    protected void setup() {
        super.setup();
        if(getArguments().length ==1) {
            consumerContainer = (ConsumerContainer) getArguments()[0];
            consumerContainer.setConsumerAgent(this);
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    System.out.println("Réception du message : ");
                    System.out.println(aclMessage.getContent());
                    System.out.println(aclMessage.getSender().getName());
                    System.out.println(aclMessage.getPerformative());
                    System.out.println(aclMessage.getLanguage());
                    System.out.println(aclMessage.getOntology());
                    consumerContainer.logMessage(aclMessage);
            }
                else
                    block();
            }
        });
        addBehaviour(parallelBehaviour);
    }

    /*Méthodes de la migration*/

     /*Avant la migration*/
    @Override
    protected void beforeMove() {
        super.beforeMove();
        System.out.println("Avant la migration");
    }

    /*Avant la migration*/
    /*Apres qu'il rentre dans une autre machine*/
    @Override
    protected void afterMove() {
        super.afterMove();
        System.out.println("Apres la migration");
    }

    /*Exécuté avant de détruire l'agent*/
    @Override
    protected void takeDown() {
        super.takeDown();
        System.out.println("Entrain de mourir ...");
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            String livre = guiEvent.getParameter(0).toString();
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(livre);
            aclMessage.addReceiver(new AID("Acheteur",AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}
