package com.mygdx.game.movimiento;

import com.badlogic.gdx.math.Rectangle;

/** Patron Strategy: algoritmo de movimiento intercambiable para una entidad. */
public interface EstrategiaMovimiento {
    void mover(Rectangle area, float velocidad, float delta);
}