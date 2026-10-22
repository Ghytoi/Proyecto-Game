package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.escenarios.TipoEscenario;

public abstract class EntidadMovil {
    private final Rectangle area;
    private final Rectangle hitbox;
    private final Texture textura;
    private final float velocidad;
    private final TipoEscenario[] escenariosPermitidos;

    protected EntidadMovil(Texture textura,float x,float y,float ancho,float alto,float velocidad,TipoEscenario... escenariosPermitidos) {
        if(textura==null) throw new IllegalArgumentException("La textura no puede ser null");
        if(ancho<=0||alto<=0||velocidad<0) throw new IllegalArgumentException("Las dimensiones deben ser positivas y la velocidad no puede ser negativa");
        if(escenariosPermitidos==null||escenariosPermitidos.length==0) throw new IllegalArgumentException("Debe existir al menos un escenario permitido");

        this.textura=textura;
        this.area=new Rectangle(x,y,ancho,alto);
        this.hitbox=new Rectangle();
        this.velocidad=velocidad;
        this.escenariosPermitidos=escenariosPermitidos.clone();

        actualizarHitbox();
    }

    public abstract void actualizar(float delta);

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura,area.x,area.y,area.width,area.height);
    }

    public Rectangle getArea() {
        return new Rectangle(area);
    }

    public Rectangle getHitbox() {
        float anchoHitbox = area.width * 0.8f;
        float altoHitbox = area.height * 0.8f;
        float margenX = (area.width - anchoHitbox) / 2f;
        float margenY = (area.height - altoHitbox) / 2f;

        return new Rectangle(
            area.x + margenX,
            area.y + margenY,
            anchoHitbox,
            altoHitbox
        );
    }

    protected Rectangle obtenerAreaInterna() {
        return area;
    }

    protected float getVelocidad() {
        return velocidad;
    }

    public void configurarHitbox(float margenX,float margenY) {
        if(margenX<0||margenY<0||margenX>=0.5f||margenY>=0.5f) {
            throw new IllegalArgumentException("Los márgenes deben estar entre 0 y 0.5");
        }
        actualizarHitbox();
    }

    private void actualizarHitbox() {
        float margenX = (area.width - 80f) / 2f;
        float margenY = (area.height - 80f) / 2f;

        hitbox.set(
            area.x + margenX,
            area.y + margenY,
            80f,
            80f
        );
    }
    

    public boolean colisionaCon(Rectangle areaJugador) {
        return areaJugador != null && getHitbox().overlaps(areaJugador);
    }

    public boolean perteneceAEscenario(TipoEscenario escenario) {
        if(escenario==null) return false;

        for(TipoEscenario permitido:escenariosPermitidos) {
            if(permitido==escenario) return true;
        }

        return false;
    }

    public void destruir() {
        // La textura pertenece a quien administra los recursos.
    }
}