import Map.Aresta;
import Map.Grafo;

public class Main {
  public static void main(String[] args) {
    // Criação das capitais (vértices do grafo)
    Grafo sp = new Grafo("São Paulo");
    Grafo bh = new Grafo("Belo Horizonte");
    Grafo salvador = new Grafo("Salvador");
    Grafo rio = new Grafo("Rio de Janeiro");
    Grafo curitiba = new Grafo("Curitiba");
    Grafo portoAlegre = new Grafo("Porto Alegre");

    // Adicionando as arestas (conexões entre as capitais com distâncias)
    Aresta sp_bh = new Aresta(bh, 600);  // São Paulo -> Belo Horizonte com distância 600 km
    Aresta sp_rio = new Aresta(rio, 400);  // São Paulo -> Rio de Janeiro com distância 400 km
    Aresta sp_curitiba = new Aresta(curitiba, 400);  // São Paulo -> Curitiba com distância 400 km
    Aresta bh_salvador = new Aresta(salvador, 1000);  // Belo Horizonte -> Salvador com distância 1000 km
    Aresta rio_curitiba = new Aresta(curitiba, 300);  // Rio de Janeiro -> Curitiba com distância 300 km
    Aresta rio_portoAlegre = new Aresta(portoAlegre, 1200);  // Rio de Janeiro -> Porto Alegre com distância 1200 km
    Aresta curitiba_portoAlegre = new Aresta(portoAlegre, 600);  // Curitiba -> Porto Alegre com distância 600 km
    Aresta salvador_portoAlegre = new Aresta(portoAlegre, 1600);  // Salvador -> Porto Alegre com distância 1600 km

    // Adicionando as arestas aos respectivos grafos
    sp.setNewAresta(sp_bh);
    sp.setNewAresta(sp_rio);
    sp.setNewAresta(sp_curitiba);
    bh.setNewAresta(bh_salvador);
    rio.setNewAresta(rio_curitiba);
    rio.setNewAresta(rio_portoAlegre);
    curitiba.setNewAresta(curitiba_portoAlegre);
    salvador.setNewAresta(salvador_portoAlegre);

    // Criando o objeto Navegador para calcular a melhor rota
    Navegador navegador = new Navegador();

    // Calculando a rota entre São Paulo e Porto Alegre
    System.out.println("Calculando a melhor rota entre São Paulo e Porto Alegre:");
    navegador.calcularRota(sp, portoAlegre);  // sp é São Paulo e portoAlegre é Porto Alegre

    // Exibindo o caminho e a distância total
    navegador.listarMelhorCaminho();
  }
}
