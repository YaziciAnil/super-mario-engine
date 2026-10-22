//Batuhan Özgür
//2024400150

public class Enemy {
    private int x;
    private int y;
    private int size;
    private int x1; //left border
    private int x2;//right border
    private int direction;//1 for right -1 for left
    private boolean canJump;
    private int jumpTimer = 0;//following jumping interval
    private double speedY = 0;//vertical speed
    private int baseY;//after the jump enemy will be here
    private int jumpThreshold = 80;//triggering jumping by time


    public Enemy(int x, int y, int size, int direction, int x1, int x2,boolean canJump) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.direction = direction;
        this.x1 = x1;
        this.x2 = x2;
        this.canJump = canJump;
        this.baseY=y;
    }
    public void move(Map map) {
        int halfSize = size / 2;

        if (y == baseY) {
            //it calculates next x position of enemy
            int nextX = x + direction * 2;
            //control for screen border
            if (nextX - halfSize < 0 || nextX + halfSize > 800) {
                direction *= -1;
            }
            //control for obstacles
            else if (map.isHittingObstacle(nextX, y, halfSize, false, true,false)) {
                direction *= -1;
            }
            //in this case,position should change
            else {
                x = nextX;
                //movement borders of enemy
                if (x >= x2) { x = x2; direction = -1; }
                else if (x <= x1) { x = x1; direction = 1; }
            }
        }

        if (canJump) {
            //if enemy is on the ground
            if (y == baseY && speedY == 0) {
                jumpTimer++;
                if (jumpTimer >= jumpThreshold) {
                    //initial jumping speed
                    speedY = 14;
                    jumpTimer = 0;
                //it should be unpredictable,we are randomizing jumping interval
                    jumpThreshold = (int) (Math.random() * 40) + 20;
                }
            }
            //if jumping continues.
            if (speedY != 0) {
                int nextY = y + (int) speedY;
                if (nextY + halfSize > 800) {
                    speedY = 0;
                }
                //if it collides with any obstacle at the top height it starts to fall.
                else if (map.isHittingObstacle(x, nextY, halfSize, false, false,false)) {
                    speedY = 0;
                } else {
                    y = nextY;
                }
                //gravity
                speedY -= 0.6;
                //when it reached the ground,we fixed vertical speed and adjusting position
                if (y <= baseY && speedY < 0) {
                    y = baseY;
                    speedY = 0;
                }
            }
        }
    }
    public void draw() {
        if (this.canJump) {
            StdDraw.picture(x, y, "assets/enemy2.png", 53, 53);
        }
        else {
            StdDraw.picture(x, y, "assets/mushroom.png", size, size);
        }
    }
    //getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

}
