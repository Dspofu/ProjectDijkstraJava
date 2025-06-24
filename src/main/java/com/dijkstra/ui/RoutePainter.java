package com.dijkstra.ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Pinta uma rota no mapa.
 * Inclui uma linha vermelha, círculos nos pontos intermediários e círculos
 * destacados para o início e o fim da rota.
 */
public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;
    private final GeoPosition origin;
    private final GeoPosition destination;

    /**
     * @param track       a lista de posições geográficas que compõem a rota
     * @param origin      a posição de origem para destaque
     * @param destination a posição de destino para destaque
     */
    public RoutePainter(List<GeoPosition> track, GeoPosition origin, GeoPosition destination) {
        this.track = track == null ? List.of() : track;
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer mapViewer, int width, int height) {
        g = (Graphics2D) g.create();
        Rectangle rect = mapViewer.getViewportBounds();
        g.translate(-rect.x, -rect.y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(220, 38, 38));
        g.setStroke(new BasicStroke(3));

        drawRoute(g, mapViewer);
        drawPoints(g, mapViewer);

        g.dispose();
    }

    /**
     * Desenha as linhas conectando os pontos da rota.
     */
    private void drawRoute(Graphics2D g, JXMapViewer mapViewer) {
        // CORREÇÃO 1: Use Point2D em vez de Point
        Point2D prevPoint = null;
        for (GeoPosition geo : track) {
            Point2D currentPoint = mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom());
            if (prevPoint != null) {
                // CORREÇÃO 2: Converta as coordenadas para int na hora de desenhar
                g.drawLine((int) prevPoint.getX(), (int) prevPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY());
            }
            prevPoint = currentPoint;
        }
    }

    /**
     * Desenha um círculo em cada ponto (capital) da rota.
     */
    private void drawPoints(Graphics2D g, JXMapViewer mapViewer) {
        int pointSize = 10;
        int halfSize = pointSize / 2;

        for (GeoPosition geo : track) {
            // CORREÇÃO 3: Use Point2D em vez de Point
            Point2D p = mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom());

            if (geo.equals(origin)) {
                g.setColor(new Color(34, 197, 94));
            } else if (geo.equals(destination)) {
                g.setColor(new Color(37, 99, 235));
            } else {
                g.setColor(new Color(139, 92, 246));
            }

            // CORREÇÃO 4: Converta as coordenadas para int na hora de desenhar
            g.fillOval((int) p.getX() - halfSize, (int) p.getY() - halfSize, pointSize, pointSize);
        }
    }
}