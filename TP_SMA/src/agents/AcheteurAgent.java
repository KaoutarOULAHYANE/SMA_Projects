package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {

    protected transient AcheteurContainer achteurContainer;
    private AID[] vendeurs;

    @Override
    protected void setup() {
        super.setup();
        if (getArguments().length == 1) {
            achteurContainer = (AcheteurContainer) getArguments()[0];
            achteurContainer.setAcheteurAgent(this);
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private List<ACLMessage> listMsg = new ArrayList<>();
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                    MessageTemplate.or(
                          MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                          MessageTemplate.MatchPerformative(ACLMessage
                                        .REJECT_PROPOSAL)
                        )
                    )
                );
                ACLMessage aclMessage = receive(messageTemplate);
                if (aclMessage != null) {
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.REQUEST:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setContent("Demande reçu du livre : " + aclMessage.getContent());
                            send(reply);

                            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                            msg.setContent("Quel est le prix du livre : " + aclMessage.getContent() + " ?");
                            for (AID aid : vendeurs) {
                                msg.addReceiver(aid);
                            }
                            send(msg);
                            break;
                        case ACLMessage.PROPOSE:
                            listMsg.add(aclMessage);
                            achteurContainer.logMessage(aclMessage);
                            if (listMsg.size() == vendeurs.length) {
                                int minPrix = Integer.valueOf(listMsg.get(0).getContent());
                                ACLMessage meilleurOffre = listMsg.get(0);
                                for (ACLMessage acl : listMsg) {
                                    int prixACL = Integer.valueOf(acl.getContent());
                                    if (prixACL < minPrix) {
                                        meilleurOffre = acl;
                                        minPrix = prixACL;
                                    }
                                }
                                ACLMessage reply1 = meilleurOffre.createReply();
                                reply1.setPerformative(ACLMessage.
                                        ACCEPT_PROPOSAL);
                                reply1.setContent(meilleurOffre.getContent());
                                send(reply1);
                            }
                            break;
                        case ACLMessage.AGREE:
                            achteurContainer.logMessage(aclMessage);
                            ACLMessage aclMessage1 = new ACLMessage(ACLMessage.INFORM);
                            aclMessage1.setContent("Le prix minimal est : " + aclMessage.getContent());
                            aclMessage1.addReceiver(new AID("Consumer", AID.ISLOCALNAME));
                            send(aclMessage1);
                            break;
                        case ACLMessage.REFUSE:
                            break;
                    }
                } else block();
            }
        });

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("Transaction");
                serviceDescription.setName("vente-livres");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] res = DFService.search(myAgent, dfAgentDescription);
                    vendeurs = new AID[res.length];
                    for (int i = 0; i < vendeurs.length; i++)
                        vendeurs[i] = res[i].getName();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
    }
}
