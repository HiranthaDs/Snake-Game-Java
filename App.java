import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int windowSize = 600;
        int gamePanelSize = windowSize;

        JFrame gameWindow = new JFrame("Snake Game");
        gameWindow.setVisible(true);
        gameWindow.setSize(windowSize, gamePanelSize);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setResizable(false);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = new GamePanel(windowSize, gamePanelSize);
        gameWindow.add(gamePanel);
        gameWindow.pack();
        gamePanel.requestFocus();
    }
}
