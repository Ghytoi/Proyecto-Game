package com.mygdx.game.recogibles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.jugador.Player;

public class Monedas implements ObjetoRecogible {
    private static final float DURACION_FRAME = 0.1f;

    private final Rectangle area;
    private final Texture textura;
    private final TextureRegion[] frames;
    private float tiempoAnimacion;
    private final Sound sonido;
    private final boolean mala;
    private final float velocidad;

    // La moneda mala reutiliza la misma textura, pero se dibuja tenida de rojo.
    public Monedas(Texture textura, Sound sonido, boolean mala, float x, float y, float velocidad) {
        this.textura = textura;
        this.sonido = sonido;
        this.mala = mala;
        this.velocidad = velocidad;
        this.area = new Rectangle(x, y, 64, 64);
        this.tiempoAnimacion = (float) (Math.random() * 1.0);

        if (textura != null && textura.getWidth() > textura.getHeight()) {
            int alto = textura.getHeight();
            int columnas = textura.getWidth() / alto;
            TextureRegion[][] tmp = TextureRegion.split(textura, alto, alto);
            this.frames = new TextureRegion[columnas];
            for (int i = 0; i < columnas; i++) {
                this.frames[i] = tmp[0][i];
            }
        } else if (textura != null) {
            this.frames = new TextureRegion[] { new TextureRegion(textura) };
        } else {
            this.frames = null;
        }
    }

    @Override
    public void actualizar(float delta) {
        area.y -= velocidad * delta;
        tiempoAnimacion += delta;
    }

    @Override
    public void actualizar(float delta, Player player) {
        actualizar(delta);

        if (!mala && player.estaAtrayendoMonedas() && player.atraeMonedas(area.x, area.y)) {
            Rectangle jugador = player.getArea();

            float dx = jugador.x + jugador.width / 2f - (area.x + area.width / 2f);
            float dy = jugador.y + jugador.height / 2f - (area.y + area.height / 2f);
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia > 0) {
                float atraccion = 700f * delta;
                area.x += dx / distancia * atraccion;
                area.y += dy / distancia * atraccion;
            }
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        if (textura == null) return;

        if (mala) {
            batch.setColor(1f, 0.25f, 0.25f, 1f);
        }

        if (frames != null && frames.length > 0) {
            int frameActual = (int) (tiempoAnimacion / DURACION_FRAME) % frames.length;
            batch.draw(frames[frameActual], area.x, area.y, area.width, area.height);
        } else {
            batch.draw(textura, area.x, area.y, area.width, area.height);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public Rectangle getArea() {
        return new Rectangle(area);
    }

    @Override
    public void recoger(Player player) {
        if (mala) {
            player.danar();
        } else {
            player.sumarMoneda();
            player.sumarPuntos(3); // Cada moneda recolectada suma puntaje e incrementa la adrenalina dinamica

            if (sonido != null) {
                sonido.play();
            }
        }
    }

    @Override
    public boolean estaFueraPantalla() {
        return area.y + area.height < 0;
    }

    @Override
    public void destruir() {
        // Los recursos compartidos pertenecen a GestorRecursos.
    }
}