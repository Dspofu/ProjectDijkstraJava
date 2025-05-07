import Map.Aresta;
import Map.Grafo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
  private JComboBox<Grafo> origemCombo;
  private JComboBox<Grafo> destinoCombo;
  private JTextArea resultadoArea;
  private Navegador navegador;

  public Main() {
    setTitle("Calculadora de Rotas");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 300);
    setLayout(new BorderLayout());

    // Criar cidades
    Grafo sp = new Grafo("São Paulo");
    Grafo bh = new Grafo("Belo Horizonte");
    Grafo salvador = new Grafo("Salvador");
    Grafo rio = new Grafo("Rio de Janeiro");
    Grafo curitiba = new Grafo("Curitiba");
    Grafo portoAlegre = new Grafo("Porto Alegre");
    Grafo recife = new Grafo("Recife");
    Grafo fortaleza = new Grafo("Fortaleza");
    Grafo brasilia = new Grafo("Brasília");
    Grafo goiania = new Grafo("Goiânia");
    Grafo natal = new Grafo("Natal");
    Grafo vitoria = new Grafo("Vitória");
    Grafo maceio = new Grafo("Maceió");
    Grafo manaus = new Grafo("Manaus");
    Grafo belem = new Grafo("Belém");
    Grafo palmas = new Grafo("Palmas");
    Grafo saoLuis = new Grafo("São Luís");
    Grafo teresina = new Grafo("Teresina");
    Grafo joaoPessoa = new Grafo("João Pessoa");
    Grafo aracaju = new Grafo("Aracaju");
    Grafo campoGrande = new Grafo("Campo Grande");
    Grafo cuiaba = new Grafo("Cuiabá");
    Grafo portoVelho = new Grafo("Porto Velho");
    Grafo rioBranco = new Grafo("Rio Branco");
    Grafo macapa = new Grafo("Macapá");
    Grafo boaVista = new Grafo("Boa Vista");
    Grafo florianopolis = new Grafo("Florianópolis"); // ADICIONADO

    // Conexões bidirecionais
    conectarBidirecional(sp, rio, 430);
    conectarBidirecional(sp, curitiba, 410);
    conectarBidirecional(sp, bh, 580);
    conectarBidirecional(sp, brasilia, 1015);
    conectarBidirecional(sp, campoGrande, 990);

    conectarBidirecional(rio, vitoria, 520);
    conectarBidirecional(rio, bh, 440);

    conectarBidirecional(bh, goiania, 910);
    conectarBidirecional(bh, salvador, 1370);
    conectarBidirecional(bh, maceio, 1600);

    conectarBidirecional(curitiba, portoAlegre, 710);
    conectarBidirecional(curitiba, campoGrande, 990);
    conectarBidirecional(curitiba, florianopolis, 300); // ADICIONADO (ou florianopolis, curitiba)

    conectarBidirecional(portoAlegre, cuiaba, 1900);
    conectarBidirecional(portoAlegre, campoGrande, 1400);
    conectarBidirecional(portoAlegre, florianopolis, 470); // ADICIONADO (ou florianopolis, portoAlegre)

    conectarBidirecional(salvador, aracaju, 330);
    conectarBidirecional(aracaju, maceio, 280);
    conectarBidirecional(maceio, recife, 260);
    conectarBidirecional(recife, joaoPessoa, 120);
    conectarBidirecional(joaoPessoa, natal, 180);
    conectarBidirecional(natal, fortaleza, 520);
    conectarBidirecional(fortaleza, teresina, 630);
    conectarBidirecional(teresina, saoLuis, 450);
    conectarBidirecional(saoLuis, belem, 800);

    conectarBidirecional(brasilia, goiania, 210);
    conectarBidirecional(brasilia, cuiaba, 1130);
    conectarBidirecional(brasilia, palmas, 970);
    conectarBidirecional(brasilia, salvador, 1450);

    conectarBidirecional(palmas, belem, 990);
    conectarBidirecional(palmas, teresina, 970);

    conectarBidirecional(belem, macapa, 530);
    conectarBidirecional(belem, manaus, 1600);

    conectarBidirecional(manaus, boaVista, 750);
    conectarBidirecional(manaus, portoVelho, 900);
    conectarBidirecional(portoVelho, rioBranco, 510);
    conectarBidirecional(portoVelho, cuiaba, 1450);

    conectarBidirecional(campoGrande, cuiaba, 700);

    conectarBidirecional(vitoria, salvador, 1200);

    // --- NOVAS SUGESTÕES DE CONEXÕES DA RESPOSTA ANTERIOR (se desejar mantê-las)
    // ---
    conectarBidirecional(goiania, campoGrande, 900);
    conectarBidirecional(goiania, cuiaba, 900);
    conectarBidirecional(salvador, recife, 840);
    conectarBidirecional(teresina, salvador, 1000);
    conectarBidirecional(saoLuis, palmas, 1100);

    Grafo[] cidades = { sp, bh, salvador, rio, curitiba, portoAlegre, recife, fortaleza, brasilia, goiania, natal,
        vitoria, maceio, manaus, belem, palmas, saoLuis, teresina, joaoPessoa, aracaju, campoGrande, cuiaba, portoVelho,
        rioBranco, macapa, boaVista };

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(3, 2));

    inputPanel.add(new JLabel("Origem:"));
    origemCombo = new JComboBox<>(cidades);
    inputPanel.add(origemCombo);

    inputPanel.add(new JLabel("Destino:"));
    destinoCombo = new JComboBox<>(cidades);
    inputPanel.add(destinoCombo);

    JButton calcularBtn = new JButton("Calcular Rota");
    inputPanel.add(calcularBtn);

    resultadoArea = new JTextArea(5, 30);
    resultadoArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(resultadoArea);

    add(inputPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);

    navegador = new Navegador();

    calcularBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Grafo origem = (Grafo) origemCombo.getSelectedItem();
        Grafo destino = (Grafo) destinoCombo.getSelectedItem();
        if (origem.equals(destino)) {
          JOptionPane.showMessageDialog(null, "Origem e destino devem ser diferentes.");
          return;
        }
        if (origem != null && destino != null) {
          navegador.calcularRota(origem, destino);
          resultadoArea.setText(navegador.getMelhorCaminhoComoTexto());
        } else {
          resultadoArea.setText("Selecione origem e destino.");
        }
      }
    });

    setLocationRelativeTo(null);
    setVisible(true);
  }

  // Criar conexões bidirecionais
  private static void conectarBidirecional(Grafo a, Grafo b, int distancia) {
    a.setNewAresta(new Aresta(b, distancia));
    b.setNewAresta(new Aresta(a, distancia));
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Main());
  }
}