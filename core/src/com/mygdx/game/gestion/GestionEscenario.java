package com.mygdx.game.gestion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.escenarios.Escenario;
import com.mygdx.game.escenarios.EscenarioAereo;
import com.mygdx.game.escenarios.EscenarioTunelUrbano;
import com.mygdx.game.escenarios.EscenarioViasTrenes;
import com.mygdx.game.escenarios.EscenarioUrbanoCitadino;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestionEscenario implements Disposable {
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

    public void render(SpriteBatch batch, float ancho, float alto) {
        actual.render(batch, ancho, alto);
    }

    public Escenario getEscenarioActual() {
        return actual;
    }
    
    public void actualizar(float delta) {
        // Actualiza la lógica del escenario activo.
        // Por ahora, los escenarios no tienen lógica de actualización propia.
    }

    public void dibujar(SpriteBatch batch, float ancho, float alto) {
        if (actual != null) {
            actual.render(batch, ancho, alto);
        }
    }
    
    public String getNombreEscenarioActual() {
        if (actual == vias) {
            return "VIAS";
        } else if (actual == ciudad) {
            return "CIUDAD";
        } else if (actual == tunel) {
            return "TUNEL";
        } else if (actual == aereo) {
            return "AEREO";
        }

        return "";
    }

    @Override
    public void dispose() {
        vias.dispose();
        ciudad.dispose();
        tunel.dispose();
        aereo.dispose();
    }
}