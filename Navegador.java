import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Map.Aresta;
import Map.Grafo;

public class Navegador {
    Grafo atual;
    List<Grafo> caminho = new LinkedList<Grafo>();

    public void calcularRota(Grafo inicio, Grafo destino){
        this.atual = inicio;
        List<Aresta> listaArestas = new ArrayList<>();
           
    }

    public void verificarMelhorRota(){
    }

}
