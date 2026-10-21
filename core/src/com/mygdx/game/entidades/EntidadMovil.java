package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EntidadMovil {
    protected Rectangle area;
    protected Texture textura;
    protected float velocidad;

    protected EntidadMovil(Texture textura, float x, float y, float ancho, float alto, float velocidad) {
        this.textura = textura;
        this.area = new Rectangle(x, y, ancho, alto);
        this.velocidad = velocidad;
    }

    public abstract void actualizar(float delta);

    public void dibujar(SpriteBatch batch) {
        if (textura != null) {
            batch.draw(textura, area.x, area.y, area.width, area.height);
        }
    }

    public Rectangle getArea() {
        return area;
    }

    public void destruir() {
        // La textura se administra externamente.
    }
}