package io.github.leknatz.weapons;

import com.badlogic.gdx.utils.Array;
import io.github.leknatz.entities.Bullet;
import io.github.leknatz.entities.Player;

public abstract class Weapon {
    protected String name;
    protected int maxAmmo;
    protected float reloadDuration;
    protected float bulletSpeed;
    protected float damage;
    protected float knockbackX;
    protected float knockbackY;

    public Weapon(String name, int maxAmmo, float reloadDuration, float bulletSpeed, float damage, float knockbackX, float knockbackY) {
        this.name = name;
        this.maxAmmo = maxAmmo;
        this.reloadDuration = reloadDuration;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.knockbackX = knockbackX;
        this.knockbackY = knockbackY;
    }

    /**
     * Spawns projectile(s) based on player orientation and position.
     */
    public abstract Array<Bullet> fire(Player shooter);

    // Getters
    public String getName() { return name; }
    public int getMaxAmmo() { return maxAmmo; }
    public float getReloadDuration() { return reloadDuration; }
    public float getBulletSpeed() { return bulletSpeed; }
    public float getDamage() { return damage; }
    public float getKnockbackX() { return knockbackX; }
    public float getKnockbackY() { return knockbackY; }
}