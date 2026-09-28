package io.github.leknatz.gunmayhem;

import com.badlogic.gdx.Game;
import io.github.leknatz.screens.GameScreen;

public class GunMayhemGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
