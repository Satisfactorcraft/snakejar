package de.satis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.security.NoSuchAlgorithmException;

public class Main extends JFrame {
    public static GamePanel gamePanel;

    public Main() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);
        pack();

        setLocationRelativeTo(null); // Fenster zentrieren
        setVisible(true);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new Main(); // Das Spiel wird gestartet
    }
}

class GamePanel extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    private final int TOTAL_TILES = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);

    private int[] x = new int[TOTAL_TILES];
    private int[] y = new int[TOTAL_TILES];
    public int bodyParts = 1;
    private int foodX;
    private int foodY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A: // A für Links
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_D: // D für Rechts
                        if (direction != 'L') direction = 'R';
                        break;
                    case KeyEvent.VK_W: // W für Hoch
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_S: // S für Runter
                        if (direction != 'U') direction = 'D';
                        break;
                }
            }
        });

        random = new Random();
        startGame();
    }

    public void startGame() {
        spawnFood();
        running = true;
        timer = new Timer(100, this);
        timer.start();
    }

    public void spawnFood() {
        foodX = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] -= TILE_SIZE;
                break;
            case 'D':
                y[0] += TILE_SIZE;
                break;
            case 'L':
                x[0] -= TILE_SIZE;
                break;
            case 'R':
                x[0] += TILE_SIZE;
                break;
        }
    }

    public void checkCollision() {
        // Überprüfung, ob der Kopf mit dem Körper kollidiert
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        // Überprüfung, ob der Kopf die Wände berührt
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++;
            spawnFood();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            // Nahrung zeichnen
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, TILE_SIZE, TILE_SIZE);

            // Schlange zeichnen
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }
        } else {
            showGameOver(g);
        }
    }

    public void showGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = "Game Over";
        g.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2);
    }
}
