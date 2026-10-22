//Batuhan Özgür
//2024400150
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
public class Game {
    private int width;
    private int height;
    private Map map;
    private ArrayList<Level> levels;
    private int pauseDuration = 25;//control game speed
    private boolean cameraFollow = false;
    private boolean cWasPressed = false;
    public Game(ArrayList<Level> levels, Map map) {
        this.width = 800;
        this.height = 800;
        this.map = map;
        this.levels = levels;
    }
    // This is the main engine of the game
    public void run() {
        Mario mario = new Mario(60, 850);
        mario.y = 140;
        startScreen(mario);
        mario.respawn();
        int levelIdx = 0;
        int frames = 0;
        // Loop through all 4 levels
        while (levelIdx < levels.size()) {
            Level currentLevel = levels.get(levelIdx);
            map.setShowCoins(currentLevel.getId() == 3);
            // The main loop for a single leve
            while (true) {
                StdDraw.clear(new Color(146, 144, 255));
                frames++;
                int oldX = mario.x;
                // Handling Mario's movement
                if (!mario.isDead && !mario.isSpawning) {
                    mario.handleInput(0, true);
                    int halfSize = mario.getSize() / 2;
                    // Screen boundaries (don't let him walk off the screen)
                    if (mario.x - halfSize < 0) mario.x = halfSize;
                    if (mario.x + halfSize > width) mario.x = width - halfSize;
                    // Collision check for walls and pipes
                    boolean sPressed = StdDraw.isKeyPressed(KeyEvent.VK_S) || mario.isSinking;
                    if (map.isHittingObstacle(mario.x, mario.y, halfSize, sPressed,true,mario.isSpawning)) {
                        mario.x = oldX;
                    }
                }
                applyGravity(mario);
                // Restart current level if 'R' is pressed
                if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                    levelIdx = 0;
                    frames = 0;
                    mario.resetDeathCount();
                    mario.respawn();
                    while (StdDraw.isKeyPressed(KeyEvent.VK_R)) StdDraw.pause(10);
                    break;
                }
                map.handleTeleport(mario, StdDraw.isKeyPressed(KeyEvent.VK_S));
                boolean cNow = StdDraw.isKeyPressed(KeyEvent.VK_C);
                if (cNow && !cWasPressed) cameraFollow = !cameraFollow;
                cWasPressed = cNow;
                // Zoom mode settings
                if (cameraFollow) {
                    int hw = 200;
                    int hh = 200;
                    // Keep camera inside the map bounds
                    int camX = mario.x;
                    int camY = mario.y;
                    if (camX - hw < 0) camX = hw;
                    if (camX + hw > width) camX = width - hw;
                    if (camY - hh < 0) camY = hh;
                    if (camY + hh > height) camY = height - hh;
                    StdDraw.setXscale(camX - hw, camX + hw);
                    StdDraw.setYscale(camY - hh, camY + hh);
                } else {
                    StdDraw.setXscale(0, width);
                    StdDraw.setYscale(0, height);
                }
                if (!map.isInsidePipe(mario)) {
                    mario.draw();
                }
                map.draw();
                    for (int i = 0; i < currentLevel.getEnemies().size(); i++) {
                    Enemy e = currentLevel.getEnemies().get(i);
                    e.move(map);
                    e.draw();
                        // If Mario touches a mushroom, he dies
                    if (!mario.isDead && mario.checkEnemyCollision(e)) {
                        mario.die();
                    }
                }
                if (currentLevel.getId() == 3) {
                    map.collectCoin(mario);
                }
                // Death animation
                if (mario.isDead) {
                    mario.speedY += mario.gravity;
                    mario.y = mario.y + (int) mario.speedY;
                    if (mario.y < 0) {// Respawn once he falls off screen
                        mario.isDead = false;
                        mario.speedY = 0;
                        mario.respawn();
                    }
                }
                // DRAW HUD (Blue panel at the bottom)
                StdDraw.setXscale(0, width);
                StdDraw.setYscale(0, height);
                StdDraw.setPenColor(new Color(32, 56, 136)); // Görseldeki o koyu lacivert
                StdDraw.filledRectangle(400, 100, 400, 100); // Panelin yüksekliği
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 14));
                StdDraw.textLeft(40, 140, "Move: [A] [D] [W]");
                StdDraw.textLeft(40, 110, "Restart: [R]");
                StdDraw.textLeft(40, 80, "Camera: [C]");
                StdDraw.setPenColor(StdDraw.YELLOW);
                StdDraw.text(400, 110, currentLevel.getClue());
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textRight(760, 140, "Level " + currentLevel.getId());
                StdDraw.textRight(760, 110, getTime(frames));
                StdDraw.setPenColor(StdDraw.YELLOW);
                StdDraw.textRight(760, 80, "Deaths: " + mario.getDeathCount());
                // Check for level finish
                if (map.isAtExit(mario)) {
                    // In level 3, you need all coins to pass
                    if (currentLevel.getId() == 3 && !map.allCoinsCollected()) {
                    } else {
                        levelBanner(currentLevel.getId(), currentLevel, currentLevel.getEnemies(), mario, getTime(frames));
                        levelIdx++;
                        mario.respawn();
                        break;
                    }
                }
                StdDraw.show();
                StdDraw.pause(this.pauseDuration);
            }
        }
        // Final screen when all levels are done
        int result = endScreen(getTime(frames), mario.getDeathCount());
        if (result == 1) {
            mario.resetDeathCount();
            mario.respawn();
            frames = 0;
            run();
        }
    }
    // Handles gravity and floor collisions
    private void applyGravity (Mario mario){
        if (mario.isDead) return;
        boolean sPressed = StdDraw.isKeyPressed(KeyEvent.VK_S) || mario.isSinking;

        mario.speedY += mario.gravity;
        int nextY = (int) (mario.y + mario.speedY);
        double half = mario.getSize() / 2.0;
        // If he hits a floor or ceiling
        if (map.isHittingObstacle(mario.x, nextY, half, sPressed, false,mario.isSpawning)) {
            int step = (mario.speedY > 0) ? 1 : -1;

            while (!map.isHittingObstacle(mario.x, mario.y + step, half, sPressed,false,mario.isSpawning)) {
                mario.y += step;
            }
            mario.speedY = 0;
        } else {
            mario.y = nextY;
        }
        if (mario.y > height - mario.getSize() / 2) {
            mario.y = height - mario.getSize() / 2;
            mario.speedY = 0;
        }

        if (mario.isSpawning && mario.speedY==0&& mario.y<750) {
            mario.isSpawning = false;
        }
    }
    //adjusting HUD
        private String getTime ( long frames){
            long totalMs = frames * pauseDuration;
            long minutes = totalMs / 60000;
            long seconds = (totalMs % 60000) / 1000;
            long ms = (totalMs % 1000) / 10; // 2 haneli ms
            return String.format("%02d:%02d:%02d", minutes, seconds, ms);
        }
        //images about transition between levels
        private void levelBanner ( int id, Level currentLevel, ArrayList < Enemy > currentEnemies, Mario mario, String
        elapsedTime){
            StdDraw.setPenColor(new Color(32, 56, 136));
            StdDraw.filledRectangle(width / 2.0, height / 2.0, width / 2.0, 80);
            StdDraw.setPenColor(StdDraw.YELLOW);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 45));
            StdDraw.text(width / 2.0, height / 2.0 + 20, "Level " + id + " Complete!");
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(width / 2.0, height / 2.0 - 30, "Press [Space] to Continue");
            StdDraw.show();
            StdDraw.pause(500);
            while (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) StdDraw.pause(20);
            while (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) StdDraw.pause(10);
        }
        private int endScreen (String elapsedTime,int deathCount){
        //we are adjusting the end screen and images
            int selected = 1;
            boolean upWasPressed = false;
            boolean downWasPressed = false;
            while (true) {
                StdDraw.clear(new Color(25, 35, 100));
                StdDraw.setPenColor(StdDraw.YELLOW);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 70));
                StdDraw.text(width / 2.0, 620, "You Won!");
                StdDraw.setFont(new Font("Arial", Font.PLAIN, 28));
                StdDraw.setPenColor(selected == 0 ? StdDraw.WHITE : new Color(150, 150, 200));
                StdDraw.text(width / 2.0, 420, selected == 0 ? "> Exit <" : "Exit");
                StdDraw.setPenColor(selected == 1 ? StdDraw.WHITE : new Color(150, 150, 200));
                StdDraw.text(width / 2.0, 360, selected == 1 ? "> Restart <" : "Restart");
                StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(width / 2.0, 250, "Time: " + elapsedTime);
                StdDraw.text(width / 2.0, 220, "Deaths: " + deathCount);
                // Menu navigation
                boolean upNow = StdDraw.isKeyPressed(KeyEvent.VK_UP);
                boolean downNow = StdDraw.isKeyPressed(KeyEvent.VK_DOWN);
                if (upNow && !upWasPressed) selected = 0;
                if (downNow && !downWasPressed) selected = 1;
                upWasPressed = upNow;
                downWasPressed = downNow;
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    while (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) StdDraw.pause(10);
                    return selected;
                }
                StdDraw.show();
                StdDraw.pause(pauseDuration);
            }
        }
    // Main menu logic with FPS adjuster
        private void startScreen (Mario mario){
            boolean inMenu = true;
            while (inMenu) {
                StdDraw.clear(new Color(146, 144, 255));
                // Draw floor blocks for style
                for (int x = 20; x < width; x += 40) {
                    StdDraw.picture(x, 20, "assets/block.png", 40, 40);
                    StdDraw.picture(x, 60, "assets/block.png", 40, 40);
                    StdDraw.picture(x, 100, "assets/block.png", 40, 40);
                }
                double menuGroundY = 140.0;
                StdDraw.setPenColor(new Color(153, 78, 0));
                StdDraw.filledRectangle(width / 2.0, 550, 260, 90);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
                StdDraw.text(width / 2.0, 575, "SUPER");
                StdDraw.setFont(new Font("Arial", Font.BOLD, 55));
                StdDraw.text(width / 2.0, 525, "MARIO BROS.");
                StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
                StdDraw.text(width / 2.0, 390, "Press [Space] to start");
                StdDraw.text(width / 2.0, 365, "Move: [A] [D] [W]");
                StdDraw.setFont(new Font("Arial", Font.PLAIN, 14));
                // Allow user to change game speed
                int currentFPS = 1000 / pauseDuration;
                StdDraw.text(width / 2.0, 340, "FPS: ~" + currentFPS + "   Adjust: [+] [-]");
                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                    if (pauseDuration < 100) {
                        pauseDuration++;
                    }
                    StdDraw.pause(10);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    if (pauseDuration > 5) {
                        pauseDuration--;
                    }
                    StdDraw.pause(10);
                }
                // Mario jumps around in the menu background
                mario.handleInput(0, true);
                mario.speedY += mario.gravity;
                mario.y += mario.speedY;
                if (mario.y <= menuGroundY) {
                    mario.y = (int) menuGroundY;
                    mario.speedY = 0;
                    if (StdDraw.isKeyPressed(KeyEvent.VK_W)) mario.jump();
                }
                if (mario.x < 20) mario.x = 20;
                if (mario.x > width - 20) mario.x = width - 20;
                mario.draw();
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    inMenu = false;
                }
                StdDraw.show();
                StdDraw.pause(pauseDuration);
            }

        }
    }







