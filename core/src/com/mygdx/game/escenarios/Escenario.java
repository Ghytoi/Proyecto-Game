package com.mygdx.game.escenarios;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Escenario {
    protected Texture textura;

    protected Escenario(Texture textura) {
        this.textura = textura;
    }

    public abstract void actualizar(float delta);

    public void dibujar(SpriteBatch batch, float ancho, float alto) {
        if (textura != null) {
            batch.draw(textura, 0, 0, ancho, alto);
        }
    }

    public void destruir() {
        // La textura se administra externamente.
    }
}