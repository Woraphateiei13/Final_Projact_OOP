package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.JPanel;

import character.*;
import event.Event;
import ui.*;
import game.GameState;

public class Game extends JPanel implements KeyListener, MouseListener {

    private GameState currenState = GameState.MENU;

    // private Enemy_Bug enebug;
    // private Enemy_Firewall enefire;
    private List<Object> activeEnemies;
    // private Timer spawnTimer;
    private Random random;
    private Timer gameLoopTimer;

    private int spawnInterval = 2000;
    private long lastSpawnTime = 0;

    private Hp_bar hpbar;
    private Background background;
    private GameStats gameStats;

    int gameSpeed = 30;
    long lastPress = 0;

    Botnoi bot = new Botnoi(50, 450, 100, 100);
    Hp_bar hpBar = new Hp_bar(bot.getMaxHealth());
    Background bg = new Background("/img/Cyber_World_bg.png");
    // Enemy[] enemies;

    public Game() {
        this.setBounds(0, 0, 1000, 600);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);
        this.setLayout(null);
        this.setDoubleBuffered(true);

        // enemies = makeEnemySet(5);
        activeEnemies = new ArrayList<>();
        random = new Random();
        gameStats = new GameStats();

        // spawnTimer.start();
        gameLoopTimer = new Timer(16, e -> {
            if (currenState == GameState.PLAYING) {
                updateGame();
                repaint();
            }
        });
        gameLoopTimer.start();

