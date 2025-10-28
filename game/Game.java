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
    private Enemy_Boss boss = null;
    // private Timer spawnTimer;
    private Random random;
    private Timer gameLoopTimer;
    private Timer difficultyTimer;

    private int difficultyLevel = 0;
    private int spawnInterval = 1200;
    private long lastSpawnTime = 0;

    private Hp_bar hpbar;
    private Background bg;
    private GameStats gameStats;
    private long lastBossAttackTime = 0;

    int gameSpeed = 30;
    long lastPress = 0;

    Botnoi bot = new Botnoi(50, 450, 100, 100);
    Hp_bar hpBar = new Hp_bar(bot.getMaxHealth());
    // Background bg = new Background("/img/Cyber_World_bg.png");
    private final String[] BG_PATHS = {
            "/img/Cyber_World_bg.png",
            "/img/Cyber_World_bg_Level2.png",
            "/img/Cyber_World_bg_Level3.png"
    };
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
        bg = new Background(BG_PATHS[0]);
        difficultyTimer = new Timer(20000, e -> {
            if (currenState == GameState.PLAYING) {
                increaseDifficulty();
            }
        });
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
        if (boss != null && !boss.isDead()) {
            boss.updatePosition(this);
            if (boss.getCurrentState() == Enemy_Boss.BossState.ATTACKING_BUG) {
                if (System.currentTimeMillis() - lastBossAttackTime > 500) {
                    activeEnemies.add(new Enemy_Bug(1000, 370, 50, 70, 20, this));
                    lastBossAttackTime = System.currentTimeMillis();
                }
            } else if (boss.getCurrentState() == Enemy_Boss.BossState.ATTACKING_FIREWALL) {
                if (System.currentTimeMillis() - lastBossAttackTime > 500) {
                    activeEnemies.add(new Enemy_Firewall(1000, 470, 50, 60, 15, this));
                    lastBossAttackTime = System.currentTimeMillis();
                }
            } else if (boss.getCurrentState() == Enemy_Boss.BossState.ATTACKING_MIXED) {
                if (System.currentTimeMillis() - lastBossAttackTime > 400) {
                    if (boss.isMixedAttackBug()) {
                        activeEnemies.add(new Enemy_Bug(1000, 370, 50, 70, 20, this));
                    } else {
                        activeEnemies.add(new Enemy_Firewall(1000, 470, 50, 60, 15, null));
                    }
                    lastBossAttackTime = System.currentTimeMillis();
                }
            }
        }
        activeEnemies.forEach(en -> {
            if (en instanceof Enemy_Bug) {
                ((Enemy_Bug) en).updatePosition(this);
            } else if (en instanceof Enemy_Firewall) {
                ((Enemy_Firewall) en).updatePosition(this);
            } else if (en instanceof Item_hp) {
                Item_hp item = (Item_hp) en;
                item.updatePosition(this);

                if (Event.checkHit(bot, item)) {
                    item.collect();
                    bot.health = Math.min(bot.getMaxHealth(), bot.health + 20);
                }
            }
        });
        if (boss == null || boss.isDead()) {
            if (System.currentTimeMillis() - lastSpawnTime > spawnInterval) {
                spawnEnemy();
                lastSpawnTime = System.currentTimeMillis();
            }
        } else {
            lastSpawnTime = System.currentTimeMillis();
        }

        activeEnemies.removeIf(en -> {
            int x = 0;
            boolean shouldRemove = false;
            if (en instanceof Enemy_Boss) {
                shouldRemove = ((Enemy_Boss) en).isDead();
            } else if (en instanceof Enemy_Bug) {
                Enemy_Bug bug = (Enemy_Bug) en;
                x = bug.x;
                shouldRemove = bug.isScored() || (x < -200);
            } else if (en instanceof Enemy_Firewall) {
                Enemy_Firewall fire = (Enemy_Firewall) en;
                x = fire.x;
                shouldRemove = fire.isScored() || (x < -200);
            } else if (en instanceof Item_hp) {
                Item_hp item = (Item_hp) en;
                x = item.x;
                shouldRemove = item.isCollected() || (x < -100);
            }

            return shouldRemove;
        });

    }

    /*
     * if (boss != null && boss.isDead()) {
     * currenState = GameState.GAMEOVER;
     * gameLoopTimer.stop();
     * difficultyTimer.stop();
     * }
     */

    public void increaseDifficulty() {
        if (spawnInterval > 500) {
            spawnInterval -= 200;
        }

        difficultyLevel++;

        if (difficultyLevel < BG_PATHS.length) {
            String newBgPath = BG_PATHS[difficultyLevel];
            bg = new Background(newBgPath);
        }

        // System.out.println(spawnInterval);
    }

    private void spawnEnemy() {
        if (difficultyLevel >= 2 && (boss == null || boss.isDead())) {
            if (random.nextInt(100) < 1) {
                boss = new Enemy_Boss(1200, 250, 300, 300, this);
                return;
            }
        }
        if (random.nextInt(100) < 10) {
            int yPos = 400;
            int w = 40;
            int h = 40;

            activeEnemies.add(new Item_hp(1000, yPos, w, h, this));
            return;
        }
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
        difficultyLevel = 0;
        spawnInterval = 1200;

        this.boss = null;

        bg = new Background(BG_PATHS[0]);

        activeEnemies.forEach(enemy -> {
            if (enemy instanceof Enemy_Bug)
                ((Enemy_Bug) enemy).stopMoving();
            if (enemy instanceof Enemy_Firewall)
                ((Enemy_Firewall) enemy).stopMoving();
        });

        activeEnemies.clear();

        difficultyTimer.start();
        gameLoopTimer.start();

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
        if (bg != null) {
            bg.draw(g, 1000, 600);
        }
        if (currenState == GameState.PLAYING) {
            boolean tookDamage = false;

            if (boss != null && !boss.isDead()) {
                g2.drawImage(boss.getImage(), boss.x, boss.y, boss.width, boss.height, null);

                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(700, 20, 200, 15);

                /*
                 * if (Event.checkHit(bot, boss)) {
                 * if (boss.isVulnerable()) {
                 * boss.receiveDamage(10);
                 * } else {
                 * if (bot.receiveDamage(5)) {
                 * 
                 * }
                 * }
                 * }
                 */
            }

            for (int i = 0; i < activeEnemies.size(); i++) {
                Object enemy = activeEnemies.get(i);

                if (enemy instanceof Item_hp) {
                    Item_hp item = (Item_hp) enemy;
                    g2.drawImage(item.getImage(), item.x, item.y, item.width, item.height, null);
                }

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
                difficultyTimer.stop();
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

    /*
     * private Enemy_Bug[] makeEnemySet(int enemyNumber) {
     * Enemy_Bug[] enemySet = new Enemy_Bug[enemyNumber];
     * for (int i = 0; i < enemyNumber; i++) {
     * double enemyLocation = 1000 + Math.floor(Math.random() * 1000);
     * enemySet[i] = new Enemy_Bug((int) enemyLocation, 300, 30, 40, 10, this);
     * }
     * return enemySet;
     * }
     */

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