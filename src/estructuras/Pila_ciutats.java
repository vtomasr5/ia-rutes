/*
 * Pila_ciutats.java
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

import elementos.Element;
import elementos.Vol;
import java.util.Date;

public class Pila_ciutats {

    private Element Cap;

    public Pila_ciutats() {
        Cap = null;
    }

    public Element primero() {
        return Cap;
    }

    public void añadir_pila(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos) {
        Element q;
        Element nuevo;
        q = primero();
        nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public void añadir_pila_tiempo(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, Date t, long l) {
        Element q;
        Element nuevo;
        q = primero();
        nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, t, l);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public Element quitar_pila() {
        Element q, r;
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
