package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {
    protected VendeurContainer vendeurContainer;

    @Override
    protected void setup() {
        super.setup();
        if (getArguments().length == 1) {
            vendeurContainer = (VendeurContainer) getArguments()[0];
            vendeurContainer.setVendeurAgent(this);
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                dfAgentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("Transaction");
                serviceDescription.setName("vente-livres");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, dfAgentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CFP:
                            vendeurContainer.logMessage(aclMessage);
                            ACLMessage reply = aclMessage.createReply();
                            int prix = new Random().nextInt((450 - 100) + 1) + 100;
                            reply.setContent(String.valueOf(prix));
                            reply.setPerformative(ACLMessage.PROPOSE);
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            vendeurContainer.logMessage(aclMessage);
                            ACLMessage reply1 = aclMessage.createReply();
                            reply1.setPerformative(ACLMessage.AGREE);
                            reply1.setContent(aclMessage.getContent());
                            send(reply1);
                            break;
                    }
                } else block();
            }
        });
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
    }
}
