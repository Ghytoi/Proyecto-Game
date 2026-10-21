package com.mygdx.game.escenarios;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EscenarioViasTrenes extends Escenario {
    public EscenarioViasTrenes() {
        super("vias.jpg");
    }

    @Override
    public void render(SpriteBatch batch, float ancho, float alto) {
        float anchoCarril = ancho / 3;

        batch.draw(getFondo(), 0, 0, anchoCarril, alto);
        batch.draw(getFondo(), anchoCarril, 0, anchoCarril, alto);
        batch.draw(getFondo(), anchoCarril * 2, 0, anchoCarril, alto);
    }
    
    @Override
    public String getNombre() {
        return "Vias de trenes";
    }
}