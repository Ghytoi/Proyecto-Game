package com.mygdx.game.movimiento;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/** Cae y ademas oscila de lado a lado (onda senoidal) alrededor de su X inicial. */
public class MovimientoOndulante implements EstrategiaMovimiento {
    private static final float AMPLITUD = 50f;
    private static final float FRECUENCIA = 2f;

    private final float xInicial;
    private float tiempo;

    public MovimientoOndulante(float xInicial) {
        this.xInicial = xInicial;
    }

    @Override
    public void mover(Rectangle area, float velocidad, float delta) {
        tiempo += delta;
        area.y -= velocidad * delta;
        area.x = xInicial + MathUtils.sin(tiempo * FRECUENCIA) * AMPLITUD;
    }
}