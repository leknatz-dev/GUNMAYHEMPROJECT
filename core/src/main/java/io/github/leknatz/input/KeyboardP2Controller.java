package io.github.leknatz.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardP2Controller implements PlayerInputController {

    @Override
    public boolean isMoveLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    @Override
    public boolean isMoveRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    @Override
    public boolean isJumpJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.UP);
    }

    @Override
    public boolean isDropDownPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    @Override
    public boolean isShootJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.M);
    }

    @Override
    public boolean isPunchJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.O);
    }

    @Override
    public boolean isReloadJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.L);
    }
}