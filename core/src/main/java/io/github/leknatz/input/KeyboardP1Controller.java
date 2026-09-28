package io.github.leknatz.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardP1Controller implements PlayerInputController {

    @Override
    public boolean isMoveLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.A);
    }

    @Override
    public boolean isMoveRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.D);
    }

    @Override
    public boolean isJumpJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }

    @Override
    public boolean isDropDownPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.S);
    }

    @Override
    public boolean isShootJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.F);
    }

    @Override
    public boolean isPunchJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E);
    }

    @Override
    public boolean isReloadJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.R);
    }
}