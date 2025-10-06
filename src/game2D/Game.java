package game2D;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


// Student ID:3031223


public class Game extends GameCore {
    // Useful game constants
    static int screenWidth = 1024;
    static int screenHeight = 576;

    // Game constants
    float lift = 0.005f;
    float gravity = 0.0001f;
    float fly = -0.04f;
    float moveSpeed = 0.08f;

    // Game screen
    boolean showStartScreen = true;
    Rectangle startButtonBounds = new Rectangle(412, 320, 200, 60); // x, y, width, height

    boolean jump = false;
    boolean onGround = false;
    boolean moveRight = false;
    boolean moveLeft = false;
    boolean debug = true;

    // superjump animation
    boolean superJump = false;
    boolean isCrawling = false;

    //death animation
    boolean isDead = false;
    boolean deathAnimationFinished = false;

    boolean transitioningLevel = false;
    long transitionStartTime = 0;

    //map
    private Image bgFront;
    private Image bgMid;
    private Image bgFurthest;

    // New background for start screen
    private Image startScreenBackground;



    // level declaration
    int currentLevel = 1;

    // bullets declaration
    ArrayList<Bullet> bullets = new ArrayList<>();
    int ammo = 75;         // current bullets
    int maxAmmo = 100;     // maximum bullets

    // Health and kills
    int playerHealth = 100;   // Player starts with 100 HP
    int playerKills = 0;      // Starts with 0 kills

    ArrayList<Villain> villains = new ArrayList<>();


    // Game resources
    Animation landing;
    Sound bgMusic; // Background music for the game

    public static Animation smokeAnimation;

    Animation walkAnimation, idleAnimation, jumpAnimation, fallAnimation, crawlAnimation, villainIdle, deathAnimation, superVillainIdle, villainWalkAnimation, superWalkAnimation;




    // assets
    Animation medkitAnimation, ammoBoxAnimation;
    ArrayList<Medkit> medkits = new ArrayList<>();
    ArrayList<AmmoBox> ammoboxes = new ArrayList<>();


    Sprite player = null;
    ArrayList<Sprite> clouds = new ArrayList<Sprite>();
    ArrayList<Tile> collidedTiles = new ArrayList<Tile>();

    TileMap tmap = new TileMap();    // Our tile map, note that we load it in init()

    long total;                    // The score will be the total time elapsed since a crash


