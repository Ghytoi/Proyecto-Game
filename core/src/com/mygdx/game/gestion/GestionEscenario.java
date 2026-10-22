package com.mygdx.game.gestion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.escenarios.Escenario;
import com.mygdx.game.escenarios.EscenarioAereo;
import com.mygdx.game.escenarios.EscenarioTunelUrbano;
import com.mygdx.game.escenarios.EscenarioUrbanoCitadino;
import com.mygdx.game.escenarios.EscenarioViasTrenes;

// Las texturas de fondo las libera GestorRecursos, por eso ya no es Disposable.
public class GestionEscenario {
    private final EscenarioViasTrenes vias;
    private final EscenarioUrbanoCitadino ciudad;
    private final EscenarioTunelUrbano tunel;
    private final EscenarioAereo aereo;

    private Escenario actual;

    public GestionEscenario() {
        vias = new EscenarioViasTrenes();
        ciudad = new EscenarioUrbanoCitadino();
        tunel = new EscenarioTunelUrbano();
        aereo = new EscenarioAereo();
        actual = vias;
    }

    public void cambiarEscenario(String destino) {
        switch (destino) {
            case "VIAS":
                actual = vias;
                break;
            case "CIUDAD":
                actual = ciudad;
                break;
            case "TUNEL":
                actual = tunel;
                break;
            case "AEREO":
                actual = aereo;
                break;
            default:
                throw new IllegalArgumentException("Escenario desconocido: " + destino);
        }
    }

    public Escenario getEscenarioActual() {
        return actual;
    }

    public String getNombreEscenarioActual() {
        if (actual == vias) {
            return "VIAS";
        }
        if (actual == ciudad) {
            return "CIUDAD";
        }
        if (actual == tunel) {
            return "TUNEL";
        }
        if (actual == aereo) {
            return "AEREO";
        }
        return "";
    }

    public void actualizar(float delta) {
        // Los escenarios todavia no necesitan actualizar su propia logica.
    }

    public void dibujar(SpriteBatch batch, float ancho, float alto) {
        if (actual != null) {
            actual.render(batch, ancho, alto);
        }
    }
}