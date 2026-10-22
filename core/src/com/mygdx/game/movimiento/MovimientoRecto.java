package com.mygdx.game.movimiento;

import com.badlogic.gdx.math.Rectangle;

/** Cae en linea recta de arriba hacia abajo. */
public class MovimientoRecto implements EstrategiaMovimiento {
    @Override
    public void mover(Rectangle area, float velocidad, float delta) {
        area.y -= velocidad * delta;
    }
}