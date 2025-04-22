package Map;

import java.util.LinkedList;
import java.util.List;

public class Grafo {
  private String nome;
  List<Aresta> listaArestas = new LinkedList<Aresta>();

  public Grafo() {
  }

  /**
   * Metodo construtor com o unico parametro de nome.
   * 
   * @param nome
   */
  public Grafo(String nome) {
    this.nome = nome;
  }

  /**
   * Retorna o nome atual do grafo.
   * 
   * @return nome
   */
  public String getNome() {
    return nome;
  }

  @Override
  public String toString() {
    return this.getNome(); // ou apenas nome, se for público
  }

  /**
   * Altera o nome do grafo.
   * 
   * @param nome
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * Inclui em uma lista interna do grafo uma nova ligação para outro
   * 
   * @param aresta
   */
  public void setNewAresta(Aresta aresta) {
    this.listaArestas.add(aresta);
  }

  /**
   * Retorna as ligações existentes ao grafo
   * 
   * @return List<Aresta>
   */
  public List<Aresta> getListaArestas() {
    return listaArestas;
  }
}
