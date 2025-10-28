package character;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import game.Game;

public class Botnoi {
    public int x, y, botSize, health;
    private int jumpHigh = 100;
    public boolean isHit = false;

    public Botnoi(int x, int y, int botSize, int health) {
        this.x = x;
        this.y = y;
        this.botSize = botSize;
        this.health = health;
    }

    public boolean receiveDamage(int damage) {
        if (isHit) {
            return false;
        }

        this.health -= damage;
        this.isHit = true;

        Timer hitTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isHit = false;
                ((Timer) e.getSource()).stop();
            }
        });
        hitTimer.setRepeats(false);
        hitTimer.start();

        return true;
    }

    public void jump(JPanel game) {
        this.y -= jumpHigh;
        Timer timer = new Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                y += jumpHigh;
                game.repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public BufferedImage getImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("img\\Robot noi.png"));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return 100;
    }
}
