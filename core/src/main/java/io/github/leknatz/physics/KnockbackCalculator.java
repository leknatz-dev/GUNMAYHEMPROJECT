package io.github.leknatz.physics;

import com.badlogic.gdx.math.Vector2;
import io.github.leknatz.entities.Player;

public class KnockbackCalculator {

    /**
     * Calculates effective impulse force vector based on health:
     * Effective Knockback = Base Knockback * (1 + (100 - Health) / 100)
     */
    public static Vector2 calculateKnockback(Player target, float baseForceX, float baseForceY, int direction) {
        float healthDeficit = Math.max(0f, 100f - target.getHealth());
        float multiplier = 1.0f + (healthDeficit / 100.0f);

        float finalX = direction * baseForceX * multiplier;
        float finalY = baseForceY * multiplier;

        return new Vector2(finalX, finalY);
    }
}