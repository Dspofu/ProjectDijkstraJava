package com.dijkstra.Map;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

    // Adicionando todas as capitais com a sigla do estado
    grafo.adicionarCidade(new Cidade("Aracaju", -10.9167, -37.0500, "SE"));
    grafo.adicionarCidade(new Cidade("Belém", -1.4558, -48.5039, "PA"));
    grafo.adicionarCidade(new Cidade("Belo Horizonte", -19.9167, -43.9333, "MG"));
    grafo.adicionarCidade(new Cidade("Boa Vista", 2.8197, -60.6733, "RR"));
    grafo.adicionarCidade(new Cidade("Brasília", -15.7939, -47.8828, "DF"));
    grafo.adicionarCidade(new Cidade("Campo Grande", -20.4428, -54.6464, "MS"));
    grafo.adicionarCidade(new Cidade("Cuiabá", -15.5989, -56.0949, "MT"));
    grafo.adicionarCidade(new Cidade("Curitiba", -25.4297, -49.2719, "PR"));
    grafo.adicionarCidade(new Cidade("Florianópolis", -27.5969, -48.5495, "SC"));
    grafo.adicionarCidade(new Cidade("Fortaleza", -3.7172, -38.5431, "CE"));
    grafo.adicionarCidade(new Cidade("Goiânia", -16.6869, -49.2648, "GO"));
    grafo.adicionarCidade(new Cidade("João Pessoa", -7.1195, -34.8450, "PB"));
    grafo.adicionarCidade(new Cidade("Macapá", 0.0389, -51.0664, "AP"));
    grafo.adicionarCidade(new Cidade("Maceió", -9.6658, -35.7353, "AL"));
    grafo.adicionarCidade(new Cidade("Manaus", -3.1190, -60.0217, "AM"));
    grafo.adicionarCidade(new Cidade("Natal", -5.7945, -35.2110, "RN"));
    grafo.adicionarCidade(new Cidade("Palmas", -10.1689, -48.3319, "TO"));
    grafo.adicionarCidade(new Cidade("Porto Alegre", -30.0346, -51.2177, "RS"));
    grafo.adicionarCidade(new Cidade("Porto Velho", -8.7619, -63.9039, "RO"));
    grafo.adicionarCidade(new Cidade("Recife", -8.0578, -34.8829, "PE"));
    grafo.adicionarCidade(new Cidade("Rio Branco", -9.9747, -67.8100, "AC"));
    grafo.adicionarCidade(new Cidade("Rio de Janeiro", -22.9068, -43.1729, "RJ"));
    grafo.adicionarCidade(new Cidade("Salvador", -12.9747, -38.5108, "BA"));
    grafo.adicionarCidade(new Cidade("São Luís", -2.5297, -44.3028, "MA"));
    grafo.adicionarCidade(new Cidade("São Paulo", -23.5505, -46.6333, "SP"));
    grafo.adicionarCidade(new Cidade("Teresina", -5.0949, -42.8038, "PI"));
    grafo.adicionarCidade(new Cidade("Vitória", -20.3194, -40.3378, "ES"));

    // Adicionando conexões (arestas) - mesmas do seu código original
    grafo.adicionarArestaBidirecional("São Paulo", "Rio de Janeiro", 357);
    grafo.adicionarArestaBidirecional("São Paulo", "Curitiba", 338);
    grafo.adicionarArestaBidirecional("São Paulo", "Belo Horizonte", 489);
    grafo.adicionarArestaBidirecional("São Paulo", "Campo Grande", 893);
    grafo.adicionarArestaBidirecional("Rio de Janeiro", "Vitória", 418);
    grafo.adicionarArestaBidirecional("Rio de Janeiro", "Belo Horizonte", 340);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Goiânia", 705);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Salvador", 971);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Brasília", 625);
    grafo.adicionarArestaBidirecional("Belo Horizonte", "Vitória", 376);
    grafo.adicionarArestaBidirecional("Curitiba", "Campo Grande", 789);
    grafo.adicionarArestaBidirecional("Curitiba", "Florianópolis", 250);
    grafo.adicionarArestaBidirecional("Porto Alegre", "Florianópolis", 375);
    grafo.adicionarArestaBidirecional("Salvador", "Aracaju", 277);
    grafo.adicionarArestaBidirecional("Salvador", "Recife", 675);
    grafo.adicionarArestaBidirecional("Aracaju", "Maceió", 199);
    grafo.adicionarArestaBidirecional("Maceió", "Recife", 202);
    grafo.adicionarArestaBidirecional("Maceió", "Salvador", 474);
    grafo.adicionarArestaBidirecional("Recife", "João Pessoa", 104);
    grafo.adicionarArestaBidirecional("João Pessoa", "Natal", 151);
    grafo.adicionarArestaBidirecional("Natal", "Fortaleza", 435);
    grafo.adicionarArestaBidirecional("Fortaleza", "Teresina", 496);
    grafo.adicionarArestaBidirecional("Fortaleza", "Recife", 629);
    grafo.adicionarArestaBidirecional("Teresina", "São Luís", 328);
    grafo.adicionarArestaBidirecional("Teresina", "Salvador", 891);
    grafo.adicionarArestaBidirecional("São Luís", "Belém", 482);
    grafo.adicionarArestaBidirecional("São Luís", "Palmas", 894);
    grafo.adicionarArestaBidirecional("Brasília", "Goiânia", 173);
    grafo.adicionarArestaBidirecional("Palmas", "Belém", 973);
    grafo.adicionarArestaBidirecional("Palmas", "Teresina", 745);
    grafo.adicionarArestaBidirecional("Belém", "Macapá", 330);
    grafo.adicionarArestaBidirecional("Belém", "Manaus", 1292);
    grafo.adicionarArestaBidirecional("Manaus", "Boa Vista", 662);
    grafo.adicionarArestaBidirecional("Manaus", "Porto Velho", 761);
    grafo.adicionarArestaBidirecional("Porto Velho", "Rio Branco", 447);
    grafo.adicionarArestaBidirecional("Porto Velho", "Cuiabá", 1152);
    grafo.adicionarArestaBidirecional("Campo Grande", "Cuiabá", 553);
    grafo.adicionarArestaBidirecional("Vitória", "Salvador", 837);
    grafo.adicionarArestaBidirecional("Goiânia", "Campo Grande", 730);
    grafo.adicionarArestaBidirecional("Goiânia", "Cuiabá", 739);

    // Novas conexões adicionadas (apenas entre estados vizinhos que faltavam)
    grafo.adicionarArestaBidirecional("Salvador", "Palmas", 973); // BA-TO
    grafo.adicionarArestaBidirecional("Salvador", "Goiânia", 1199); // BA-GO
    grafo.adicionarArestaBidirecional("Palmas", "Goiânia", 711); // TO-GO
    grafo.adicionarArestaBidirecional("Palmas", "Cuiabá", 832); // TO-MT
    grafo.adicionarArestaBidirecional("Cuiabá", "Manaus", 1448); // MT-AM
    grafo.adicionarArestaBidirecional("Cuiabá", "Belém", 1555); // MT-PA
    grafo.adicionarArestaBidirecional("Manaus", "Rio Branco", 1183); // AM-AC
    grafo.adicionarArestaBidirecional("Boa Vista", "Belém", 1850); // AM-AC

    return grafo;
  }

  public List<Aresta> getVizinhos(Cidade c) {
    // TODO Auto-generated method stub
    return cidades.get(c.getNome()).getVizinhos();
  }
}