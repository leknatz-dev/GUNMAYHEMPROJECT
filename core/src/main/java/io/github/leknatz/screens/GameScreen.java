package io.github.leknatz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.leknatz.AudioManager;
import io.github.leknatz.animation.CharacterAnimator;
import io.github.leknatz.config.GameConstants;
import io.github.leknatz.entities.Bullet;
import io.github.leknatz.entities.Player;
import io.github.leknatz.input.KeyboardP1Controller;
import io.github.leknatz.input.KeyboardP2Controller;
import io.github.leknatz.input.PlayerInputController;
import io.github.leknatz.map.ArenaMapManager;
import io.github.leknatz.map.ParallaxBackground;
import io.github.leknatz.map.ParallaxLayer;
import io.github.leknatz.physics.CollisionEngine;
import io.github.leknatz.ui.HitMarker;
import io.github.leknatz.ui.OffscreenIndicator;
import io.github.leknatz.weapons.Pistol;
import io.github.leknatz.weapons.Weapon;

public class GameScreen extends ScreenAdapter {
    private ParallaxBackground parallaxBackground;
    
    // Gameplay Camera & Viewport
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;

    // Fixed UI Camera & Viewport
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private ArenaMapManager mapManager;
    private CharacterAnimator animator;

    private Player player1;
    private Player player2;
    private PlayerInputController p1Controller;
    private PlayerInputController p2Controller;

    private Weapon defaultWeapon;
    private Array<Bullet> bullets;

    // Hitmark FX Systems
    private Array<Texture> hitmarkTextures;
    private Array<TextureRegion> hitmarkRegions;
    private Array<HitMarker> hitMarkers;

    // Offscreen Indicator System
    private Texture arrowTexture;
    private OffscreenIndicator offscreenIndicator;

    private float stateTime = 0f;
    private float matchTimer = GameConstants.DEFAULT_MATCH_TIME;

    // Audio Manager Instance
    private AudioManager audioManager;

