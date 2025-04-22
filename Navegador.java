import java.util.*;

import Map.Aresta;
import Map.Grafo;

public class Navegador {
  @SuppressWarnings("unused")
  private Grafo atual;
  private List<Grafo> caminho = new LinkedList<>();
  private Map<Grafo, Double> distancias = new HashMap<>(); // Distâncias de cada grafo
  private Map<Grafo, Grafo> predecessores = new HashMap<>(); // Predecessores para reconstruir o caminho

  public void calcularRota(Grafo inicio, Grafo destino) {
    // Limpar caminho e distâncias antes de calcular novamente
    caminho.clear();
    distancias.clear();
    predecessores.clear();

    this.atual = inicio;

    // Inicializando a distância do nó inicial e os predecessores
    distancias.put(inicio, 0.0);
    Queue<Grafo> todosOsGrafos = new LinkedList<>();
    todosOsGrafos.add(inicio);

    // Inicializa distâncias para todos os vértices do grafo
    while (!todosOsGrafos.isEmpty()) {
      Grafo grafoAtual = todosOsGrafos.poll();
      for (Aresta aresta : grafoAtual.getListaArestas()) {
        Grafo vizinho = aresta.getGrafo();
        if (!distancias.containsKey(vizinho)) {
          distancias.put(vizinho, Double.MAX_VALUE);
          todosOsGrafos.add(vizinho);
        }
      }
    }

    // Fila de prioridade para Dijkstra, com a distância como chave
    PriorityQueue<Grafo> pq = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));
    pq.add(inicio);

    // Algoritmo de Dijkstra: A cada iteração, tenta relaxar as arestas
    while (!pq.isEmpty()) {
      Grafo u = pq.poll(); // Pega o grafo de menor distância
      for (Aresta aresta : u.getListaArestas()) {
        Grafo v = aresta.getGrafo();
        double novaDistancia = distancias.get(u) + aresta.getDistancia();
        if (novaDistancia < distancias.get(v)) {
          distancias.put(v, novaDistancia);
          predecessores.put(v, u);
          pq.add(v); // Re-insere o grafo para garantir a escolha do próximo menor
        }
      }
    }

    // Reconstruindo o caminho
    Grafo current = destino;
    while (current != null) {
      caminho.add(current);
      current = predecessores.get(current);
    }

    Collections.reverse(caminho); // Invertendo a lista para ter o caminho de origem a destino
    listarMelhorCaminho();
  }

  public void listarMelhorCaminho() {
    if (caminho.isEmpty()) {
      System.out.println("Não foi possível encontrar um caminho entre os pontos.");
      return;
    }

    Grafo origem = caminho.get(0);
    Grafo destino = caminho.get(caminho.size() - 1);

    // Exibindo a origem e o destino
    System.out.println("Origem: " + origem.getNome() + "\nDestino: " + destino.getNome());

    // Exibindo o caminho percorrido
    System.out.print("Menor caminho: ");
    for (int i = 0; i < caminho.size(); i++) {
      System.out.print(caminho.get(i).getNome());
      if (i != caminho.size() - 1) {
        System.out.print(" -> ");
      }
    }

    // Verificando se a distância final foi calculada corretamente
    double distanciaTotal = distancias.get(destino);
    if (distanciaTotal == Double.MAX_VALUE) {
      System.out.println("\nNão foi possível encontrar um caminho válido.");
    } else {
      System.out.println("\nDistância total: " + distanciaTotal + " km");
    }
  }

  public String getMelhorCaminhoComoTexto() {
    if (caminho.isEmpty()) {
      return "Não foi possível encontrar um caminho entre os pontos.";
    }
  
    Grafo origem = caminho.get(0);
    Grafo destino = caminho.get(caminho.size() - 1);
  
    StringBuilder sb = new StringBuilder();
    sb.append("Origem: ").append(origem.getNome()).append("\n");
    sb.append("Destino: ").append(destino.getNome()).append("\n\n");
    sb.append("Menor caminho: ");
  
    for (int i = 0; i < caminho.size(); i++) {
      sb.append(caminho.get(i).getNome());
      if (i != caminho.size() - 1) {
        sb.append(" -> ");
      }
    }
  
    double distanciaTotal = distancias.get(destino);
    if (distanciaTotal == Double.MAX_VALUE) {
      sb.append("\n\nNão foi possível encontrar um caminho válido.");
    } else {
      sb.append("\n\nDistância total: ").append(distanciaTotal).append(" km");
    }
  
    return sb.toString();
  }
}