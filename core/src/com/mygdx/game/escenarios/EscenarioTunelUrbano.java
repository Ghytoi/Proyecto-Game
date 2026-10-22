package com.mygdx.game.escenarios;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EscenarioTunelUrbano extends Escenario {
    public EscenarioTunelUrbano() {
        super("tunel.jpg");
    }

    // Gancho del Template Method: el tunel se dibuja un poco mas oscuro.
    // Si no te gusta el efecto, borra estos dos metodos.
    @Override
    protected void prepararRender(SpriteBatch batch) {
        batch.setColor(0.7f, 0.7f, 0.8f, 1f);
    }

    @Override
    protected void finalizarRender(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public String getNombre() {
        return "Tunel urbano";
    }
}