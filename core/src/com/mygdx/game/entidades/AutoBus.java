package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.escenarios.TipoEscenario;

public class AutoBus extends EntidadMovil {
    public AutoBus(Texture textura,float x,float y,float ancho,float alto,float velocidad) {
        super(textura,x,y,ancho,alto,velocidad,TipoEscenario.CIUDAD,TipoEscenario.TUNEL);
    }

    @Override
    public void actualizar(float delta) {
        obtenerAreaInterna().y-=getVelocidad()*delta;
    }
}