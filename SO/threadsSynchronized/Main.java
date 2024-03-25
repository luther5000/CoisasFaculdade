import java.util.*;

public class Main{
    public static void main(String[] args){

        /* criacao das strings que serao enviadas para os produtores
         * obs: as frases foram geradas pelo chatGPT por falta de
         * criatividade minha para criar frases que nao tenham
         * palavras repetidas*/
        ArrayList<String> mensagens = new ArrayList<>();
        mensagens.add("O sol brilha no ceu azul");
        mensagens.add("A chuva cai suavemente sobre a terra molhada");
        mensagens.add("Os passaros voam livremente entre as arvores verdes");
        mensagens.add("A lua ilumina a noite escura com seu brilho prateado");
        mensagens.add("As flores perfumam os campos");
        mensagens.add("Os lirios do campo florescem delicadamente ao amanhecer");
        mensagens.add("A grama verde oscila suavemente com a brisa fresca da manhã");
        mensagens.add("As estrelas cintilam no firmamento noturno");

        Buffer buffer = new Buffer(7, 4);
        int[] ids = {0, 1, 2, 3};

        Produtor[] produtor = new Produtor[4];
        Consumidor[] consumidor = new Consumidor[4];

        for (int i = 0; i < 4; ++i){
            produtor[i] = new Produtor();
            consumidor[i] = new Consumidor();
        }

        for (int i = 0; i < 4; ++i){
            produtor[i].executaProdutor(mensagens.get(i), buffer, ids[i]);
            consumidor[i].executaConsumidor(buffer, ids[i]);
        }

        do {
            /* vamos aguardar todas as threads consumidoras terem lido
             * uma palavra para podermos fazer a limpeza no buffer */
            if (!buffer.todasConsumiram()){
                try {
                    Thread.sleep(200);
                    continue;
                }catch (Exception e){
                    continue;
                }
            }

            /* agora que todas consumiram iremos limpar o buffer */
            buffer.clearBuffer();

            //tudo sera feito enquanto houverem caracteres no buffer
        } while (buffer.getQuantNoBuffer() > 0 || produtor[0].isAlive() ||
                produtor[1].isAlive() || produtor[2].isAlive() ||
                produtor[3].isAlive());

        /* informamos ao buffer que nao ha mais leituras a serem realizadas */
        buffer.acabou();

        //vamos esperar as threads consumidoras acabarem
        try {
            for (int i = 0; i < 8; ++i)
                consumidor[i].join();
        } catch (Exception e){
            System.exit(0);
        }
    }
}

class Produtor extends Thread{
    public void executaProdutor(String mensagem, Buffer buffer, int id){
        //separamos a mensagem toda em palavras
        String[] mensagemSeparada = mensagem.split(" ");

        //criamos a funcao que sera executada pelas threads produtoras
        Runnable runnable = () -> {

            while (!Thread.currentThread().isInterrupted()) {
                boolean foiInserido = buffer.insereBuffer(mensagemSeparada[0], id);
                if (foiInserido){
                    for (int i = 1; i < mensagemSeparada.length; ++i){
                        foiInserido = buffer.insereBuffer(mensagemSeparada[i], id);

                        if (!foiInserido){
                            --i;
                            try {
                                Thread.sleep(150);
                            } catch (Exception e){
                                continue;
                            }
                        }
                    }
                    Thread.currentThread().interrupt();
                    buffer.produziTudo();
                } else {
                    try{
                        Thread.sleep(300);
                    } catch (Exception e){
                        continue;
                    }
                }
            }
        };

        //criamos a thread e a executamos
        Thread thread = new Thread(runnable);
        thread.start();
    }
}

class Consumidor extends Thread {
    public void executaConsumidor(Buffer buffer, int id) {
        /* esse id recebido pela main servira para a identificacao
         * da thread no buffer especialmente */

        //criamos a funcao que sera executada pelas threads consumidoras
        Runnable runnable = () -> {
            //a thread ira executar enquanto nao for interrompida
            while (!Thread.currentThread().isInterrupted()) {
                //recebemos a mensagem do buffer
                String mensagem = buffer.getFromBuffer(id);

                /* caso essa mensagem seja -1, quer dizer que e para
                 * a thread interromper sua execucao porque acabou as
                 * palavras para serem lidas */
                if (mensagem.equals("-1")) {
                    interrupt();
                    continue;
                }

                /* caso a mensagem recebida seja vazia, quer dizer que ou
                 * a thread atual ja leu a palavra ou nao ha nada no
                 * buffer no momento para ser lido, entao a thread espera
                 * um pouco antes de continuar sua execucao */
                if (mensagem.isEmpty()) {
                    try {
                        Thread.sleep(200);
                        continue;
                    } catch (Exception e){
                        continue;
                    }
                }

                /* caso a thread nao entre em nenhum dos ifs, a mensagem
                 * recebida era o esperado e sera imprimido pela thread */
                System.out.print("ID '" + getId() + "': " + mensagem + " | ");
            }
        };
        //criamos a thread e a executamos
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
