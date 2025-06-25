package com.dijkstra;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import com.dijkstra.Map.Cidade;
import com.dijkstra.Map.Grafo;
import com.dijkstra.ui.CustomWaypoint;
import com.dijkstra.ui.CustomWaypointRenderer;
import com.dijkstra.ui.RoutePainter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class VisualizadorDeRotas extends JFrame {

    // Cores do tema moderno
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color PRIMARY_DARK = new Color(37, 99, 235);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color SUCCESS_DARK = new Color(22, 163, 74);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color HOVER_COLOR = new Color(241, 245, 249);

    // Fontes para emojis
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 12);
    private static final Font EMOJI_FONT_LARGE = new Font("Segoe UI Emoji", Font.PLAIN, 16);
    private static final Font EMOJI_FONT_XLARGE = new Font("Segoe UI Emoji", Font.PLAIN, 20);

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
    private JComboBox<String> cbEstiloMapa;
    private JTextField tfApiKey; // Campo para API key Thunderforest

    // Mapa de waypoints para controle de cores
    private final Map<String, CustomWaypoint> waypointsMap = new HashMap<>();

    // Timer para animações
    private javax.swing.Timer animationTimer;

    // Painters
    private WaypointPainter<CustomWaypoint> waypointPainter; // Mantém referência global

    private String thunderforestApiKey = null;
    private boolean thunderforestBloqueado = false;

    public VisualizadorDeRotas() {
        super("Navegador de Rotas - Brasil");
        
        configurarLookAndFeel();
        configurarJanela();
        configurarMapa();
        criarInterface();
        
        // Animação de entrada alternativa
        animarEntradaAlternativa();
    }

    private void configurarLookAndFeel() {
        try {
            // Configurações avançadas do UI
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ComboBox.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 950);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 800));
        
        // Ícone da janela
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            System.err.println("Erro ao definir ícone: " + e.getMessage());
        }
        
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenha um ícone simples de mapa
        g2.setColor(PRIMARY_COLOR);
        g2.fillRoundRect(4, 4, 24, 24, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(8, 12, 16, 20);
        g2.drawLine(16, 20, 24, 12);
        g2.fillOval(14, 18, 4, 4);
        
        g2.dispose();
        return icon;
    }

    private void configurarMapa() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setOverlayPainter(painter);

        // Borda moderna com sombra
        mapViewer.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createLineBorder(BORDER_COLOR, 1)
        ));

        adicionarWaypointsCidades();
        // Mostra todos os pinos ao iniciar
        CompoundPainter<JXMapViewer> novoPainter = new CompoundPainter<>(List.of(waypointPainter));
        mapViewer.setOverlayPainter(novoPainter);
        mapViewer.repaint();
        // Adiciona listener de clique nos pinos já no início
        mapViewer.addMouseListener(new WaypointClickListener(waypointsMap, mapViewer, grafo));

        // Tooltip ao passar o mouse sobre o pino (adicionado antes do PanMouseInputListener)
        mapViewer.setToolTipText(""); // Garante ativação do tooltip
        mapViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            private String lastTooltip = null;
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                Point mousePoint = e.getPoint();
                String tooltip = null;
                for (CustomWaypoint wp : waypointsMap.values()) {
                    Point2D wpPoint = mapViewer.getTileFactory().geoToPixel(wp.getPosition(), mapViewer.getZoom());
                    int pinWidth = 20;
                    int circleRadius = 10;
                    int pinX = (int) wpPoint.getX() - pinWidth / 2;
                    int pinY = (int) wpPoint.getY() - 30;
                    int circleCenterX = pinX + pinWidth / 2;
                    int circleCenterY = pinY + circleRadius;
                    double dist = mousePoint.distance(circleCenterX, circleCenterY);
                    if (dist <= circleRadius) {
                        String nome = wp.getLabel();
                        Cidade cidade = grafo.getCidade(nome);
                        if (cidade != null) {
                            tooltip = cidade.getNome() + " - " + cidade.getEstado();
                        } else {
                            tooltip = nome;
                        }
                        break;
                    }
                }
                if (!Objects.equals(lastTooltip, tooltip)) {
                    mapViewer.setToolTipText(tooltip);
                    lastTooltip = tooltip;
                }
            }
        });

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        // Garante que o ToolTipManager está ativado
        javax.swing.ToolTipManager.sharedInstance().registerComponent(mapViewer);

        // CORREÇÃO DO ZOOM: Configuração inicial do mapa com zoom adequado
        Set<GeoPosition> todasAsCidadesPos = grafo.getCidades().stream()
            .map(c -> new GeoPosition(c.getLatitude(), c.getLongitude()))
            .collect(Collectors.toSet());
        
        // Centraliza no Brasil e define zoom adequado
        mapViewer.setAddressLocation(new GeoPosition(-14.2350, -51.9253));
        mapViewer.setZoom(5); // Zoom menor para mostrar o Brasil inteiro
        
        // Ajusta para mostrar todas as cidades com margem adequada
        SwingUtilities.invokeLater(() -> {
            mapViewer.zoomToBestFit(todasAsCidadesPos, 0.9);
        });
    }

    private void adicionarWaypointsCidades() {
        Set<CustomWaypoint> waypoints = new HashSet<>();
        for (Cidade cidade : grafo.getCidades()) {
            CustomWaypoint wp = new CustomWaypoint(
                cidade.getNome(),
                new GeoPosition(cidade.getLatitude(), cidade.getLongitude())
            );
            waypoints.add(wp);
            waypointsMap.put(cidade.getNome(), wp);
        }
        waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints); // Mostra todos os pinos ao iniciar
        waypointPainter.setRenderer(new CustomWaypointRenderer());
    }

    private void criarInterface() {
        setLayout(new BorderLayout(15, 15));

        JPanel painelPrincipal = new JPanel(new BorderLayout(20, 20));
        painelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        painelPrincipal.setBackground(BACKGROUND_COLOR);

        // Header com gradiente
        JPanel header = criarHeader();
        painelPrincipal.add(header, BorderLayout.NORTH);

        // Painel central
        JPanel painelCentral = criarPainelCentral();
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);

        // Footer moderno
        JPanel footer = criarFooter();
        painelPrincipal.add(footer, BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JPanel criarHeader() {
        JPanel header = new GradientPanel(PRIMARY_COLOR, PRIMARY_DARK);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Título com ícone
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setOpaque(false);

        JLabel titulo = new JLabel("Navegador de Rotas do Brasil");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Encontre a rota mais eficiente entre capitais brasileiras usando o algoritmo de Dijkstra");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setForeground(new Color(255, 255, 255, 200));

        JPanel textos = new JPanel(new BorderLayout(0, 8));
        textos.setOpaque(false);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(subtitulo, BorderLayout.CENTER);

        painelTitulo.add(textos, BorderLayout.WEST);

        // Estatísticas
        JPanel stats = criarPainelEstatisticas();
        painelTitulo.add(stats, BorderLayout.EAST);

        header.add(painelTitulo);
        return header;
    }

    private JPanel criarPainelEstatisticas() {
        JPanel stats = new JPanel(new GridLayout(1, 2, 20, 0));
        stats.setOpaque(false);

        // Total de cidades
        JPanel statCidades = criarStatCard("Capitais", String.valueOf(grafo.getCidades().size()));
        stats.add(statCidades);

        // Conexões
        int totalConexoes = grafo.getCidades().stream()
            .mapToInt(c -> grafo.getVizinhos(c).size())
            .sum() / 2;
        
        JPanel statConexoes = criarStatCard("Rotas", String.valueOf(totalConexoes));
        stats.add(statConexoes);

        return stats;
    }

    private JPanel criarStatCard(String label, String valor) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setOpaque(false);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(Color.WHITE);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(new Color(255, 255, 255, 180));

        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);
        textos.add(lblValor, BorderLayout.NORTH);
        textos.add(lblLabel, BorderLayout.CENTER);

        card.add(textos, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarPainelCentral() {
        JPanel central = new JPanel(new BorderLayout(20, 0));
        central.setBackground(BACKGROUND_COLOR);

        JPanel painelControles = criarPainelControles();
        central.add(painelControles, BorderLayout.WEST);

        JPanel painelMapa = criarPainelMapa();
        central.add(painelMapa, BorderLayout.CENTER);

        return central;
    }

    private JPanel criarPainelControles() {
        JPanel painel = new ModernPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setPreferredSize(new Dimension(380, 0));

        // Título da seção
        JLabel tituloControles = new JLabel("PLANEJAMENTO DE ROTA");
        tituloControles.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloControles.setForeground(TEXT_PRIMARY);
        tituloControles.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(tituloControles);

        painel.add(Box.createVerticalStrut(18));

        // Estilo do mapa
        JLabel lblEstilo = new JLabel("Estilo do Mapa:");
        lblEstilo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstilo.setForeground(TEXT_PRIMARY);
        lblEstilo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblEstilo);
        painel.add(Box.createVerticalStrut(6));
        cbEstiloMapa = new JComboBox<>(new String[] {
            "Padrão (OSM)",
            "Humanitário",
            "OpenCycleMap",
            "Transport",
            "Landscape",
            "Outdoors",
            "Atlas",
            "Transport Dark",
            "Spinal Map",
            "Pioneer",
            "Neighbourhood",
            "Mobile Atlas"
        });
        cbEstiloMapa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cbEstiloMapa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstiloMapa.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbEstiloMapa.setSelectedIndex(0);
        cbEstiloMapa.addActionListener(e -> trocarEstiloMapa());
        painel.add(cbEstiloMapa);

        painel.add(Box.createVerticalStrut(18));

        // Campo para API key Thunderforest
        JLabel lblApiKey = new JLabel("API Key Thunderforest (para estilos CycleMap, Transport, Landscape):");
        lblApiKey.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblApiKey.setForeground(TEXT_SECONDARY);
        lblApiKey.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblApiKey);
        painel.add(Box.createVerticalStrut(4));
        tfApiKey = new JTextField();
        tfApiKey.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        tfApiKey.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tfApiKey.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfApiKey.setToolTipText("Obtenha uma chave gratuita em thunderforest.com");
        tfApiKey.addActionListener(e -> {
            thunderforestApiKey = tfApiKey.getText().trim();
            thunderforestBloqueado = false;
            cbEstiloMapa.repaint();
        });
        painel.add(tfApiKey);
        painel.add(Box.createVerticalStrut(10));

        // Origem
        adicionarCampoSelecao(painel, "Cidade de Origem:", cbOrigem = criarComboBox());
        cbOrigem.setSelectedItem(grafo.getCidade("São Paulo"));

        painel.add(Box.createVerticalStrut(20));

        // Destino
        adicionarCampoSelecao(painel, "Cidade de Destino:", cbDestino = criarComboBox());
        cbDestino.setSelectedItem(grafo.getCidade("Brasília"));

        painel.add(Box.createVerticalStrut(30));

        // Botões
        btnCalcular = criarBotaoModerno("Calcular Rota", PRIMARY_COLOR, PRIMARY_DARK);
        btnCalcular.addActionListener(this::calcularRota);
        painel.add(btnCalcular);

        painel.add(Box.createVerticalStrut(12));

        btnLimpar = criarBotaoSecundario("Limpar Mapa");
        btnLimpar.addActionListener(this::limparMapa);
        painel.add(btnLimpar);

        painel.add(Box.createVerticalStrut(20));

        // Progress bar moderno
        progressBar = criarProgressBarModerno();
        painel.add(progressBar);

        painel.add(Box.createVerticalStrut(30));

        // Seção de Resultados - CORREÇÃO: Usando JScrollPane para responsividade
        JScrollPane scrollResultados = criarPainelResultadosComScroll();
        painel.add(scrollResultados);

        return painel;
    }

    private void adicionarCampoSelecao(JPanel painel, String label, JComboBox<Cidade> combo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lbl);
        
        painel.add(Box.createVerticalStrut(8));
        painel.add(combo);
    }

    private JComboBox<Cidade> criarComboBox() {
        JComboBox<Cidade> combo = new JComboBox<>(grafo.getCidades().toArray(new Cidade[0]));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Renderer customizado
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof Cidade) {
                    Cidade cidade = (Cidade) value;
                    setText(cidade.getNome() + " (" + cidade.getEstado() + ")");
                }
                
                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                }
                
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return this;
            }
        });
        
        return combo;
    }

    private JButton criarBotaoModerno(String texto, Color cor, Color corHover) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente
                GradientPaint gradient = new GradientPaint(
                    0, 0, getModel().isPressed() ? corHover : cor,
                    0, getHeight(), getModel().isPressed() ? cor : corHover
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        botao.setForeground(TEXT_PRIMARY);
        botao.setBackground(HOVER_COLOR);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(226, 232, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(HOVER_COLOR);
            }
        });
        
        return botao;
    }

    private JProgressBar criarProgressBarModerno() {
        JProgressBar progress = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo
                g2.setColor(new Color(226, 232, 240));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                
                // Progresso
                if (isIndeterminate()) {
                    // Animação para progresso indeterminado
                    int width = getWidth() / 3;
                    int x = (int) ((System.currentTimeMillis() / 10) % (getWidth() + width)) - width;
                    
                    // Use two GradientPaints to simulate a three-color gradient
                    GradientPaint gradient1 = new GradientPaint(
                        x, 0, new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 0),
                        x + width / 2, 0, PRIMARY_COLOR
                    );
                    GradientPaint gradient2 = new GradientPaint(
                        x + width / 2, 0, PRIMARY_COLOR,
                        x + width, 0, new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 0)
                    );
                    // Draw first half
                    g2.setPaint(gradient1);
                    g2.fill(new RoundRectangle2D.Float(Math.max(0, x), 0, width / 2, getHeight(), 8, 8));
                    // Draw second half
                    g2.setPaint(gradient2);
                    g2.fill(new RoundRectangle2D.Float(Math.max(0, x) + width / 2, 0, Math.min(width / 2, getWidth() - (Math.max(0, x) + width / 2)), getHeight(), 8, 8));
                }
                
                g2.dispose();
            }
        };
        
        progress.setVisible(false);
        progress.setStringPainted(true);
        progress.setString("Calculando rota...");
        progress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progress.setAlignmentX(Component.LEFT_ALIGNMENT);
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        progress.setIndeterminate(true);
        
        return progress;
    }

    // CORREÇÃO: Painel de resultados com scroll para responsividade
    private JScrollPane criarPainelResultadosComScroll() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(248, 250, 252));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("RESULTADOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(titulo);

        painel.add(Box.createVerticalStrut(15));

        // Cards de resultado
        JPanel cardDistancia = criarCardResultado("Distância Total", 
            lblDistanciaTotal = new JLabel("-- km"), PRIMARY_COLOR);
        painel.add(cardDistancia);

        painel.add(Box.createVerticalStrut(12));

        JPanel cardTempo = criarCardResultado("Tempo Estimado", 
            lblTempoEstimado = new JLabel("-- horas"), SUCCESS_COLOR);
        painel.add(cardTempo);

        painel.add(Box.createVerticalStrut(15));

        // Área de rota
        JLabel lblRota = new JLabel("Rota:");
        lblRota.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRota.setForeground(TEXT_SECONDARY);
        lblRota.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblRota);

        painel.add(Box.createVerticalStrut(8));

        textAreaRota = new JTextArea(6, 20);
        textAreaRota.setText("Nenhuma rota calculada");
        textAreaRota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textAreaRota.setForeground(TEXT_SECONDARY);
        textAreaRota.setBackground(Color.WHITE);
        textAreaRota.setEditable(false);
        textAreaRota.setLineWrap(true);
        textAreaRota.setWrapStyleWord(true);
        textAreaRota.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollRota = new JScrollPane(textAreaRota);
        scrollRota.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollRota.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollRota.setPreferredSize(new Dimension(300, 120));

        painel.add(scrollRota);

        // Scroll principal para todo o painel de resultados
        JScrollPane scrollPrincipal = new JScrollPane(painel);
        scrollPrincipal.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPrincipal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPrincipal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPrincipal.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPrincipal.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        return scrollPrincipal;
    }

    private JPanel criarCardResultado(String label, JLabel valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(TEXT_SECONDARY);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valor.setForeground(cor);

        textos.add(lblLabel, BorderLayout.NORTH);
        textos.add(valor, BorderLayout.CENTER);

        card.add(textos, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarPainelMapa() {
        JPanel painel = new ModernPanel();
        painel.setLayout(new BorderLayout());

        JLabel tituloMapa = new JLabel("Mapa Interativo do Brasil");
        tituloMapa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloMapa.setForeground(TEXT_PRIMARY);
        tituloMapa.setBorder(new EmptyBorder(0, 0, 15, 0));

        painel.add(tituloMapa, BorderLayout.NORTH);
        painel.add(mapViewer, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarFooter() {
        JPanel footer = new ModernPanel();
        footer.setLayout(new BorderLayout());

        lblStatus = new JLabel("Pronto para calcular rotas");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(SUCCESS_COLOR);

        JLabel creditos = new JLabel("Desenvolvido com algoritmo de Dijkstra | JXMapViewer");
        creditos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        creditos.setForeground(TEXT_SECONDARY);

        footer.add(lblStatus, BorderLayout.WEST);
        footer.add(creditos, BorderLayout.EAST);

        return footer;
    }

    private void calcularRota(ActionEvent e) {
        Cidade origem = (Cidade) cbOrigem.getSelectedItem();
        Cidade destino = (Cidade) cbDestino.getSelectedItem();

        if (origem == null || destino == null) {
            mostrarNotificacao("Atenção", "Por favor, selecione a origem e o destino.", WARNING_COLOR);
            return;
        }

        if (origem.equals(destino)) {
            mostrarNotificacao("Erro", "A cidade de origem e destino devem ser diferentes.", DANGER_COLOR);
            return;
        }

        // Animação de loading
        iniciarAnimacaoCarregamento();

        SwingWorker<Navegador.Resultado, Void> worker = new SwingWorker<Navegador.Resultado, Void>() {
            @Override
            protected Navegador.Resultado doInBackground() throws Exception {
                Thread.sleep(800); // Simula processamento
                return Navegador.calcularRota(grafo, origem, destino);
            }

            @Override
            protected void done() {
                try {
                    Navegador.Resultado resultado = get();
                    System.out.println("DEBUG resultado: " + resultado);
                    processarResultado(resultado, origem, destino);
                } catch (Exception ex) {
                    ex.printStackTrace(); // Mostra o stack trace completo
                    mostrarNotificacao("Erro", "Erro ao calcular rota: " + ex.getMessage(), DANGER_COLOR);
                } finally {
                    VisualizadorDeRotas.this.pararAnimacaoCarregamento();
                }
            }
        };

        worker.execute();
    }

    private void iniciarAnimacaoCarregamento() {
        progressBar.setVisible(true);
        btnCalcular.setEnabled(false);
        lblStatus.setText("Calculando a melhor rota...");
        lblStatus.setForeground(PRIMARY_COLOR);

        // Timer para animar a progress bar
        animationTimer = new javax.swing.Timer(50, e -> progressBar.repaint());
        animationTimer.start();
    }

    private void pararAnimacaoCarregamento() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        progressBar.setVisible(false);
        btnCalcular.setEnabled(true);
    }

    private void processarResultado(Navegador.Resultado resultado, Cidade origem, Cidade destino) {
        // Reset waypoints colors
        waypointsMap.values().forEach(wp -> wp.setButtonColor(PRIMARY_COLOR));

        // Garante que o waypointPainter está inicializado
        if (waypointPainter == null) {
            adicionarWaypointsCidades();
        }

        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        if (!resultado.temCaminho()) {
            lblStatus.setText("Nenhuma rota encontrada");
            lblStatus.setForeground(DANGER_COLOR);

            lblDistanciaTotal.setText("-- km");
            textAreaRota.setText("Nenhuma rota foi encontrada entre " + origem.getNome() + " e " + destino.getNome() + ".");
            lblTempoEstimado.setText("-- horas");
            // Adiciona só os pinos de origem e destino
            Set<CustomWaypoint> waypoints = new HashSet<>();
            if (waypointsMap.containsKey(origem.getNome())) waypoints.add(waypointsMap.get(origem.getNome()));
            if (waypointsMap.containsKey(destino.getNome())) waypoints.add(waypointsMap.get(destino.getNome()));
            waypointPainter.setWaypoints(waypoints);
            painters.add(waypointPainter);
        } else {
            // Destaca origem e destino
            if (waypointsMap.containsKey(origem.getNome())) {
                waypointsMap.get(origem.getNome()).setButtonColor(SUCCESS_COLOR);
            }
            if (waypointsMap.containsKey(destino.getNome())) {
                waypointsMap.get(destino.getNome()).setButtonColor(DANGER_COLOR);
            }

            // Cria rota
            List<GeoPosition> track = resultado.caminho.stream()
                .map(c -> new GeoPosition(c.getLatitude(), c.getLongitude()))
                .collect(Collectors.toList());

            GeoPosition origemPos = new GeoPosition(origem.getLatitude(), origem.getLongitude());
            GeoPosition destinoPos = new GeoPosition(destino.getLatitude(), destino.getLongitude());

            rotaAtualPainter = new RoutePainter(track, origemPos, destinoPos);
            // Adiciona primeiro a linha, depois os pinos
            painters.add(rotaAtualPainter);

            // Cria waypoints apenas para o caminho traçado
            Set<CustomWaypoint> waypoints = new HashSet<>();
            for (Cidade cidade : resultado.caminho) {
                CustomWaypoint wp = waypointsMap.get(cidade.getNome());
                if (wp != null) waypoints.add(wp);
            }
            waypointPainter.setWaypoints(waypoints);
            painters.add(waypointPainter);

            // Zoom na rota
            mapViewer.zoomToBestFit(new HashSet<>(track), 0.8);

            // Atualiza resultados com animação
            atualizarResultadosComAnimacao(resultado);

            lblStatus.setText("Rota calculada com sucesso!");
            lblStatus.setForeground(SUCCESS_COLOR);
        }
        // Em vez de usar painter.setPainters, cria um novo CompoundPainter e seta direto no mapViewer
        CompoundPainter<JXMapViewer> novoPainter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(novoPainter);
        mapViewer.repaint();

        // Remove listeners antigos do tipo WaypointClickListener
        Arrays.stream(mapViewer.getMouseListeners())
            .filter(ml -> ml.getClass().getName().contains("WaypointClickListener"))
            .forEach(mapViewer::removeMouseListener);
        // Adiciona listener após todos os outros
        mapViewer.addMouseListener(new WaypointClickListener(waypointsMap, mapViewer, grafo));
    }

    // Listener para clique em pinos (corrigido para considerar viewport e ambos botões)
    private static class WaypointClickListener extends java.awt.event.MouseAdapter {
        private final Map<String, CustomWaypoint> waypointsMap;
        private final JXMapViewer mapViewer;
        private final Grafo grafo;
        public WaypointClickListener(Map<String, CustomWaypoint> waypointsMap, JXMapViewer mapViewer, Grafo grafo) {
            this.waypointsMap = waypointsMap;
            this.mapViewer = mapViewer;
            this.grafo = grafo;
        }
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            // Aceita qualquer botão
            if (e.getButton() != java.awt.event.MouseEvent.BUTTON1 && e.getButton() != java.awt.event.MouseEvent.BUTTON3) return;
            // Corrige para viewport
            Point mousePoint = e.getPoint();
            Rectangle viewport = mapViewer.getViewportBounds();
            int mouseX = mousePoint.x + viewport.x;
            int mouseY = mousePoint.y + viewport.y;
            for (CustomWaypoint wp : waypointsMap.values()) {
                Point2D wpPoint = mapViewer.getTileFactory().geoToPixel(wp.getPosition(), mapViewer.getZoom());
                int pinWidth = 20;
                int circleRadius = 12; // raio maior para facilitar
                int pinX = (int) wpPoint.getX() - pinWidth / 2;
                int pinY = (int) wpPoint.getY() - 30;
                int circleCenterX = pinX + pinWidth / 2;
                int circleCenterY = pinY + 10;
                double dist = Point2D.distance(mouseX, mouseY, circleCenterX, circleCenterY);
                if (dist <= circleRadius) {
                    String nome = wp.getLabel();
                    Cidade cidade = grafo.getCidade(nome);
                    String info = cidade != null ? cidade.getNome() + " - " + cidade.getEstado() : nome;
                    JOptionPane.showMessageDialog(mapViewer, info, "Informação da Cidade", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
    }

    private void atualizarResultadosComAnimacao(Navegador.Resultado resultado) {
        NumberFormat formatador = NumberFormat.getInstance(Locale.of("pt", "BR"));
        
        // Animação da distância
        javax.swing.Timer distanceTimer = new javax.swing.Timer(20, null);
        final double[] currentDistance = {0};
        final double targetDistance = resultado.distanciaTotal;
        final double increment = targetDistance / 50;
        
        distanceTimer.addActionListener(e -> {
            currentDistance[0] += increment;
            if (currentDistance[0] >= targetDistance) {
                currentDistance[0] = targetDistance;
                distanceTimer.stop();
            }
            lblDistanciaTotal.setText(formatador.format((int)currentDistance[0]) + " km");
        });
        distanceTimer.start();

        // Rota com animação de digitação
        String caminhoStr = resultado.caminho.stream()
            .map(cidade -> cidade.getNome() + " (" + cidade.getEstado() + ")")
            .collect(Collectors.joining(" -> "));

        animarTexto(textAreaRota, caminhoStr, 30);

        // Tempo estimado
        double tempoHoras = resultado.distanciaTotal / 80.0;
        int horas = (int) tempoHoras;
        int minutos = (int) ((tempoHoras - horas) * 60);
        
        javax.swing.Timer timeTimer = new javax.swing.Timer(100, e -> {
            lblTempoEstimado.setText(String.format("~ %dh %02dmin", horas, minutos));
            ((javax.swing.Timer)e.getSource()).stop();
        });
        timeTimer.setInitialDelay(500);
        timeTimer.start();
    }

    private void animarTexto(JTextArea textArea, String texto, int delay) {
        textArea.setText("");
        javax.swing.Timer timer = new javax.swing.Timer(delay, null);
        final int[] index = {0};
        
        timer.addActionListener(e -> {
            if (index[0] < texto.length()) {
                textArea.setText(texto.substring(0, index[0] + 1));
                index[0]++;
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    private void limparMapa(ActionEvent e) {
        if (rotaAtualPainter != null) {
            painter.removePainter(rotaAtualPainter);
            rotaAtualPainter = null;
            // Reset waypoints
            waypointsMap.values().forEach(wp -> wp.setButtonColor(PRIMARY_COLOR));
            // Restaurar todos os pinos ao limpar
            Set<CustomWaypoint> waypoints = new HashSet<>(waypointsMap.values());
            waypointPainter.setWaypoints(waypoints);
            // Repinta todos
            CompoundPainter<JXMapViewer> novoPainter = new CompoundPainter<>(List.of(waypointPainter));
            mapViewer.setOverlayPainter(novoPainter);
            mapViewer.repaint();
            // Limpar resultados com animação
            animarTexto(textAreaRota, "Nenhuma rota calculada", 20);
            lblDistanciaTotal.setText("-- km");
            lblTempoEstimado.setText("-- horas");

            lblStatus.setText("Mapa limpo");
            lblStatus.setForeground(TEXT_SECONDARY);
        }
    }

    private void animarEntradaAlternativa() {
        // Inicia com a janela um pouco menor e cresce suavemente
        Dimension targetSize = getSize();
        Dimension startSize = new Dimension(
            (int)(targetSize.width * 0.95), 
            (int)(targetSize.height * 0.95)
        );
        
        setSize(startSize);
        
        javax.swing.Timer growTimer = new javax.swing.Timer(20, null);
        final int[] step = {0};
        final int totalSteps = 15;
        
        growTimer.addActionListener(e -> {
            step[0]++;
            if (step[0] <= totalSteps) {
                double progress = (double) step[0] / totalSteps;
                progress = 1 - Math.pow(1 - progress, 3);
                
                int currentWidth = (int) (startSize.width + (targetSize.width - startSize.width) * progress);
                int currentHeight = (int) (startSize.height + (targetSize.height - startSize.height) * progress);
                
                setSize(currentWidth, currentHeight);
                setLocationRelativeTo(null);
            } else {
                growTimer.stop();
                setSize(targetSize);
                setLocationRelativeTo(null);
            }
        });
        
        javax.swing.Timer delayTimer = new javax.swing.Timer(100, e -> {
            growTimer.start();
            ((javax.swing.Timer)e.getSource()).stop();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void mostrarNotificacao(String titulo, String mensagem, Color cor) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void trocarEstiloMapa() {
        int idx = cbEstiloMapa.getSelectedIndex();
        TileFactoryInfo info;
        boolean thunderforest = false;
        String apiKey = thunderforestApiKey;
        // Todos os estilos Thunderforest (idx >= 2)
        if (idx >= 2) thunderforest = true;
        if (thunderforestBloqueado && thunderforest) {
            mostrarNotificacao("API Key inválida", "A chave Thunderforest informada não é válida. Por favor, forneça uma chave válida para usar este estilo.", DANGER_COLOR);
            cbEstiloMapa.setSelectedIndex(0);
            return;
        }
        if (thunderforest && (apiKey == null || apiKey.isEmpty())) {
            mostrarNotificacao("API Key necessária", "Para usar este estilo de mapa, forneça uma API key Thunderforest.", WARNING_COLOR);
            cbEstiloMapa.setSelectedIndex(0);
            return;
        }
        switch (idx) {
            case 1: // Humanitário
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile-b.openstreetmap.fr/hot/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png";
                    }
                };
                break;
            case 2: // OpenCycleMap
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/cycle/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 3: // Transport
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/transport/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 4: // Landscape
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/landscape/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 5: // Outdoors
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/outdoors/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 6: // Atlas
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/atlas/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 7: // Transport Dark
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/transport-dark/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 8: // Spinal Map
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/spinal-map/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 9: // Pioneer
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/pioneer/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 10: // Neighbourhood
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/neighbourhood/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            case 11: // Mobile Atlas
                info = new TileFactoryInfo(
                    1, 15, 17, 256, true, true, "https://tile.thunderforest.com/mobile-atlas/",
                    "x", "y", "z") {
                    public String getTileUrl(int x, int y, int zoom) {
                        int z = 17 - zoom;
                        return this.baseURL + z + "/" + x + "/" + y + ".png?apikey=" + apiKey;
                    }
                };
                break;
            default: // Padrão
                info = new OSMTileFactoryInfo();
        }
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.repaint();
        // Se for Thunderforest, testa se a key é válida (tile 0,0,0)
        if (thunderforest) {
            new Thread(() -> {
                try {
                    String testUrl = info.getTileUrl(0, 0, 0);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(testUrl).openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    int code = conn.getResponseCode();
                    if (code == 401) {
                        thunderforestBloqueado = true;
                        SwingUtilities.invokeLater(() -> {
                            mostrarNotificacao("API Key inválida", "A chave Thunderforest informada não é válida. Voltando ao estilo padrão.", DANGER_COLOR);
                            cbEstiloMapa.setSelectedIndex(0);
                        });
                    } else {
                        thunderforestBloqueado = false;
                    }
                } catch (Exception ex) {
                    // Falha de rede, não bloqueia, mas pode avisar
                }
            }).start();
        }
    }

    // Classes auxiliares para design moderno
    private static class ModernPanel extends JPanel {
        public ModernPanel() {
            setBackground(CARD_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                new EmptyBorder(25, 25, 25, 25)
            ));
        }
    }

    private static class GradientPanel extends JPanel {
        private final Color color1, color2;
        
        public GradientPanel(Color color1, Color color2) {
            this.color1 = color1;
            this.color2 = color2;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.dispose();
        }
    }

    private static class ShadowBorder implements javax.swing.border.Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Sombra
            g2.setColor(new Color(0, 0, 0, 20));
            for (int i = 0; i < 5; i++) {
                g2.drawRoundRect(x + i, y + i, width - 2*i - 1, height - 2*i - 1, 12, 12);
            }
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 5, 5);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
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