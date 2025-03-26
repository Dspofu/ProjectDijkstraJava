package Map;

public class Aresta {
    private Grafo grafo;
    private double distancia;
    
    public Aresta(Grafo grafo, double distancia) {
        this.grafo = grafo;
        this.distancia = distancia;
    }

    public Aresta() {
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public void setGrafo(Grafo grafo) {
        this.grafo = grafo;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
