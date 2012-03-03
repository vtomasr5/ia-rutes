package estructuras;

import elementos.Ciutat;

public class Fitxer implements java.io.Serializable {

    Ciutat[] ciudades;
    LlistaVols[][] vuelos;
    int ciudades_creadas;

    public Fitxer(Ciutat[] ciu, LlistaVols[][] vuelos, int ciu_crea) {
        this.ciudades = ciu;
        this.vuelos = vuelos;
        this.ciudades_creadas = ciu_crea;
    }

    public Ciutat[] getCiudad() {
        return ciudades;
    }

    public LlistaVols[][] getVuelos() {
        return vuelos;
    }

    public int getCiudadescreadas() {
        return ciudades_creadas;
    }
}
