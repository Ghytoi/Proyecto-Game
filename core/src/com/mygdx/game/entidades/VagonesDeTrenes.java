package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.escenarios.TipoEscenario;

public class VagonesDeTrenes extends EntidadMovil {
    public VagonesDeTrenes(Texture textura,float x,float y,float ancho,float alto,float velocidad) {
        super(textura,x,y,ancho,alto,velocidad,TipoEscenario.VIAS);
    }

    @Override
    public void actualizar(float delta) {
        obtenerAreaInterna().y-=getVelocidad()*delta;
    }
}