import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private class Segment {
        int x;
        int y;

        Segment(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int panelWidth;
    int panelHeight;
    int segmentSize = 25;

    // snake
    Segment head;
    ArrayList<Segment> body;

    // food
    Segment foodItem;
    Random rng;

    // game logic
    int directionX;
    int directionY;
    Timer gameTimer;

    boolean isGameOver = false;
    JButton restartButton;

    // Highscore
    int highScore = 0;
    final String highScoreFile = "highscore.txt";

    GamePanel(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        setPreferredSize(new Dimension(this.panelWidth, this.panelHeight));
        setBackground(Color.WHITE);
        setLayout(null); // Allow manual positioning of components

        addKeyListener(this);
        setFocusable(true);

        head = new Segment(5, 5);
        body = new ArrayList<>();

        foodItem = new Segment(10, 10);
        rng = new Random();
        spawnFood();

        directionX = 1;
        directionY = 0;

        // Load high score
        loadHighScore();

        // game timer
        gameTimer = new Timer(85, this);
        gameTimer.start();

        // Restart button setup
        restartButton = new JButton("Restart");
        restartButton.setBounds(panelWidth / 2 - 50, panelHeight / 2 - 20, 100, 40);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    public void render(Graphics g) {
        // Food
        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(foodItem.x * segmentSize, foodItem.y * segmentSize, segmentSize, segmentSize, true);

        // Snake head
        g.setColor(Color.BLACK);
        g.fill3DRect(head.x * segmentSize, head.y * segmentSize, segmentSize, segmentSize, true);

        // Snake body
        for (Segment segment : body) {
            g.fill3DRect(segment.x * segmentSize, segment.y * segmentSize, segmentSize, segmentSize, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + body.size(), segmentSize - 16, segmentSize);
        g.drawString("High Score: " + highScore, segmentSize - 16, segmentSize + 20);

        if (isGameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over", panelWidth / 2 - 40, panelHeight / 2 - 60);
        }
    }

    public void spawnFood() {
        foodItem.x = rng.nextInt(panelWidth / segmentSize);
        foodItem.y = rng.nextInt(panelHeight / segmentSize);
    }

    public void update() {
        if (isGameOver) {
            return;
        }

        // Eat food
        if (checkCollision(head, foodItem)) {
            body.add(new Segment(foodItem.x, foodItem.y));
            spawnFood();
        }

        // Move snake body
        for (int i = body.size() - 1; i >= 0; i--) {
            Segment segment = body.get(i);
            if (i == 0) {
                segment.x = head.x;
                segment.y = head.y;
            } else {
                Segment previousSegment = body.get(i - 1);
                segment.x = previousSegment.x;
                segment.y = previousSegment.y;
            }
        }

        // Move snake head
        head.x += directionX;
        head.y += directionY;

        // Check game over conditions
        for (Segment segment : body) {
            if (checkCollision(head, segment)) {
                isGameOver = true;
                saveHighScore();
                showRestartButton();
            }
        }

        if (head.x * segmentSize < 0 || head.x * segmentSize >= panelWidth ||
            head.y * segmentSize < 0 || head.y * segmentSize >= panelHeight) {
            isGameOver = true;
            saveHighScore();
            showRestartButton();
        }
    }

    public boolean checkCollision(Segment seg1, Segment seg2) {
        return seg1.x == seg2.x && seg1.y == seg2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
        if (isGameOver) {
            gameTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isGameOver) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP && directionY != 1) {
            directionX = 0;
            directionY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && directionY != -1) {
            directionX = 0;
            directionY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && directionX != 1) {
            directionX = -1;
            directionY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && directionX != -1) {
            directionX = 1;
            directionY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private void showRestartButton() {
        restartButton.setVisible(true);
    }

    private void restartGame() {
        head = new Segment(5, 5);
        body.clear();
        spawnFood();
        directionX = 1;
        directionY = 0;
        isGameOver = false;
        restartButton.setVisible(false);
        gameTimer.start();
        repaint();
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException e) {
            System.err.println("High score file not found, starting fresh.");
        }
    }

    private void saveHighScore() {
        if (body.size() > highScore) {
            highScore = body.size();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile))) {
                writer.write(String.valueOf(highScore));
            } catch (IOException e) {
                System.err.println("Failed to save high score.");
            }
        }
    }
}