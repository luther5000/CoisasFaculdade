public class Buffer {
    private String[] buffer;//o buffer propriamente dito
    private final int tamBuffer;//tamanho do buffer
    private int proxInsercao;//o proximo lugar onde deve ser inserido no buffer
    private int proxLeitura;//o proximo lugar onde deve ser lido no buffer
    private int quantNoBuffer;//quantas coisas estao no buffer
    private boolean[] flagsConsumidor;//flag para guardar a informacao se determinada variavel ja consumiu ou nao aquela palavra
    private boolean acabou;//boolean que informa ao buffer se nao ha mais leituras a serem realizadas
    private final int numThreads;//a quantidade de threads consumidoras que irao acessar o buffer
    private int qualProdutor;

    public Buffer(int tam, int numThreads){
        buffer = new String[tam];
        tamBuffer = tam;
        proxInsercao = 0;
        proxLeitura = 0;
        quantNoBuffer = 0;
        flagsConsumidor = new boolean[numThreads];
        qualProdutor = -1;
        this.numThreads = numThreads;
        acabou = false;
    }

    synchronized boolean insereBuffer(String palavra, int id) {
        //verificacao para saber se o buffer esta cheio
        if(this.quantNoBuffer + 1 > this.tamBuffer)
            return false;

        if (qualProdutor == -1 || qualProdutor == id) {
            qualProdutor = id;
            //insere no buffer
            buffer[proxInsercao] = palavra;

            /* atualiza o proximo local a ser inserido e quantas coisas estao
             * no buffer */
            proxInsercao = (proxInsercao + 1) % tamBuffer;
            ++quantNoBuffer;

            return true;
        }
        return false;
    }

    synchronized String getFromBuffer(int id){
        //verifica se ha mais algo a ser lido
        if (acabou){
            return "-1";
        }
        /*verifica se essa thread ja leu a palavra atual e se
         * tem algo no buffer para ser lido atualmente */
        if (flagsConsumidor[id] || this.quantNoBuffer <= 0){
            return "";
        }

        //atualiza a variavel mostrando que essa thread ja leu a palavra atual
        flagsConsumidor[id] = true;

        //retorna para a thread a palavra
        return buffer[proxLeitura];
    }

    /* nessa funcao sera feita a limpeza do buffer e a atualizacao
     * das flags das threads consumidoras permitindo que elas possam
     * voltar a consumir */
    synchronized void clearBuffer(){
        //limpa o buffer
        proxLeitura = (proxLeitura + 1) % tamBuffer;
        --quantNoBuffer;

        //atualiza a flag
        for (int i = 0; i < this.numThreads; ++i)
            flagsConsumidor[i] = false;

        //quebra linha para deixar a saída organizada
        System.out.println();
    }

    /* funcao que retorna se todas as threads ja consumiram a
     * palavra atual do buffer. serve para que a main saiba a hora
     * de limpar o buffer */
    synchronized boolean todasConsumiram(){
        boolean todasConsumiram = true;
        for (int i = 0; i < this.numThreads; ++i)
            todasConsumiram = todasConsumiram && flagsConsumidor[i];

        return todasConsumiram;
    }
    synchronized void acabou(){
        this.acabou = true;
    }

    synchronized void produziTudo(){
        this.qualProdutor = -1;
    }

    public int getQuantNoBuffer (){
        return this.quantNoBuffer;
    }
}
