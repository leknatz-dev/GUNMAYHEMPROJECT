package io.github.leknatz.weapons;

import com.badlogic.gdx.utils.Array;
import io.github.leknatz.config.GameConstants;
import io.github.leknatz.entities.Bullet;
import io.github.leknatz.entities.Player;

public class Pistol extends Weapon {

    public Pistol() {
        super(
            "Standard Pistol",
            GameConstants.DEFAULT_MAX_AMMO,      // 6 rounds
            GameConstants.DEFAULT_RELOAD_DURATION, // 1.2s reload
            700f,                                 // Bullet velocity
            10f,                                  // Damage
            350f,                                 // Horizontal base knockback
            200f                                  // Vertical base knockback
        );
    }

    @Override
    public Array<Bullet> fire(Player shooter) {
        Array<Bullet> createdBullets = new Array<>();

        int facing = shooter.getFacing();
        float bulletX = (facing == 1) ? shooter.getX() + 22f : shooter.getX() + (32f - 22f) - 10f;
        float bulletY = shooter.getY() + 14f;

        Bullet bullet = new Bullet(
            bulletX,
            bulletY,
            facing * bulletSpeed,
            shooter.getPlayerId(),
            damage,
            knockbackX,
            knockbackY
        );

        createdBullets.add(bullet);
        return createdBullets;
    }
}