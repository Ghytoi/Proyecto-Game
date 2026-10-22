package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.escenarios.TipoEscenario;
import com.mygdx.game.movimiento.MovimientoRecto;

public class AutoBus extends EntidadMovil {
    public AutoBus(Texture textura,float x,float y,float ancho,float alto,float velocidad) {
        super(textura,x,y,ancho,alto,velocidad,new MovimientoRecto(),TipoEscenario.CIUDAD,TipoEscenario.TUNEL);
    }

    @Override
    protected float getMargenHitbox() {
        return 0.1f;
    }
}