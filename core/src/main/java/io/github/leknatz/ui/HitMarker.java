package io.github.leknatz.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import io.github.leknatz.entities.Player;

public class HitMarker {
    private TextureRegion region;
    private Player targetPlayer;
    private float offsetX, offsetY; // Relative offset from target player
    private float lifetime;
    private float maxLifetime;
    
    private float baseSize = 36f;
    private float scale;
    private float alpha;

    public HitMarker(TextureRegion region, Player targetPlayer, float offsetX, float offsetY) {
        this.region = region;
        this.targetPlayer = targetPlayer;
        
        // Small random jitter relative to offset so rapid hits don't stack perfectly
        this.offsetX = offsetX + MathUtils.random(-4f, 4f);
        this.offsetY = offsetY + MathUtils.random(-4f, 4f);
        
        this.maxLifetime = 1.3f;
        this.lifetime = maxLifetime;
        this.scale = 0.3f;
        this.alpha = 1.0f;
    }

    public void update(float delta) {
        lifetime -= delta;
        float progress = 1.0f - (lifetime / maxLifetime);

        // Pop-in scale curve (0.3x -> 1.25x -> 1.0x)
        if (progress < 0.15f) {
            scale = MathUtils.lerp(0.3f, 1.25f, progress / 0.15f);
        } else {
            scale = MathUtils.lerp(1.25f, 1.0f, Math.min(1.0f, (progress - 0.15f) / 0.2f));
        }

        // Float slightly upwards relative to player over time
        offsetY += 12f * delta;

        // Smooth fade out over final 0.4 seconds
        if (lifetime <= 0.4f) {
            alpha = lifetime / 0.4f;
        }
    }

    public void draw(SpriteBatch batch) {
        if (lifetime <= 0 || region == null || targetPlayer == null) return;

        // Dynamically compute absolute world position based on player's CURRENT location
        float currentX = targetPlayer.getX() + offsetX;
        float currentY = targetPlayer.getY() + offsetY;

        batch.setColor(1f, 1f, 1f, MathUtils.clamp(alpha, 0f, 1f));

        float origin = baseSize / 2f;
        batch.draw(
            region,
            currentX - origin, currentY - origin,
            origin, origin,
            baseSize, baseSize,
            scale, scale,
            0f
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }
}