    /**
     * The obligatory main method that creates
     * an instance of our class and starts it running
     *
     * @param args The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {

        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false, screenWidth, screenHeight);
    }

    public void init() {
        Sprite s;    // Temporary reference to a sprite


        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");
        int h = tmap.getTileHeight() * tmap.getMapHeight();
        setSize(screenWidth, h);
        setLocationRelativeTo(null); // Centers the window on the screen
        setVisible(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (bgMusic != null) {
                    bgMusic.stopSound();
                }
                System.exit(0); // Fully exits the app
            }
        });

        //mouse action listener
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                Game.this.mousePressed(e);
            }
        });


        // 3 backgrounds
        bgFront = loadImage("images/bgFront.png");
        System.out.println("bgFront loaded: " + (bgFront != null));

        bgMid = loadImage("images/bgMid.png");
        System.out.println("bgMid loaded: " + (bgMid != null));

        bgFurthest = loadImage("images/bgFurthest.png");
        System.out.println("bgFurthest loaded: " + (bgFurthest != null));


        //start display
        startScreenBackground = loadImage("images/Jungle1.png");
        System.out.println("Start screen background loaded: " + (startScreenBackground != null));

        //Loading in of diffrent animations
        walkAnimation = new Animation();
        walkAnimation.addFrame(loadImage("images/run0000.png"), 100);
        walkAnimation.addFrame(loadImage("images/run0001.png"), 100);
        walkAnimation.addFrame(loadImage("images/run0002.png"), 100);
        walkAnimation.addFrame(loadImage("images/run0003.png"), 100);
        walkAnimation.addFrame(loadImage("images/run0004.png"), 100);
        walkAnimation.addFrame(loadImage("images/run0005.png"), 100);

        idleAnimation = new Animation();
        idleAnimation.addFrame(loadImage("images/idle0000.png"), 200);
        idleAnimation.addFrame(loadImage("images/idle0001.png"), 200);

        // Jump Animation (1 image)
        jumpAnimation = new Animation();
        jumpAnimation.addFrame(loadImage("images/jump.png"), 1000);

        // Fall Animation (1 image)
        fallAnimation = new Animation();
        fallAnimation.addFrame(loadImage("images/fall.png"), 1000);

        //crawl animation
        crawlAnimation = new Animation();
        crawlAnimation.addFrame(loadImage("images/crawl00.png"), 100);
        crawlAnimation.addFrame(loadImage("images/crawl01.png"), 100);
        crawlAnimation.addFrame(loadImage("images/crawl02.png"), 100);
        crawlAnimation.addFrame(loadImage("images/crawl03.png"), 100);
        crawlAnimation.addFrame(loadImage("images/crawl04.png"), 100);
        crawlAnimation.addFrame(loadImage("images/crawl05.png"), 100);


        player = new Sprite(idleAnimation);  // start player with idle animation

        // villian idle animation
        villainIdle = new Animation();
        villainIdle.addFrame(loadImage("images/enemyidle00.png"), 180);
        villainIdle.addFrame(loadImage("images/enemyidle01.png"), 180);
        villainIdle.addFrame(loadImage("images/enemyidle02.png"), 180);
        villainIdle.addFrame(loadImage("images/enemyidle03.png"), 180);


        // SuperVillain idle animation
        superVillainIdle = new Animation();
        superVillainIdle.addFrame(loadImage("images/SuperVIdle1.png"), 500);
        superVillainIdle.addFrame(loadImage("images/SuperVIdle2.png"), 500);
        superVillainIdle.addFrame(loadImage("images/SuperVIdle3.png"), 500);
        superVillainIdle.addFrame(loadImage("images/SuperVIdle4.png"), 500);
        superVillainIdle.addFrame(loadImage("images/SuperVIdle5.png"), 500);
        superVillainIdle.setLoop(true);


        // death animation
        deathAnimation = new Animation();
        deathAnimation.addFrame(loadImage("images/Death0000.png"), 150);
        deathAnimation.addFrame(loadImage("images/Death0001.png"), 150);
        deathAnimation.addFrame(loadImage("images/Death0002.png"), 150);
        deathAnimation.addFrame(loadImage("images/Death0003.png"), 150);
        deathAnimation.addFrame(loadImage("images/Death0004.png"), 150);
        deathAnimation.addFrame(loadImage("images/Death0005.png"), 150);


        // Villain Death animation

        smokeAnimation = new Animation();
        smokeAnimation.addFrame(loadImage("images/Smoke 1.png"), 100);
        smokeAnimation.addFrame(loadImage("images/Smoke 2.png"), 100);
        smokeAnimation.addFrame(loadImage("images/Smoke 3.png"), 100);
        smokeAnimation.addFrame(loadImage("images/Smoke 4.png"), 100);
        smokeAnimation.addFrame(loadImage("images/Smoke 5.png"), 100);
        smokeAnimation.addFrame(loadImage("images/Smoke 6.png"), 100);

        //VillainPatrol
        villainWalkAnimation = new Animation();
        villainWalkAnimation.addFrame(loadImage("images/VillainRun00.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun01.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun02.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun03.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun04.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun05.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun06.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun07.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun08.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun09.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun10.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun11.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun12.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun13.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun14.png"), 100);
        villainWalkAnimation.addFrame(loadImage("images/VillainRun15.png"), 100);

        //Supervillain patrol animation
        superWalkAnimation = new Animation();
        superWalkAnimation.addFrame(loadImage("images/Superwalk1.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk2.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk3.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk4.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk5.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk6.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk7.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk8.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Supsserwalk9.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk10.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk11.png"), 100);
        superWalkAnimation.addFrame(loadImage("images/Superwalk12.png"), 100);
        superWalkAnimation.setLoop(true);


        // assets
       // AmmoBox animation (1 frame)
        ammoBoxAnimation = new Animation();
        ammoBoxAnimation.addFrame(loadImage("images/ammo.png"), 1000); //

        // MedKit animation
        medkitAnimation = new Animation();
        medkitAnimation.addFrame(loadImage("images/medkit.png"), 1000);



        // Create and place Medkits for Level 1
        int[][] medkitPositions = {
                {37, 12},
                {23, 14}
        };

        for (int[] pos : medkitPositions) {
            int tileX = pos[0] * tmap.getTileWidth();
            int tileY = pos[1] * tmap.getTileHeight();

            Sprite medSprite = new Sprite(medkitAnimation);
            medSprite.setPosition(tileX, tileY - medSprite.getHeight());
            medSprite.show();

            medkits.add(new Medkit(medSprite));
        }

        // place AmmoBoxes for Level 1
        int[][] ammoPositions = {
                {62, 14},
                {33, 15},
                {2, 14}
        };

        for (int[] pos : ammoPositions) {
            int tileX = pos[0] * tmap.getTileWidth();
            int tileY = pos[1] * tmap.getTileHeight();

            Sprite ammoSprite = new Sprite(ammoBoxAnimation);
            ammoSprite.setPosition(tileX, tileY - ammoSprite.getHeight());
            ammoSprite.show();

            ammoboxes.add(new AmmoBox(ammoSprite));
        }



        // Place exactly 3 villains at specified tile coordinates
        int[][] villainCoords = {
                {25, 10}, // t
                {39, 15}, // d
                {62, 11}  // s
        };

        for (int[] coord : villainCoords) {
            int x = coord[0];
            int y = coord[1];

            int tileX = x * tmap.getTileWidth();
            int tileY = y * tmap.getTileHeight();

            Sprite vSprite = new Sprite(villainIdle);
            vSprite.setPosition(tileX - 3, tileY - vSprite.getHeight());
            vSprite.show();

            Rectangle shootingZone = null;
            int tileWidth = tmap.getTileWidth();
            int tileHeight = tmap.getTileHeight();

            // Allow vertical vision range of 3 tiles
            int zoneHeight = tileHeight * 3;

            if (x == 25 && y == 10) {
                // Range: (20–25, range)
                shootingZone = new Rectangle(
                        20 * tileWidth,
                        (y - 1) * tileHeight,
                        6 * tileWidth,
                        zoneHeight
                );
            } else if (x == 39 && y == 15) {
                // Range: (35–39, range)
                shootingZone = new Rectangle(
                        35 * tileWidth,
                        (y - 1) * tileHeight,
                        5 * tileWidth,
                        zoneHeight
                );
            } else if (x == 62 && y == 11) {
                // Range: (58–62, range)
                shootingZone = new Rectangle(
                        58 * tileWidth,
                        (y - 1) * tileHeight,
                        5 * tileWidth,
                        zoneHeight
                );
            }

            Villain villain = new Villain(vSprite, 50, shootingZone);  // 50 hp per villain
            if (x == 39 && y == 15) {
                villain.enableWalking(34, 39, tileWidth);  // Patrol between 34–39
                villain.setWalkingAnimation(villainWalkAnimation);
            } else if (x == 62 && y == 11) {
                villain.enableWalking(58, 62, tileWidth);  // Patrol between 58–62
                villain.setWalkingAnimation(villainWalkAnimation);
            }


            villains.add(villain);

            // Add 4th SuperVillain at (52,6)
            int svX = 52;
            int svY = 5;
            int svTileX = svX * tmap.getTileWidth();
            int svTileY = svY * tmap.getTileHeight();
            Sprite svSprite = new Sprite(superVillainIdle);
            svSprite.setPosition(svTileX - 3, svTileY - svSprite.getHeight() + (4 * svSprite.getHeight() / 5));
            svSprite.show();

            Rectangle svZone = new Rectangle(
                    (svX - 2) * tmap.getTileWidth(),
                    (svY - 1) * tmap.getTileHeight(),
                    6 * tmap.getTileWidth(),
                    3 * tmap.getTileHeight()
            );
            SuperVillain superVillain = new SuperVillain(svSprite, 75, svZone);
            villains.add(superVillain);
            System.out.println("SuperVillain placed at tile (" + svX + "," + svY + ")");



        }


        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);


        for (int c = 0; c < 3; c++) {
            s = new Sprite(ca);
            s.setX(screenWidth + (int) (Math.random() * 200.0f));
            s.setY(30 + (int) (Math.random() * 150.0f));
            s.setVelocityX(-0.02f);
            s.show();
            clouds.add(s);
        }

        initialiseGame();
        bgMusic = new Sound("sounds/SWATBG.mid");
        // Start playing the background music on loop
        bgMusic.loop();


        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it when restarting
     * the game when the player loses.
     */
    public void initialiseGame() {
        total = 0;

        player.setPosition(200, 200);
        player.setVelocity(0, 0);
        player.show();
    }

