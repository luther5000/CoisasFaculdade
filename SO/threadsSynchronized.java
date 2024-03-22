import java.util.*;

public class Main{
    public static void main(String[] args){
        ArrayList<String> mensagens = new ArrayList<String>();
        mensagens.add("Ola, como voce esta hoje?");
        mensagens.add("Eu estou muito bem.");
        mensagens.add("E voce, como está hoje?");
        mensagens.add("Eu também estou muito bem.");

        Buffer buffer = new Buffer(7);

        Produtor[] produtor = new Produtor[4];
        Consumidor[] consumidor = new Consumidor[4];

        for (int i = 0; i < 4; ++i){
            produtor[i] = new Produtor();
            consumidor[i] = new Consumidor();
        }

        for (int i = 0; i < 4; ++i){
            produtor[i].run(mensagens.get(i), buffer);
            consumidor[i].run(buffer);
        }
        
        
    }
}

class Buffer {
    private String[] buffer;
    private final int tamBuffer;
    private int proxInsercao;
    private int proxLeitura;
    private int quantNoBuffer;

    public Buffer(int tam){
        buffer = new String[tam];
        tamBuffer = tam;
        proxInsercao = 0;
        proxLeitura = 0;
        quantNoBuffer = 0;
    }

    synchronized String acessaBuffer(String palavra, boolean vaiInserir) {
        if (vaiInserir){
            if(this.quantNoBuffer < this.tamBuffer)
                return "1";
            buffer[proxInsercao] = palavra;

            proxInsercao = (proxInsercao + 1) % tamBuffer;
            ++quantNoBuffer;
        } else{
            if (this.quantNoBuffer > 0) {
                return buffer[proxLeitura];
            } else
                return "";
        }
        return "-1";
    }

    public void clearBuffer(){
        proxLeitura = (proxLeitura + 1) % tamBuffer;
        --quantNoBuffer;
    }
}

class Produtor extends Thread{
    public void run(String mensagem, Buffer buffer){
        String[] mensagemSeparada = mensagem.split(" ");

        for (int i = 0; i < mensagemSeparada.length; i++) {
            boolean foiInserido = buffer.acessaBuffer(mensagemSeparada[i], true).equals("1");
            if (!foiInserido)
                --i;
        }
    }
}

class Consumidor extends Thread {
    private boolean acabou = false;
    private boolean flag = true;

    public void run(Buffer buffer) {
        String mensagem;
        while (!acabou) {
            while (flag) ;
            mensagem = buffer.acessaBuffer("", false);

            if (mensagem.isEmpty())
                continue;

            System.out.print("ID '" + getId() + "': " + mensagem + " ");
            flag = false;
        }
    }

    public void setAcabou(boolean acabou) {
        this.acabou = acabou;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
