package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.escenarios.TipoEscenario;

public class Avion extends EntidadMovil {
    public Avion(Texture textura,float x,float y,float ancho,float alto,float velocidad) {
        super(textura,x,y,ancho,alto,velocidad,TipoEscenario.AEREO);
    }

    @Override
    public void actualizar(float delta) {
        obtenerAreaInterna().y-=getVelocidad()*delta;
    }
}