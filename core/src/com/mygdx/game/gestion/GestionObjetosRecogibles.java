package com.mygdx.game.gestion;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.jugador.Player;
import com.mygdx.game.recogibles.Monedas;
import com.mygdx.game.recogibles.ObjetoRecogible;
import com.mygdx.game.recogibles.TransicionRecogible;

public class GestionObjetosRecogibles {
	private Array<ObjetoRecogible> objetos;
    private Array<TransicionRecogible> transiciones;

    private Texture texturaBuena;
    private Texture texturaMala;
    
    private Texture portalVias;
    private Texture portalCiudad;
    private Texture portalTunel;
    private Texture portalAereo;

    private Sound sonido;

    private float tiempoGeneracion;
    private float tiempoTransicion;
    private float tiempoEntreObjetos = 0.8f;
    private static final float INTERVALO_TRANSICION = 5f;
    private float velocidad = 350;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;
    private static final float ANCHO_OBJETO = 64;
    
    private GestionEscenario gestionEscenario;

    public GestionObjetosRecogibles(Texture texturaBuena,Texture texturaMala,Sound sonido, GestionEscenario gestionEscenario) {
        this.texturaBuena = texturaBuena;
        this.texturaMala = texturaMala;
        this.sonido = sonido;
        this.gestionEscenario = gestionEscenario;

        portalVias = new Texture("portal_vias.png");
        portalCiudad = new Texture("portal_ciudad.jpg");
        portalTunel = new Texture("portal_tunel.jpg");
        portalAereo = new Texture("portal_aereo.png");
        

        objetos = new Array<ObjetoRecogible>();
        transiciones = new Array<TransicionRecogible>();
    }

    public void crear() {
        crearMoneda();
    }

    private void crearMoneda() {
        int carril = MathUtils.random(0,2);
        float anchoCarril = ANCHO_MUNDO / 3;
        float x = carril * anchoCarril + (anchoCarril - ANCHO_OBJETO) / 2;
        boolean mala = MathUtils.random(0,1) == 1;

        objetos.add(new Monedas(texturaBuena,texturaMala,sonido,mala,x,ALTO_MUNDO,velocidad));
        tiempoGeneracion = 0;
    }


	private void crearTransicion(String escenarioActual) {
	    int carril = MathUtils.random(0,2);
	    float anchoCarril = ANCHO_MUNDO / 3;
	    float x = carril * anchoCarril + (anchoCarril - ANCHO_OBJETO) / 2;
	
	    Array<String> destinos = new Array<String>();
	
	    destinos.add("VIAS");
	    destinos.add("CIUDAD");
	    destinos.add("TUNEL");
	    destinos.add("AEREO");
	
	    for (int i = destinos.size - 1; i >= 0; i--) {
	        if (destinos.get(i).equals(escenarioActual)) {
	            destinos.removeIndex(i);
	        }
	    }
	
	    String destino = destinos.random();
	    Texture texturaPortal;
	
	    switch (destino) {
	        case "VIAS":
	            texturaPortal = portalVias;
	            break;
	        case "CIUDAD":
	            texturaPortal = portalCiudad;
	            break;
	        case "TUNEL":
	            texturaPortal = portalTunel;
	            break;
	        case "AEREO":
	            texturaPortal = portalAereo;
	            break;
	        default:
	            return;
	    }
	
	    transiciones.add(new TransicionRecogible(texturaPortal,x,ALTO_MUNDO,ANCHO_OBJETO,100,velocidad,destino));
	
	    tiempoTransicion = 0;
	}


    public void agregar(ObjetoRecogible objeto) {
        if (objeto != null) {
            objetos.add(objeto);
        }
    }

    public String actualizar(float delta,Player player) {
        if (player.getVidas() <= 0) {
            return null;
        }

        tiempoGeneracion += delta;
        tiempoTransicion += delta;

        if (tiempoGeneracion >= tiempoEntreObjetos) {
            crearMoneda();
        }

        if (tiempoTransicion >= INTERVALO_TRANSICION && transiciones.size == 0) {
            String escenarioActual = gestionEscenario.getNombreEscenarioActual();
            crearTransicion(escenarioActual);
        }

        for (int i = objetos.size - 1; i >= 0; i--) {
            ObjetoRecogible objeto = objetos.get(i);
            objeto.actualizar(delta);

            if (objeto.getArea().overlaps(player.getArea())) {
                objeto.recoger(player);
                objeto.destruir();
                objetos.removeIndex(i);
            } else if (objeto.estaFueraPantalla()) {
                objeto.destruir();
                objetos.removeIndex(i);
            }
        }

        for (int i = transiciones.size - 1; i >= 0; i--) {
            TransicionRecogible transicion = transiciones.get(i);
            transicion.actualizar(delta);

            if (transicion.getArea().overlaps(player.getArea())) {
                String destino = transicion.getDestino();
                transiciones.removeIndex(i);
                return destino;
            } else if (transicion.estaFueraPantalla()) {
                transiciones.removeIndex(i);
                tiempoTransicion = 0;
            }
        }

        return null;
    }

    public void dibujar(SpriteBatch batch) {
        for (ObjetoRecogible objeto : objetos) {
            objeto.dibujar(batch);
        }

        for (TransicionRecogible transicion : transiciones) {
            transicion.dibujar(batch);
        }
    }


	public void destruir() {
	    reiniciarObjetos();
	
	    if (sonido != null) {
	        sonido.dispose();
	    }
	
	    if (texturaBuena != null) {
	        texturaBuena.dispose();
	    }
	
	    if (texturaMala != null) {
	        texturaMala.dispose();
	    }
	
	    if (portalVias != null) {
	        portalVias.dispose();
	    }
	
	    if (portalCiudad != null) {
	        portalCiudad.dispose();
	    }
	
	    if (portalTunel != null) {
	        portalTunel.dispose();
	    }
	
	    if (portalAereo != null) {
	        portalAereo.dispose();
	    }
	}

    
    public void reiniciarObjetos() {
        for (ObjetoRecogible objeto : objetos) {
            objeto.destruir();
        }

        objetos.clear();
        transiciones.clear();

        tiempoGeneracion = 0;
        tiempoTransicion = 0;
    }
}