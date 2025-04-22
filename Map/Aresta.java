package Map;

public class Aresta {
  private Grafo grafo;
  private double distancia;

  /**
   * Metodo construtor com o grafo destino e sua distancia
   * 
   * @param grafo
   * @param distancia
   */
  public Aresta(Grafo grafo, double distancia) {
    this.grafo = grafo;
    this.distancia = distancia;
  }

  /**
   * Metodo construtor sem parametros
   */
  public Aresta() {
  }

  /**
   * Metodo get do grafo destino
   * 
   * @return Grafo
   */
  public Grafo getGrafo() {
    return grafo;
  }

  /**
   * Metodo set do grafo destino
   * 
   * @param grafo
   */
  public void setGrafo(Grafo grafo) {
    this.grafo = grafo;
  }

  /**
   * Metodo get da distancia até o grafo destino
   * 
   * @return
   */
  public double getDistancia() {
    return distancia;
  }

  /**
   * Metodo set da distancia até ao grafo destino
   * 
   * @param distancia
   */
  public void setDistancia(double distancia) {
    this.distancia = distancia;
  }
}
