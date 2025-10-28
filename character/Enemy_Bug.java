package character;

import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
//import javax.swing.Timer;

import game.Game;
import game.GameState;

public class Enemy_Bug {
    public int x, y, width, height, speed;
    private int xEnemy;
    // private Timer moveTimer;

    public Enemy_Bug(int x, int y, int w, int h, int speed, JPanel game) {
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
        if (x < 0) {
            if (game.getCurrentState() == GameState.PLAYING) {
                game.getGameStats().increaseScore(50);

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
     * x -= speed;
     * game.repaint();
     * if (x < 0) {
     * if (game instanceof Game) {
     * ((Game) game).getGameStats().increaseScore(50);
     * 
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

    public BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/img/Bug.png"));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
