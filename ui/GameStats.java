package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;

public class GameStats {
    private long startTime;
    private int score;
    private Image scoreIcon;

    private final Font STATS_FONT = new Font("Arial", Font.BOLD, 24);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.000");

    private final int ICON_SIZE = 40;

    public GameStats() {
        this.startTime = System.currentTimeMillis();
        this.score = 0;

        try {
            scoreIcon = ImageIO.read(getClass().getResourceAsStream("/img/Wi-Fi.png"));
        } catch (Exception e) {
            System.out.println("Can't find  GameStats.java:29 - Gamestats.java:29");
            scoreIcon = null;
        }
    }

    public int getScore() {
        return this.score;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }

    public double getElapsedTimeMilliseSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

    public void draw(Graphics g, int width) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(STATS_FONT);
        g2.setColor(Color.WHITE);

        String formattedTime = DECIMAL_FORMAT.format(getElapsedTimeMilliseSeconds());

        String scoreText = "SCORE: " + score;
        String timeText = "TIME: " + formattedTime + "s";

        int xOffset = 20;
        int yOffset = 35;

        int timeWidth = g2.getFontMetrics().stringWidth(timeText);
        int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);

        g2.drawString(timeText, width - timeWidth - xOffset, yOffset);

        if (scoreIcon != null) {
            g2.drawImage(scoreIcon, width - timeWidth - xOffset - (ICON_SIZE + 5), yOffset - ICON_SIZE + 5, ICON_SIZE,
                    ICON_SIZE, null);
        }

        g2.drawString(scoreText, width - scoreWidth - xOffset, yOffset + 30);
    }
}
