package com.dijkstra.ui;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;

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
    // Mostra a primeira letra da cidade
    this.button = new JButton(label.substring(0, 1));
    this.button.setSize(24, 24);
    this.button.setPreferredSize(new Dimension(24, 24));
    // Mostra o nome completo ao passar o mouse
    this.button.setToolTipText(label);

    // --- SUGESTÕES DE MELHORIA ---
    this.button.setFont(new Font("Arial", Font.BOLD, 10)); // Fonte um pouco maior e em negrito
    this.button.setBackground(new Color(37, 99, 235)); // Um azul moderno, igual ao do seu tema
    this.button.setForeground(Color.WHITE); // Texto branco
    this.button.setBorder(BorderFactory.createEmptyBorder()); // Remove a borda padrão
    this.button.setFocusPainted(false); // Remove a borda de foco
  }

  public JButton getButton() {
    return button;
  }

  public String getLabel() {
    return label;
  }

  /**
   * Permite alterar a cor de fundo do botão do waypoint.
   * Útil para destacar origem, destino, etc.
   * 
   * @param color A nova cor de fundo.
   */
  public void setButtonColor(Color color) {
    this.button.setBackground(color);
  }
}