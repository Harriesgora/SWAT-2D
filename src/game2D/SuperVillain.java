package game2D;

import java.awt.Rectangle;

public class SuperVillain extends Villain {

    private boolean markedForRemoval = false;
    private boolean isWalking = false;
    private int leftBound, rightBound;
    private float walkSpeed = 0.04f;
    private Animation walkAnimation;
    private boolean walkingRight = true;

    public SuperVillain(Sprite sprite, int health, Rectangle shootingZone) {
        super(sprite, health, shootingZone);
    }

    @Override
    public void update(long elapsed) {
        Sprite sprite = getSprite();

        if (isDead()) {
            if (!markedForRemoval) {
                sprite.hide();
                markedForRemoval = true;
            }
        } else {
            if (isWalking && walkAnimation != null) {
                // Move left or right
                if (walkingRight) {
                    sprite.setX(sprite.getX() + walkSpeed * elapsed);
                    sprite.setScale(1, 1); // Face right
                    if (sprite.getX() >= rightBound) {
                        walkingRight = false;
                    }
                } else {
                    sprite.setX(sprite.getX() - walkSpeed * elapsed);
                    sprite.setScale(-1, 1); // Face left
                    if (sprite.getX() <= leftBound) {
                        walkingRight = true;
                    }
                }

                // Set walking animation
                sprite.setAnimation(walkAnimation);
            }

            sprite.update(elapsed);
        }
    }

    @Override
    public void updateShooting(long elapsed, Sprite player) {
        if (isDead()) return;

        if (getShootingZone() != null && getShootingZone().intersects(player.getBounds())) {
            float villainX = getSprite().getX();
            float playerX = player.getX();

            boolean canShoot = (walkingRight && playerX > villainX) || (!walkingRight && playerX < villainX);

            if (canShoot) {
                long timeSinceLastShot = getTimeSinceLastShot() + elapsed;

                if (timeSinceLastShot >= getShootCooldown()) {
                    float bx = getSprite().getX() + getSprite().getWidth() / 2;
                    float by = getSprite().getY() + getSprite().getHeight() / 2;
                    int direction = walkingRight ? 1 : -1;
                    getBullets().add(new VillainBullet(bx, by, direction));
                    resetTimeSinceLastShot();

                    Sound gunshot = new Sound("sounds/Gun Shot Sound1.wav");
                    gunshot.start();
                } else {
                    setTimeSinceLastShot(timeSinceLastShot);
                }
            } else {
                setTimeSinceLastShot(getTimeSinceLastShot() + elapsed);
            }
        } else {
            setTimeSinceLastShot(getTimeSinceLastShot() + elapsed);
        }
    }


    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    private long getTimeSinceLastShot() {
        return super.timeSinceLastShot;
    }

    private void setTimeSinceLastShot(long value) {
        super.timeSinceLastShot = value;
    }

    private void resetTimeSinceLastShot() {
        super.timeSinceLastShot = 0;
    }

    private long getShootCooldown() {
        return 600;
    }

    public void enableWalking(int leftTile, int rightTile, int tileWidth) {
        this.leftBound = leftTile * tileWidth;
        this.rightBound = rightTile * tileWidth;
        this.isWalking = true;
        this.walkingRight = true;
    }

    public void setWalkingAnimation(Animation walkAnim) {
        this.walkAnimation = walkAnim;
    }
}
