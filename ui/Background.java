package ui;

import java.awt.*;
import javax.imageio.ImageIO;

public class Background {
    private Image bgImage;

    public Background(String path) {
        try {
            // ใช้พารามิเตอร์ path ในการโหลดรูป
            bgImage = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("❌ ไม่พบภาพพื้นหลัง: - Background.java:14" + path);
        }
    }

    public void draw(Graphics g, int width, int height) {
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(200, 230, 255));
            g.fillRect(0, 0, width, height);
        }
    }
}