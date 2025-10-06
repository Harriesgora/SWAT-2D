package game2D;

import java.awt.*;
import java.util.ArrayList;
import game2D.Game;

public class Villain {
    private Sprite sprite;
    private int health;
    private Rectangle shootingZone;

    private ArrayList<VillainBullet> bullets;
    protected long shootCooldown = 600;
    protected long timeSinceLastShot = 0;
    private boolean dead = false;

    private int smokeLoopCount = 0;
    private int lastFrameIndex = -1;
    private final int maxSmokeLoops = 3;

    //  Patrolling variables
    private boolean canWalk = false;
    private float patrolSpeed = 0.02f;
    private int patrolStartX, patrolEndX;
    private boolean walkingRight = true;

    // Walk animation
    private Animation walkAnimation;

    public Villain(Sprite sprite, int health, Rectangle shootingZone) {
        this.sprite = sprite;
        this.health = health;
        this.shootingZone = shootingZone;
        this.bullets = new ArrayList<>();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    public void reduceHealth(int damage) {
        this.health -= damage;

        if (this.health <= 0 && !dead) {
            this.health = 0;
            dead = true;
            sprite.setVelocityX(0);
            sprite.setVelocityY(0);
            sprite.setAnimation(Game.smokeAnimation);
        }
    }

    public boolean isDead() {
        return dead;
    }

    public Rectangle getShootingZone() {
        return shootingZone;
    }

    public ArrayList<VillainBullet> getBullets() {
        return bullets;
    }

    public void updateShooting(long elapsed, Sprite player) {
        if (!isDead() && shootingZone.intersects(player.getBounds())) {
            timeSinceLastShot += elapsed;

            // Determine where the player is relative to the villain
            boolean playerRight = player.getX() > sprite.getX();
            boolean facingRight = sprite.getScaleX() == -1;
            boolean facingLeft = sprite.getScaleX() == 1;

            // Only shoot if facing the player
            if ((playerRight && facingRight) || (!playerRight && facingLeft)) {
                if (timeSinceLastShot >= shootCooldown) {
                    int direction = playerRight ? 1 : -1;
                    float bx = sprite.getX() + sprite.getWidth() / 2;
                    float by = sprite.getY() + sprite.getHeight() / 2;
                    bullets.add(new VillainBullet(bx, by, direction));
                    timeSinceLastShot = 0;

                    Sound gunshot = new Sound("sounds/Gun Shot Sound1.wav");
                    gunshot.start();
                }
            }
        } else {
            timeSinceLastShot += elapsed;
        }
    }


    public boolean isSmokeAnimationDone() {
        if (!dead) return false;

        Animation anim = sprite.getAnimation();
        if (anim == null) return false;

        int currentIndex = anim.getCurrentFrameIndex();
        if (currentIndex == 0 && lastFrameIndex == anim.getFrameCount() - 1) {
            smokeLoopCount++;
        }

        lastFrameIndex = currentIndex;
        return smokeLoopCount >= maxSmokeLoops;
    }

    public void update(long elapsed) {
        sprite.update(elapsed);

        if (canWalk && !isDead()) {
            float currentX = sprite.getX();

            // Apply walking animation if set
            if (walkAnimation != null) {
                sprite.setAnimation(walkAnimation);
            }

            if (walkingRight) {
                sprite.setVelocityX(patrolSpeed);


                if (sprite.getScaleX() > 0) {
                    sprite.setScale(-1, 1);
                }

                if (currentX >= patrolEndX) {
                    walkingRight = false;
                }
            } else {
                sprite.setVelocityX(-patrolSpeed);


                if (sprite.getScaleX() < 0) {
                    sprite.setScale(1, 1);
                }

                if (currentX <= patrolStartX) {
                    walkingRight = true;
                }
            }

        } else {
            sprite.setVelocityX(0);
        }
    }


    public void enableWalking(int startTileX, int endTileX, int tileWidth) {
        this.canWalk = true;
        this.patrolStartX = startTileX * tileWidth;
        this.patrolEndX = endTileX * tileWidth;
        this.sprite.setVelocityX(patrolSpeed);
        this.walkingRight = true;
    }

    public void setWalkingAnimation(Animation anim) {
        this.walkAnimation = anim;
        if (canWalk && !dead) {
            sprite.setAnimation(anim);
        }
    }
}
