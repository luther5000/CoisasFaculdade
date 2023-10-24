from mip import *
import numpy as py

def solve(model):
    status = model.optimize()

    print("Status = ", status)
    if status != OptimizationStatus.OPTIMAL:
        return

    print(f"Solution value  = {model.objective_value:.2f}\n")

    print("Solution:")
    for v in model.vars:
        if v.x > 0.001:
            print(f"{v.name} = {v.x:.2f}")


def criaModel(numVar, numRest, objetivo, restricoes):
    model = Model(sense=MAXIMIZE, solver_name=CBC)

    x = [model.add_var(var_type=CONTINUOUS, lb=0, up=1, name="x_" + str(i)) for i in range(numVar)]

    objetivo = objetivo.split(' ')

    model.objective = xsum(int(objetivo[i]) * x[i] for i in range(numVar))

    for i in range(numRest):
        linhaAtual = restricoes[i]
        model += xsum(int(linhaAtual[j]) * x[j] for j in range(numVar - 1)) <= int(linhaAtual[numVar])

    return model


linhas = []
with open("arquivo.txt") as file:
    for line in file:
        linhas.append(line)

linhaAtual = linhas[0].split(' ')
numVar = int(linhaAtual[0])
numRest = int(linhaAtual[1])


objetivo = linhas[1].split(' ')

restricoes = []
for i in range(numRest):
    restricoes.append(linhas[i+2])

model = criaModel(numVar, numRest, objetivo, restricoes)
solve(model)

stack = []
if py.round(model.var_by_name("x[1]")) != model.var_by_name("x[1]"):
    stack.append(model.var_by_name("x[1]"))
