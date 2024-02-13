#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <malloc.h>

typedef struct node{
    int content;
    struct node *prox;
}node;
typedef struct filaa{
    struct node *root;
    int tam;
}filaa;

filaa* cria(){
    filaa *p = (filaa *) malloc(sizeof (filaa));
    node *placeholder = (node *) malloc(sizeof (node));
    p->root = placeholder;
    p->tam = 0;
    placeholder->prox = placeholder;
    placeholder->content = -1;
    return p;
}

int isEmpty(filaa *p){
    return (p->root->content == -1);
}

void push(filaa *p, int num){
    node *novo = (node *) malloc(sizeof(node));
    novo->content = num;
    novo->prox = p->root->prox;
    if (isEmpty(p))
        p->root->prox = novo;
    p->root = novo;
    p->tam++;
}

int top(filaa *p){
    return (p->root->content);
}

int remove_(filaa *p){
    node *aux = p->root->prox->prox;
    p->root->prox->prox = p->root->prox->prox->prox;
    return aux->content;
}

int tam(filaa *p){
    return p->tam;
}

int main(){
    filaa *fila = cria();
    int escolha;
    scanf("%d", &escolha);
    while (1){
        switch (escolha){
            case 1:{
                int num;
                scanf("%d", &num);
                push(fila, num);
                break;
            }
            case 2:{
                printf("%d\n", remove_(fila));
                break;
            }
            case 3:{
                return 0;
            }
        }
    }
}
