package io.github.leknatz.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import io.github.leknatz.config.GameConstants;
import io.github.leknatz.entities.Player;

public class CharacterAnimator implements Disposable {
    private Texture walkGunSheet;
    private Animation<TextureRegion> walkGunAnimation;

    private Texture jumpGunSheet;
    private TextureRegion[] jumpGunFrames;

    private Texture walkReloadSheet;
    private Animation<TextureRegion> walkReloadAnimation;

    private Texture jumpReloadSheet;
    private TextureRegion[] jumpReloadFrames;

    private Texture punchSheet;
    private Animation<TextureRegion> punchAnimation;

    public CharacterAnimator() {
        loadAssets();
    }

    private void loadAssets() {
        // 1. Walking with Gun
        walkGunSheet = new Texture("WALKING WITH GUN.png");
        walkGunSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] tmpWalkGun = TextureRegion.split(walkGunSheet, 32, 32);
        TextureRegion[] walkGunFrames = new TextureRegion[10];
        System.arraycopy(tmpWalkGun[0], 0, walkGunFrames, 0, 10);
        walkGunAnimation = new Animation<>(0.08f, walkGunFrames);
        walkGunAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // 2. Jumping with Gun
        jumpGunSheet = new Texture("JUMPING WITH GUN.png");
        jumpGunSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] tmpJumpGun = TextureRegion.split(jumpGunSheet, 32, 32);
        jumpGunFrames = new TextureRegion[10];
        System.arraycopy(tmpJumpGun[0], 0, jumpGunFrames, 0, 10);

        // 3. Walking Reload
        walkReloadSheet = new Texture("WALKING RELOAD.png");
        walkReloadSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] tmpWalkReload = TextureRegion.split(walkReloadSheet, 32, 32);
        TextureRegion[] walkReloadFrames = new TextureRegion[10];
        System.arraycopy(tmpWalkReload[0], 0, walkReloadFrames, 0, 10);
        walkReloadAnimation = new Animation<>(0.08f, walkReloadFrames);
        walkReloadAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // 4. Jumping Reload
        jumpReloadSheet = new Texture("JUMPING RELOAD.png");
        jumpReloadSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] tmpJumpReload = TextureRegion.split(jumpReloadSheet, 32, 32);
        jumpReloadFrames = new TextureRegion[10];
        System.arraycopy(tmpJumpReload[0], 0, jumpReloadFrames, 0, 10);

        // 5. Punching
        punchSheet = new Texture("PUNCHING.png");
        punchSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] tmpPunch = TextureRegion.split(punchSheet, 32, 32);
        TextureRegion[] punchFrames = new TextureRegion[5];
        System.arraycopy(tmpPunch[0], 0, punchFrames, 0, 5);
        punchAnimation = new Animation<>(GameConstants.PUNCH_DURATION / 5f, punchFrames);
        punchAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void drawPlayer(SpriteBatch batch, Player player, float stateTime, boolean isMoving, float r, float g, float b) {
        if (player.isInvulnerable() && ((int)(player.getSpawnBufferTimer() * 10) % 2 == 0)) {
            batch.setColor(1f, 1f, 1f, 0.4f);
        } else {
            batch.setColor(r, g, b, 1f);
        }

        TextureRegion currentFrame;
        boolean isReloading = player.isReloading();

        if (player.isPunching()) {
            float elapsedTime = GameConstants.PUNCH_DURATION - player.getPunchTimer();
            currentFrame = punchAnimation.getKeyFrame(elapsedTime, false);
        } else if (!player.isGrounded()) {
            TextureRegion[] targetJumpFrames = isReloading ? jumpReloadFrames : jumpGunFrames;
            float velocityY = player.getVelocity().y;
            if (velocityY > 0) {
                float progress = MathUtils.clamp(1f - (velocityY / GameConstants.JUMP_FORCE), 0f, 1f);
                int frameIndex = Math.min((int)(progress * 10), 9);
                currentFrame = targetJumpFrames[frameIndex];
            } else {
                float fallProgress = MathUtils.clamp(Math.abs(velocityY) / 600f, 0f, 1f);
                int frameIndex = 9 - Math.min((int)(fallProgress * 10), 9);
                currentFrame = targetJumpFrames[frameIndex];
            }
        } else if (isMoving) {
            Animation<TextureRegion> targetWalkAnim = isReloading ? walkReloadAnimation : walkGunAnimation;
            currentFrame = targetWalkAnim.getKeyFrame(stateTime, true);
        } else {
            if (isReloading) {
                currentFrame = walkReloadAnimation.getKeyFrame(stateTime, true);
            } else {
                currentFrame = walkGunAnimation.getKeyFrames()[0];
            }
        }

        if ((player.getFacing() == -1 && !currentFrame.isFlipX()) || (player.getFacing() == 1 && currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }

        float renderWidth = 32f;
        float renderHeight = 32f;
        float offsetX = player.getX() - ((renderWidth - player.getWidth()) / 2f);

        batch.draw(currentFrame, offsetX, player.getY(), renderWidth, renderHeight);
    }

    public Animation<TextureRegion> getPunchAnimation() {
        return punchAnimation;
    }

    @Override
    public void dispose() {
        walkGunSheet.dispose();
        jumpGunSheet.dispose();
        walkReloadSheet.dispose();
        jumpReloadSheet.dispose();
        punchSheet.dispose();
    }
}