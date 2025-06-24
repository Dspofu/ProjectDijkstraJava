package com.dijkstra.Map;

/**
 * Representa uma aresta (uma estrada/conexão) no grafo.
 * Contém a cidade de destino e a distância (peso).
 */
public class Aresta {
  private final Cidade destino;
  private final int distancia;

  public Aresta(Cidade destino, int distancia) {
    this.destino = destino;
    this.distancia = distancia;
  }

  public Cidade getDestino() {
    return destino;
  }

  public int getDistancia() {
    return distancia;
  }
}