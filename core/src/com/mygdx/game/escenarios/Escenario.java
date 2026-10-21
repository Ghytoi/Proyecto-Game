package com.mygdx.game.escenarios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class Escenario implements Disposable {
    private final Texture fondo;

    public Escenario(String rutaFondo) {
        fondo = new Texture(Gdx.files.internal(rutaFondo));
    }

    public void render(SpriteBatch batch, float ancho, float alto) {
        batch.draw(fondo, 0, 0, ancho, alto);
    }

    public abstract String getNombre();

    protected Texture getFondo() {
        return fondo;
    }
    
    @Override
    public void dispose() {
        fondo.dispose();
    }
}