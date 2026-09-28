package io.github.leknatz.physics;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.leknatz.config.GameConstants;
import io.github.leknatz.entities.Bullet;
import io.github.leknatz.entities.Player;
import io.github.leknatz.ui.HitMarker;

public class CollisionEngine {

    public static Rectangle getCurrentPlatform(Player player, Array<Rectangle> platforms) {
        for (Rectangle p : platforms) {
            boolean xOverlap = player.getX() + player.getWidth() > p.x && player.getX() < p.x + p.width;
            if (xOverlap && Math.abs(player.getY() - (p.y + p.height)) < 2f) {
                return p;
            }
        }
        return null;
    }

    public static boolean checkPlatformCollisions(Player player, float deltaTime, Array<Rectangle> platforms) {
        boolean grounded = false;
        float velocityY = player.getVelocity().y;
        float prevY = player.getY() - (velocityY * deltaTime);

        for (Rectangle p : platforms) {
            if (p == player.getIgnoredPlatform()) continue;

            boolean xOverlap = player.getX() + player.getWidth() > p.x && player.getX() < p.x + p.width;

            if (xOverlap) {
                float platformTop = p.y + p.height;
                if (velocityY <= 0 && prevY >= platformTop - 2f && player.getY() <= platformTop) {
                    player.setY(platformTop);
                    grounded = true;
                }
            }
        }
        return grounded;
    }

    public static void checkPunchCollision(
        Player attacker,
        Player defender,
        Animation<TextureRegion> punchAnim,
        Array<HitMarker> hitMarkers,
        Array<TextureRegion> hitmarkRegions
    ) {
        if (!attacker.isPunching() || attacker.hasHitInCurrentPunch()) return;

        float elapsedTime = GameConstants.PUNCH_DURATION - attacker.getPunchTimer();
        int currentFrameIndex = punchAnim.getKeyFrameIndex(elapsedTime);

        if (currentFrameIndex == 2) {
            Rectangle punchBox = new Rectangle(
                attacker.getFacing() == 1 ? attacker.getX() + attacker.getWidth() : attacker.getX() - 20f,
                attacker.getY() + 5f,
                20f, 18f
            );

            if (punchBox.overlaps(defender.getBounds()) && !defender.isInvulnerable()) {
                defender.takeDamage(GameConstants.PUNCH_DAMAGE);
                
                Vector2 impulse = KnockbackCalculator.calculateKnockback(
                    defender,
                    GameConstants.PUNCH_KNOCKBACK_X,
                    GameConstants.PUNCH_KNOCKBACK_Y,
                    attacker.getFacing()
                );

                defender.getVelocity().x = impulse.x;
                defender.getVelocity().y = impulse.y;
                defender.setGrounded(false);
                attacker.setHasHitInCurrentPunch(true);

                // Spawn attached hitmark at the REAR of defender
                if (hitMarkers != null && hitmarkRegions != null && hitmarkRegions.size > 0) {
                    float relX = (attacker.getFacing() == 1) ? defender.getWidth() + 10f : -10f;
                    float relY = defender.getHeight() * 0.5f;
                    hitMarkers.add(new HitMarker(hitmarkRegions.random(), defender, relX, relY));
                }
            }
        }
    }

    public static void updateBulletsAndCollisions(
        Array<Bullet> bullets,
        Array<Rectangle> platforms,
        Player p1,
        Player p2,
        float deltaTime,
        Array<HitMarker> hitMarkers,
        Array<TextureRegion> hitmarkRegions
    ) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(deltaTime);

            if (!b.isActive()) {
                bullets.removeIndex(i);
                continue;
            }

            boolean hitPlatform = false;
            for (Rectangle p : platforms) {
                if (b.getBounds().overlaps(p)) {
                    hitPlatform = true;
                    break;
                }
            }
            if (hitPlatform) {
                bullets.removeIndex(i);
                continue;
            }

            // Bullet overlap check against P1
            if (b.getShooterId() != p1.getPlayerId() && b.getBounds().overlaps(p1.getBounds())) {
                if (!p1.isInvulnerable()) {
                    p1.takeDamage(b.getDamage());
                    int dir = (int) Math.signum(b.getVelocity().x);
                    Vector2 impulse = KnockbackCalculator.calculateKnockback(p1, b.getBaseKnockbackX(), b.getBaseKnockbackY(), dir);
                    p1.getVelocity().x = impulse.x;
                    p1.getVelocity().y = impulse.y;
                    p1.setGrounded(false);

                    // Spawn attached hitmark at P1's back
                    if (hitMarkers != null && hitmarkRegions != null && hitmarkRegions.size > 0) {
                        float relX = (dir > 0) ? p1.getWidth() + 10f : -10f;
                        float relY = p1.getHeight() * 0.5f;
                        hitMarkers.add(new HitMarker(hitmarkRegions.random(), p1, relX, relY));
                    }
                }
                bullets.removeIndex(i);
                continue;
            }

            // Bullet overlap check against P2
            if (b.getShooterId() != p2.getPlayerId() && b.getBounds().overlaps(p2.getBounds())) {
                if (!p2.isInvulnerable()) {
                    p2.takeDamage(b.getDamage());
                    int dir = (int) Math.signum(b.getVelocity().x);
                    Vector2 impulse = KnockbackCalculator.calculateKnockback(p2, b.getBaseKnockbackX(), b.getBaseKnockbackY(), dir);
                    p2.getVelocity().x = impulse.x;
                    p2.getVelocity().y = impulse.y;
                    p2.setGrounded(false);

                    // Spawn attached hitmark at P2's back
                    if (hitMarkers != null && hitmarkRegions != null && hitmarkRegions.size > 0) {
                        float relX = (dir > 0) ? p2.getWidth() + 10f : -10f;
                        float relY = p2.getHeight() * 0.5f;
                        hitMarkers.add(new HitMarker(hitmarkRegions.random(), p2, relX, relY));
                    }
                }
                bullets.removeIndex(i);
            }
        }
    }

    public static void keepInBounds(Player player) {
        if (player.getX() < 0) player.setX(0);
        if (player.getX() > GameConstants.VIRTUAL_WIDTH - player.getWidth()) {
            player.setX(GameConstants.VIRTUAL_WIDTH - player.getWidth());
        }
    }
}