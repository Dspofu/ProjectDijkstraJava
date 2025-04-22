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

    // Adicionar arestas
    sp.setNewAresta(new Aresta(bh, 600));
    sp.setNewAresta(new Aresta(rio, 400));
    sp.setNewAresta(new Aresta(curitiba, 400));
    bh.setNewAresta(new Aresta(salvador, 1000));
    rio.setNewAresta(new Aresta(curitiba, 300));
    rio.setNewAresta(new Aresta(portoAlegre, 1200));
    curitiba.setNewAresta(new Aresta(portoAlegre, 600));
    salvador.setNewAresta(new Aresta(portoAlegre, 1600));

    Grafo[] cidades = { sp, bh, salvador, rio, curitiba, portoAlegre };

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

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Main());
  }
}
