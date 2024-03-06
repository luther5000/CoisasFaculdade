#include <stdio.h>
#include <pthread.h>
#include <unistd.h> 
#include <stdlib.h>


void *threads(void *t){
    
    int *id = (int *) t;
    
    if (*id % 2 == 0) //n funciona
    	printf("Sou uma thread de ID: %d\n", *id);
    
    else 
    	printf("Não sou uma threade de ID: %d", *id);
}

int main()
{
	pthread_t th;
	
	for (int i = 0; i < 8; i++){
		pthread_create(&th, NULL, threads, (void *)&th);
	}
	
	pthread_exit(NULL);

    return 0;
}
