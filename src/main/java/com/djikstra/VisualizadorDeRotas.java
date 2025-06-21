package com.djikstra;

import com.djikstra.Navegador;
import com.djikstra.Map.Cidade;
import com.djikstra.Map.Grafo;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;
import com.djikstra.ui.CustomWaypoint;
import com.djikstra.ui.RoutePainter;
import com.djikstra.ui.CustomWaypointRenderer;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class VisualizadorDeRotas extends JFrame {

    private final Grafo grafo = Grafo.criarGrafoBrasil();
    private final JXMapViewer mapViewer = new JXMapViewer();
    private final CompoundPainter<JXMapViewer> painter = new CompoundPainter<>();
    private RoutePainter rotaAtualPainter; // Guarda a rota desenhada para poder removê-la

    public VisualizadorDeRotas() {
        super("Visualizador de Rotas - Brasil");

        // --- 1. Configuração do Mapa ---
        configurarMapa();

        // --- 2. Criação dos Controles (Painel Superior) ---
        JPanel painelControles = criarPainelControles();

        // --- 3. Layout da Janela ---
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(painelControles, BorderLayout.NORTH);
        getContentPane().add(mapViewer, BorderLayout.CENTER);

        // --- 4. Configurações da Janela ---
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
    }

    private void configurarMapa() {
        // Configura o provedor de mapas (OpenStreetMap)
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Adiciona o painter composto ao mapa (ele vai gerenciar todos os outros painters)
        mapViewer.setOverlayPainter(painter);

        // Adiciona os marcadores (waypoints) para todas as cidades
        adicionarWaypointsCidades();
        
        // --- NOVO: Adiciona interações de mouse (arrastar para mover, roda do mouse para zoom) ---
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        // --- NOVO: Faz o mapa dar um zoom inicial que enquadre todas as cidades ---
        Set<GeoPosition> todasAsCidadesPos = grafo.getCidades().stream()
                .map(c -> new GeoPosition(c.getLatitude(), c.getLongitude()))
                .collect(Collectors.toSet());
        // O valor 0.7 significa que os pontos ocuparão 70% da tela, deixando uma margem de 30%
        mapViewer.zoomToBestFit(todasAsCidadesPos, 0.7);

        // Centraliza o mapa no Brasil
        mapViewer.setAddressLocation(new GeoPosition(-14.2350, -51.9253)); // Centro aproximado do Brasil
        mapViewer.setZoom(14); // Ajuste o nível de zoom conforme necessário
    }

    private void adicionarWaypointsCidades() {
        Set<CustomWaypoint> waypoints = new HashSet<>();
        for (Cidade cidade : grafo.getCidades()) {
            waypoints.add(new CustomWaypoint(cidade.getNome(), new GeoPosition(cidade.getLatitude(), cidade.getLongitude())));
        }

        // Cria um painter para os waypoints
        WaypointPainter<CustomWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        // Usa um renderer customizado para mostrar botões ao invés de imagens
        waypointPainter.setRenderer(new CustomWaypointRenderer());

        // Adiciona o painter de waypoints ao painter principal
        painter.addPainter(waypointPainter);
    }

    private JPanel criarPainelControles() {
        // ComboBox para selecionar a cidade de origem
        JComboBox<Cidade> cbOrigem = new JComboBox<>(grafo.getCidades().toArray(new Cidade[0]));
        cbOrigem.setSelectedItem(grafo.getCidade("São Paulo"));

        // ComboBox para selecionar a cidade de destino
        JComboBox<Cidade> cbDestino = new JComboBox<>(grafo.getCidades().toArray(new Cidade[0]));
        cbDestino.setSelectedItem(grafo.getCidade("Brasília"));
        
        // Botão para calcular a rota
        JButton btnCalcular = new JButton("Traçar Rota");

        // Painel para organizar os controles
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painel.add(new JLabel("Origem:"));
        painel.add(cbOrigem);
        painel.add(new JLabel("Destino:"));
        painel.add(cbDestino);
        painel.add(btnCalcular);

        // Ação do botão
        btnCalcular.addActionListener(e -> {
            Cidade origem = (Cidade) cbOrigem.getSelectedItem();
            Cidade destino = (Cidade) cbDestino.getSelectedItem();

            if (origem == null || destino == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione a origem e o destino.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (origem.equals(destino)) {
                JOptionPane.showMessageDialog(this, "A cidade de origem e destino devem ser diferentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Calcula a rota e desenha no mapa
            calcularEdesenharRota(origem, destino);
        });

        return painel;
    }

    private void calcularEdesenharRota(Cidade origem, Cidade destino) {
        Navegador.Resultado resultado = Navegador.calcularRota(grafo, origem, destino);

        // Remove a rota anterior do mapa, se existir
        if (rotaAtualPainter != null) {
            painter.removePainter(rotaAtualPainter);
        }

        if (!resultado.temCaminho()) {
            JOptionPane.showMessageDialog(this, "Não foi possível encontrar uma rota entre " + origem.getNome() + " e " + destino.getNome() + ".", "Rota não encontrada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Cria a lista de coordenadas geográficas para o painter
            List<GeoPosition> track = resultado.caminho.stream()
                    .map(c -> new GeoPosition(c.getLatitude(), c.getLongitude()))
                    .collect(Collectors.toList());

            // Cria o novo painter da rota e o adiciona ao mapa
            rotaAtualPainter = new RoutePainter(track);
            painter.addPainter(rotaAtualPainter);

            // Ajusta o zoom do mapa para mostrar a rota completa
            mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);

            // Exibe o resultado em uma janela de diálogo
            exibirResultadoDialog(resultado);
        }
        
        // Solicita que o mapa seja redesenhado para mostrar as atualizações
        mapViewer.repaint();
    }

    private void exibirResultadoDialog(Navegador.Resultado resultado) {
        String caminhoStr = resultado.caminho.stream()
                .map(Cidade::getNome)
                .collect(Collectors.joining(" -> "));

        NumberFormat formatador = NumberFormat.getInstance(new Locale("pt", "BR"));

        String mensagem = "<html>"
                + "<b>Rota mais curta encontrada:</b><br><br>"
                + caminhoStr + "<br><br>"
                + "<b>Distância Total:</b> " + formatador.format(resultado.distanciaTotal) + " km"
                + "</html>";

        JOptionPane.showMessageDialog(this, mensagem, "Resultado da Rota", JOptionPane.INFORMATION_MESSAGE);
    }


    public static void main(String[] args) {
        // Executa a criação da GUI na Event Dispatch Thread do Swing
        SwingUtilities.invokeLater(() -> {
            VisualizadorDeRotas visualizador = new VisualizadorDeRotas();
            visualizador.setVisible(true);
        });
    }
}
