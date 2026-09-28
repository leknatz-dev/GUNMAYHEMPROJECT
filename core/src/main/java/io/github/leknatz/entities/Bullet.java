package io.github.leknatz.entities;

import io.github.leknatz.config.GameConstants;

public class Bullet extends Entity {
    private final int shooterId;
    private final float damage;
    private final float baseKnockbackX;
    private final float baseKnockbackY;

    public Bullet(float x, float y, float velocityX, int shooterId) {
        this(x, y, velocityX, shooterId, 10f, 350f, 200f);
    }

    public Bullet(float x, float y, float velocityX, int shooterId, float damage, float baseKnockbackX, float baseKnockbackY) {
        super(x, y, GameConstants.BULLET_WIDTH, GameConstants.BULLET_HEIGHT);
        this.velocity.x = velocityX;
        this.shooterId = shooterId;
        this.damage = damage;
        this.baseKnockbackX = baseKnockbackX;
        this.baseKnockbackY = baseKnockbackY;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (bounds.x < 0 || bounds.x > GameConstants.VIRTUAL_WIDTH) {
            setActive(false);
        }
    }

    public int getShooterId() {
        return shooterId;
    }

    public float getDamage() {
        return damage;
    }

    public float getBaseKnockbackX() {
        return baseKnockbackX;
    }

    public float getBaseKnockbackY() {
        return baseKnockbackY;
    }
}