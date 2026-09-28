package io.github.leknatz.entities;

import com.badlogic.gdx.math.Rectangle;
import io.github.leknatz.config.GameConstants;

public class Player extends Entity {
    private final int playerId;
    
    // Core combat state
    private float health;
    private int score;
    private int facing; // 1 for Right, -1 for Left
    private boolean grounded;
    private Rectangle ignoredPlatform;

    // Weapon & Ammo state
    private int ammo;
    private int maxAmmo;
    private float reloadTimer;

    // Abilities & Timers
    private float punchTimer;
    private boolean hasHitInCurrentPunch;
    private float spawnBufferTimer;

    public Player(int playerId, float spawnX, float spawnY, int initialFacing) {
        super(spawnX, spawnY, GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT);
        this.playerId = playerId;
        this.facing = initialFacing;
        this.maxAmmo = GameConstants.DEFAULT_MAX_AMMO;
        resetStats();
    }

    public void updateTimers(float deltaTime) {
        if (spawnBufferTimer > 0) {
            spawnBufferTimer -= deltaTime;
        }

        if (reloadTimer > 0) {
            reloadTimer -= deltaTime;
            if (reloadTimer <= 0) {
                ammo = maxAmmo;
            }
        }

        if (punchTimer > 0) {
            punchTimer -= deltaTime;
        }
    }

    public void resetStats() {
        this.health = 100f;
        this.ammo = maxAmmo;
        this.reloadTimer = 0f;
        this.punchTimer = 0f;
        this.hasHitInCurrentPunch = false;
        this.spawnBufferTimer = GameConstants.DEFAULT_SPAWN_BUFFER;
        this.grounded = false;
        this.ignoredPlatform = null;
        this.velocity.set(0, 0);
    }

    public void respawn(float spawnX, float spawnY) {
        setX(spawnX);
        setY(spawnY);
        resetStats();
    }

    public boolean canShoot() {
        return reloadTimer <= 0 && ammo > 0 && punchTimer <= 0;
    }

    public boolean canReload() {
        return reloadTimer <= 0 && ammo < maxAmmo && punchTimer <= 0;
    }

    public boolean canPunch() {
        return punchTimer <= 0;
    }

    public void startReload() {
        this.reloadTimer = GameConstants.DEFAULT_RELOAD_DURATION;
    }

    public void startPunch() {
        this.punchTimer = GameConstants.PUNCH_DURATION;
        this.hasHitInCurrentPunch = false;
    }

    public boolean isInvulnerable() {
        return spawnBufferTimer > 0;
    }

    public boolean isReloading() {
        return reloadTimer > 0;
    }

    public boolean isPunching() {
        return punchTimer > 0;
    }

    // Getters and Setters
    public int getPlayerId() { return playerId; }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }
    public void takeDamage(float amount) { this.health = Math.max(0f, this.health - amount); }
    public int getScore() { return score; }
    public void incrementScore() { this.score++; }
    public int getFacing() { return facing; }
    public void setFacing(int facing) { this.facing = facing; }
    public boolean isGrounded() { return grounded; }
    public void setGrounded(boolean grounded) { this.grounded = grounded; }
    public Rectangle getIgnoredPlatform() { return ignoredPlatform; }
    public void setIgnoredPlatform(Rectangle ignoredPlatform) { this.ignoredPlatform = ignoredPlatform; }
    public int getAmmo() { return ammo; }
    public void decrementAmmo() { this.ammo--; }
    public float getReloadTimer() { return reloadTimer; }
    public float getPunchTimer() { return punchTimer; }
    public boolean hasHitInCurrentPunch() { return hasHitInCurrentPunch; }
    public void setHasHitInCurrentPunch(boolean hit) { this.hasHitInCurrentPunch = hit; }
    public float getSpawnBufferTimer() { return spawnBufferTimer; }
}