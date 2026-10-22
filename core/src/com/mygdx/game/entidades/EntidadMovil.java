package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.escenarios.TipoEscenario;
import com.mygdx.game.movimiento.EstrategiaMovimiento;

public abstract class EntidadMovil {
    private final Rectangle area;
    private final Texture textura;
    private final float velocidad;
    private final EstrategiaMovimiento movimiento;
    private final TipoEscenario[] escenariosPermitidos;

    protected EntidadMovil(Texture textura,float x,float y,float ancho,float alto,float velocidad,EstrategiaMovimiento movimiento,TipoEscenario... escenariosPermitidos) {
        if(textura==null) throw new IllegalArgumentException("La textura no puede ser null");
        if(movimiento==null) throw new IllegalArgumentException("La estrategia de movimiento no puede ser null");
        if(ancho<=0||alto<=0||velocidad<0) throw new IllegalArgumentException("Las dimensiones deben ser positivas y la velocidad no puede ser negativa");
        if(escenariosPermitidos==null||escenariosPermitidos.length==0) throw new IllegalArgumentException("Debe existir al menos un escenario permitido");

        this.textura=textura;
        this.area=new Rectangle(x,y,ancho,alto);
        this.velocidad=velocidad;
        this.movimiento=movimiento;
        this.escenariosPermitidos=escenariosPermitidos.clone();
    }

    /** Cada tipo de entidad define cuanto "perdona" su hitbox (fraccion por lado, entre 0 y 0.5). */
    protected abstract float getMargenHitbox();

    public void actualizar(float delta) {
        movimiento.mover(area,velocidad,delta);
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura,area.x,area.y,area.width,area.height);
    }

    public Rectangle getArea() {
        return new Rectangle(area);
    }

    public Rectangle getHitbox() {
        float mx = area.width * getMargenHitbox();
        float my = area.height * getMargenHitbox();

        return new Rectangle(
            area.x + mx,
            area.y + my,
            area.width - 2f * mx,
            area.height - 2f * my
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
        // La textura pertenece a GestorRecursos.
    }
}