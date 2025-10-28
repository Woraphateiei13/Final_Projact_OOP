package character;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.Game;
import event.Event;

public class Item_hp {
    public int x, y, width, height, speed = 10;
    private boolean isCollected = false;
    private BufferedImage itemImage;

    public Item_hp(int x, int y, int w, int h, JPanel game) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        try {
            itemImage = ImageIO.read(getClass().getResourceAsStream("/img/health_hp.png"));
        } catch (Exception e) {
            System.out.println("❌ ไม่สามารถโหลดภาพไอเทม HP: /img/health_hp.png - Item_hp.java:24");
            // e.printStackTrace(); // Uncomment สำหรับ Debug
        }
    }

    public void updatePosition(Game game) {
        x -= speed;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        this.isCollected = true;
    }

    public void stopMoving() {

    }

    public BufferedImage getImage() {
        return itemImage;
        /*
         * try {
         * return ImageIO.read(getClass().getResourceAsStream("/img/Health.png"));
         * } catch (Exception e) {
         * System.out.println("Can't find - Item_hp.java:49");
         * return null;
         * }
         */
    }
}
