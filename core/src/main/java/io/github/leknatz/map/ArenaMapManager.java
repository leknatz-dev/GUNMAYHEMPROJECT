package io.github.leknatz.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class ArenaMapManager {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final Array<Rectangle> platforms;
    private final Array<float[]> spawnPoints;

    public ArenaMapManager() {
        platforms = new Array<>();
        spawnPoints = new Array<>();
    }

    public void loadMap(String mapPath, SpriteBatch batch) {
        try {
            map = new TmxMapLoader().load(mapPath);
            mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
            parseMapObjects();
        } catch (Exception e) {
            Gdx.app.error("TILEMAP", "Failed to load map: " + mapPath, e);
        }
    }

    private void parseMapObjects() {
        platforms.clear();
        spawnPoints.clear();

        if (map == null) return;

        // Tiled layer search matching PRD specs (Collision, collision, or platforms)
        MapLayer platformLayer = map.getLayers().get("Collision");
        if (platformLayer == null) platformLayer = map.getLayers().get("collision");
        if (platformLayer == null) platformLayer = map.getLayers().get("platforms");

        if (platformLayer != null) {
            for (MapObject object : platformLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    platforms.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
                    
                    // Default spawn point registration above platform
                    spawnPoints.add(new float[]{ rect.x + (rect.width / 2f) - 10f, rect.y + rect.height + 10f });
                }
            }
            Gdx.app.log("TILEMAP", "Loaded " + platforms.size + " platforms.");
        } else {
            Gdx.app.error("TILEMAP", "No valid platform layer found in Tiled map.");
        }

        if (spawnPoints.size == 0) {
            spawnPoints.add(new float[]{ 320f, 300f });
        }
    }

    public void render(OrthographicCamera camera) {
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
    }

    public float[] getRandomSpawnPoint() {
        if (spawnPoints.size > 0) {
            int index = MathUtils.random(0, spawnPoints.size - 1);
            return spawnPoints.get(index);
        }
        return new float[]{ 320f, 300f };
    }

    public Array<Rectangle> getPlatforms() {
        return platforms;
    }

    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}