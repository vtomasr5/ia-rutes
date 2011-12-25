/*
 * Elemento_pila.java
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
package estructuras;

import java.util.Date;

public class Elemento_pila {

    String nombre;
    int coord_x;
    int coord_y;
    float precio;
    Date hora_llegada;
    Elemento_pila sig;

    public Elemento_pila(String nombre, int cx, int cy, Date hora_lleg, float precio) {
        this.nombre = nombre;
        coord_x = cx;
        coord_y = cy;
        this.precio = precio;
        hora_llegada = hora_lleg;
        sig = null;
    }

    public void set_nombre(String nombre_ciudad) {
        nombre = nombre_ciudad;
    }

    public void set_sig(Elemento_pila sig_ciudad) {
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

    public Elemento_pila get_sig() {
        return (sig);
    }
}
