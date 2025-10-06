package game2D;

import java.awt.*;

public class VillainBullet {
    private float x, y;
    private float speed;
    private boolean visible;
    private int direction; // 1 = right, -1 = left
    private int width = 10;
    private int height = 4;

    private float distanceTravelled = 0;
    private float maxDistance = 250;

    public VillainBullet(float x, float y, int direction) {
        this.x = x;
        this.y = y;
        this.speed = 0.3f; // slower than player's bullet
        this.direction = direction;
        this.visible = true;
    }

    public void update(long elapsed) {
        float distance = direction * speed * elapsed;
        x += distance;
        distanceTravelled += Math.abs(distance);

        if (distanceTravelled >= maxDistance) {
            visible = false;
        }
    }

    public void draw(Graphics2D g, int xOffset, int yOffset) {
        g.setColor(Color.RED);
        g.fillRect((int)(x + xOffset), (int)(y + yOffset), width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getX() {
        return x;
    }
}
