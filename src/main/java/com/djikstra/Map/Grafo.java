package com.djikstra.Map;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa o grafo completo, contendo todas as cidades e suas conexões.
 * O método estático `criarGrafoBrasil()` é um factory que retorna um grafo
 * já populado com as capitais brasileiras e suas principais rodovias.
 */
public class Grafo {
  // Usamos LinkedHashMap para manter a ordem de inserção, o que deixa os
  // ComboBoxes mais organizados.
  private final Map<String, Cidade> cidades = new LinkedHashMap<>();

  public void adicionarCidade(Cidade cidade) {
    cidades.put(cidade.getNome(), cidade);
  }

  public void adicionarArestaBidirecional(String nomeOrigem, String nomeDestino, int distancia) {
    Cidade origem = getCidade(nomeOrigem);
    Cidade destino = getCidade(nomeDestino);

    if (origem != null && destino != null) {
      origem.adicionarVizinho(new Aresta(destino, distancia));
      destino.adicionarVizinho(new Aresta(origem, distancia));
    }
  }

  public Cidade getCidade(String nome) {
    return cidades.get(nome);
  }

  public Collection<Cidade> getCidades() {
    return Collections.unmodifiableCollection(cidades.values());
  }

  // Método Factory para criar o grafo de capitais do Brasil
  public static Grafo criarGrafoBrasil() {
    Grafo grafo = new Grafo();

    // Adicionando todas as cidades (capitais) com suas coordenadas
    grafo.adicionarCidade(new Cidade("Aracaju", -10.9167, -37.0500));
    grafo.adicionarCidade(new Cidade("Belém", -1.4558, -48.5039));
    grafo.adicionarCidade(new Cidade("Belo Horizonte", -19.9167, -43.9333));
    grafo.adicionarCidade(new Cidade("Boa Vista", 2.8197, -60.6733));
    grafo.adicionarCidade(new Cidade("Brasília", -15.7939, -47.8828));
    grafo.adicionarCidade(new Cidade("Campo Grande", -20.4428, -54.6464));
    grafo.adicionarCidade(new Cidade("Cuiabá", -15.5989, -56.0949));
    grafo.adicionarCidade(new Cidade("Curitiba", -25.4297, -49.2719));
    grafo.adicionarCidade(new Cidade("Florianópolis", -27.5969, -48.5495));
    grafo.adicionarCidade(new Cidade("Fortaleza", -3.7172, -38.5431));
    grafo.adicionarCidade(new Cidade("Goiânia", -16.6869, -49.2648));
    grafo.adicionarCidade(new Cidade("João Pessoa", -7.1195, -34.8450));
    grafo.adicionarCidade(new Cidade("Macapá", 0.0389, -51.0664));
    grafo.adicionarCidade(new Cidade("Maceió", -9.6658, -35.7353));
    grafo.adicionarCidade(new Cidade("Manaus", -3.1190, -60.0217));
    grafo.adicionarCidade(new Cidade("Natal", -5.7945, -35.2110));
    grafo.adicionarCidade(new Cidade("Palmas", -10.1689, -48.3319));
    grafo.adicionarCidade(new Cidade("Porto Alegre", -30.0346, -51.2177));
    grafo.adicionarCidade(new Cidade("Porto Velho", -8.7619, -63.9039));
    grafo.adicionarCidade(new Cidade("Recife", -8.0578, -34.8829));
    grafo.adicionarCidade(new Cidade("Rio Branco", -9.9747, -67.8100));
    grafo.adicionarCidade(new Cidade("Rio de Janeiro", -22.9068, -43.1729));
    grafo.adicionarCidade(new Cidade("Salvador", -12.9747, -38.5108));
    grafo.adicionarCidade(new Cidade("São Luís", -2.5297, -44.3028));
    grafo.adicionarCidade(new Cidade("São Paulo", -23.5505, -46.6333));
    grafo.adicionarCidade(new Cidade("Teresina", -5.0949, -42.8038));
    grafo.adicionarCidade(new Cidade("Vitória", -20.3194, -40.3378));

    // Adicionando conexões (arestas) - mesmas do seu código original
    grafo.adicionarArestaBidirecional("São Paulo", "Rio de Janeiro", 430);
    grafo.adicionarArestaBidirecional("São Paulo", "Curitiba", 410);
    grafo.adicionarArestaBidirecional("São Paulo", "Belo Horizonte", 580);
    grafo.adicionarArestaBidirecional("São Paulo", "Brasília", 1015);
    grafo.adicionarArestaBidirecional("São Paulo", "Campo Grande", 990);
    grafo.adicionarArestaBidirecional("Rio de Janeiro", "Vitória", 520);
    grafo.adicionarArestaBidirecional("Rio de Janeiro", "Belo Horizonte", 440);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Goiânia", 910);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Salvador", 1370);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Maceió", 1600);
    grafo.adicionarArestaBidirecional("Curitiba", "Porto Alegre", 710);
    grafo.adicionarArestaBidirecional("Curitiba", "Campo Grande", 990);
    grafo.adicionarArestaBidirecional("Curitiba", "Florianópolis", 300);
    grafo.adicionarArestaBidirecional("Porto Alegre", "Cuiabá", 1900);
    grafo.adicionarArestaBidirecional("Porto Alegre", "Campo Grande", 1400);
    grafo.adicionarArestaBidirecional("Porto Alegre", "Florianópolis", 470);
    grafo.adicionarArestaBidirecional("Salvador", "Aracaju", 330);
    grafo.adicionarArestaBidirecional("Aracaju", "Maceió", 280);
    grafo.adicionarArestaBidirecional("Maceió", "Recife", 260);
    grafo.adicionarArestaBidirecional("Recife", "João Pessoa", 120);
    grafo.adicionarArestaBidirecional("João Pessoa", "Natal", 180);
    grafo.adicionarArestaBidirecional("Natal", "Fortaleza", 520);
    grafo.adicionarArestaBidirecional("Fortaleza", "Teresina", 630);
    grafo.adicionarArestaBidirecional("Teresina", "São Luís", 450);
    grafo.adicionarArestaBidirecional("São Luís", "Belém", 800);
    grafo.adicionarArestaBidirecional("Brasília", "Goiânia", 210);
    grafo.adicionarArestaBidirecional("Brasília", "Cuiabá", 1130);
    grafo.adicionarArestaBidirecional("Brasília", "Palmas", 970);
    grafo.adicionarArestaBidirecional("Brasília", "Salvador", 1450);
    grafo.adicionarArestaBidirecional("Palmas", "Belém", 990);
    grafo.adicionarArestaBidirecional("Palmas", "Teresina", 970);
    grafo.adicionarArestaBidirecional("Belém", "Macapá", 530);
    grafo.adicionarArestaBidirecional("Belém", "Manaus", 1600);
    grafo.adicionarArestaBidirecional("Manaus", "Boa Vista", 750);
    grafo.adicionarArestaBidirecional("Manaus", "Porto Velho", 900);
    grafo.adicionarArestaBidirecional("Porto Velho", "Rio Branco", 510);
    grafo.adicionarArestaBidirecional("Porto Velho", "Cuiabá", 1450);
    grafo.adicionarArestaBidirecional("Campo Grande", "Cuiabá", 700);
    grafo.adicionarArestaBidirecional("Vitória", "Salvador", 1200);
    grafo.adicionarArestaBidirecional("Goiânia", "Campo Grande", 900);
    grafo.adicionarArestaBidirecional("Goiânia", "Cuiabá", 900);
    grafo.adicionarArestaBidirecional("Salvador", "Recife", 840);
    grafo.adicionarArestaBidirecional("Teresina", "Salvador", 1000);
    grafo.adicionarArestaBidirecional("São Luís", "Palmas", 1100);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Brasília", 716);
    grafo.adicionarArestaBidirecional("Brasília", "Campo Grande", 1099);
    grafo.adicionarArestaBidirecional("Rio de Janeiro", "Curitiba", 850);
    grafo.adicionarArestaBidirecional("Rio de Janeiro", "Porto Alegre", 1130);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Vitória", 520);
    grafo.adicionarArestaBidirecional("Fortaleza", "Recife", 710);
    grafo.adicionarArestaBidirecional("Maceió", "Salvador", 530);
    grafo.adicionarArestaBidirecional("João Pessoa", "Maceió", 450);

    return grafo;
  }
}
