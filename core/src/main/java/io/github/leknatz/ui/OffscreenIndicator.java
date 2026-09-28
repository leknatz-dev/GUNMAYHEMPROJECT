package io.github.leknatz.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import io.github.leknatz.entities.Player;

public class OffscreenIndicator {
    private TextureRegion arrowRegion;
    private float arrowWidth = 24f;
    private float arrowHeight = 24f;
    private float margin = 20f; // Distance from screen edge

    public OffscreenIndicator(TextureRegion arrowRegion) {
        this.arrowRegion = arrowRegion;
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera, Player player) {
        if (arrowRegion == null || player == null) return;

        // Calculate Camera bounds in world coordinates
        float camLeft = camera.position.x - (camera.viewportWidth / 2f);
        float camRight = camera.position.x + (camera.viewportWidth / 2f);
        float camBottom = camera.position.y - (camera.viewportHeight / 2f);
        float camTop = camera.position.y + (camera.viewportHeight / 2f);

        // Center position of the player
        float playerCenterX = player.getX() + (player.getWidth() / 2f);
        float playerCenterY = player.getY() + (player.getHeight() / 2f);

        // Check if player is outside the visible screen view
        boolean isOffscreen = playerCenterX < camLeft || playerCenterX > camRight ||
                              playerCenterY < camBottom || playerCenterY > camTop;

        if (!isOffscreen) return;

        // Clamp indicator position inside screen boundaries with a margin
        float indicatorX = MathUtils.clamp(playerCenterX, camLeft + margin, camRight - margin);
        float indicatorY = MathUtils.clamp(playerCenterY, camBottom + margin, camTop - margin);

        // Calculate angle from the clamped indicator position to the actual player position
        float angleRad = MathUtils.atan2(playerCenterY - indicatorY, playerCenterX - indicatorX);
        float angleDeg = angleRad * MathUtils.radiansToDegrees;

        // Draw arrow rotated toward the offscreen character
        float originX = arrowWidth / 2f;
        float originY = arrowHeight / 2f;

        batch.draw(
            arrowRegion,
            indicatorX - originX,
            indicatorY - originY,
            originX, originY,
            arrowWidth, arrowHeight,
            1.0f, 1.0f,
            angleDeg
        );
    }
}