    public void loadLevel2() {
        // Load the new tile map
        tmap.loadMap("maps", "Level2.txt");

        // Recalculate screen height in case the map size changed
        int newHeight = tmap.getTileHeight() * tmap.getMapHeight();
        setSize(screenWidth, newHeight);

        // Reset player state
        player.setPosition(200, 200);
        player.setVelocity(0, 0);
        player.show();

        // Clear old entities
        bullets.clear();
        villains.clear();
        medkits.clear();
        ammoboxes.clear();

        // Reset gameplay flags
        playerKills = 0;
        playerHealth = 100;
        isDead = false;
        deathAnimationFinished = false;

        // Spawn Medkits for Level 2
        int[][] medkitPositions = {
                {12, 9},
                {33, 15},
                {49, 10}
        };

        for (int[] pos : medkitPositions) {
            int tileX = pos[0] * tmap.getTileWidth();
            int tileY = pos[1] * tmap.getTileHeight();

            Sprite medSprite = new Sprite(medkitAnimation);
            medSprite.setPosition(tileX, tileY - medSprite.getHeight()); // Align to tile
            medSprite.show();

            medkits.add(new Medkit(medSprite));
        }

        // Spawn AmmoBoxes for Level 2
        int[][] ammoBoxPositions = {
                {2, 14},
                {62, 11}
        };

        for (int[] pos : ammoBoxPositions) {
            int tileX = pos[0] * tmap.getTileWidth();
            int tileY = pos[1] * tmap.getTileHeight();

            Sprite ammoSprite = new Sprite(ammoBoxAnimation);
            ammoSprite.setPosition(tileX, tileY - ammoSprite.getHeight()); // Align to tile
            ammoSprite.show();

            ammoboxes.add(new AmmoBox(ammoSprite));
        }

        // Spawn SuperVillain at fixed tile (24, 12)
        int superX = 13;
        int superY = 13;

        int tileX = superX * tmap.getTileWidth();
        int tileY = superY * tmap.getTileHeight();

        Sprite superSprite = new Sprite(superVillainIdle);
        superSprite.setPosition(tileX - 3, (superY - 1) * tmap.getTileHeight());
        superSprite.setPosition(tileX - 3, tileY - superSprite.getHeight() + (4 * superSprite.getHeight() / 5));


        superSprite.show();

        // Shooting zone for SuperVillain
        Rectangle superZone = new Rectangle(
                (superX - 3) * tmap.getTileWidth(),
                (superY - 1) * tmap.getTileHeight(),
                7 * tmap.getTileWidth(),
                3 * tmap.getTileHeight()
        );

        SuperVillain sv = new SuperVillain(superSprite, 75, superZone);
        sv.enableWalking(13, 18, tmap.getTileWidth());
        sv.setWalkingAnimation(superWalkAnimation);
        villains.add(sv);

        // Spawn 4 more SuperVillains
        int[][] superCoords = {
                {2, 8},
                {36, 6},
                {35, 14},
                {32, 11}
        };

        for (int[] coord : superCoords) {
            int x = coord[0];
            int y = coord[1];

            int tx = x * tmap.getTileWidth();
            int ty = y * tmap.getTileHeight();

            Sprite sSprite = new Sprite(superVillainIdle);
            sSprite.setPosition(tx - 3, ty - sSprite.getHeight() + (4 * sSprite.getHeight() / 5));
            sSprite.show();

            Rectangle zone = new Rectangle(
                    (x - 1) * tmap.getTileWidth(),
                    (y - 1) * tmap.getTileHeight(),
                    7 * tmap.getTileWidth(),
                    3 * tmap.getTileHeight()
            );

            SuperVillain extraSV = new SuperVillain(sSprite, 75, zone);
            if (x == 35 && y == 14) {
                extraSV.enableWalking(34, 40, tmap.getTileWidth());
                extraSV.setWalkingAnimation(superWalkAnimation);
            }
            villains.add(extraSV);

            System.out.println("SuperVillain placed at (" + x + "," + y + ")");
        }

        System.out.println("Level 2 loaded with Medkits, AmmoBoxes, and SuperVillain at tile (24,12)");
    }


