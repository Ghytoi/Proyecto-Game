package com.mygdx.game.recogibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.jugador.Player;

public class PowerUp implements ObjetoRecogible {
    private Rectangle area;
    private Texture textura;
    private float velocidad;

    public PowerUp(Texture textura, float x, float y, float velocidad) {
        this.textura = textura;
        this.velocidad = velocidad;
        area = new Rectangle(x, y, 64, 64);
    }

    @Override
    public void actualizar(float delta) {
        area.y -= velocidad * delta;
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        if (textura != null) {
            batch.draw(textura, area.x, area.y, area.width, area.height);
        }
    }

    @Override
    public Rectangle getArea() {
        return area;
    }

    @Override
    public void recoger(Player player) {
        player.sumarPuntos(5);
    }

    @Override
    public boolean estaFueraPantalla() {
        return area.y + area.height < 0;
    }

    @Override
    public void destruir() {
        // La textura se administra externamente.
    }
}