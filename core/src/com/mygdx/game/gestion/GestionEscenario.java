package com.mygdx.game.gestion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.escenarios.Escenario;

public class GestionEscenario {
    private Escenario escenario;

    public GestionEscenario(Escenario escenario) {
        this.escenario = escenario;
    }

    public void cambiarEscenario(Escenario nuevoEscenario) {
        if (escenario != null) {
            escenario.destruir();
        }

        escenario = nuevoEscenario;
    }

    public void actualizar(float delta) {
        if (escenario != null) {
            escenario.actualizar(delta);
        }
    }

    public void dibujar(SpriteBatch batch, float ancho, float alto) {
        if (escenario != null) {
            escenario.dibujar(batch, ancho, alto);
        }
    }

    public void destruir() {
        if (escenario != null) {
            escenario.destruir();
            escenario = null;
        }
    }
}