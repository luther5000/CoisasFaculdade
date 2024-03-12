#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#define BUFFERSIZE 10
int flag[] = { 0, 0, 0, 0 };

char buffer[BUFFERSIZE];
int insere = 0;
int le = 0;
int acabou = 0;
int cont = 0;

typedef struct string_t{
  int tam;
  char *string;
}string_t;


void *consumidor (void *t){
    int *id = (int *) t;
    while (1){
        if (acabou){
            return NULL;
        }
        
        while(cont == 0);
        printf("%c", buffer[le]);
        
        flag[*id] = 1;
        while (flag[0] || flag[1] || flag[2] || flag[3]);
        
    }
    return NULL;
}

void *produtor (void *t)
{
  string_t string = *(string_t *) t;

  for (int i = 0; i < string.tam; i++){
	  while (cont >= 8 && (insere + 1) % BUFFERSIZE == le);
	  
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

    for (int i = 0; i < 4; i++){
	    pthread_create (&th[i], NULL, produtor, (void *) &s[i]);
    }

	for (int i = 0; i < 4; i++){
		pthread_create (&th[i + 4], NULL, consumidor, (void *) &id[i]);
	}
    
    for (int k= 0; k < 4; k++){
        for (int j = 0; j < s[k].tam; j++){
            while (!flag[0] || !flag[1] || !flag[2] || !flag[3]);
            
            if (k == 3 && j + 1 == s[k].tam)
                acabou = 1;
                
            le += 1;
            cont -= 1;
            
            printf("\n");
            flag[0] = flag[1] = flag[2] = flag[3] = 0;
        }
    }
    
    for(int i = 0; i < 8; ++i) {
        pthread_join(th[i], NULL);
    }
	return 0;
}
