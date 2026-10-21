
package com.mygdx.game.recogibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.jugador.Player;

public class PowerUp implements ObjetoRecogible {
    public enum Tipo {
        ESCUDO,
        IMAN,
        ESTRELLA
    }

    private final Rectangle area;
    private final Texture textura;
    private final Tipo tipo;
    private final int multiplicador;
    private final float velocidad;
    
    private static final float DURACION_ESCUDO = 30f;
    private static final float DURACION_IMAN = 15f;
    private static final float DURACION_ESTRELLA = 10f;

    public PowerUp(Texture textura,Tipo tipo,int multiplicador,float x,float y,float velocidad) {
        this.textura = textura;
        this.tipo = tipo;
        this.multiplicador = multiplicador;
        this.velocidad = velocidad;
        area = new Rectangle(x,y,64,64);
    }

    @Override
    public void actualizar(float delta) {
        area.y -= velocidad * delta;
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura,area.x,area.y,area.width,area.height);
    }

    @Override
    public Rectangle getArea() {
        return area;
    }

    @Override
    public void recoger(Player player) {
        switch (tipo) {
            case ESCUDO:
                player.activarEscudo();
                break;
            case IMAN:
                player.activarIman();
                break;
            case ESTRELLA:
                player.activarEstrella(multiplicador);
                break;
        }
    }
    
    public static float getDuracion(Tipo tipo) {
        switch (tipo) {
            case ESCUDO:
                return DURACION_ESCUDO;
            case IMAN:
                return DURACION_IMAN;
            case ESTRELLA:
                return DURACION_ESTRELLA;
            default:
                return 0;
        }
    }

    @Override
    public boolean estaFueraPantalla() {
        return area.y + area.height < 0;
    }

    @Override
    public void destruir() {
        // La textura es compartida y la libera el gestor.
    }

	@Override
	public void actualizar(float delta, Player player) {
		// TODO Auto-generated method stub
		
	}
}