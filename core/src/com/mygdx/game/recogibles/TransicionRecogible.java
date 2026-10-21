package com.mygdx.game.recogibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class TransicionRecogible implements Disposable {
    private final Rectangle area;
    private final Texture textura;
    private final String destino;
    private final float velocidad;

    public TransicionRecogible(Texture textura,float x,float y,float ancho,float alto,float velocidad,String destino) {
        this.textura = textura;
        this.area = new Rectangle(x,y,ancho,alto);
        this.velocidad = velocidad;
        this.destino = destino;
    }

    public void actualizar(float delta) {
        area.y -= velocidad * delta;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura,area.x,area.y,area.width,area.height);
    }

    public Rectangle getArea() {
        return area;
    }

    public String getDestino() {
        return destino;
    }

    public boolean estaFueraPantalla() {
        return area.y + area.height < 0;
    }

    @Override
    public void dispose() {
        textura.dispose();
    }
}