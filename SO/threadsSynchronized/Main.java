import java.util.*;

public class Main{
    public static void main(String[] args){
        
        /* criacao das strings que serao enviadas para os produtores
        * obs: as frases foram geradas pelo chatGPT por falta de 
        * criatividade minha para criar frases que não tenham
        * palavras repetidas*/
        ArrayList<String> mensagens = new ArrayList<>();
        mensagens.add("O sol brilha no céu azul");
        mensagens.add("A chuva cai suavemente sobre a terra molhada");
        mensagens.add("Os pássaros voam livremente entre as árvores verdes");
        mensagens.add("A lua ilumina a noite escura com seu brilho prateado");

        Buffer buffer = new Buffer(7, 4);
        int[] ids = {0, 1, 2, 3};

        Produtor[] produtor = new Produtor[4];
        Consumidor[] consumidor = new Consumidor[4];

        for (int i = 0; i < 4; ++i){
            produtor[i] = new Produtor();
            consumidor[i] = new Consumidor();
        }

        for (int i = 0; i < 4; ++i){
            produtor[i].executaProdutor(mensagens.get(i), buffer);
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

            //tudo será feito enquanto houverem caracteres no buffer
        } while (buffer.getQuantNoBuffer() > 0);

        /* informamos ao buffer que não há mais leituras a serem realizadas */
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
    public void executaProdutor(String mensagem, Buffer buffer){
        //separamos a mensagem toda em palavras 
        String[] mensagemSeparada = mensagem.split(" ");
        
        //criamos a funcao que sera executada pelas threads produtoras
        Runnable runnable = () -> {
            
            for (int i = 0; i < mensagemSeparada.length; ++i) {
                //tentamos fazer a adição no buffer 
                boolean foiInserido = buffer.insereBuffer(mensagemSeparada[i]);
                
                /* caso não tenha sido inserido decrescemos o 
                 * contador em um para podermos tentar inserir essa 
                 * palavra novamente*/
                if (!foiInserido)
                    --i;
                
                //a thread dorme por 200 ms
                try {
                    Thread.sleep(200);
                } catch (Exception e){continue;}
            }
        };
        
        //criamos a thread e a executamos
        Thread thread = new Thread(runnable);
        thread.start();
    }
}

class Consumidor extends Thread {
    public void executaConsumidor(Buffer buffer, int id) {
        /* esse id recebido pela main servirá para a identificacao
        * da thread no buffer especialmente */
        
        //criamos a função que sera executada pelas threads consumidoras
        Runnable runnable = () -> {
            //a thread irá executar enquanto não for interrompida
            while (!Thread.currentThread().isInterrupted()) {
                //recebemos a mensagem do buffer
                String mensagem = buffer.getFromBuffer(id);

                /* caso essa mensagem seja -1, quer dizer que é para
                * a thread interromper sua execução porque acabou as 
                * palavras para serem lidas */
                if (mensagem.equals("-1")) {
                    interrupt();
                    continue;
                }

                /* caso a mensagem recebida seja vazia, quer dizer que ou
                * a thread atual já leu a palavra ou nao ha nada no 
                * buffer no momento para ser lido, então a thread espera 
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
                * recebida era o esperado e será imprimido pela thread */ 
                System.out.print("ID '" + getId() + "': " + mensagem + " | ");
            }
        };
        //criamos a thread e a executamos
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
