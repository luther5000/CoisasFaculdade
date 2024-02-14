#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>

int main() {
    int pid1 = fork();//criou um filho, guarda pid main

    int pid2;//usado na criação do segundo filho, guarda pid main
    int pid45;//usado na criação de dois netos, guarda pid de um dos dois filhos
    int pid56 = 0;//usado na criação de mais dois netos, guarda pid de um dos dois filhos

    if (pid1 != 0) {//não entra o filho ja criado
        pid2 = fork();//cria segundo filho

        if (pid2 != 0) {
            wait(NULL);
            wait(NULL);//a main vai esperar os dois filhos encerrarem

            printf ("Eu sou o pai de PID %d\n", getpid());
            return 0;
        }
    }

    pid45 = fork();//cria primeiro e segundo neto

    if (pid45 != 0) {//netos já criados não entram
        pid56 = fork();//cria terceiro e quarto neto

    } else {//aqui os dois netos criados entram e vão ser tratados

        if (getppid() % 2 == 0){
            /*como foram dois netos criados por dois filhos com pid diferentes e sequenciais, um será
             *par e outro ímpar, assim pegando o pid deles conseguimos separar os netos*/
            char *binaryPath = "/bin/ping\0";
            char *arg1 = "ping\0";
            char *arg2 = "150.165.253.95\0";
            char *arg3 = "-c\0";
            char *arg4 = "1\0";

            //vai enviar e receber um pacote para o ip do sigaa
            execl(binaryPath, arg1, arg2, arg3, arg4, (char *)NULL);

        } else {
            char *binaryPath = "/bin/iostat\0";
            char *arg1 = "iostat\0";
            char *arg2 = "-c\0";

            //exibe as informações sobre a CPU do computador
            execl(binaryPath, arg1, arg2, (char *)NULL);
        }
    }

    if (pid45 != 0 && pid56 != 0){//netos ja criados não entram
        wait(NULL);
        wait(NULL);//os filhos esperam os netos encerrarem
        sleep(1);

        printf("Eu sou um processo filho de PID %d e meu pai tem pid %d\n",
               getpid(), getppid());
        return 0;//encerra a execução dos filhos para que a main volte a ser realidada

    } else {//os outros dois netos criados entram aqui para serem trados

        if (getppid() % 2 == 0) {
            //usamos a mesma forma de separação da última vez
            char *binaryPath = "/bin/free\0";
            char *arg1 = "free\0";
            char *arg2 = "-h\0";

            //exibe as informações sobre a utilização da memória ram
            execl(binaryPath, arg1, arg2, (char *)NULL);

        } else {
            char *binaryPath = "/bin/df\0";
            char *arg1 = "df\0";
            char *arg2 = "-m\0";

            //exibe as informações de utilização de disco em MB
            execl(binaryPath, arg1, arg2, (char *)NULL);
        }
    }
    return 0;//encerra a execução de todos os netos
}
