package io.github.leknatz.config;

public final class GameConstants {
    private GameConstants() {}

    // Screen & World Dimensions
    public static final float VIRTUAL_WIDTH = 960f;
    public static final float VIRTUAL_HEIGHT = 540f;

    // Physics Constants
    public static final float GRAVITY = -1000f;
    public static final float JUMP_FORCE = 350f;
    public static final float MOVE_SPEED = 200f;
    public static final float KNOCKBACK_DAMPING = 0.85f;

    // Gameplay Defaults
    public static final float DEFAULT_MATCH_TIME = 60.0f;
    public static final float DEFAULT_SPAWN_BUFFER = 1.0f;
    public static final float DEFAULT_RELOAD_DURATION = 1.2f;
    public static final int DEFAULT_MAX_AMMO = 6;

    // Melee Parameters
    public static final float PUNCH_DURATION = 0.20f;
    public static final float PUNCH_DAMAGE = 20f;
    public static final float PUNCH_KNOCKBACK_X = 600f;
    public static final float PUNCH_KNOCKBACK_Y = 320f;

    // Entity Sizes
    public static final float PLAYER_WIDTH = 20f;
    public static final float PLAYER_HEIGHT = 30f;
    public static final float BULLET_WIDTH = 10f;
    public static final float BULLET_HEIGHT = 5f;
}