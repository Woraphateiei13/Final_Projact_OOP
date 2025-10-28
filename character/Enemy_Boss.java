package character;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.Game;
import game.GameState;
import java.util.Random;

public class Enemy_Boss {
    public enum BossState {
        ENTERING, IDLE, ATTACKING_BUG, ATTACKING_FIREWALL, ATTACKING_MIXED, VULNERABLE, DEAD
    }

    public int x, y, width, height, speed = 3;
    private BossState curBossState = BossState.ENTERING;
    private long stateStartTime;
    private Random random = new Random();
    private boolean isMixedAttackBug = true;

    public Enemy_Boss(int x, int y, int w, int h, JPanel gamPanel) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.stateStartTime = System.currentTimeMillis();
    }

    private BossState selectNextAttack() {
        int chance = random.nextInt(3);
        if (chance == 0)
            return BossState.ATTACKING_BUG;
        if (chance == 1)
            return BossState.ATTACKING_FIREWALL;
        return BossState.ATTACKING_MIXED;
    }

    public void updatePosition(Game game) {
        long elapsed = System.currentTimeMillis() - stateStartTime;

        switch ((curBossState)) {
            case ENTERING:
                if (x > 700) {
                    x -= speed;
                } else {
                    changeState(selectNextAttack());
                }
                break;
            case IDLE:
                if (elapsed > 2000) {
                    changeState(selectNextAttack());
                }
                break;
            case ATTACKING_BUG:
                if (elapsed > 4000) {
                    changeState(BossState.VULNERABLE);
                }
                break;
            case ATTACKING_FIREWALL:
                if (elapsed > 4000) {
                    changeState(BossState.VULNERABLE);
                }
                break;
            case ATTACKING_MIXED:
                if (elapsed > 6000) {
                    changeState(BossState.VULNERABLE);
                }

                if (elapsed % 500 < 16) {
                    isMixedAttackBug = !isMixedAttackBug;
                }
                break;
            case VULNERABLE:
                if (elapsed > 1000) {
                    changeState(BossState.IDLE);
                }
            case DEAD:
                break;
            default:
                break;
        }
    }

    private void changeState(BossState newState) {
        this.curBossState = newState;
        this.stateStartTime = System.currentTimeMillis();
    }

    public boolean isMixedAttackBug() {
        return isMixedAttackBug;
    }

    public BossState getCurrentState() {
        return curBossState;
    }

    public boolean isDead() {
        return false;
    }

    public boolean isVulnerable() {
        return isVulnerable();
    }

    public boolean isScored() {
        return isDead();
    }

    public void stopMoving() {

    }

    public BufferedImage getImage() {
        try {
            return ImageIO.read(getClass().getResourceAsStream("/img/Boss_re.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Can't find - Enemy_Boss.java:120");
            return null;
        }
    }
}
