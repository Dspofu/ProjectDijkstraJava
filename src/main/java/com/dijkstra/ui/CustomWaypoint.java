package com.dijkstra.ui;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

/**
 * CustomWaypoint corrigido - agora funciona como um pino de mapa
 */
public class CustomWaypoint extends DefaultWaypoint {

    private final String label;
    private Color currentColor;
    
    // Cores padrão
    private static final Color DEFAULT_COLOR = new Color(59, 130, 246);

    public CustomWaypoint(String label, GeoPosition coord) {
        super(coord);
        this.label = label;
        this.currentColor = DEFAULT_COLOR;
    }

    public String getLabel() {
        return label;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setButtonColor(Color color) {
        this.currentColor = color;
    }
}