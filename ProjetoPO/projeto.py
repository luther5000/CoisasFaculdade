import math
from mip import *


class Modelo:
    def __init__(self):
        self.menor = 0

    def save(self, model, filename):
        model.write(filename)
        with open(filename, "r") as f:
            print(f.read())

    def solve(self, model):
        status = model.optimize()

        print("Status = ", status)
        if status != OptimizationStatus.OPTIMAL:
            return

        print(f"Solution value  = {model.objective_value:.2f}\n")

        print("Solution:")
        for v in model.vars:
            if v.x > 0.001:
                print(f"{v.name} = {v.x:.2f}")

    def criaModel(self, numVar, numRest, objetivo, restricoes):
        model = Model(sense=MAXIMIZE, solver_name=CBC)

        x = [model.add_var(var_type=CONTINUOUS, lb=0, ub=1, name="x_" + str(i)) for i in range(numVar)]

        model.objective = xsum(int(objetivo[i]) * x[i] for i in range(numVar))

        for i in range(numRest):
            linhaAtual = restricoes[i].split()
            model += xsum(int(linhaAtual[j]) * x[j] for j in range(numVar)) <= int(linhaAtual[numVar])

        self.save(model, "model.lp")
        return model


    def BranchAndBound(self, model, y, s):
        model0 = model.copy()
        if s == '1':
            cont = 0
            for i in model0.vars:
                if i.name == y:
                    model0 += model0.var_by_name("x_" + str(cont)) == 1
                cont += 1
        else:
            cont = 0
            for i in model0.vars:
                if i.name == y:
                    model0 += model0.var_by_name("x_" + str(cont)) == 0
                cont += 1
            self.save(model0, "model.lp")

        if model0.optimize() != OptimizationStatus.OPTIMAL:
            return

        self.save(model0, "model.lp")
        self.solve(model0)

        if self.menor != 0:
            if model0.objective_value <= self.menor:
                return

        lista = []
        for i in model0.vars:
            if round(i.x) != i.x:
                lista.append(i)

        if len(lista) != 0:
            menorDist = 10
            indice = -1
            for i in range(len(lista)):
                if abs(lista[i].x - 0.5) < menorDist:
                    indice = i
                    menorDist = abs(lista[i].x - 0.5)
            self.BranchAndBound(model0, str(lista[indice]), "1")
            self.BranchAndBound(model0, str(lista[indice]), "0")

        else:
            if self.menor != 0:
                if self.menor < model0.objective_value:
                    self.menor = model0.objective_value
                return
            else:
                self.menor = model0.objective_value
                return


linhas = []
with open("teste4.txt") as file:
    for line in file:
        linhas.append(line)

linhaAtual = linhas[0].split()
numVar = int(linhaAtual[0])
numRest = int(linhaAtual[1])

objetivo = linhas[1].split()


restricoes = []
for i in range(numRest):
    restricoes.append(linhas[i + 2])

modelo = Modelo()
model = modelo.criaModel(numVar, numRest, objetivo, restricoes)
modelo.solve(model)

lista = []
for i in model.vars:
    if round(i.x) != i.x:
        lista.append(i)

if len(lista) != 0:
    menorDist = 10
    indice = -1
    for i in range(len(lista)):
        if abs(lista[i].x - 0.5) < menorDist:
            indice = i
            menorDist = abs(lista[i].x - 0.5)
    modelo.BranchAndBound(model, str(lista[indice]), "1")
    modelo.BranchAndBound(model, str(lista[indice]), "0")

    print(modelo.menor)
