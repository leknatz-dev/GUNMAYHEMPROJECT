package io.github.leknatz.input;

public interface PlayerInputController {
    boolean isMoveLeftPressed();
    boolean isMoveRightPressed();
    boolean isJumpJustPressed();
    boolean isDropDownPressed();
    boolean isShootJustPressed();
    boolean isPunchJustPressed();
    boolean isReloadJustPressed();
}