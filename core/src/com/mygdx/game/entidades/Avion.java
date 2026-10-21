package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Avion extends EntidadMovil {
    public Avion(Texture textura, float x, float y, float ancho, float alto, float velocidad) {
        super(textura, x, y, ancho, alto, velocidad);
    }

    @Override
    public void actualizar(float delta) {
        area.y -= velocidad * delta;
    }
}