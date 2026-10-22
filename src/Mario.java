//Batuhan Özgür
//2024400150

public class Mario {

    final int spawnX = 60;//initial horizontal position
    final int spawnY = 770;//initial vertical position
    final int size = 40;
        public int x;//current horizontal coordinate
        public int y;//current verticall coordinate
        public double speedY;
        public double gravity=-0.8;
        private int deathCount=0;
        public boolean isDead=false;
        public boolean canTeleport=true;
    public boolean isSinking = false;//for movement in portal
    public boolean isSpawning = true;
    private boolean wWasPressed = false;
    //walking animation
    private String direction = "Right";
    private int walkCounter = 0;
    private int walkFrame = 1;
        //constructor
        public Mario(int x, int y) {
            this.x = 60;
            this.y = 850;
            this.speedY = 0;
        }
        public void jump() {
            this.speedY = 16.0;
        }
        //getters,
        public int getSize() { return size; }
        public int getDeathCount() { return deathCount; }
        public void draw() {
            //animation for death
            if(isDead){
                StdDraw.picture(x, y, "assets/dead.png", size, size);
                return;
            }
            //crouch animation on the bottom portal
            if (isSinking || (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_S) && speedY == 0)) {
                StdDraw.picture(x, y, "assets/crouch.png", size, size);
                return;
            }
            //drawing mario according to direction
            String img = "assets/stand" + direction + ".png";
            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_S) && speedY == 0) {
                img = "assets/crouch.png";
            }
            //drawing jump
            if (speedY != 0) {
                img = "assets/jump" + direction + ".png";
            } else if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_A) || StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_D)) {
                walkCounter++;
                //frame counter for movement animation
                if (walkCounter >= 8) {
                    if (walkFrame == 1) {
                        walkFrame = 2;
                    } else {
                        walkFrame = 1;
                    }
                    walkCounter = 0;
                }
                img = "assets/walk" + direction + walkFrame + ".png";
            }
            StdDraw.picture(x, y, img, size, size);
        }

    public int handleInput(int frame, boolean onGround) {
        boolean wNow = StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_W);
        if (wNow && !wWasPressed && speedY == 0) {
            jump();
        }
        wWasPressed = wNow;
        //going right
        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_D)) {
            this.x += 6;
            this.direction = "Right";
            return 1;
        }
        //going left
        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_A)) {
            this.x -= 6;
            this.direction = "Left";
            return -1;
        }
        return 0;
    }

    public void die() {
            this.isDead = true;
            this.deathCount++;
            speedY=8;}
    public void respawn() {
        this.isDead = false;
        this.x = 60;
        this.y = 850;
        this.speedY = 0;
        this.isSpawning = true;
    }
    //collision algorithm between enemy and mario
    public boolean checkEnemyCollision(Enemy enemy) {
        double halfM = this.size / 2.0;
        double halfE = enemy.getSize() / 2.0;
        if (this.x + halfM > enemy.getX() - halfE &&
                this.x - halfM < enemy.getX() + halfE &&
                this.y + halfM > enemy.getY() - halfE &&
                this.y - halfM < enemy.getY() + halfE) {
            return true;
        }
        return false;
    }
    public void resetDeathCount() {
        this.deathCount = 0;
    }
    }