    /**
     * Draw the current state of the game. Note the sample use of
     * debugging output that is drawn directly to the game screen.
     */
    public void draw(Graphics2D g) {
        if (showStartScreen) {
            drawStartScreen(g);
            return;
        }
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());


        int yo = 0; // Keep vertical offset

        int xo = (int) (getWidth() / 2 - player.getX() - player.getWidth() / 2);

        xo = Math.max(getWidth() - tmap.getPixelWidth(), Math.min(0, xo));

        // Scroll factors for parallax background
        double frontScrollFactor = 0.8;
        double midScrollFactor = 0.5;
        double furthestScrollFactor = 0.2;

        //  Draw Parallax Backgrounds

        if (bgFurthest != null) {
            int bgWidth = bgFurthest.getWidth(null);
            int bgHeight = bgFurthest.getHeight(null);
            int bgX = (int) (xo * furthestScrollFactor);

            int scaledHeight = getHeight();
            int scaledWidth = (int) ((double) bgWidth / bgHeight * scaledHeight);

            for (int x = bgX - scaledWidth; x < screenWidth; x += scaledWidth) {
                g.drawImage(bgFurthest, x, 0, scaledWidth, scaledHeight, null);
            }
        }

        if (bgMid != null) {
            int bgWidth = bgMid.getWidth(null);
            int bgHeight = bgMid.getHeight(null);
            int bgX = (int) (xo * midScrollFactor);

            int scaledHeight = getHeight();
            int scaledWidth = (int) ((double) bgWidth / bgHeight * scaledHeight);

            for (int x = bgX - scaledWidth; x < screenWidth; x += scaledWidth) {
                g.drawImage(bgMid, x, 0, scaledWidth, scaledHeight, null);
            }
        }

        if (bgFront != null) {
            int bgWidth = bgFront.getWidth(null);
            int bgHeight = bgFront.getHeight(null);
            int bgX = (int) (xo * frontScrollFactor);

            int scaledHeight = getHeight();
            int scaledWidth = (int) ((double) bgWidth / bgHeight * scaledHeight);

            for (int x = bgX - scaledWidth; x < screenWidth; x += scaledWidth) {
                g.drawImage(bgFront, x, 0, scaledWidth, scaledHeight, null);
            }
        }

        // Apply offsets to sprites then draw them
        for (Sprite s : clouds) {
            s.setOffsets(xo, yo);
            s.draw(g);
        }

        // Apply offsets to tile map and draw  it
        tmap.draw(g, xo, yo);

        // Apply offsets to player and draw
        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        // Draw Med Kits
        for (Medkit medkit : medkits) {
            Sprite medSprite = medkit.getSprite();
            medSprite.setOffsets(xo, yo);
            medSprite.draw(g);
        }

        // Draw Ammo Boxes
        for (AmmoBox ammoBox : ammoboxes) {
            Sprite ammoSprite = ammoBox.getSprite();
            ammoSprite.setOffsets(xo, yo);
            ammoSprite.draw(g);
        }

        // Draw ammo BAR ABOVE player's head
        int ammoBarWidth = 120;
        int ammoBarHeight = 12;
        int ammoBarX = 10;
        int ammoBarY = 60;

        // Draw bar background
        g.setColor(Color.GRAY);
        g.fillRect(ammoBarX, ammoBarY, ammoBarWidth, ammoBarHeight);

        int currentAmmoWidth = (int) ((ammo / (double) maxAmmo) * ammoBarWidth);
        g.setColor(new Color(135, 206, 250)); // light blue
        g.fillRect(ammoBarX, ammoBarY, currentAmmoWidth, ammoBarHeight);

        // Draw ammo bar border (black)
        g.setColor(Color.BLACK);
        g.drawRect(ammoBarX, ammoBarY, ammoBarWidth, ammoBarHeight);

        String ammoText = ammo + "/" + maxAmmo;

        Font ammoFont = new Font("SansSerif", Font.BOLD, 11);
        g.setFont(ammoFont);
        g.setColor(Color.BLACK);

