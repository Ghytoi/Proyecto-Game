package com.mygdx.game.recogibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.jugador.Player;
import com.badlogic.gdx.audio.Sound;

public class Monedas implements ObjetoRecogible {
    private Rectangle area;
    private Texture texturaBuena;
    private Texture texturaMala;
    private Sound sonido;
    private boolean mala;
    private float velocidad;

    public Monedas(Texture texturaBuena, Texture texturaMala, Sound sonido, boolean mala, float x, float y, float velocidad) {
        this.texturaBuena = texturaBuena;
        this.texturaMala = texturaMala;
        this.sonido = sonido;
        this.mala = mala;
        this.velocidad = velocidad;

        area = new Rectangle(x, y, 64, 64);
    }

    @Override
    public void actualizar(float delta,Player player) {
        area.y -= velocidad * delta;

        if (!mala && player.estaAtrayendoMonedas()) {
            float monedaX = area.x + area.width / 2;
            float monedaY = area.y + area.height / 2;
            float jugadorX = player.getArea().x + player.getArea().width / 2;
            float jugadorY = player.getArea().y + player.getArea().height / 2;

            if (player.atraeMonedas(monedaX,monedaY)) {
                float dx = jugadorX - monedaX;
                float dy = jugadorY - monedaY;
                float distancia = (float) Math.sqrt(dx * dx + dy * dy);

                if (distancia > 0) {
                    float velocidadIman = 700 * delta;
                    float movimiento = Math.min(velocidadIman,distancia);
                    area.x += dx / distancia * movimiento;
                    area.y += dy / distancia * movimiento;
                }
            }
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        Texture textura = mala ? texturaMala : texturaBuena;
        batch.draw(textura, area.x, area.y, area.width, area.height);
    }

    @Override
    public Rectangle getArea() {
        return area;
    }

    @Override
    public void recoger(Player player) {
        if (mala) {
            player.dañar();
        } else {
            player.sumarMoneda();
            sonido.play();
        }
    }

    @Override
    public boolean estaFueraPantalla() {
        return area.y + area.height < 0;
    }
    
    @Override
    public void destruir() {
        // Las texturas y el sonido son compartidos por el gestor.
    }

	@Override
	public void actualizar(float delta) {
		// TODO Auto-generated method stub
		
	}
}