    @Override
    public void show() {
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT, gameCamera);
        gameViewport.apply();
        gameCamera.position.set(GameConstants.VIRTUAL_WIDTH / 2f, GameConstants.VIRTUAL_HEIGHT / 2f, 0);

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT, hudCamera);
        hudViewport.apply();
        hudCamera.position.set(GameConstants.VIRTUAL_WIDTH / 2f, GameConstants.VIRTUAL_HEIGHT / 2f, 0);
        hudCamera.update();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);

        // --- Audio System Setup ---
        audioManager = AudioManager.getInstance();
        loadAudioAssets();
        audioManager.playMusic("stage_theme", true);

        parallaxBackground = new ParallaxBackground();
        parallaxBackground.addLayer(new ParallaxLayer(new Texture("1.png"), 0.15f));
        parallaxBackground.addLayer(new ParallaxLayer(new Texture("2.png"), 0.35f));
        parallaxBackground.addLayer(new ParallaxLayer(new Texture("3.png"), 0.60f));
        parallaxBackground.addLayer(new ParallaxLayer(new Texture("4.png"), 0.85f));

        mapManager = new ArenaMapManager();
        mapManager.loadMap("TILEMAP.tmx", batch);

        animator = new CharacterAnimator();
        defaultWeapon = new Pistol();
        bullets = new Array<>();

        hitmarkTextures = new Array<>();
        hitmarkRegions = new Array<>();
        
        String[] assetNames = {"hitmark.png", "hitmark2.png", "hitmark3.png"};
        for (String name : assetNames) {
            Texture tex = new Texture(Gdx.files.internal(name));
            hitmarkTextures.add(tex);
            hitmarkRegions.add(new TextureRegion(tex));
        }

        hitMarkers = new Array<>();

        arrowTexture = new Texture(Gdx.files.internal("arrow.png"));
        offscreenIndicator = new OffscreenIndicator(new TextureRegion(arrowTexture));

        p1Controller = new KeyboardP1Controller();
        p2Controller = new KeyboardP2Controller();

        // Spawn initially offscreen at the top
        float topY = GameConstants.VIRTUAL_HEIGHT + 60f;
        player1 = new Player(1, GameConstants.VIRTUAL_WIDTH * 0.35f, topY, 1);
        player2 = new Player(2, GameConstants.VIRTUAL_WIDTH * 0.65f, topY, -1);

        resetMatch();
    }

    private void loadAudioAssets() {
        // Load Sound Effects (ensure these exist in your assets folder)
        audioManager.loadSound("shoot", "shoot.wav");
        audioManager.loadSound("punch", "punch.wav");
        audioManager.loadSound("reload", "reload.wav");
        audioManager.loadSound("hit", "hit.wav");
        audioManager.loadSound("jump", "jump.wav");
        audioManager.loadSound("death", "death.wav");

        // Load Background Music
        audioManager.loadMusic("stage_theme", "stage_theme.mp3");
    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        matchTimer -= delta;
        if (matchTimer <= 0) {
            resetMatch();
        }

        player1.updateTimers(delta);
        player2.updateTimers(delta);

        handlePlayerInput(player1, p1Controller);
        handlePlayerInput(player2, p2Controller);

        applyPhysics(player1, delta);
        applyPhysics(player2, delta);

        player1.setGrounded(CollisionEngine.checkPlatformCollisions(player1, delta, mapManager.getPlatforms()));
        if (player1.isGrounded()) {
            player1.getVelocity().y = 0;
            player1.setIgnoredPlatform(null);
        }

        player2.setGrounded(CollisionEngine.checkPlatformCollisions(player2, delta, mapManager.getPlatforms()));
        if (player2.isGrounded()) {
            player2.getVelocity().y = 0;
            player2.setIgnoredPlatform(null);
        }

        int p1HitCountBefore = hitMarkers.size;
        CollisionEngine.checkPunchCollision(player1, player2, animator.getPunchAnimation(), hitMarkers, hitmarkRegions);
        CollisionEngine.checkPunchCollision(player2, player1, animator.getPunchAnimation(), hitMarkers, hitmarkRegions);

        int bulletsBefore = bullets.size;
        CollisionEngine.updateBulletsAndCollisions(bullets, mapManager.getPlatforms(), player1, player2, delta, hitMarkers, hitmarkRegions);

        // Play hit sound effect if a punch or bullet created new hitmarkers
        if (hitMarkers.size > p1HitCountBefore) {
            audioManager.playSoundWithPitchVar("hit", 0.1f);
        }

        for (int i = hitMarkers.size - 1; i >= 0; i--) {
            HitMarker hm = hitMarkers.get(i);
            hm.update(delta);
            if (hm.isExpired()) {
                hitMarkers.removeIndex(i);
            }
        }

        checkDeathAndRespawn(player1, player2);
        checkDeathAndRespawn(player2, player1);

        updateCamera(delta);

        draw();
    }

    private void updateCamera(float delta) {
        float midX = (player1.getX() + player2.getX()) / 2f;
        gameCamera.position.x += (midX - gameCamera.position.x) * 3f * delta;
        gameCamera.position.x = MathUtils.clamp(gameCamera.position.x, 300f, GameConstants.VIRTUAL_WIDTH - 300f);
        gameCamera.position.y = GameConstants.VIRTUAL_HEIGHT / 2f;
        
        gameCamera.update();
    }

    private void handlePlayerInput(Player player, PlayerInputController controller) {
        float moveX = 0f;
        if (controller.isMoveLeftPressed()) {
            moveX = -GameConstants.MOVE_SPEED;
            player.setFacing(-1);
        }
        if (controller.isMoveRightPressed()) {
            moveX = GameConstants.MOVE_SPEED;
            player.setFacing(1);
        }

        if (controller.isJumpJustPressed() && player.isGrounded()) {
            player.getVelocity().y = GameConstants.JUMP_FORCE;
            player.setGrounded(false);
            audioManager.playSoundWithPitchVar("jump", 0.05f);
        }

        if (controller.isDropDownPressed() && player.isGrounded()) {
            Rectangle currentPlatform = CollisionEngine.getCurrentPlatform(player, mapManager.getPlatforms());
            if (currentPlatform != null) {
                player.setIgnoredPlatform(currentPlatform);
                player.setGrounded(false);
                player.getVelocity().y = -150f;
                player.setY(player.getY() - 4f);
            }
        }

        if (controller.isReloadJustPressed() && player.canReload()) {
            player.startReload();
            audioManager.playSound("reload");
        }

        if (controller.isShootJustPressed() && player.canShoot()) {
            bullets.addAll(defaultWeapon.fire(player));
            player.decrementAmmo();
            audioManager.playSoundWithPitchVar("shoot", 0.08f);

            if (player.getAmmo() == 0) {
                player.startReload();
                audioManager.playSound("reload");
            }
        }

        if (controller.isPunchJustPressed() && player.canPunch()) {
            player.startPunch();
            audioManager.playSoundWithPitchVar("punch", 0.1f);
        }

        player.getVelocity().x *= GameConstants.KNOCKBACK_DAMPING;
        player.setX(player.getX() + (moveX + player.getVelocity().x) * Gdx.graphics.getDeltaTime());
    }

    private void applyPhysics(Player player, float delta) {
        player.getVelocity().y += GameConstants.GRAVITY * delta;
        player.setY(player.getY() + player.getVelocity().y * delta);

        if (player.getIgnoredPlatform() != null &&
            player.getY() + player.getHeight() < player.getIgnoredPlatform().y + player.getIgnoredPlatform().height) {
            player.setIgnoredPlatform(null);
        }
    }

    private void checkDeathAndRespawn(Player player, Player opponent) {
        if (player.getY() < -50 || player.getHealth() <= 0) {
            opponent.incrementScore();
            audioManager.playSound("death");
            
            // Fixed top offscreen respawn position
            float spawnX = (player.getPlayerId() == 1) ? GameConstants.VIRTUAL_WIDTH * 0.35f : GameConstants.VIRTUAL_WIDTH * 0.65f;
            float topY = GameConstants.VIRTUAL_HEIGHT + 60f;
            
            player.respawn(spawnX, topY);
        }
    }

    private void resetMatch() {
        matchTimer = GameConstants.DEFAULT_MATCH_TIME;
        float topY = GameConstants.VIRTUAL_HEIGHT + 60f;
        
        player1.respawn(GameConstants.VIRTUAL_WIDTH * 0.35f, topY);
        player2.respawn(GameConstants.VIRTUAL_WIDTH * 0.65f, topY);
        
        bullets.clear();
        if (hitMarkers != null) hitMarkers.clear();
    }

    private void draw() {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        gameViewport.apply();

        batch.setProjectionMatrix(gameCamera.combined);
        parallaxBackground.render(
            batch, 
            gameCamera.position.x, 
            GameConstants.VIRTUAL_WIDTH, 
            GameConstants.VIRTUAL_HEIGHT
        );

        mapManager.render(gameCamera);

        shapeRenderer.setProjectionMatrix(gameCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 0.9f, 0.2f, 1f);
        for (Bullet b : bullets) {
            shapeRenderer.rect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        boolean p1IsMoving = p1Controller.isMoveLeftPressed() || p1Controller.isMoveRightPressed();
        boolean p2IsMoving = p2Controller.isMoveLeftPressed() || p2Controller.isMoveRightPressed();

        animator.drawPlayer(batch, player1, stateTime, p1IsMoving, 1.0f, 0.7f, 0.7f);
        animator.drawPlayer(batch, player2, stateTime, p2IsMoving, 0.7f, 0.85f, 1.0f);

        for (HitMarker hm : hitMarkers) {
            hm.draw(batch);
        }

        if (offscreenIndicator != null) {
            offscreenIndicator.draw(batch, gameCamera, player1);
            offscreenIndicator.draw(batch, gameCamera, player2);
        }

        batch.end();

        hudViewport.apply();

        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(20, 500, 200, 16);
        shapeRenderer.setColor(0.95f, 0.25f, 0.25f, 1f);
        shapeRenderer.rect(20, 500, 2.0f * Math.max(0, player1.getHealth()), 16);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(740, 500, 200, 16);
        shapeRenderer.setColor(0.25f, 0.65f, 0.95f, 1f);
        shapeRenderer.rect(740, 500, 2.0f * Math.max(0, player2.getHealth()), 16);

        shapeRenderer.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        font.draw(batch, "P1 Kills: " + player1.getScore() + " | Ammo: " + (player1.isReloading() ? "RELOADING" : player1.getAmmo()), 20, 530);
        font.draw(batch, "TIME: " + (int) matchTimer, 440, 530);
        font.draw(batch, "P2 Kills: " + player2.getScore() + " | Ammo: " + (player2.isReloading() ? "RELOADING" : player2.getAmmo()), 740, 530);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (parallaxBackground != null) parallaxBackground.dispose();
        if (mapManager != null) mapManager.dispose();
        if (animator != null) animator.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (arrowTexture != null) arrowTexture.dispose();
        if (hitmarkTextures != null) {
            for (Texture t : hitmarkTextures) {
                t.dispose();
            }
        }
        if (audioManager != null) audioManager.dispose();
    }
}