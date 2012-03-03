/*
 * Element.java
 * 
 * Copyright (C) 2011 Vicenç Juan Tomàs Monserrat
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package elementos;

import java.util.Date;

public class Element {

    String nombre;
    int coord_x;
    int coord_y;
    float precio;
    Date hora_llegada;
    Element sig;
    String[] ciudades = new String[100];
    Vol[] vuelos = new Vol[100];
    double distancia;
    Date primer_vuelo;
    long tiempo_acumulado;

    public Element(String nombre, int cx, int cy, Date hora_lleg, float precio, String[] ciudades, Vol[] vuelos) {
        this.nombre = nombre;
        coord_x = cx;
        coord_y = cy;
        this.precio = precio;
        hora_llegada = hora_lleg;
        sig = null;
        distancia = -1;
        System.arraycopy(ciudades, 0, this.ciudades, 0, ciudades.length);
        System.arraycopy(vuelos, 0, this.vuelos, 0, vuelos.length);
        primer_vuelo = null;
        tiempo_acumulado = 0;
    }

    public Element(String nombre, int cx, int cy, Date hora_lleg, float precio, String[] ciudades, Vol[] vuelos, Date pr_vuelo, long t_acum) {
        this.nombre = nombre;
        coord_x = cx;
        coord_y = cy;
        this.precio = precio;
        hora_llegada = hora_lleg;
        sig = null;
        distancia = -1;
        System.arraycopy(ciudades, 0, this.ciudades, 0, ciudades.length);
        System.arraycopy(vuelos, 0, this.vuelos, 0, vuelos.length);
        primer_vuelo = pr_vuelo;
        tiempo_acumulado = t_acum;
    }

    public void set_dist(double dist) {
        distancia = dist;
    }

    public void set_ciudades(String[] ciudades) {
        this.ciudades = ciudades;
    }

    public void set_vuelos(Vol[] vuelos) {
        this.vuelos = vuelos;
    }

    public void set_nombre(String nombre_ciudad) {
        nombre = nombre_ciudad;
    }

    public void set_sig(Element sig_ciudad) {
        sig = sig_ciudad;
    }

    public void set_coordx(int cx) {
        coord_x = cx;
    }

    public void set_coordy(int cy) {
        coord_y = cy;
    }

    public void set_precio(float precio) {
        this.precio = precio;
    }

    public void set_horalleg(Date hora) {
        hora_llegada = hora;
    }

    public String getnombre() {
        return (nombre);
    }

    public int getcx() {
        return (coord_x);
    }

    public int getcy() {
        return (coord_y);
    }

    public float get_precio() {
        return (precio);
    }

    public Date get_horalleg() {
        return (hora_llegada);
    }

    public Element get_sig() {
        return (sig);
    }

    public String[] get_ciudades() {
        return ciudades;
    }

    public Vol[] get_vuelos() {
        return vuelos;
    }

    public double get_dist() {
        return distancia;
    }

    public long get_tiempo_acum() {
        return tiempo_acumulado;
    }

    public Date get_primer_vuelo() {
        return primer_vuelo;
    }
}
