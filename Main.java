import Map.Aresta;
import Map.Grafo;

public class Main{
    public static void main(String[] args) {
        Grafo a = new Grafo("Cariacica");
        Grafo b = new Grafo("Vitoria");
        Grafo c = new Grafo("Vila Velha");

        Aresta ab = new Aresta(b, 150);
        Aresta ac = new Aresta(c, 280.3);
        
        a.setNewAresta(ab);
        a.setNewAresta(ac);
        
    }
}