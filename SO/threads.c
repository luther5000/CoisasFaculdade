#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#define BUFFERSIZE 10
int flag[] = { 0, 0, 0, 0 };
//flags para controle de limpagem do buffer
char buffer[BUFFERSIZE];

int insere = 0;
//posição para inserir

int le = 0;
//posição para ler

int acabou = 0;
//informa quando não há mais oq ler para a thread consumidora

int cont = 0;
//quantos valores estão no buffer

typedef struct string_t{
  int tam;
  char *string;
}string_t;


void *consumidor (void *t){
    int *id = (int *) t;
    while (1){
    /*esse laço irá se repetir até que a main determine
    *quando não há mais o que ler no buffer*/
        if (acabou){
            return NULL;
        }

        while(cont == 0);
        //se não houver nada no buffer o consumidor espera
        
        printf("%c", buffer[le]);

        flag[*id] = 1;
        //esse flag informa a main que ele já leu o caracter da vez
        while (flag[*id]);
        /*o consumidor espera seu flag ser liberado pela main
        *para poder continuar consumindo*/

    }
    return NULL;
}

void *produtor (void *t)
{
  string_t string = *(string_t *) t;

  for (int i = 0; i < string.tam; i++){
	  while (cont >= 8 && (insere + 1) % BUFFERSIZE == le);
	  /*essa restrição garante que de nenhum jeito o produtor 
	  *vai inserir coisas a mais do que o buffer suporta, 
	  *por isso mesmo o buffer cabendo 10 valores ele irá
	  *parar de adicionar no 8*/

	  cont += 1;
	  buffer[insere] = string.string[i];
	  insere = (insere + 1) % BUFFERSIZE;
	}
    return NULL;
}

int main (){
    pthread_t th[8];

    string_t s[] = { {4, "0123"}, {2, "45"}, {1, "6"}, {3, "789"} };
    int id[] = { 0, 1, 2, 3 };
    /*esse id será passado para os consumidores para que a main
    *saiba quando eles já tiverem consumido o caracter da vez*/

    for (int i = 0; i < 4; i++){
	    pthread_create (&th[i], NULL, produtor, (void *) &s[i]);
	    /*criação dos consumidores; passamos para eles a string
	    *que ele deve estar inserindo no buffer*/
    }

	for (int i = 0; i < 4; i++){
		pthread_create (&th[i + 4], NULL, consumidor, (void *) &id[i]);
		//criação dos consumidores passando o id deles
	}

    do{
		while (!flag[0] || !flag[1] || !flag[2] || !flag[3]);
		/*enquanto todos os consumidores não tiverem consumido
		*a main vai esperar*/

        le += 1;
        cont -= 1;
        /*aqui é feita a limpagem do buffer. 
        *na lógica desse código apenas um caracter será lido
        *e imprimido por vez*/

        printf("\n");
        //se não tiver quebra de linha os valores não são impressos
        flag[0] = flag[1] = flag[2] = flag[3] = 0;
        //libera os consumidores para poderem consumir

    }while (cont > 0);
    /*enquanto houver algo no buffer a main irá aguardar os consumidores 
    *imprimirem algo. 
    *não há como a main passar daqui sem nada ter sido colocado no 
    *buffer (logo nada ter sido imprimido) porque a main espera os
    *consumidores (linha 91), que por sua vez esperam os produtores
    *colocarem algo no buffer (linha 38)*/

    acabou = 1;
    //avisa aos consumidores que eles devem encerrar suas atividades
    flag[0] = flag[1] = flag[2] = flag[3] = 0;
    /*garantia que todos os os consumidores irão executar o laço 
    mais uma vez e com isso entrarão no if para terminar sua execução*/

    for(int i = 0; i < 8; ++i) {
    //aguarda as 8 threads encerrarem
        pthread_join(th[i], NULL);
    }
	return 0;
	//encerra a main
}
