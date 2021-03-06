package Agentes;

import comportamentos.ComportamentoSequencial;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import java.net.URL;

import java.util.Collections;

import java.util.LinkedList;
import javax.swing.ImageIcon;
import objetos.Carta;

public class Carteador extends Agent {

    protected LinkedList<Carta> baralho = new LinkedList<Carta>();
    String[] nomesJog = {"Jogador1", "Jogador2", "Jogador3", "Jogador4"};
    String msgRespostas = "";
    boolean distribuiu = false, maoCompleta = true;
    protected AID[] agentesJogadores;
    int i = 0;
    int[] qtdCartas = {0, 0, 0, 0};
    int contJog = 0;

    public void geraCartas() {
         ClassLoader cldr = this.getClass().getClassLoader();
        Carta card;
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 14; j++) {
                card = new Carta(j, i);
                String imagePath = "imagens/" + j
                        + i + ".gif";
//                System.out.println(imagePath);
                URL imageURL = cldr.getResource(imagePath);
                ImageIcon img = new ImageIcon(imageURL);
                card.imagem = img;
                baralho.add(card);
            }
        }
//        for (Carta carta : baralho) {
//            System.out.println(carta.toString());
//        }

        Collections.shuffle(baralho);
    }

    protected void setup() {

        geraCartas();
        geraCartas();
        //        comportamento senquencial faz ele executar um comportamento de cada vez
        ComportamentoSequencial comp = new ComportamentoSequencial(this);
        addBehaviour(comp);
        
        
        
        
        //distribui as cartas pros jogadores 
        comp.adicionaComp(new Behaviour() {

            @Override
            public void action() {
                try {

                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(new AID(nomesJog[i], AID.ISLOCALNAME));
                    msg.setContentObject((Carta) baralho.pop());
                    myAgent.send(msg);
                    ACLMessage reply = blockingReceive();
                    if (reply != null) {
                        qtdCartas[i] = Integer.parseInt(reply.getContent());

                    } else {
                        block();
                    }
//                     
                    distribuiu = true;
                    for (int j = 0; j < 4; j++) {
                        if (qtdCartas[j] != 9) {
                            distribuiu = false;
                        }
                    }
                    i++;
                    if (i == 4) {
                        i = 0;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public boolean done() {
                return distribuiu;
            }

            @Override
            public int onEnd() {

                System.out.println("Envio Cartas para Jogadoers " + baralho.size());
                return 0;
            }
        }
        );

        comp.adicionaComp(new Behaviour() {
            int[] qtdCartas = new int[4];

            @Override
            public void action() {
                try {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(new AID("Juiz", AID.ISLOCALNAME));
                    msg.setContentObject(baralho);
                    myAgent.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean done() {
                return true;
            }

            @Override
            public int onEnd() {

                System.out.println("Envio Cartas para Juiz" + baralho.size());
                return 0;
            }
        }
        );

    }

}
