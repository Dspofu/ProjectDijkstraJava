package com.dijkstra.ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.Timer;

/**
 * RoutePainter corrigido com animação que funciona adequadamente
 */
public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;
    private final GeoPosition origin;
    private final GeoPosition destination;
    
    // Propriedades de animação
    private float animationProgress = 0.0f;
    private Timer animationTimer;
    private boolean isAnimating = false;
    private JXMapViewer mapViewer; // Referência para repaint
    
    // Cores modernas
    private static final Color ROUTE_COLOR = new Color(59, 130, 246);
    private static final Color ROUTE_SHADOW = new Color(59, 130, 246, 80);
    private static final Color ORIGIN_COLOR = new Color(34, 197, 94);
    private static final Color DESTINATION_COLOR = new Color(239, 68, 68);
    private static final Color INTERMEDIATE_COLOR = new Color(139, 92, 246);

    public RoutePainter(List<GeoPosition> track, GeoPosition origin, GeoPosition destination) {
        this.track = track != null ? track : List.of();
        this.origin = origin;
        this.destination = destination;
    }

    // CORREÇÃO: Método para iniciar animação com referência ao mapViewer
    public void startAnimation(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        isAnimating = true;
        animationProgress = 0.0f;
        
        animationTimer = new Timer(50, e -> { // 20 FPS para suavidade
            animationProgress += 0.05f; // Incremento maior para animação mais rápida
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                isAnimating = false;
                animationTimer.stop();
            }
            
            // CORREÇÃO: Força o repaint do mapa
            if (mapViewer != null) {
                mapViewer.repaint();
            }
        });
        animationTimer.start();
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer mapViewer, int width, int height) {
        if (track.isEmpty()) return;
        
        // Inicia animação se ainda não foi iniciada
        if (!isAnimating && animationProgress == 0.0f) {
            startAnimation(mapViewer);
        }
        
        g = (Graphics2D) g.create();
        Rectangle rect = mapViewer.getViewportBounds();
        g.translate(-rect.x, -rect.y);
        
        // Configurações de renderização de alta qualidade
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Desenha a rota com animação
        drawAnimatedRoute(g, mapViewer);
        
        // Desenha os pontos
        drawAnimatedPoints(g, mapViewer);
        
        g.dispose();
    }

    private void drawAnimatedRoute(Graphics2D g, JXMapViewer mapViewer) {
        if (track.size() < 2) return;

        // Converte todas as posições para pixels
        Point2D[] points = track.stream()
            .map(geo -> mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom()))
            .toArray(Point2D[]::new);

        // Desenha sombra da rota
        drawRouteShadow(g, points);
        
        // Desenha a rota principal
        drawMainRoute(g, points);
    }

    private void drawRouteShadow(Graphics2D g, Point2D[] points) {
        g.setColor(ROUTE_SHADOW);
        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int totalSegments = points.length - 1;
        int segmentsToShow = (int) (totalSegments * animationProgress);
        
        for (int i = 0; i < segmentsToShow; i++) {
            Point2D p1 = points[i];
            Point2D p2 = points[i + 1];
            g.drawLine((int) p1.getX() + 2, (int) p1.getY() + 2, 
                      (int) p2.getX() + 2, (int) p2.getY() + 2);
        }
        
        // Desenha segmento parcial se necessário
        if (segmentsToShow < totalSegments) {
            float segmentProgress = (totalSegments * animationProgress) - segmentsToShow;
            if (segmentProgress > 0) {
                Point2D p1 = points[segmentsToShow];
                Point2D p2 = points[segmentsToShow + 1];
                
                int x2 = (int) (p1.getX() + (p2.getX() - p1.getX()) * segmentProgress);
                int y2 = (int) (p1.getY() + (p2.getY() - p1.getY()) * segmentProgress);
                
                g.drawLine((int) p1.getX() + 2, (int) p1.getY() + 2, x2 + 2, y2 + 2);
            }
        }
    }

    private void drawMainRoute(Graphics2D g, Point2D[] points) {
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int totalSegments = points.length - 1;
        int segmentsToShow = (int) (totalSegments * animationProgress);
        
        for (int i = 0; i < segmentsToShow; i++) {
            Point2D p1 = points[i];
            Point2D p2 = points[i + 1];
            
            // Gradiente baseado na posição na rota
            float progress = (float) i / totalSegments;
            Color segmentColor = interpolateColor(ROUTE_COLOR, DESTINATION_COLOR, progress);
            g.setColor(segmentColor);
            
            g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        }
        
        // Desenha segmento parcial se necessário
        if (segmentsToShow < totalSegments) {
            float segmentProgress = (totalSegments * animationProgress) - segmentsToShow;
            if (segmentProgress > 0) {
                Point2D p1 = points[segmentsToShow];
                Point2D p2 = points[segmentsToShow + 1];
                
                float progress = (float) segmentsToShow / totalSegments;
                Color segmentColor = interpolateColor(ROUTE_COLOR, DESTINATION_COLOR, progress);
                g.setColor(segmentColor);
                
                int x2 = (int) (p1.getX() + (p2.getX() - p1.getX()) * segmentProgress);
                int y2 = (int) (p1.getY() + (p2.getY() - p1.getY()) * segmentProgress);
                
                g.drawLine((int) p1.getX(), (int) p1.getY(), x2, y2);
            }
        }
    }

    private void drawAnimatedPoints(Graphics2D g, JXMapViewer mapViewer) {
        for (int i = 0; i < track.size(); i++) {
            GeoPosition geo = track.get(i);
            Point2D p = mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom());
            
            // Animação de aparição dos pontos
            float pointProgress = Math.max(0, Math.min(1, (animationProgress * track.size() - i) / 1.0f));
            if (pointProgress <= 0) continue;
            
            // Determina a cor do ponto
            Color pointColor;
            int pointSize;
            
            if (geo.equals(origin)) {
                pointColor = ORIGIN_COLOR;
                pointSize = 14;
            } else if (geo.equals(destination)) {
                pointColor = DESTINATION_COLOR;
                pointSize = 14;
            } else {
                pointColor = INTERMEDIATE_COLOR;
                pointSize = 10;
            }
            
            drawPoint(g, p, pointColor, pointSize, pointProgress);
        }
    }

    private void drawPoint(Graphics2D g, Point2D p, Color color, int size, float progress) {
        int x = (int) p.getX();
        int y = (int) p.getY();
        int animatedSize = (int) (size * progress);
        
        // Sombra
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x - animatedSize/2 + 1, y - animatedSize/2 + 1, animatedSize, animatedSize);
        
        // Círculo principal
        g.setColor(color);
        g.fillOval(x - animatedSize/2, y - animatedSize/2, animatedSize, animatedSize);
        
        // Borda
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - animatedSize/2, y - animatedSize/2, animatedSize, animatedSize);
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        
        return new Color(r, g, b);
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isAnimating = false;
    }
}