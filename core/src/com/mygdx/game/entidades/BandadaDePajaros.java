package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.escenarios.TipoEscenario;
import com.mygdx.game.movimiento.MovimientoOndulante;

public class BandadaDePajaros extends EntidadMovil {
    public BandadaDePajaros(Texture textura,float x,float y,float ancho,float alto,float velocidad) {
        super(textura,x,y,ancho,alto,velocidad,new MovimientoOndulante(x),TipoEscenario.AEREO);
    }

    @Override
    protected float getMargenHitbox() {
        return 0.1f;
    }
}