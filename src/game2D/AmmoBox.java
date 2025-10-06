package game2D;

public class AmmoBox {
    private Sprite sprite;
    private boolean collected;

    public AmmoBox(Sprite sprite) {
        this.sprite = sprite;
        this.collected = false;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
        sprite.hide(); // Makes it disappear
    }

    public void draw(java.awt.Graphics2D g) {
        if (!collected) {
            sprite.drawTransformed(g);
        }
    }
}
