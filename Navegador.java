import java.util.*;

import Map.Aresta;
import Map.Grafo;

public class Navegador {
  private Grafo atual;
  private List<Grafo> caminho = new LinkedList<Grafo>();
  private Map<Grafo, Double> distancias = new HashMap<>();  // Mapear distância de cada grafo
  private Map<Grafo, Grafo> predecessores = new HashMap<>(); // Mapear predecessores para reconstruir o caminho

  public void calcularRota(Grafo inicio, Grafo destino) {
    // Limpar caminho e distâncias antes de calcular novamente
    caminho.clear();
    distancias.clear();
    predecessores.clear();

    this.atual = inicio;

    // Inicializando as distâncias e predecessores
    distancias.put(inicio, 0.0);  // A distância até o nó inicial é 0
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

    // Fila de prioridade para Dijkstra
    PriorityQueue<Grafo> pq = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));
    pq.add(inicio);

    // Dijkstra: A cada iteração, tenta relaxar as arestas
    while (!pq.isEmpty()) {
        Grafo u = pq.poll();
        for (Aresta aresta : u.getListaArestas()) {
            Grafo v = aresta.getGrafo();
            double novaDistancia = distancias.get(u) + aresta.getDistancia();
            if (novaDistancia < distancias.get(v)) {
                distancias.put(v, novaDistancia);
                predecessores.put(v, u);
                pq.add(v);
            }
        }
    }

    // Reconstruindo o caminho
    Grafo current = destino;
    while (current != null) {
        caminho.add(current);
        current = predecessores.get(current);
    }

    Collections.reverse(caminho); // Invertendo a lista para ter o caminho de início a destino
    listarMelhorCaminho();  // Agora sem parâmetros, pois a lógica já está dentro da classe
  }

  public void listarMelhorCaminho() {
    // Exibindo a origem e o destino
    if (caminho.isEmpty()) {
        System.out.println("Não foi possível encontrar um caminho entre os pontos.");
        return;
    }

    Grafo origem = caminho.get(0);
    Grafo destino = caminho.get(caminho.size() - 1);

    // System.out.println(caminho.get(1).getNome());

    // Verificar se o destino foi alcançado
    if (!distancias.containsKey(destino) || distancias.get(destino) == Double.MAX_VALUE) {
        System.out.println("Não foi possível encontrar um caminho entre " + origem.getNome() + " e " + destino.getNome() + ".");
        return;
    }

    System.out.println("Origem: " + origem.getNome() + "\nDestino: " + destino.getNome());

    // Exibindo o caminho percorrido
    System.out.print("Menor caminho: ");
    for (int i = 0; i < caminho.size(); i++) {
        System.out.print(caminho.get(i).getNome());
        if (i != caminho.size() - 1) {
            System.out.print(" -> ");
        }
    }

    // Exibindo a distância total
    double distanciaTotal = distancias.get(destino);
    System.out.println("\nDistância total: " + distanciaTotal + " km");
  }
}
