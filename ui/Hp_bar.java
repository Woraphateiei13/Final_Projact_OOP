package ui;

import java.awt.*;

public class Hp_bar {
    private int maxHp;

    public Hp_bar(int maxHp) {
        this.maxHp = maxHp;
    }

    public void draw(Graphics g, int currentHp, int x, int y) {
        int hearts = maxHp / 10;
        int filledHearts = Math.max(0, currentHp / 10);

        for (int i = 0; i < hearts; i++) {
            if (i < filledHearts) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillRect(x + i * 20, y, 15, 15);
            g.setColor(Color.BLACK);
            g.drawRect(x + i * 20, y, 15, 15);
        }
    }
}