        int ammoTextX = ammoBarX + ammoBarWidth + 5;
        int ammoTextY = ammoBarY + ammoBarHeight - 1;
        g.drawString(ammoText, ammoTextX, ammoTextY);

        String loadoutText = "Current Loadout";
        Font loadoutFont = new Font("SansSerif", Font.BOLD, 12);
        g.setFont(loadoutFont);
        g.setColor(Color.BLACK);

        int loadoutTextWidth = g.getFontMetrics().stringWidth(loadoutText);
        int loadoutX = ammoBarX + (ammoBarWidth - loadoutTextWidth) / 2;
        int loadoutY = ammoBarY - 6; // Slightly above the bar
        g.drawString(loadoutText, loadoutX, loadoutY);


        for (Villain v : villains) {
            Sprite sprite = v.getSprite();
            sprite.setOffsets(xo, yo);
            sprite.drawTransformed(g);


            // Drawing Health bar above villain's head
            int barWidth = 50;
            int barHeight = 6;

            int healthBarX = (int) sprite.getX() + xo + sprite.getWidth() / 2 - barWidth / 2;
            int baseY = (int) sprite.getY() + yo - barHeight - 10;

            // Default values for normal Villain
            Color healthColor = Color.RED;
            double maxHealth = 50;
            int healthBarY = baseY;
            Color textColor = Color.BLACK;

            // Adjustments for SuperVillain
            if (v instanceof SuperVillain) {
                healthColor = new Color(128, 0, 128); // Purple
                maxHealth = 75;
                healthBarY = baseY + tmap.getTileHeight() / 3;
                textColor = new Color(102, 0, 153);

            }

            // Draw background
            g.setColor(Color.GRAY);
            g.fillRect(healthBarX, healthBarY, barWidth, barHeight);

            // Draw foreground based on health
            g.setColor(healthColor);
            int currentHealthWidth = (int) ((v.getHealth() / maxHealth) * barWidth);
            g.fillRect(healthBarX, healthBarY, currentHealthWidth, barHeight);

            // Draw border
            g.setColor(Color.BLACK);
            g.drawRect(healthBarX, healthBarY, barWidth, barHeight);

            String healthText = (int) v.getHealth() + " / " + (int) maxHealth;
            Font hpFont = new Font("SansSerif", Font.BOLD, 11);
            g.setFont(hpFont);
            g.setColor(textColor);

            int textWidth = g.getFontMetrics().stringWidth(healthText);
            int textX = healthBarX + (barWidth - textWidth) / 2;
            int textY = healthBarY - 4;

            g.drawString(healthText, textX, textY);

        }

        // Draw bullets
        for (Bullet b : bullets) {
            b.draw(g, xo, yo);
        }
        // Draw villain bullets
        for (Villain v : villains) {
            for (VillainBullet vb : v.getBullets()) {
                vb.draw(g, xo, yo);
            }
        }
        // Draw Health Bar
        int barWidth = 60;
        int barHeight = 8;

        int healthBarX = (int) (player.getX() + player.getWidth() / 2 + xo - barWidth / 2);
        int healthBarY = (int) (player.getY() + yo - 40); // Slightly above ammo bar

        // Draw grey background
        g.setColor(Color.GRAY);
        g.fillRect(healthBarX, healthBarY, barWidth, barHeight);

        // Draw green foreground based on health
        g.setColor(Color.GREEN);
        int currentHealthWidth = (int) (playerHealth / 100.0 * barWidth);
        g.fillRect(healthBarX, healthBarY, currentHealthWidth, barHeight);

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(healthBarX, healthBarY, barWidth, barHeight);

        // Set nice font
        Font uiFont = new Font("SansSerif", Font.BOLD, 13);
        g.setFont(uiFont);
        g.setColor(Color.white);

        // Draw HP text above bar
        String hpText = "HP: " + playerHealth + "/100";
        int hpTextWidth = g.getFontMetrics().stringWidth(hpText);
        g.drawString(hpText, healthBarX, healthBarY - 5);

        // Draw Kill Counter below bar,
        String killText = (currentLevel == 1 ? "Kills: " + playerKills + " / 4" : "Kills: " + playerKills + " / 5");
        int killTextWidth = g.getFontMetrics().stringWidth(killText);
        g.drawString(killText, healthBarX, healthBarY + barHeight + 15);


        if (debug) {

            tmap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.red);
            player.drawBoundingBox(g);


