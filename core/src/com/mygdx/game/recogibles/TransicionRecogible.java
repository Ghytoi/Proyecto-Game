package com.mygdx.game.recogibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

// La textura es compartida (la administra GestorRecursos), por eso ya no es Disposable.
public class TransicionRecogible {
    private static final float DURACION_FRAME = 0.08f; // ~12.5 FPS de animacion

    private final Rectangle area;
    private final Texture textura;
    private final TextureRegion[] frames;
    private float tiempoAnimacion;
    private final String destino;
    private final float velocidad;

    public TransicionRecogible(Texture textura,float x,float y,float ancho,float alto,float velocidad,String destino) {
        this.textura = textura;
        this.area = new Rectangle(x,y,ancho,alto);
        this.velocidad = velocidad;
        this.destino = destino;

        if (textura != null && textura.getWidth() > textura.getHeight()) {
            int altoFrame = textura.getHeight();
            int columnas = textura.getWidth() / altoFrame;
            TextureRegion[][] tmp = TextureRegion.split(textura, altoFrame, altoFrame);
            frames = new TextureRegion[columnas];
            for (int i = 0; i < columnas; i++) {
                frames[i] = tmp[0][i];
            }
        } else {
            frames = null;
        }
    }

    public void actualizar(float delta) {
        area.y -= velocidad * delta;
        tiempoAnimacion += delta;
    }

    public void dibujar(SpriteBatch batch) {
        if (frames != null && frames.length > 0) {
            int frameActual = (int) (tiempoAnimacion / DURACION_FRAME) % frames.length;
            batch.draw(frames[frameActual], area.x, area.y, area.width, area.height);
        } else {
            batch.draw(textura, area.x, area.y, area.width, area.height);
        }
    }

    public Rectangle getArea() {
        return new Rectangle(area);
    }

    public String getDestino() {
        return destino;
    }

    public boolean estaFueraPantalla() {
        return area.y + area.height < 0;
    }
}