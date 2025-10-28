package character;

import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
//import javax.swing.Timer;

import game.*;

public class Enemy_Firewall {
    public int x, y, width, height, speed;
    private int xEnemy;
    private boolean isScored = false;
    // private Timer moveTimer;

    public Enemy_Firewall(int x, int y, int w, int h, int speed, JPanel game) {
        this.x = x;
        this.xEnemy = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.speed = speed;
        // move(game);
    }

    public void updatePosition(Game game) {
        x -= speed;
        if (x < 0 && !isScored) {
            if (game.getCurrentState() == GameState.PLAYING) {
                game.getGameStats().increaseScore(100);
                this.isScored = true;

                if (game.getGameStats().getScore() % 500 == 0) {
                    game.increaseDifficulty();
                }
            }
        }
    }

    public void stopMoving() {
    }

    /*
     * public void move(JPanel game) {
     * moveTimer = new Timer(10, new ActionListener() {
     * 
     * @Override
     * public void actionPerformed(ActionEvent e) {
     * if (game instanceof Game && ((Game) game).getCurrentState() !=
     * GameState.PLAYING) {
     * return;
     * }
     * 
     * x -= speed;
     * game.repaint();
     * if (x < 0) {
     * if (game instanceof Game) {
     * ((Game) game).getGameStats().increaseScore(100);
     * if (((Game) game).getGameStats().getScore() % 500 == 0) {
     * ((Game) game).increaseDifficulty();
     * }
     * }
     * moveTimer.stop();
     * x = xEnemy;
     * }
     * }
     * });
     * moveTimer.start();
     * }
     */

    public boolean isScored() {
        return isScored;
    }

    public BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/img/firewall.png"));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
