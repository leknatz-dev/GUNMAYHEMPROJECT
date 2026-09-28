package io.github.leknatz.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ParallaxLayer {
    private Texture texture;
    private float scrollRatioX;

    public ParallaxLayer(Texture texture, float scrollRatioX) {
        this.texture = texture;
        this.scrollRatioX = scrollRatioX;
    }

    public void render(SpriteBatch batch, float cameraX, float worldWidth, float worldHeight) {
        // Set render height to match world height exactly (or add a small symmetrical padding)
        float paddingY = 0f; // Increase slightly (e.g. 20f) if you ever see sub-pixel seam lines
        float renderHeight = worldHeight + (paddingY * 2f);
        
        // Calculate aspect width based on render height
        float imageAspect = (float) texture.getWidth() / texture.getHeight();
        float renderWidth = renderHeight * imageAspect;

        if (renderWidth < worldWidth) {
            renderWidth = worldWidth;
        }

        // Horizontal Parallax Offset relative to camera center
        float offsetX = (cameraX * scrollRatioX) % renderWidth;
        if (offsetX < 0) offsetX += renderWidth;

        // Draw starting from camera left boundary
        float startX = (cameraX - worldWidth / 2f) - offsetX - renderWidth;
        float endX = (cameraX + worldWidth / 2f) + renderWidth;
        
        // Symmetrically center the vertical position relative to the world height (270px)
        float startY = (worldHeight - renderHeight) / 2f;

        float currentX = startX;
        while (currentX < endX) {
            batch.draw(texture, currentX, startY, renderWidth, renderHeight);
            currentX += renderWidth;
        }
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}