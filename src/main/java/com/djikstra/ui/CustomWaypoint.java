package com.djikstra.ui;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Representa um marcador (ponto) no mapa.
 * Inclui um botão para interatividade.
 */
public class CustomWaypoint extends DefaultWaypoint {

    private final String label;
    private final JButton button;

    public CustomWaypoint(String label, GeoPosition coord) {
        super(coord);
        this.label = label;
        this.button = new JButton(label.substring(0, 1)); // Mostra a primeira letra da cidade
        this.button.setSize(24, 24);
        this.button.setPreferredSize(new Dimension(24, 24));
        this.button.setToolTipText(label); // Mostra o nome completo ao passar o mouse
        this.button.setFont(new Font("Arial", Font.BOLD, 9));
        this.button.setMargin(new Insets(1, 1, 1, 1));
        this.button.setFocusPainted(false);
    }

    public JButton getButton() {
        return button;
    }

    public String getLabel() {
        return label;
    }
}
