/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CustomElements;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import java.awt.*;

public class CircularProgressBar extends JComponent {
    private int progress = 0;
    private String label = "";
    private Color progressColor = new Color(212, 175, 55); // default gold color

    public CircularProgressBar() {
        setPreferredSize(new Dimension(150, 150));
        setMinimumSize(new Dimension(50, 50));
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        int old = this.progress;
        this.progress = Math.min(100, Math.max(0, progress));
        if (old != this.progress) {
            repaint();
            firePropertyChange("progress", old, this.progress);
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        String old = this.label;
        this.label = label;
        if (!label.equals(old)) {
            repaint();
            firePropertyChange("label", old, label);
        }
    }

    public Color getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(Color progressColor) {
        Color old = this.progressColor;
        this.progressColor = progressColor != null ? progressColor : new Color(212, 175, 55);
        if (!this.progressColor.equals(old)) {
            repaint();
            firePropertyChange("progressColor", old, this.progressColor);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int strokeWidth = size / 10;
        int radius = size - strokeWidth;

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawOval(strokeWidth / 2, strokeWidth / 2, radius, radius);

        g2.setColor(progressColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        int angle = (int) (360 * progress / 100.0);
        g2.drawArc(strokeWidth / 2, strokeWidth / 2, radius, radius, 90, -angle);

        if (label != null && !label.isEmpty()) {
            Font font = getFont().deriveFont(Font.BOLD, size / 5f);
            FontMetrics fm = g2.getFontMetrics(font);
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();

            g2.setFont(font);
            g2.setColor(Color.WHITE);
            g2.drawString(label, (size - textWidth) / 2, (size + textHeight) / 2 - 5);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 150);
    }
}
