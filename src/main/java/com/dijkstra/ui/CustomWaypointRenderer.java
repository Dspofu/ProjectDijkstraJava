package com.dijkstra.ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;
import java.awt.*;

/**
 * Renderer customizado para desenhar nossos CustomWaypoint (que contêm um
 * botão)
 * no mapa, em vez do ícone padrão.
 */
public class CustomWaypointRenderer implements WaypointRenderer<CustomWaypoint> {

  /**
   * Pinta um único waypoint no mapa.
   * 
   * @param g   o contexto gráfico
   * @param map o JXMapViewer
   * @param wp  o waypoint a ser pintado
   */
  @Override
  public void paintWaypoint(Graphics2D g, JXMapViewer map, CustomWaypoint wp) {
    // Cria uma cópia do contexto gráfico para não afetar outras renderizações
    g = (Graphics2D) g.create();

    // Posiciona o botão no centro do ponto geográfico
    g.translate(-wp.getButton().getWidth() / 2, -wp.getButton().getHeight() / 2);

    // Desenha o botão
    wp.getButton().paint(g);

    g.dispose();
  }
}