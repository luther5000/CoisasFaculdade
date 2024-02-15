#include <stdio.h>
#include <malloc.h>
#define BLOCKSIZE = 64

typedef struct tree_node{
    struct tree_node *left;
    struct tree_node *right;
    int key;
}tree_node;


tree_node *currentblock = NULL;
int size_left = 0;
tree_node *free_list = NULL;


tree_node *getNode(){
    tree_node *aux;
    if (free_list != NULL){
        *aux = free_list;
        free_list = free_list->right
    } 
    else {
        if (size_left == 0) {
            currentblock = (tree_node *) malloc(BLOCKSIZE*sizeof(tree_node));
            size_left = BLOCKSIZE;
        }    
        
        aux = currentblock++;
        size_left -= 1;
    }
    
    aux->left = NULL;
    aux->right = NULL;
    aux->key = -1;
    return (aux);
}

void retornaNode(tree_node *p){
    p->right = free_list;
    free_list = p;
}

typedef struct objeto{
    int key;
    int content;
}

tree_node *criaArvore(int ){
    return getNode();
}

tree_node *insereNaArvore(tree_node *p, *objeto objeto, int key){
    if (p->left == NULL){
        p->key = key;
        p->left = (tree_node *) objeto;
    }
    else {
        tree_node *aux = p;
        
        while (aux->right != NULL){
            if (key < aux-> key)
                aux = aux->left;
            else
                aux = aux->right
        }
    }
}

int main()
{
    printf("Hello World");

    return 0;
}