        // enebug = new Enemy_Bug(800, 300, 50, 70, 10, this);
        // enefire = new Enemy_Firewall(900, 470, 50, 60, 10, this);
    }

    private void updateGame() {
        activeEnemies.forEach(en -> {
            if (en instanceof Enemy_Bug) {
                ((Enemy_Bug) en).updatePosition(this);
            } else if (en instanceof Enemy_Firewall) {
                ((Enemy_Firewall) en).updatePosition(this);
            }
        });

        if (System.currentTimeMillis() - lastSpawnTime > spawnInterval) {
            spawnEnemy();
            lastSpawnTime = System.currentTimeMillis();
        }
        activeEnemies.removeIf(en -> {
            int x = 0;
            if (en instanceof Enemy_Bug) {
                x = ((Enemy_Bug) en).x;
            } else if (en instanceof Enemy_Firewall) {
                x = ((Enemy_Firewall) en).x;
            }
            return x < -100;
        });
    }

    public void increaseDifficulty() {
        if (spawnInterval > 500) {
            spawnInterval -= 100;
        }

        // spawnTimer.setDelay(spawnInterval);
        System.out.println(spawnInterval);
    }

    private void spawnEnemy() {
        boolean spawnMons = random.nextBoolean();

        int yPos = spawnMons ? 370 : 470;
        int w = spawnMons ? 50 : 50;
        int h = spawnMons ? 70 : 60;
        int speed = 10;

        Object newEnemy;
        if (spawnMons) {
            newEnemy = new Enemy_Bug(1000, yPos, w, h, speed, this);
        } else {
            newEnemy = new Enemy_Firewall(1000, yPos, w, h, speed, this);
        }

        activeEnemies.add(newEnemy);
    }

    public GameState getCurrentState() {
        return currenState;
    }

    public void resetGame() {
        bot = new Botnoi(50, 450, 100, 100);
        hpBar = new Hp_bar(bot.getMaxHealth());
        gameStats = new GameStats();

        activeEnemies.forEach(enemy -> {
            if (enemy instanceof Enemy_Bug)
                ((Enemy_Bug) enemy).stopMoving();
            if (enemy instanceof Enemy_Firewall)
                ((Enemy_Firewall) enemy).stopMoving();
        });

        activeEnemies.clear();

        lastSpawnTime = System.currentTimeMillis();

        // spawnTimer.restart();
        // enebug.stopMoving();
        // enefire.stopMoving();
        // enebug = new Enemy_Bug(800, 370, 50, 70, 10, this);
        // enefire = new Enemy_Firewall(900, 470, 50, 60, 10, this);

        currenState = GameState.PLAYING;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        bg.draw(g, 1000, 600);

        if (currenState == GameState.PLAYING) {
            boolean tookDamage = false;

            for (int i = 0; i < activeEnemies.size(); i++) {
                Object enemy = activeEnemies.get(i);

                if (enemy instanceof Enemy_Bug) {
                    Enemy_Bug bug = (Enemy_Bug) enemy;
                    g2.drawImage(bug.getImage(), bug.x, bug.y, bug.width, bug.height, null);
                    if (Event.checkHit(bot, bug)) {
                        if (bot.receiveDamage(5)) {
                            tookDamage = true;
                        }
                    }
                } else if (enemy instanceof Enemy_Firewall) {
                    Enemy_Firewall fire = (Enemy_Firewall) enemy;
                    g2.drawImage(fire.getImage(), fire.x, fire.y, fire.width, fire.height, null);

                    if (Event.checkHit(bot, fire)) {
                        if (bot.receiveDamage(10)) {
                            tookDamage = true;
                        }
                    }
                }
            }

            gameStats.draw(g, 1000);
            g2.drawImage(bot.getImage(), bot.x, bot.y, bot.botSize, bot.botSize, null);
            hpBar.draw(g, bot.getHealth(), 40, 40);

            if (tookDamage) {
                g2.setStroke(new BasicStroke(10.0f));
                g2.setColor(Color.RED);
                g2.drawRect(0, 0, 1000, 900);
            }

            if (bot.health <= 0) {
                currenState = GameState.GAMEOVER;
                gameLoopTimer.stop();

                activeEnemies.forEach(e -> {
                    if (e instanceof Enemy_Bug)
                        ((Enemy_Bug) e).stopMoving();
                    if (e instanceof Enemy_Firewall)
                        ((Enemy_Firewall) e).stopMoving();
                });

                repaint();
                return;
            }

        } else if (currenState == GameState.MENU) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, 1000, 600);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("Wi-Fi Wanderrer", 280, 200);

            drawButton(g2, 400, 300, 200, 60, "START", Color.GREEN);

        } else if (currenState == GameState.GAMEOVER) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, 1000, 600);

            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 60));
            g2.drawString("GAME OVER", 330, 150);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.drawString("TIME: " + gameStats.getElapsedTimeMilliseSeconds() + "s", 380, 250);
            g2.drawString("SCORE: " + gameStats.getScore(), 380, 290);
            drawButton(g2, 400, 400, 200, 60, "RESTART", Color.YELLOW);
        }
        // g2.drawRect(bot.x, bot.y, bot.botSize, bot.botSize);
        // g2.drawImage(enefire.getImage(), enefire.x, enefire.y, enefire.width,
        // enefire.height, null);
        // g2.drawImage(enebug.getImage(), enebug.x, enebug.y, enebug.width,
        // enebug.height, null);
        // g2.setColor(Color.BLUE);
        // g2.drawString(bot.health + "%", 40, 40);
        /*
         *          * for (Enemy e : enemies) {
         *          * g2.drawRect(e.x, e.y, e.width, e.height);
         *          * if (Event.checkHit(bot, e)) {
         *          * g2.setStroke(new BasicStroke(10.0f));
         *          * g2.setColor(Color.RED);
         *          * g2.drawRect(0, 0, 1000, 900);
         *          
         */
        // if (Event.checkHit(bot, enebug) || Event.checkHit(bot, enefire)) {

    }
    // if (Event.checkHit(bot, enefire)) {
    // g2.setStroke(new BasicStroke(10.0f));
    // g2.setColor(Color.ORANGE);
    // g2.drawRect(0, 0, 1000, 900);
    // bot.health -= 2;
    // }
    /*
     *          * }
     *          * }
     *          
     */

    private void drawButton(Graphics2D g2, int x, int y, int w, int h, String text, Color color) {
        g2.setColor(color);
        g2.fillRect(x, y, w, h);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, w, h);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));

        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (w - fm.stringWidth(text)) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString(text, textX, textY);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (currenState == GameState.MENU) {
            if (mouseX >= 400 && mouseX <= 600 && mouseY >= 300 && mouseY <= 360) {
                resetGame();
            }
        } else if (currenState == GameState.GAMEOVER) {
            if (mouseX >= 400 && mouseX <= 600 && mouseY >= 400 && mouseY <= 460) {
                resetGame();
            }
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    private Enemy_Bug[] makeEnemySet(int enemyNumber) {
        Enemy_Bug[] enemySet = new Enemy_Bug[enemyNumber];
        for (int i = 0; i < enemyNumber; i++) {
            double enemyLocation = 1000 + Math.floor(Math.random() * 1000);
            enemySet[i] = new Enemy_Bug((int) enemyLocation, 300, 30, 40, 10, this);
        }
        return enemySet;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (currenState != GameState.PLAYING)
            return;

        if (System.currentTimeMillis() - lastPress > 600) {
            if (e.getKeyCode() == 38 || e.getKeyCode() == 32) {
                bot.jump(this);
                this.repaint();
            }
            lastPress = System.currentTimeMillis();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}