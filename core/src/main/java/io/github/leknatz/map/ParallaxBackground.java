package io.github.leknatz.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ParallaxBackground implements Disposable {
    private Array<ParallaxLayer> layers;

    public ParallaxBackground() {
        layers = new Array<>();
    }

    public void addLayer(ParallaxLayer layer) {
        layers.add(layer);
    }

    public void render(SpriteBatch batch, float cameraX, float viewportWidth, float viewportHeight) {
        batch.begin();
        for (ParallaxLayer layer : layers) {
            layer.render(batch, cameraX, viewportWidth, viewportHeight);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        for (ParallaxLayer layer : layers) {
            layer.dispose();
        }
    }
}