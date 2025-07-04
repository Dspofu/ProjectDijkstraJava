package com.dijkstra.Map;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um nó (uma cidade) no grafo.
 * Contém o nome, coordenadas geográficas e a lista de arestas (vizinhos).
 */
public class Cidade {
  private final String nome;
  private final double latitude;
  private final double longitude;
  private final String estado;
  private final List<Aresta> vizinhos = new LinkedList<>();

  public Cidade(String nome, double latitude, double longitude, String estado) {
    this.nome = nome;
    this.latitude = latitude;
    this.longitude = longitude;
    this.estado = estado;
  }

  public void adicionarVizinho(Aresta aresta) {
    this.vizinhos.add(aresta);
  }

  // Getters
  public String getNome() {
    return nome;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public List<Aresta> getVizinhos() {
    return vizinhos;
  }

  public String getEstado() { // NOVO GETTER
    return estado;
  }

  @Override
  public String toString() {
    // É uma boa prática sobrescrever o toString, embora não seja usado pelo
    // renderer.
    return nome + " - (" + estado + ")";
  }

  // É importante sobrescrever equals e hashCode para o correto funcionamento em
  // Maps e Sets
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Cidade cidade = (Cidade) o;
    return Objects.equals(nome, cidade.nome);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nome);
  }
}