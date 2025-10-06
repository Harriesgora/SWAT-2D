package game2D;

import java.awt.*;

public class Bullet {
    private float x, y;
    private float speed;
    private boolean visible;
    private int direction; // 1: right, -1: left
    private int width = 10;
    private int height = 4;

    private float distanceTravelled = 0;
    private float maxDistance = 200; // Maximum bullet travel

    public Bullet(float x, float y, int direction) {
        this.x = x;
        this.y = y;
        this.speed = 0.4f;
        this.direction = direction;
        this.visible = true;
    }

    public void update(long elapsed, TileMap tmap) {
        float distance = direction * speed * elapsed;
        x += distance;
        distanceTravelled += Math.abs(distance);

        // Tile Collision Detection
        int tileX = (int) (x / tmap.getTileWidth());
        int tileY = (int) (y / tmap.getTileHeight());

        Tile tile = tmap.getTile(tileX, tileY);
        if (tile != null) {
            char c = tile.getCharacter();
            if ("sxbgdt".indexOf(c) != -1) {  // Solid tile
                visible = false;
                return;
            }
        }

        // Check if bullet exceeded max range
        if (distanceTravelled >= maxDistance) {
            visible = false;
        }
    }

    public void draw(Graphics2D g, int xOffset, int yOffset) {
        g.setColor(Color.yellow);
        g.fillRect((int)(x + xOffset), (int)(y + yOffset), width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public float getX() {
        return x;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
