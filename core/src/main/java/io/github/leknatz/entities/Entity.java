package io.github.leknatz.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Rectangle bounds;
    protected Vector2 velocity;
    protected boolean active;

    public Entity(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.velocity = new Vector2(0, 0);
        this.active = true;
    }

    public void update(float deltaTime) {
        bounds.x += velocity.x * deltaTime;
        bounds.y += velocity.y * deltaTime;
    }

    // Getters and Setters
    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getX() {
        return bounds.x;
    }

    public void setX(float x) {
        this.bounds.x = x;
    }

    public float getY() {
        return bounds.y;
    }

    public void setY(float y) {
        this.bounds.y = y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}