            drawCollidedTiles(g, tmap, xo, yo);
        }

        // Game messages
        if (playerHealth <= 0) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", getWidth() / 2 - 100, getHeight() / 2);
        } else if (currentLevel == 1 && playerKills >= 4 && !transitioningLevel) {
            // Trigger level switch immediately, then show message on Level 2
            currentLevel = 2;
            transitioningLevel = true;
            transitionStartTime = System.currentTimeMillis();
            loadLevel2();
        }

        // Show transition message for 2 seconds after switching to Level 2
        if (transitioningLevel && currentLevel == 2) {
            g.setFont(new Font("Britannic Bold", Font.BOLD, 28));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Level Complete, Onto the Final Level", getWidth() / 2 - 200 + 2, getHeight() / 2 + 2);
            g.setColor(Color.BLUE);
            g.drawString("Level Complete, Onto the Final Level", getWidth() / 2 - 200, getHeight() / 2);

            if (System.currentTimeMillis() - transitionStartTime >= 2000) {
                transitioningLevel = false;
            }
        } else if (currentLevel == 2 && playerKills >= 5) {
            g.setFont(new Font("Britannic Bold", Font.BOLD, 30));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Level Complete, You Are Undisputed", getWidth() / 2 - 240 + 2, getHeight() / 2 + 2);
            g.setColor(new Color(0, 128, 0));
            g.drawString("Level Complete, You Are Undisputed", getWidth() / 2 - 240, getHeight() / 2);
        }
    }

    public void drawCollidedTiles(Graphics2D g, TileMap map, int xOffset, int yOffset) {
        if (collidedTiles.size() > 0) {
            int tileWidth = map.getTileWidth();
            int tileHeight = map.getTileHeight();

            g.setColor(Color.blue);
            for (Tile t : collidedTiles) {
                g.drawRect(t.getXC() + xOffset, t.getYC() + yOffset, tileWidth, tileHeight);
            }
        }
    }


    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    public void update(long elapsed) {

        // Handle level transition delay
        if (transitioningLevel && currentLevel == 1) {
            long now = System.currentTimeMillis();
            if (now - transitionStartTime >= 2000) {
                currentLevel = 2;
                transitioningLevel = false;
                loadLevel2();
            }
            return; // Pause updates while transition text is showing
        }

        // If player has died and animation hasn't started yet
        if (playerHealth <= 0 && !isDead) {
            if (bgMusic != null) {
                bgMusic.stopSound();
            }

            isDead = true;
            player.setAnimation(deathAnimation);
            player.setVelocityX(0);
            player.setVelocityY(0);
        }


        // While dead
        if (isDead) {
            // Always update player animation, even after switching to deadLoop
            player.update(elapsed);

            if (!deathAnimationFinished && player.getAnimation().getCurrentFrameIndex() == 4) {
                deathAnimationFinished = true;

                Animation deadLoop = new Animation();
                deadLoop.addFrame(loadImage("images/Death0004.png"), 500);
                deadLoop.addFrame(loadImage("images/Death0005.png"), 500);
                deadLoop.setLoop(true);

                player.setAnimation(deadLoop);
                player.setY(player.getY() + 15); // adjust so it's not floating
            }

            return; // Skip rest of game logic if player is dead
        }


        // Apply gravity
        player.setVelocityY(player.getVelocityY() + (gravity * elapsed));

        // Adjust movement speed based on crawling state
        if (isCrawling) {
            moveSpeed = 0.03f; // Slower movement when crawling
            player.setAnimation(crawlAnimation);
        } else {
            moveSpeed = 0.08f; // Normal movement speed
        }

        // Handle Jumping or Falling Animation
        if (player.getVelocityY() < 0) { // Jumping upwards
            player.setAnimation(jumpAnimation);
        } else if (player.getVelocityY() > 0.05f) { // Falling downwards
            player.setAnimation(fallAnimation);
        } else {
            // Walking, Crawling, or Idle Animation
            if (isCrawling) {
                player.setAnimation(crawlAnimation);
            } else if (moveRight || moveLeft) {
                player.setAnimation(walkAnimation);
            } else {
                player.setAnimation(idleAnimation);
            }
        }

        // Handle jumping logic
        if (jump && onGround) {
            if (superJump) {
                player.setVelocityY(fly * 3); // Triple jump height
                Sound superJumpSound = new Sound("sounds/SuperJump.wav");
                superJumpSound.start();
            } else {
                player.setVelocityY(fly);// Normal jump height
                Sound jumpSound = new Sound("sounds/jump.wav");
                jumpSound.start();
            }
            onGround = false;
            jump = false; // Reset jump after execution
            superJump = false; // Reset superJump after use
        }

        // Horizontal movement handling
        if (moveRight) {
            player.setVelocityX(moveSpeed);
            player.setScale(1, 1); // facing right (original orientation)
        } else if (moveLeft) {
            player.setVelocityX(-moveSpeed);
            player.setScale(-1, 1); // facing left (flipped horizontally)
        } else {
            player.setVelocityX(0);
        }


        // Update the player's position
        player.update(elapsed);

        ArrayList<Medkit> usedMedkits = new ArrayList<>();
        for (Medkit medkit : medkits) {
            if (boundingBoxCollision(player, medkit.getSprite())) {
                int healAmount = 25;
                int newHealth = Math.min(playerHealth + healAmount, 100);
                if (newHealth > playerHealth) {
                    playerHealth = newHealth;
                    usedMedkits.add(medkit);


                    Sound healSound = new Sound("sounds/Health.wav");
                    healSound.start();
                }
            }
        }
        medkits.removeAll(usedMedkits);


        ArrayList<AmmoBox> usedAmmoboxes = new ArrayList<>();
        for (AmmoBox ammoBox : ammoboxes) {
            if (boundingBoxCollision(player, ammoBox.getSprite())) {
                int ammoGain = 20;
                int newAmmo = Math.min(ammo + ammoGain, maxAmmo);
                if (newAmmo > ammo) {
                    ammo = newAmmo;
                    usedAmmoboxes.add(ammoBox);

                    Sound ammoSound = new Sound("sounds/Ammo Box.wav");
                    ammoSound.start();
                }
            }
        }
        ammoboxes.removeAll(usedAmmoboxes);


        for (Villain v : villains) {
            Sprite sprite = v.getSprite();
            handleVillainCollisionWithPlayer(player, sprite);
        }

        for (Villain v : villains) {
            if (v instanceof SuperVillain) {
                Sprite svSprite = v.getSprite();
                handleVillainCollisionWithPlayer(player, svSprite);
            }
        }

        // Prevent moving too far left
        if (player.getX() <= 0) {
            player.setX(0); // Stop at the left edge of the map
            player.setVelocityX(0); // Ensure the velocity is zero
        }

        // Prevent moving too far right (past the map width)
        float maxRight = 2006;
        if (player.getX() >= maxRight) {
            player.setX(maxRight);
            player.setVelocityX(0);
        }

        // Update the background clouds
        for (Sprite s : clouds) {
            s.update(elapsed);
        }

        ArrayList<Villain> villainsToRemove = new ArrayList<>();

        for (Villain v : villains) {
            Sprite sprite = v.getSprite();

            if (v.isDead()) {
                // Update smoke animation
                sprite.setVelocity(0, 0);
                sprite.update(elapsed);

                // Check if smoke animation looped 3 times
                if (v.isSmokeAnimationDone()) {
                    villainsToRemove.add(v); // Mark for removal
                }
            } else {

                v.update(elapsed);
            }
        }

        // Safely remove villains whose smoke animation is done
        villains.removeAll(villainsToRemove);


        for (Villain v : villains) {
            v.updateShooting(elapsed, player);

            ArrayList<VillainBullet> bulletsToRemove = new ArrayList<>();
            for (VillainBullet vb : v.getBullets()) {
                vb.update(elapsed);

                // Remove if invisible or out of bounds
                if (!vb.isVisible() || vb.getX() < 0 || vb.getX() > tmap.getPixelWidth()) {
                    bulletsToRemove.add(vb);
                    continue;
                }

                // Collision with player
                if (vb.getBounds().intersects(player.getBounds())) {
                    playerHealth -= 8;
                    playerHealth = Math.max(0, playerHealth);
                    vb.setVisible(false);
                    bulletsToRemove.add(vb);
                }
            }
            v.getBullets().removeAll(bulletsToRemove);
        }


        // Update bullets
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet b : bullets) {
            b.update(elapsed, tmap);

            // Check bullet collision with villains
            for (Villain v : villains) {
                if (!v.isDead() && b.getBounds().intersects(v.getSprite().getBounds())) {
                    v.reduceHealth(15);        // Deal damage
                    b.setVisible(false);
                    if (v.isDead()) {
                        playerKills++;// Increment kill count if villain dies

                        // Play death sound
                        Sound deathSound = new Sound("sounds/male death sound.wav");
                        deathSound.start();
                    }
                }
            }

            // Remove bullets that are off-screen or invisible
            if (!b.isVisible() || b.getX() < 0 || b.getX() > tmap.getPixelWidth()) {
                bulletsToRemove.add(b);
            }
        }

        bullets.removeAll(bulletsToRemove);


        // Handle screen edges and collisions
        handleScreenEdge(player, tmap, elapsed);
        checkTileCollision(player, tmap);
    }

    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed) {


        float difference = s.getY() + s.getHeight() - tmap.getPixelHeight();
        if (difference > 0) {
            // Put the player back on the map according to how far over they were
            s.setVelocityY(0);  // Stop vertical movement
            s.setY(tmap.getPixelHeight() - s.getHeight()); // Keep player on ground

        }
    }


    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     *
     * @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); // Cmd on Mac, Ctrl on Win/Linux


        if (showStartScreen && e.getKeyCode() == KeyEvent.VK_ENTER) {
            showStartScreen = false;
            initialiseGame();
            return;
        }

        // Jump (Up or Down). Hold Cmd/Ctrl at the same time for super-jump.
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            jump = true;
            if ((e.getModifiersEx() & shortcut) != 0) {
                superJump = true;
            }
        }

        // Handle crawling
        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) &&
                (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
            isCrawling = true;
        }

        // Handle Shooting bullets with Spacebar
        if (key == KeyEvent.VK_SPACE && ammo > 0) {
            int bulletDirection = player.getScaleX() > 0 ? 1 : -1; // 1: right, -1: left
            float bulletX = player.getX() + player.getWidth() / 2;
            float bulletY = player.getY() + player.getHeight() / 2;

            bullets.add(new Bullet(bulletX, bulletY, bulletDirection));

            Sound gunshot = new Sound("sounds/Gun Shot Sound1.wav");
            gunshot.start();


            ammo--; // Decrease ammo by 1
        }


        switch (key) {
            case KeyEvent.VK_RIGHT:
                moveRight = true;
                break;
            case KeyEvent.VK_LEFT:
                moveLeft = true;
                break;
            case KeyEvent.VK_S:
                Sound s = new Sound("sounds/caw.wav");
                s.start();
                break;
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_B:
                debug = !debug;
                break;
            case KeyEvent.VK_L: //key binding to skip to Level 2
                if (currentLevel == 1) {
                    currentLevel = 2;
                    loadLevel2();
                }
                break;
            default:
                break;
        }
    }

    public boolean boundingBoxCollision(Sprite s1, Sprite s2) {
        Rectangle r1 = s1.getBounds();
        Rectangle r2 = s2.getBounds();
        return r1.intersects(r2);
    }


    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     *
     * @param s    The Sprite to check collisions for
     * @param tmap The tile map to check
     */

    public void checkTileCollision(Sprite s, TileMap tmap) {
        collidedTiles.clear();

        float sx = s.getX();
        float sy = s.getY();
        float sw = s.getWidth();
        float sh = isCrawling ? s.getHeight() / 2 : s.getHeight();

        float vx = s.getVelocityX();
        float vy = s.getVelocityY();

        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        onGround = false;

        if (vy > 0) { // falling
            int ytile = (int) ((sy + sh) / tileHeight);
            int xtile1 = (int) (sx / tileWidth);
            int xtile2 = (int) ((sx + sw - 1) / tileWidth);

            for (int xt = xtile1; xt <= xtile2; xt++) {
                Tile t = tmap.getTile(xt, ytile);
                if (t != null && t.getCharacter() != '.') {
                    s.setVelocityY(0);
                    s.setY(ytile * tileHeight - sh);
                    collidedTiles.add(t);
                    onGround = true;
                    break;
                }
            }
        } else if (vy < 0) { // jumping upward
            int ytile = (int) (sy / tileHeight);
            int xtile1 = (int) (sx / tileWidth);
            int xtile2 = (int) ((sx + sw - 1) / tileWidth);

            for (int xt = xtile1; xt <= xtile2; xt++) {
                Tile t = tmap.getTile(xt, ytile);
                if (t != null && t.getCharacter() != '.') {
                    s.setVelocityY(0);
                    s.setY((ytile + 1) * tileHeight);
                    collidedTiles.add(t);
                    break;
                }
            }
        }

        // moving right
        if (vx > 0) {
            int xtile = (int) ((sx + sw) / tileWidth);
            int ytile1 = (int) (sy / tileHeight);
            int ytile2 = (int) ((sy + sh - 1) / tileHeight);

            for (int yt = ytile1; yt <= ytile2; yt++) {
                Tile t = tmap.getTile(xtile, yt);
                if (t != null && t.getCharacter() != '.') {
                    s.setVelocityX(0);
                    s.setX(xtile * tileWidth - sw);
                    collidedTiles.add(t);
                    break;
                }
            }
            // moving left
        } else if (vx < 0) {
            int xtile = (int) (sx / tileWidth);
            int ytile1 = (int) (sy / tileHeight);
            int ytile2 = (int) ((sy + sh - 1) / tileHeight);

            for (int yt = ytile1; yt <= ytile2; yt++) {
                Tile t = tmap.getTile(xtile, yt);
                if (t != null && t.getCharacter() != '.') {
                    s.setVelocityX(0);
                    s.setX((xtile + 1) * tileWidth);
                    collidedTiles.add(t);
                    break;
                }
            }
        }
    }


    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SHIFT) {
            isCrawling = false;
        }

        switch (key) {
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_UP:
                jump = false;
                superJump = false;
                break;
            case KeyEvent.VK_RIGHT:
                moveRight = false;
                break;
            case KeyEvent.VK_LEFT:
                moveLeft = false;
                break;
            default:
                break;
        }
    }
    private void drawStartScreen(Graphics2D g) {
        // Fill the background black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());


        // Draw new jungle background image if available
        if (startScreenBackground != null) {
            g.drawImage(startScreenBackground, 0, 0, getWidth(), getHeight(), null);
        }


        // Start Button
        g.setColor(new Color(0, 102, 204)); // Blue button
        g.fillRect(startButtonBounds.x, startButtonBounds.y, startButtonBounds.width, startButtonBounds.height);

        // Button Label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("START", startButtonBounds.x + 55, startButtonBounds.y + 40);
    }

    public void mousePressed(java.awt.event.MouseEvent e) {
        if (showStartScreen) {
            int mx = e.getX();
            int my = e.getY();

            if (startButtonBounds.contains(mx, my)) {
                showStartScreen = false;
                initialiseGame();
            }
        }

    }
    private void handleVillainCollisionWithPlayer(Sprite player, Sprite villain) {
        if (boundingBoxCollision(player, villain)) {
            float px = player.getX();
            float pw = player.getWidth();
            float pvx = player.getVelocityX();

            float vx = villain.getX();
            float vw = villain.getWidth();
            float vvx = villain.getVelocityX();

            // Calculate overlap
            float overlapLeft = (px + pw) - vx;
            float overlapRight = (vx + vw) - px;

            // collision based on direction
            if (pvx > 0) {
                // Player pushing into villain from left
                player.setX(vx - pw);
                player.setVelocityX(0);
            } else if (pvx < 0) {
                // Player pushing into villain from right
                player.setX(vx + vw);
                player.setVelocityX(0);
            } else if (vvx > 0 && overlapLeft > 0) {
                // Villain pushing into stationary player from right
                villain.setX(px - vw);
                villain.setVelocityX(0);
            } else if (vvx < 0 && overlapRight > 0) {
                // Villain pushing into stationary player from left
                villain.setX(px + pw);
                villain.setVelocityX(0);
            }
        }
    }

}