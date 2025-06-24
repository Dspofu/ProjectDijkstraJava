package com.djikstra;

import com.djikstra.Map.Aresta;
import com.djikstra.Map.Cidade;
import com.djikstra.Map.Grafo;

import java.util.*;

/**
 * Lógica do algoritmo de Dijkstra para encontrar o caminho mais curto.
 * Esta classe é uma versão adaptada da sua, utilizando a nova estrutura de
 * dados.
 */
public class Navegador {

  // Classe interna para encapsular o resultado do cálculo
  public static class Resultado {
    public final List<Cidade> caminho;
    public final int distanciaTotal;

    public Resultado(List<Cidade> caminho, int distanciaTotal) {
      this.caminho = caminho;
      this.distanciaTotal = distanciaTotal;
    }

    public boolean temCaminho() {
      return !caminho.isEmpty() && distanciaTotal >= 0;
    }
  }

  public static Resultado calcularRota(Grafo grafo, Cidade inicio, Cidade destino) {
    Map<Cidade, Integer> distancias = new HashMap<>();
    Map<Cidade, Cidade> predecessores = new HashMap<>();
    // PriorityQueue é mais eficiente para Dijkstra do que verificar a lista toda a
    // cada iteração
    PriorityQueue<Cidade> filaPrioridade = new PriorityQueue<>(Comparator.comparingInt(distancias::get));

    // 1. Inicialização
    for (Cidade cidade : grafo.getCidades()) {
      distancias.put(cidade, Integer.MAX_VALUE);
      predecessores.put(cidade, null);
    }
    distancias.put(inicio, 0);
    filaPrioridade.add(inicio);

    // 2. Loop principal do algoritmo
    while (!filaPrioridade.isEmpty()) {
      Cidade cidadeAtual = filaPrioridade.poll();

      if (cidadeAtual.equals(destino)) {
        break; // Otimização: para quando o destino é alcançado
      }

      // Para cada vizinho da cidade atual
      for (Aresta aresta : cidadeAtual.getVizinhos()) {
        Cidade vizinho = aresta.getDestino();
        int novaDistancia = distancias.get(cidadeAtual) + aresta.getDistancia();

        // Se um caminho mais curto for encontrado
        if (novaDistancia < distancias.get(vizinho)) {
          distancias.put(vizinho, novaDistancia);
          predecessores.put(vizinho, cidadeAtual);
          // Atualiza a prioridade na fila
          filaPrioridade.remove(vizinho);
          filaPrioridade.add(vizinho);
        }
      }
    }

    // 3. Reconstrução do caminho
    List<Cidade> caminho = new ArrayList<>();
    Cidade passo = destino;
    // Se o destino não foi alcançado, seu predecessor será null (e ele não é o
    // início)
    if (predecessores.get(passo) == null && !passo.equals(inicio)) {
      return new Resultado(Collections.emptyList(), -1); // Caminho não encontrado
    }

    while (passo != null) {
      caminho.add(passo);
      passo = predecessores.get(passo);
    }
    Collections.reverse(caminho);

    int distanciaFinal = distancias.get(destino);
    return new Resultado(caminho, distanciaFinal);
  }
}