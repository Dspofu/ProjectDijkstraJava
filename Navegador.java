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
        caminho.add(inicio);
        Grafo grafoMenor = null;
        double distanciaMenor = Double.MAX_VALUE;
        for (Aresta aresta : atual.getListaArestas()) {
            if (aresta.getDistancia() < distanciaMenor) {
                distanciaMenor = aresta.getDistancia();
                grafoMenor = aresta.getGrafo();
            }
        } 
        caminho.add(grafoMenor);
        listarMelhorCaminho();
        caminho.clear();
    }

    public void listarMelhorCaminho(){
        for (Grafo grafo : caminho) {
            System.out.print(" -> "+grafo.getNome());
            
        }
    }
}
