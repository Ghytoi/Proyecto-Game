package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.escenarios.TipoEscenario;

public class BandadaDePajaros extends EntidadMovil {
    private final float xInicial;
    private float tiempo;
    private static final float AMPLITUD=50f;
    private static final float FRECUENCIA=2f;

    public BandadaDePajaros(Texture textura,float x,float y,float ancho,float alto,float velocidad) {
        super(textura,x,y,ancho,alto,velocidad,TipoEscenario.AEREO);
        xInicial=x;
    }

    @Override
    public void actualizar(float delta) {
        tiempo+=delta;
        obtenerAreaInterna().y-=getVelocidad()*delta;
        obtenerAreaInterna().x=xInicial+MathUtils.sin(tiempo*FRECUENCIA)*AMPLITUD;
    }
}