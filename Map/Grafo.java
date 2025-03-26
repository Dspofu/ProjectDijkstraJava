package Map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Grafo {
    private String nome;
    List<Aresta> listaArestas = new LinkedList<Aresta>();

    public Grafo(){
    }

    /**
     * Metodo construtor com o unico parametro de nome.
     * @param nome
     */
    public Grafo(String nome){
        this.nome = nome;
    }

    /**
     * Retorna o nome atual do grafo.
     * @return nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Altera o nome do grafo.
     * @param nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNewAresta(Aresta aresta){
        this.listaArestas.add(aresta);
    }

    public List<Aresta> getListaArestas() {
        return listaArestas;
    }
}
