public class Buffer {
    private String[] buffer;//o buffer propriamente dito
    private final int tamBuffer;//tamanho do buffer
    private int proxInsercao;//o próximo lugar onde deve ser inserido no buffer
    private int proxLeitura;//o proximo lugar onde deve ser lido no buffer
    private int quantNoBuffer;//quantas coisas estão no buffer
    private boolean[] flags;//flag para guardar a informação se determinada variável já consumiu ou não aquela palavra
    private boolean acabou;//boolean que informa ao buffer se não há mais leituras a serem realizadas
    private final int numThreads;//a quantidade de threads consumidoras que irão acessar o buffer

    public Buffer(int tam, int numThreads){
        buffer = new String[tam];
        tamBuffer = tam;
        proxInsercao = 0;
        proxLeitura = 0;
        quantNoBuffer = 0;
        flags = new boolean[numThreads];
        this.numThreads = numThreads;
        acabou = false;
    }

    synchronized boolean insereBuffer(String palavra) {
        //verificação para saber se o buffer está cheio
        if(this.quantNoBuffer + 1 > this.tamBuffer)
            return false;

        //insere no buffer
        buffer[proxInsercao] = palavra;

        /* atualiza o proximo local a ser inserido e quantas coisas estão
         * no buffer */
        proxInsercao = (proxInsercao + 1) % tamBuffer;
        ++quantNoBuffer;

        return true;
    }

    synchronized String getFromBuffer(int id){
        //verifica se há mais algo a ser lido
        if (acabou){
            return "-1";
        }
        /*verifica se essa thread já leu a palavra atual e se
         * tem algo no buffer para ser lido atualmente */
        if (flags[id] || this.quantNoBuffer <= 0){
            return "";
        }

        //atualiza a variável mostrando que essa thread já leu a palavra atual
        flags[id] = true;

        //retorna para a thread a palavra
        return buffer[proxLeitura];
    }

    /* nessa função sera feita a limpeza do buffer e a atualizacao
     * das flags das threads consumidoras permitindo que elas possam
     * voltar a consumir */
    synchronized void clearBuffer(){
        //limpa o buffer
        proxLeitura = (proxLeitura + 1) % tamBuffer;
        --quantNoBuffer;

        //atualiza a flag
        for (int i = 0; i < this.numThreads; ++i)
            flags[i] = false;

        //quebra linha para deixar a saída organizada
        System.out.println();
    }

    /* função que retorna se todas as threads já consumiram a
     * palavra atual do buffer. serve para que a main saiba a hora
     * de limpar o buffer */
    synchronized boolean todasConsumiram(){
        boolean todasConsumiram = true;
        for (int i = 0; i < this.numThreads; ++i)
            todasConsumiram = todasConsumiram && flags[i];

        return todasConsumiram;
    }
    synchronized void acabou(){
        this.acabou = true;
    }

    public int getQuantNoBuffer (){
        return this.quantNoBuffer;
    }
}
