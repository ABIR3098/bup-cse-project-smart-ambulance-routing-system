package com.ambulance.routing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AmbulanceRoutingSystem extends JPanel {

    private final int[][] adjacencyMatrix = {
            { 0, 4, 0, 6, 2, 0 },
            { 4, 0, 5, 0, 0, 0 },
            { 0, 5, 0, 3, 0, 4 },
            { 6, 0, 3, 0, 4, 2 },
            { 2, 0, 0, 4, 0, 3 },
            { 0, 0, 4, 2, 3, 0 }
    };

    private final Node[] nodes = {
            new Node("A", "Hospital", new java.awt.geom.Point2D.Double(0.73, 0.16)),
            new Node("B", "House", new java.awt.geom.Point2D.Double(0.22, 0.17)),
            new Node("C", "Factory", new java.awt.geom.Point2D.Double(0.18, 0.84)),
            new Node("D", "Church", new java.awt.geom.Point2D.Double(0.52, 0.80)),
            new Node("E", "Office", new java.awt.geom.Point2D.Double(0.66, 0.45)),
            new Node("F", "Shop", new java.awt.geom.Point2D.Double(0.83, 0.80))
    };

    private final List<Point> screenPositions = new ArrayList<>();
    private Image backgroundImage;
    private Image ambulanceImage;

    private int startNode;
    private int destNode;
    private List<Integer> shortestPath;
    private int totalDistance;

    private double progress = 0.0;
    private int currentSegment = 0;
    private javax.swing.Timer animationTimer;

    public AmbulanceRoutingSystem(int start, int dest) {
        this.startNode = start;
        this.destNode = dest;
        loadImages();
        setPreferredSize(new Dimension(1445, 768));
        for (int i = 0; i < nodes.length; i++) screenPositions.add(new Point(0, 0));
        shortestPath = DijkstraAlgorithm.getShortestPath(adjacencyMatrix, startNode, destNode);
        totalDistance = DijkstraAlgorithm.calculateTotalDistance(adjacencyMatrix, shortestPath);
        startAnimation();
    }

    private void loadImages() {
        backgroundImage = new ImageIcon("city_map.png.jpg").getImage();
        ambulanceImage = new ImageIcon("ambulance.png").getImage();
    }

    private void startAnimation() {
        animationTimer = new javax.swing.Timer(30, e -> {
            if (shortestPath == null || shortestPath.size() < 2) return;
            progress += 0.02;
            if (progress >= 1.0) {
                progress = 0.0;
                currentSegment++;
                if (currentSegment >= shortestPath.size() - 1)
                    animationTimer.stop();
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        updateScreenPositions();
        drawEdges(g);
        drawShortestPath(g);
        drawNodes(g);
        drawAmbulance(g);
        drawInfo(g);
        g.dispose();
    }

    private void updateScreenPositions() {
        screenPositions.clear();
        int w = getWidth(), h = getHeight();
        for (Node node : nodes) {
            int sx = (int) Math.round(node.getNormalizedPosition().getX() * w);
            int sy = (int) Math.round(node.getNormalizedPosition().getY() * h);
            screenPositions.add(new Point(sx, sy));
        }
    }

    private void drawEdges(Graphics2D g) {
        g.setStroke(new BasicStroke(2f));
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < adjacencyMatrix.length; j++) {
                if (adjacencyMatrix[i][j] > 0) {
                    Point p1 = screenPositions.get(i);
                    Point p2 = screenPositions.get(j);
                    g.setColor(new Color(200, 200, 200, 200));
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);

                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.PLAIN, 12));
                    g.drawString(String.valueOf(adjacencyMatrix[i][j]), midX, midY);
                }
            }
        }
    }

    private void drawShortestPath(Graphics2D g) {
        if (shortestPath == null) return;
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(4f));
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Point p1 = screenPositions.get(shortestPath.get(i));
            Point p2 = screenPositions.get(shortestPath.get(i + 1));
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void drawNodes(Graphics2D g) {
        for (int i = 0; i < screenPositions.size(); i++) {
            Point p = screenPositions.get(i);
            if (i == startNode) g.setColor(Color.RED);
            else if (i == destNode) g.setColor(Color.GREEN);
            else g.setColor(Color.YELLOW);
            g.fillOval(p.x - 10, p.y - 10, 20, 20);

            g.setColor(Color.BLACK);
            g.drawString(nodes[i].getLabel(), p.x - 4, p.y + 4);
            g.drawString(nodes[i].getPlaceName(), p.x - 20, p.y + 25);
        }
    }

    private void drawAmbulance(Graphics2D g) {
        if (shortestPath == null || shortestPath.size() < 2) return;
        int from = shortestPath.get(currentSegment);
        int to = shortestPath.get(Math.min(currentSegment + 1, shortestPath.size() - 1));
        Point p1 = screenPositions.get(from);
        Point p2 = screenPositions.get(to);

        double x = p1.x + (p2.x - p1.x) * progress;
        double y = p1.y + (p2.y - p1.y) * progress;
        double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);

        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(angle);
        if (ambulanceImage != null) g.drawImage(ambulanceImage, -20, -10, 40, 20, this);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(-15, -8, 30, 16);
        }
        g.setTransform(old);
    }

    private void drawInfo(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillRect(10, 10, 340, 70);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Start: " + nodes[startNode].getLabel() + " (" + nodes[startNode].getPlaceName() + ")", 20, 35);
        g.drawString("Destination: " + nodes[destNode].getLabel() + " (" + nodes[destNode].getPlaceName() + ")", 20, 55);
        g.drawString("Path: " + pathToString(shortestPath), 20, 75);
    }

    private String pathToString(List<Integer> path) {
        if (path == null) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(nodes[path.get(i)].getLabel());
            if (i < path.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }
}
