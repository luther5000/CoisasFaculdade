#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <malloc.h>


int main() {
    int pid1 = fork();//criou um filho, guarda pid main
    int pid2;//criou segundo filho, guarda pid main
    int pid45 = 0;//criou dois netos, guarda pid d um dos dois filhos
    int pid56 = 0;//criou mais dois netos, guarda pid d um dos dois filhos

    if (pid1 != 0) {//n entra o filho ja criado
        pid2 = fork();//cria segundo filho
        if (pid2 != 0) {
            wait(NULL);
            wait(NULL);//a main vai esperar os dois filhos encerrarem
            printf ("Eu sou o pai de PID %d", getpid());
            return 0;
        }
    }

    //printf("Filho: %d\n", getpid());
    pid45 = fork();//cria primeiro e segundo neto

    if (pid45 != 0) {//netos já criados não entram
        //printf("Filho: %d\n", getpid());
        pid56 = fork();//cria terceiro e quarto neto
    }

    if (pid45 != 0 && pid56 != 0){//netos ja criados não entram
        //printf("Filho: %d\n", getpid());
        wait(NULL);
        wait(NULL);//os filhos esperam os netos encerrarem
        printf("Eu sou um processo filho de PID %d e meu pai tem pid %d\n",
               getpid(), getppid());
        return 0;//encerra a execução dos filhos para que a main volte a ser realidada
    }

    if (pid45 == 0 || pid56 == 0) {

        if (getpid() % 2 != 0){
            /*char *binaryPath = "/bin/ls";
            char *arg1 = "-lh";
            char *arg2 = "/home";*/
            printf("1Sou neto de pid: %d\n", getpid());
            //execl(binaryPath, binaryPath, arg1, arg2, NULL);
            printf("2Sou neto de pid: %d\n", getpid());

        } else {
            printf("3Sou neto de pid: %d\n", getpid());
            printf("4Sou neto de pid: %d\n", getpid());

        }

    }
    return 0;
}
