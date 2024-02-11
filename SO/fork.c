#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <malloc.h>

typedef struct node{
    int content;
    struct node *prox;
}node;
typedef struct fila{
    struct node *root;
    int tam;
}fila;

fila* cria(){
    fila *p = (fila *) malloc(sizeof (fila));
    node *placeholder = malloc(sizeof (node));
    p->root = placeholder;
    p->tam = 0;
    placeholder->prox = placeholder;
    placeholder->content = -1;
    return p;
}

int isEmpty(fila *p){
    return (p->root->content == -1);
}

void push(fila *p, int num){
    node *new = (node *) malloc(sizeof(node));
    new->content = num;
    new->prox = p->root->prox;
    if (isEmpty(p))
        p->root->prox = new;
    p->root = new;
    p->tam++;
}

int top(fila *p){
    return (p->root->content);
}

int remove_(fila *p){
    node *aux = p->root->prox->prox;
    p->root->prox->prox = p->root->prox->prox->prox;
    return aux->content;
}

int tam(fila *p){
    return p->tam;
}

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

    fila *fila = cria();
    push(fila, getpid());
    sleep(1);
    if (pid45 == 0 || pid56 == 0) {

        if (top(fila) == getpid()){
            remove_(fila);
            char *binaryPath = "/bin/ls";
            char *arg1 = "-lh";
            char *arg2 = "/home";

            execl(binaryPath, binaryPath, arg1, arg2, NULL);
        }
        sleep(1);

        if (top(fila) == getpid()){
            remove_(fila);
            char *binaryPath = "/bin/ls";
            char *arg1 = "ls";
            char *arg2 = "/home";

            execl(binaryPath, binaryPath, arg1, arg2, NULL);
        }

        sleep(1);
        if (top(fila) == getpid()){
            remove_(fila);
            execl("/home/lutero/CLionProjects/trem_d_SO/Executaveis", "", NULL);
        }

        sleep(1);
        if (top(fila) == getpid()){
            remove_(fila);
            execl("/home/lutero/CLionProjects/trem_d_SO/Executaveis", "", NULL);
        }
        sleep(1);
         }
    return 0;
}
