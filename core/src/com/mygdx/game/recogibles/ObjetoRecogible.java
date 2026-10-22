package com.mygdx.game.recogibles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.jugador.Player;

public interface ObjetoRecogible {
    void actualizar(float delta);
    void actualizar(float delta,Player player);
    void dibujar(SpriteBatch batch);
    Rectangle getArea();
    void recoger(Player player);
    boolean estaFueraPantalla();
    void destruir();
}