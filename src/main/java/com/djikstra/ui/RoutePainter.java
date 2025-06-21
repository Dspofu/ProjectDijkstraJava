package com.djikstra.ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Um "Painter" customizado para desenhar a linha da rota no mapa.
 */
public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;

    public RoutePainter(List<GeoPosition> track) {
        // É importante criar uma cópia da lista
        this.track = new ArrayList<>(track);
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        // Cria uma cópia do Graphics para não modificar o original
        g = (Graphics2D) g.create();
        
        // Converte o Graphics para o sistema de coordenadas do mapa
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        // Configurações de renderização para a linha ficar mais suave
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 0, 0, 150)); // Vermelho semi-transparente
        g.setStroke(new BasicStroke(4)); // Espessura da linha

        // Desenha as linhas conectando os pontos da rota
        Point2D ptLast = null;
        for (GeoPosition gp : track) {
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
            if (ptLast != null) {
                g.drawLine((int) ptLast.getX(), (int) ptLast.getY(), (int) pt.getX(), (int) pt.getY());
            }
            ptLast = pt;
        }

        g.dispose();
    }
}
