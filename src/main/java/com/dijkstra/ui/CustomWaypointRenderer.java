package com.dijkstra.ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Renderer que desenha pinos de mapa em vez de botões
 */
public class CustomWaypointRenderer implements WaypointRenderer<CustomWaypoint> {

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer map, CustomWaypoint wp) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Configurações de renderização de alta qualidade
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Converte a posição geográfica para coordenadas de pixel
        Point2D point = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
        
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // Desenha o pino do mapa
        drawMapPin(g2, x, y, wp.getCurrentColor(), wp.getLabel());
        
        g2.dispose();
    }
    
    private void drawMapPin(Graphics2D g2, int x, int y, Color color, String label) {
        // Dimensões do pino
        int pinWidth = 20;
        int pinHeight = 30;
        int circleRadius = 10;
        
        // Posição do pino (centralizado horizontalmente, ponta na posição)
        int pinX = x - pinWidth / 2;
        int pinY = y - pinHeight;
        
        // Desenha sombra do pino
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(pinX + 2, pinY + circleRadius + 2, pinWidth, circleRadius);
        
        // Desenha a parte inferior do pino (triângulo)
        int[] triangleX = {x, x - 6, x + 6};
        int[] triangleY = {y, pinY + circleRadius + 8, pinY + circleRadius + 8};
        g2.setColor(darkenColor(color, 0.3f));
        g2.fillPolygon(triangleX, triangleY, 3);
        
        // Desenha o círculo principal do pino
        g2.setColor(color);
        g2.fillOval(pinX, pinY, pinWidth, pinWidth);
        
        // Desenha borda do círculo
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(pinX, pinY, pinWidth, pinWidth);
        
        // Desenha a primeira letra da cidade no centro
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        String letter = label.substring(0, 1).toUpperCase();
        int letterX = x - fm.stringWidth(letter) / 2;
        int letterY = pinY + circleRadius + fm.getAscent() / 2 - 2;
        g2.drawString(letter, letterX, letterY);
        
        // Efeito de destaque para waypoints especiais
        if (isSpecialColor(color)) {
            drawPulsingRing(g2, x, pinY + circleRadius, color);
        }
    }
    
    private boolean isSpecialColor(Color color) {
        // Verde (origem) ou Vermelho (destino)
        return (color.getGreen() > 150 && color.getRed() < 100) || // Verde
               (color.getRed() > 200 && color.getGreen() < 100);    // Vermelho
    }
    
    private void drawPulsingRing(Graphics2D g2, int centerX, int centerY, Color baseColor) {
        // Calcula o tamanho do pulso baseado no tempo
        long time = System.currentTimeMillis();
        float pulse = (float) (Math.sin(time * 0.005) * 0.5 + 0.5); // Valor entre 0 e 1
        
        // Tamanho do anel baseado no pulso
        int ringSize = (int) (25 + pulse * 10);
        
        // Cor do anel com transparência baseada no pulso
        int alpha = (int) (80 + pulse * 80); // Entre 80 e 160
        Color ringColor = new Color(
            baseColor.getRed(),
            baseColor.getGreen(),
            baseColor.getBlue(),
            Math.min(255, alpha)
        );
        
        // Desenha o anel
        g2.setColor(ringColor);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(centerX - ringSize/2, centerY - ringSize/2, ringSize, ringSize);
    }
    
    private Color darkenColor(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b, color.getAlpha());
    }
}