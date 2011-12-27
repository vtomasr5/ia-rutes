package estructuras;

import elementos.Ciudad;

public class Fichero implements java.io.Serializable {

    Ciudad[] ciudades;
    Lista_vuelos[][] vuelos;
    int ciudades_creadas;

    public Fichero(Ciudad[] ciu, Lista_vuelos[][] vuelos, int ciu_crea) {
        this.ciudades = ciu;
        this.vuelos = vuelos;
        this.ciudades_creadas = ciu_crea;
    }

    public Ciudad[] getCiudad() {
        return ciudades;
    }

    public Lista_vuelos[][] getVuelos() {
        return vuelos;
    }

    public int getCiudadescreadas() {
        return ciudades_creadas;
    }
}
