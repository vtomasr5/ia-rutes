/*
 * Pila_ciudades.java
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

import elementos.Elemento;
import elementos.Vuelo;
import java.util.Date;

public class Pila_ciudades {

    private Elemento Cap;

    public Pila_ciudades() {
        Cap = null;
    }

    public Elemento primero() {
        return Cap;
    }

    public void añadir_pila(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vuelo[] vuelos) {
        Elemento q;
        Elemento nuevo;
        q = primero();
        nuevo = new Elemento(nombre, cx, cy, h_l, p, ciudades, vuelos);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public void añadir_pila_tiempo(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vuelo[] vuelos, Date t, long l) {
        Elemento q;
        Elemento nuevo;
        q = primero();
        nuevo = new Elemento(nombre, cx, cy, h_l, p, ciudades, vuelos, t, l);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public Elemento quitar_pila() {
        Elemento q, r;
        q = primero();
        r = primero().get_sig();
        Cap = r;
        return q;
    }

    public boolean Vacia() {
        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }
}
