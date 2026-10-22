


import java.awt.*;

//Batuhan Özgür
//2024400150
public class Map {
    private int[][] obstacles;
    private int[][] pipes;
    private int[][] portals;
    private int[][] coins;
    private boolean showCoins = false;//visibility of coins
    //constructor
    public Map(int[][] obstacles, int[][] pipes, int[][] portals, int[][] coins) {
        this.obstacles = obstacles;
        this.pipes = pipes;
        this.portals = portals;
        this.coins = coins;
    }
//collision between blocks and mario
    private boolean checkCollision(double x, double y, double size, int[][] list) {
        for (int[] block : list) {
            double bX = block[0];
            double bY = block[1];
            double bW = block[2];
            double bH = block[3];
            if (x + size > bX - bW &&
                    x - size < bX + bW &&
                    y + size > bY - bH &&
                    y - size < bY + bH) {
                return true;
            }
        }
        return false;
    }
//algorithm for coin collection
    public int collectCoin(Mario mario) {
        for (int i = 0; i < coins.length; i++) {
            if (checkCollision(mario.x, mario.y, mario.getSize() / 2.0, new int[][]{coins[i]})) {
                coins[i][0] = 0;
                coins[i][1] = 0;
                return 1;
            }
        }
        return 0;
    }

    public boolean isOnGround(int x, int y, double halfSize, double speedY) {
        if (speedY > 0) return false;
        return checkCollision(x, y - 2, halfSize, obstacles);
    }

//setter
    public void setShowCoins(boolean show) {
        this.showCoins = show;
    }

    public boolean isOnCeiling(int x, int y, double halfSize, double speedY) {
        if (speedY <= 0) return false;
        return checkCollision(x, y + 2, halfSize, obstacles);
    }

    public boolean isAtExit(Mario mario) {
        double halfSize = mario.getSize() / 2.0;
        for (int[] pipe : pipes) {
            //we are determining the initial pipe
            boolean isStartPipe = pipe[1] > 500;
            boolean isMouth = pipe[2] >= pipe[3];
            if (!isStartPipe && !isMouth) {
                if (checkCollision(mario.x, mario.y, halfSize, new int[][]{pipe})) {
//we are checking whether it is inside the pipe with enough distance c
                    if (mario.x > (pipe[0] - pipe[2] + halfSize)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //we are controlling condition of finiishing level,all coins should be collected
    public boolean allCoinsCollected(){
        for(int[] coin : coins){
            if(coin[0] != 0) return false;
        }
        return true;
    }
    public boolean isHittingObstacle(double nextX, double nextY, double size, boolean sPressed, boolean isHorizontal, boolean isSpawning) {
        //blocks are always solid
        if (checkCollision(nextX, nextY, size, obstacles)) return true;
        for (int[] portal : portals) {
            if (isHorizontal) {
                //portals are solid from sides
                if (checkCollision(nextX, nextY, size, new int[][]{portal})) return true;
            } else {
                //portals act as floors unless the character is centered
                boolean isAligned = Math.abs(nextX - portal[0]) <= 5;
                if (!isAligned) {
                    if (checkCollision(nextX, nextY, size, new int[][]{portal})) return true;
                } else {
                    boolean isUpperPortal = portal[1] > 500;
                    if (isUpperPortal) {
                    } else {
                        if (!sPressed && checkCollision(nextX, nextY, size, new int[][]{portal})) return true;
                    }
                }
            }
        }

        for (int[] pipe : pipes) {
            boolean isStartPipe = pipe[1] > 500;
            boolean isMouth = pipe[2] >= pipe[3];
            if (isHorizontal) {
                // Mouth and Start pipe are solid walls from the sides.
                if (isStartPipe) {
                    if (checkCollision(nextX, nextY, size, new int[][]{pipe})) return true;
                } else {
                    if (isMouth) {
                        if (checkCollision(nextX, nextY, size, new int[][]{pipe})) return true;
                    } else {
                        if (checkCollision(nextX, nextY, size, new int[][]{pipe})) {
                            if (nextX > pipe[0]) {
                            } else {
                                return true;
                            }
                        }
                    }
                }
            } else {
                if (isStartPipe) {
                    //  Mario can fall through the pipe only during spawn.
                    if (!isSpawning && checkCollision(nextX, nextY, size, new int[][]{pipe})) return true;
                } else {
                    // Exit Pipe: Always acts as a solid floor for walking.
                    if (checkCollision(nextX, nextY, size, new int[][]{pipe})) return true;
                }
            }
        }
        return false;
    }
    public void handleTeleport(Mario mario, boolean sPressed) {
        boolean inAnyPortal = false;
        for (int[] portal : portals) {
            boolean isAligned = Math.abs(mario.x - portal[0]) <= 15;
            if (isAligned&&checkCollision(mario.x, mario.y, mario.getSize() / 2.0, new int[][]{portal})) {
                inAnyPortal = true;
                if (!mario.canTeleport) break;
                boolean isUpperPortal = portal[1] > 500;
                if (isUpperPortal) {
                    // Upper to Lower: Teleport to the bottom portal.
                    int targetX = 0, targetY = 9999;
                    // Find the lowest portal in the list.
                    for (int[] p : portals) {
                        if (p[1] < 500 && p[1] < targetY) {
                            targetX = p[0];
                            targetY = p[1];
                        }
                    }
                    mario.x = targetX;
                    mario.y = targetY + 60;// Spawn above the lower portal.
                    mario.speedY = 12.0;
                    mario.canTeleport = false;// Disable teleport until exiting portal area.
                } else if (sPressed || mario.isSinking) {
                    // Lower to upper: Sinking animation logic.
                    mario.isSinking = true;
                    mario.speedY = -1.5;
                    if (mario.y < portal[1] + 15) {
                        int targetX = 0, targetY = 0;
                        for (int[] p : portals) {
                            if (p[1] > 500 && p[1] > targetY) {
                                targetX = p[0];
                                targetY = p[1];
                            }
                        }
                        mario.x = targetX;
                        mario.y = targetY - 40;
                        mario.speedY = 0.0;
                        mario.isSinking = false;
                        mario.canTeleport = false;
                    }
                }break;}}
        // Reset teleport capability once Mario moves away from portals.
        if (!inAnyPortal) mario.canTeleport = true;mario.isSinking = false;
    }
    //only pipe should be displayed this method is important for this.
    public boolean isInsidePipe(Mario mario) {
        for (int[] pipe : pipes) {
            if (checkCollision(mario.x, mario.y, mario.getSize() / 2.0, new int[][]{pipe})) {
                return true;
            }
        }
        return false;
    }
//basic drawing
    public void draw() {
        for (int[] obs : obstacles) {
            StdDraw.picture(obs[0], obs[1], "assets/block.png", obs[2] * 2, obs[3] * 2);
        }
        StdDraw.setPenColor(new Color(255, 255, 0));
        for (int[] pipe : pipes) {
            StdDraw.filledRectangle(pipe[0], pipe[1], pipe[2], pipe[3]);
        }
        StdDraw.setPenColor(new Color(255, 0, 0));
        for (int[] portal : portals) {
            StdDraw.filledRectangle(portal[0], portal[1], portal[2], portal[3]);
        }
        if (showCoins) {
            for (int[] coin : coins) {
                if (coin[0] != 0) {
                    StdDraw.picture(coin[0], coin[1], "assets/coin.png", coin[2]*2, coin[3]*2);
                }
            }
        }
    }
}



