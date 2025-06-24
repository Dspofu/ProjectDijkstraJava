package com.djikstra;

import com.djikstra.Map.Cidade;
import com.djikstra.Map.Grafo;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import com.djikstra.ui.CustomWaypoint;
import com.djikstra.ui.RoutePainter;
import com.djikstra.ui.CustomWaypointRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class VisualizadorDeRotas extends JFrame {

  // Cores do tema moderno
  private static final Color PRIMARY_COLOR = new Color(37, 99, 235); // Azul moderno
  private static final Color SUCCESS_COLOR = new Color(34, 197, 94); // Verde
  private static final Color WARNING_COLOR = new Color(251, 146, 60); // Laranja
  private static final Color DANGER_COLOR = new Color(239, 68, 68); // Vermelho
  private static final Color BACKGROUND_COLOR = new Color(248, 250, 252); // Cinza claro
  private static final Color CARD_COLOR = Color.WHITE;
  private static final Color TEXT_PRIMARY = new Color(15, 23, 42); // Cinza escuro
  private static final Color TEXT_SECONDARY = new Color(100, 116, 139); // Cinza médio

  private final Grafo grafo = Grafo.criarGrafoBrasil();
  private final JXMapViewer mapViewer = new JXMapViewer();
  private final CompoundPainter<JXMapViewer> painter = new CompoundPainter<>();
  private RoutePainter rotaAtualPainter;

  // Componentes de resultado
  private JLabel lblDistanciaTotal;
  private JTextArea textAreaRota;
  private JLabel lblTempoEstimado;

  // Componentes da UI
  private JComboBox<Cidade> cbOrigem;
  private JComboBox<Cidade> cbDestino;
  private JButton btnCalcular;
  private JButton btnLimpar;
  private JLabel lblStatus;
  private JProgressBar progressBar;

  public VisualizadorDeRotas() {
    super("Navegador de Rotas - Brasil");

    // Configura o Look and Feel moderno
    configurarLookAndFeel();

    // Configura a janela principal
    configurarJanela();

    // Configura o mapa
    configurarMapa();

    // Cria a interface
    criarInterface();
  }

  private void configurarLookAndFeel() {
    try {
      // Tenta usar o FlatLaf se disponível, senão usa o Nimbus
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      // Configurações personalizadas do UI
      UIManager.put("Button.arc", 8);
      UIManager.put("Component.arc", 8);
      UIManager.put("TextComponent.arc", 8);
      UIManager.put("ComboBox.arc", 8);

    } catch (Exception e) {
      System.err.println("Não foi possível definir o Look and Feel: " + e.getMessage());
    }
  }

  private void configurarJanela() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1400, 900);
    setLocationRelativeTo(null);
    setMinimumSize(new Dimension(1000, 700));
    getContentPane().setBackground(BACKGROUND_COLOR);
  }

  private void configurarMapa() {
    TileFactoryInfo info = new OSMTileFactoryInfo();
    DefaultTileFactory tileFactory = new DefaultTileFactory(info);
    mapViewer.setTileFactory(tileFactory);
    mapViewer.setOverlayPainter(painter);

    // Adiciona borda arredondada ao mapa
    mapViewer.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)));

    adicionarWaypointsCidades();

    MouseInputListener mia = new PanMouseInputListener(mapViewer);
    mapViewer.addMouseListener(mia);
    mapViewer.addMouseMotionListener(mia);
    mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

    Set<GeoPosition> todasAsCidadesPos = grafo.getCidades().stream()
        .map(c -> new GeoPosition(c.getLatitude(), c.getLongitude()))
        .collect(Collectors.toSet());
    mapViewer.zoomToBestFit(todasAsCidadesPos, 0.7);
    mapViewer.setAddressLocation(new GeoPosition(-14.2350, -51.9253));
    mapViewer.setZoom(14);
  }

  private void adicionarWaypointsCidades() {
    Set<CustomWaypoint> waypoints = new HashSet<>();
    for (Cidade cidade : grafo.getCidades()) {
      waypoints.add(new CustomWaypoint(cidade.getNome(),
          new GeoPosition(cidade.getLatitude(), cidade.getLongitude())));
    }

    WaypointPainter<CustomWaypoint> waypointPainter = new WaypointPainter<>();
    waypointPainter.setWaypoints(waypoints);
    waypointPainter.setRenderer(new CustomWaypointRenderer());
    painter.addPainter(waypointPainter);
  }

  private void criarInterface() {
    setLayout(new BorderLayout(10, 10));

    // Painel principal com margem
    JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
    painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
    painelPrincipal.setBackground(BACKGROUND_COLOR);

    // Header
    JPanel header = criarHeader();
    painelPrincipal.add(header, BorderLayout.NORTH);

    // Painel central com mapa e controles
    JPanel painelCentral = criarPainelCentral();
    painelPrincipal.add(painelCentral, BorderLayout.CENTER);

    // Footer com status
    JPanel footer = criarFooter();
    painelPrincipal.add(footer, BorderLayout.SOUTH);

    add(painelPrincipal);
  }

  private JPanel criarHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(CARD_COLOR);
    header.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        new EmptyBorder(20, 25, 20, 25)));

    // Título
    JLabel titulo = new JLabel("Navegador de Rotas do Brasil");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titulo.setForeground(TEXT_PRIMARY);

    JLabel subtitulo = new JLabel("Encontre a rota mais eficiente entre capitais brasileiras");
    subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitulo.setForeground(TEXT_SECONDARY);

    JPanel painelTitulo = new JPanel(new BorderLayout());
    painelTitulo.setBackground(CARD_COLOR);
    painelTitulo.add(titulo, BorderLayout.NORTH);
    painelTitulo.add(subtitulo, BorderLayout.CENTER);

    header.add(painelTitulo, BorderLayout.WEST);

    return header;
  }

  private JPanel criarPainelCentral() {
    JPanel central = new JPanel(new BorderLayout(15, 0));
    central.setBackground(BACKGROUND_COLOR);

    // Painel de controles (lateral esquerda)
    JPanel painelControles = criarPainelControles();
    central.add(painelControles, BorderLayout.WEST);

    // Painel do mapa
    JPanel painelMapa = criarPainelMapa();
    central.add(painelMapa, BorderLayout.CENTER);

    return central;
  }

  private JPanel criarPainelControles() {
    JPanel painel = new JPanel();
    painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
    painel.setBackground(CARD_COLOR);
    painel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        new EmptyBorder(25, 20, 25, 20)));
    painel.setPreferredSize(new Dimension(320, 0));

    // Título da seção
    JLabel tituloControles = new JLabel("PLANEJAMENTO DE ROTA");
    tituloControles.setFont(new Font("Segoe UI", Font.BOLD, 16));
    tituloControles.setForeground(TEXT_PRIMARY);
    tituloControles.setAlignmentX(Component.LEFT_ALIGNMENT);
    painel.add(tituloControles);

    painel.add(Box.createVerticalStrut(20));

    // Origem
    JLabel lblOrigem = criarLabel("Cidade de Origem:");
    painel.add(lblOrigem);
    painel.add(Box.createVerticalStrut(8));

    cbOrigem = criarComboBox();
    cbOrigem.setSelectedItem(grafo.getCidade("São Paulo"));
    painel.add(cbOrigem);

    painel.add(Box.createVerticalStrut(20));

    // Destino
    JLabel lblDestino = criarLabel("Cidade de Destino:");
    painel.add(lblDestino);
    painel.add(Box.createVerticalStrut(8));

    cbDestino = criarComboBox();
    cbDestino.setSelectedItem(grafo.getCidade("Brasília"));
    painel.add(cbDestino);

    painel.add(Box.createVerticalStrut(30));

    // Botões
    btnCalcular = criarBotaoPrimario("Calcular Rota", PRIMARY_COLOR);
    btnCalcular.addActionListener(this::calcularRota);
    painel.add(btnCalcular);

    painel.add(Box.createVerticalStrut(10));

    btnLimpar = criarBotaoSecundario("Limpar Mapa");
    btnLimpar.addActionListener(this::limparMapa);
    painel.add(btnLimpar);

    painel.add(Box.createVerticalStrut(20));

    // Progress bar
    progressBar = new JProgressBar();
    progressBar.setVisible(false);
    progressBar.setStringPainted(true);
    progressBar.setString("Calculando rota...");
    progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
    progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    progressBar.setIndeterminate(true);
    painel.add(progressBar);

    painel.add(Box.createVerticalStrut(30));

    // Seção de Resultados
    JLabel tituloResultados = criarLabel("RESULTADOS:");
    painel.add(tituloResultados);
    painel.add(Box.createVerticalStrut(10));

    // Painel de resultados
    JPanel painelResultados = new JPanel();
    painelResultados.setLayout(new BoxLayout(painelResultados, BoxLayout.Y_AXIS));
    painelResultados.setBackground(new Color(248, 250, 252));
    painelResultados.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        new EmptyBorder(15, 15, 15, 15)));
    painelResultados.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Distância total
    JLabel lblDistanciaLabel = new JLabel("Distancia Total:");
    lblDistanciaLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lblDistanciaLabel.setForeground(TEXT_SECONDARY);
    lblDistanciaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    lblDistanciaTotal = new JLabel("-- km");
    lblDistanciaTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblDistanciaTotal.setForeground(PRIMARY_COLOR);
    lblDistanciaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Rota
    JLabel lblRotaLabel = new JLabel("Rota:");
    lblRotaLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lblRotaLabel.setForeground(TEXT_SECONDARY);
    lblRotaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    textAreaRota = new JTextArea(4, 20);
    textAreaRota.setText("Nenhuma rota calculada");
    textAreaRota.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    textAreaRota.setForeground(TEXT_SECONDARY);
    textAreaRota.setBackground(new Color(248, 250, 252));
    textAreaRota.setEditable(false);
    textAreaRota.setLineWrap(true);
    textAreaRota.setWrapStyleWord(true);
    textAreaRota.setBorder(new EmptyBorder(5, 0, 0, 0));

    JScrollPane scrollRota = new JScrollPane(textAreaRota);
    scrollRota.setBorder(null);
    scrollRota.setBackground(new Color(248, 250, 252));
    scrollRota.setAlignmentX(Component.LEFT_ALIGNMENT);
    scrollRota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

    // Tempo estimado
    JLabel lblTempoLabel = new JLabel("Tempo Estimado:");
    lblTempoLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lblTempoLabel.setForeground(TEXT_SECONDARY);
    lblTempoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    lblTempoEstimado = new JLabel("-- horas");
    lblTempoEstimado.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblTempoEstimado.setForeground(SUCCESS_COLOR);
    lblTempoEstimado.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Adicionar componentes ao painel de resultados
    painelResultados.add(lblDistanciaLabel);
    painelResultados.add(lblDistanciaTotal);
    painelResultados.add(Box.createVerticalStrut(10));
    painelResultados.add(lblRotaLabel);
    painelResultados.add(scrollRota);
    painelResultados.add(Box.createVerticalStrut(10));
    painelResultados.add(lblTempoLabel);
    painelResultados.add(lblTempoEstimado);

    painel.add(painelResultados);

    painel.add(Box.createVerticalGlue());

    return painel;
  }

  private JPanel criarPainelMapa() {
    JPanel painel = new JPanel(new BorderLayout());
    painel.setBackground(CARD_COLOR);
    painel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        new EmptyBorder(10, 10, 10, 10)));

    // Título do mapa
    JLabel tituloMapa = new JLabel("Mapa Interativo");
    tituloMapa.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tituloMapa.setForeground(TEXT_PRIMARY);
    tituloMapa.setBorder(new EmptyBorder(0, 10, 10, 0));

    painel.add(tituloMapa, BorderLayout.NORTH);
    painel.add(mapViewer, BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarFooter() {
    JPanel footer = new JPanel(new BorderLayout());
    footer.setBackground(CARD_COLOR);
    footer.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        new EmptyBorder(15, 25, 15, 25)));

    lblStatus = new JLabel("Pronto para calcular rotas");
    lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblStatus.setForeground(SUCCESS_COLOR);

    // --- ALTERAÇÃO AQUI ---
    // Para exibir o emoji, é preciso usar uma fonte que o suporte.
    // "Segoe UI Emoji" funciona bem no Windows. "SansSerif" é um fallback.
    JLabel creditos = new JLabel("Desenvolvido usando algoritmo de Dijkstra");
    creditos.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
    creditos.setForeground(TEXT_SECONDARY);

    footer.add(lblStatus, BorderLayout.WEST);
    footer.add(creditos, BorderLayout.EAST);

    return footer;
  }

  private JLabel criarLabel(String texto) {
    JLabel label = new JLabel(texto);
    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
    label.setForeground(TEXT_PRIMARY);
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    return label;
  }

  private JComboBox<Cidade> criarComboBox() {
    JComboBox<Cidade> combo = new JComboBox<>(grafo.getCidades().toArray(new Cidade[0]));
    combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    return combo;
  }

  private JButton criarBotaoPrimario(String texto, Color cor) {
    JButton botao = new JButton(texto);
    botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
    botao.setForeground(Color.WHITE);
    botao.setBackground(cor);
    botao.setBorderPainted(false);
    botao.setFocusPainted(false);
    botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    botao.setAlignmentX(Component.LEFT_ALIGNMENT);
    botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Efeito hover
    botao.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        botao.setBackground(cor.darker());
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        botao.setBackground(cor);
      }
    });

    return botao;
  }

  private JButton criarBotaoSecundario(String texto) {
    JButton botao = new JButton(texto);
    botao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    botao.setForeground(TEXT_PRIMARY);
    botao.setBackground(new Color(241, 245, 249));
    botao.setBorderPainted(false);
    botao.setFocusPainted(false);
    botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    botao.setAlignmentX(Component.LEFT_ALIGNMENT);
    botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return botao;
  }

  private void calcularRota(ActionEvent e) {
    Cidade origem = (Cidade) cbOrigem.getSelectedItem();
    Cidade destino = (Cidade) cbDestino.getSelectedItem();

    if (origem == null || destino == null) {
      mostrarMensagem("Atenção", "Por favor, selecione a origem e o destino.", WARNING_COLOR);
      return;
    }

    if (origem.equals(destino)) {
      mostrarMensagem("Erro", "A cidade de origem e destino devem ser diferentes.", DANGER_COLOR);
      return;
    }

    // Mostra progress bar
    progressBar.setVisible(true);
    btnCalcular.setEnabled(false);
    lblStatus.setText("Calculando a melhor rota...");
    lblStatus.setForeground(PRIMARY_COLOR);

    // Simula processamento em background
    SwingWorker<Navegador.Resultado, Void> worker = new SwingWorker<Navegador.Resultado, Void>() {
      @Override
      protected Navegador.Resultado doInBackground() throws Exception {
        Thread.sleep(500); // Simula processamento
        return Navegador.calcularRota(grafo, origem, destino);
      }

      @Override
      protected void done() {
        try {
          Navegador.Resultado resultado = get();
          processarResultado(resultado, origem, destino);
        } catch (Exception ex) {
          mostrarMensagem("Erro", "Erro ao calcular rota: " + ex.getMessage(), DANGER_COLOR);
        } finally {
          progressBar.setVisible(false);
          btnCalcular.setEnabled(true);
        }
      }
    };

    worker.execute();
  }

  private void processarResultado(Navegador.Resultado resultado, Cidade origem, Cidade destino) {
    if (rotaAtualPainter != null) {
      painter.removePainter(rotaAtualPainter);
    }

    if (!resultado.temCaminho()) {
      lblStatus.setText("Nenhuma rota encontrada");
      lblStatus.setForeground(DANGER_COLOR);

      // Limpar resultados
      lblDistanciaTotal.setText("-- km");
      textAreaRota.setText("Nenhuma rota encontrada");
      lblTempoEstimado.setText("-- horas");

      mostrarMensagem("Rota não encontrada", "Não foi possível encontrar uma rota entre " + origem.getNome() + " e " + destino.getNome() + ".", WARNING_COLOR);
    } else {
      List<GeoPosition> track = resultado.caminho.stream()
          .map(c -> new GeoPosition(c.getLatitude(), c.getLongitude()))
          .collect(Collectors.toList());

      rotaAtualPainter = new RoutePainter(track);
      painter.addPainter(rotaAtualPainter);
      mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);

      // Atualizar painel de resultados
      NumberFormat formatador = NumberFormat.getInstance(new Locale("pt", "BR"));
      lblDistanciaTotal.setText(formatador.format(resultado.distanciaTotal) + " km");

      String caminhoStr = resultado.caminho.stream()
          .map(Cidade::getNome)
          .collect(Collectors.joining(" → "));
      textAreaRota.setText(caminhoStr);

      // Calcular tempo estimado (assumindo velocidade média de 80 km/h)
      double tempoHoras = resultado.distanciaTotal / 80.0;
      int horas = (int) tempoHoras;
      int minutos = (int) ((tempoHoras - horas) * 60);
      lblTempoEstimado.setText(String.format("%dh %dmin", horas, minutos));

      lblStatus.setText("Rota calculada com sucesso!");
      lblStatus.setForeground(SUCCESS_COLOR);

      exibirResultadoModerno(resultado);
    }

    mapViewer.repaint();
  }

  private void limparMapa(ActionEvent e) {
    if (rotaAtualPainter != null) {
      painter.removePainter(rotaAtualPainter);
      rotaAtualPainter = null;
      mapViewer.repaint();

      // Limpar resultados
      lblDistanciaTotal.setText("-- km");
      textAreaRota.setText("Nenhuma rota calculada");
      lblTempoEstimado.setText("-- horas");

      lblStatus.setText("Mapa limpo");
      lblStatus.setForeground(TEXT_SECONDARY);
    }
  }

  private void exibirResultadoModerno(Navegador.Resultado resultado) {
    String caminhoStr = resultado.caminho.stream()
        .map(Cidade::getNome)
        .collect(Collectors.joining(" ➡️ "));

    NumberFormat formatador = NumberFormat.getInstance(new Locale("pt", "BR"));

    // Ícone e título
    JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    painelTitulo.setBackground(Color.WHITE);

    // --- ALTERAÇÃO AQUI ---
    JLabel icone = new JLabel("🎯");
    icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32)); // Fonte para o emoji do ícone

    JLabel titulo = new JLabel("Rota Otimizada Encontrada!");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titulo.setForeground(SUCCESS_COLOR);
    painelTitulo.add(icone);
    painelTitulo.add(titulo);

    // Conteúdo
    JTextArea textArea = new JTextArea();

    // --- ALTERAÇÃO AQUI ---
    // Adicionando emojis e definindo a fonte correta para o JTextArea
    double tempoHoras = resultado.distanciaTotal / 80.0;
    int horas = (int) tempoHoras;
    int minutos = (int) ((tempoHoras - horas) * 60);

    textArea.setText("🛣️ ROTA:\n" + caminhoStr + "\n\n" + "📏 DISTÂNCIA TOTAL:\n" + formatador.format(resultado.distanciaTotal) + " km\n\n" + "⏱️ TEMPO ESTIMADO:\n" + String.format("%dh %dmin", horas, minutos));

    textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
    textArea.setEditable(false);
    textArea.setBackground(new Color(248, 250, 252));
    textArea.setBorder(new EmptyBorder(15, 15, 15, 15));
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setBorder(null);

    // Botão
    // --- ALTERAÇÃO AQUI ---
    JButton btnFechar = criarBotaoPrimario("✅ Entendi", SUCCESS_COLOR);
    btnFechar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12)); // Fonte para o emoji no botão
  }

  private void mostrarMensagem(String titulo, String mensagem, Color cor) {
    JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        new VisualizadorDeRotas().setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}