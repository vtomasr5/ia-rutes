/*
 * Coa_ciutats.java
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

public class Coa_ciutats {

    private Element Cap;

    public Coa_ciutats() {
        Cap = null;
    }

    public Element primero() {
        return Cap;
    }

    public void añadir_cola_coste(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos) {
        Element q, r;
        float coste = p;
        Element nuevo;
        q = primero();
        r = null;
        if (q == null) {
            nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
            nuevo.set_sig(q);
            Cap = nuevo;
        } else {
            while (q.get_precio() < coste && q.get_sig() != null) {
                r = q;
                q = q.get_sig();
            }
            if (q.get_precio() < coste) {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
                nuevo.set_sig(null);
                q.set_sig(nuevo);
            } else {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
                nuevo.set_sig(q);
                if (r == null) {
                    Cap = nuevo;
                } else {
                    r.set_sig(nuevo);
                }
            }
        }
    }

    public void añadir_cola_costee(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos) {
        Element q, nuevo;
        q = primero();
        nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public void añadir_cola_distancia(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, double distancia) {
        Element q, r;
        double dist = distancia;
        Element nuevo;
        q = primero();
        r = null;
        if (q == null) {
            nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
            nuevo.set_dist(dist);
            nuevo.set_sig(q);
            Cap = nuevo;
        } else {
            while (q.get_dist() < dist && q.get_sig() != null) {
                r = q;
                q = q.get_sig();
            }
            if (q.get_dist() < dist) {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
                nuevo.set_dist(dist);
                nuevo.set_sig(null);
                q.set_sig(nuevo);
            } else {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
                nuevo.set_dist(dist);
                nuevo.set_sig(q);
                if (r == null) {
                    Cap = nuevo;
                } else {
                    r.set_sig(nuevo);
                }
            }
        }
    }

    public void añadir_cola_distanciaa(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, double distancia) {
        Element q, nuevo;
        q = primero();
        nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos);
        nuevo.set_dist(distancia);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public void añadir_cola_distancia2(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, Date h_s, double distancia) {
        Element q, r;
        double dist = distancia;
        Element nuevo;
        q = primero();
        r = null;
        if (q == null) {
            nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, h_s, (long) dist);
            nuevo.set_dist(dist);
            nuevo.set_sig(q);
            Cap = nuevo;
        } else {
            while (q.get_dist() < dist && q.get_sig() != null) {
                r = q;
                q = q.get_sig();
            }
            if (q.get_dist() < dist) {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, h_s, (long) dist);
                nuevo.set_dist(dist);
                nuevo.set_sig(null);
                q.set_sig(nuevo);
            } else {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, h_s, (long) dist);
                nuevo.set_dist(dist);
                nuevo.set_sig(q);
                if (r == null) {
                    Cap = nuevo;
                } else {
                    r.set_sig(nuevo);
                }
            }
        }
    }

    public void añadir_cola_distanciaa2(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, Date h_s, double distancia) {
        Element q, nuevo;
        q = primero();
        nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, h_s, (long) distancia);
        nuevo.set_dist(distancia);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public int numero_vuelos() {
        int j = 0;
        Element q = primero();
        while (q != null) {
            j = j + 1;
            q = q.get_sig();
        }
        return j;
    }

    public void añadir_cola_tiempo(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, Date t, long l) {
        Element q, r;
        Element nuevo;
        q = primero();
        r = null;
        if (q == null) {
            nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, t, l);
            nuevo.set_sig(q);
            Cap = nuevo;
        } else {
            while ((q.get_tiempo_acum() < l) && q.get_sig() != null) {
                r = q;
                q = q.get_sig();
            }
            if (q.get_tiempo_acum() < l) {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, t, l);
                nuevo.set_sig(null);
                q.set_sig(nuevo);
            } else {
                nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, t, l);
                nuevo.set_sig(q);
                if (r == null) {
                    Cap = nuevo;
                } else {
                    r.set_sig(nuevo);
                }
            }
        }
    }

    public void añadir_cola_tiempoo(String nombre, int cx, int cy, Date h_l, float p, String[] ciudades, Vol[] vuelos, Date t, long l) {
        Element q, nuevo;
        q = primero();
        nuevo = new Element(nombre, cx, cy, h_l, p, ciudades, vuelos, t, l);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public Element quitar_cola() { //quita el primero de la cola
        Element q, r;
        q = primero();
        r = primero().get_sig();
        Cap = r;
        return q;
    }

    public Element quitar_cola_coste() { //quita el primero de la cola
        Element q, r, sel = null;
        q = primero();
        float coste = Float.MAX_VALUE;
        while (q != null) {
            if (q.get_precio() < coste) {
                coste = q.get_precio();
                sel = q;
            }
            q = q.get_sig();
        }
        q = primero();
        r = null;
        boolean trobat = false;
        while (trobat == false) {
            if (q.equals(sel)) {
                if (r == null) {
                    Cap = q.get_sig();
                    trobat = true;
                } else if (q.get_sig() == null) {
                    r.set_sig(null);
                    trobat = true;
                } else {
                    r.set_sig(q.get_sig());
                    trobat = true;
                }
            }
            r = q;
            q = q.get_sig();
        }
        return sel;
    }

    public Element quitar_cola_tiempo() {//quita el primero de la cola
        Element q, r, sel = null;
        q = primero();
        float tiempo = Float.MAX_VALUE;
        while (q != null) {
            if (q.get_tiempo_acum() < tiempo) {
                tiempo = q.get_tiempo_acum();
                sel = q;
            }
            q = q.get_sig();
        }
        q = primero();
        r = null;
        boolean trobat = false;
        while (trobat == false) {
            if (q.equals(sel)) {
                if (r == null) {
                    Cap = q.get_sig();
                    trobat = true;
                } else if (q.get_sig() == null) {
                    r.set_sig(null);
                    trobat = true;
                } else {
                    r.set_sig(q.get_sig());
                    trobat = true;
                }
            }
            r = q;
            q = q.get_sig();
        }
        return sel;
    }

    public Element quitar_cola_dist() {//quita el primero de la cola
        Element q, r, sel = null;
        q = primero();
        double dist = Double.MAX_VALUE;
        while (q != null) {
            if (q.get_dist() < dist) {
                dist = q.get_dist();
                sel = q;
            }
            q = q.get_sig();
        }
        q = primero();
        r = null;
        boolean trobat = false;
        while (trobat == false) {
            if (q.equals(sel)) {
                if (r == null) {
                    Cap = q.get_sig();
                    trobat = true;
                } else if (q.get_sig() == null) {
                    r.set_sig(null);
                    trobat = true;
                } else {
                    r.set_sig(q.get_sig());
                    trobat = true;
                }
            }
            r = q;
            q = q.get_sig();
        }
        return sel;
    }

    public Element quitar_cola_ultimo() { //quita el último de la cola
        Element q, r = null;
        q = primero();
        if (q == null) {
            return null;
        } else if (q.get_sig() == null) {
            Cap = null;
            return q;
        } else {
            while (q.get_sig() != null) {
                r = q;
                q = q.get_sig();
            }
            r.set_sig(null);
            return q;
        }
    }

    public boolean Vacia() {

        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }

    public void mostrar_cola_coste() {
        Element q = primero();
        while (q != null) {
            System.out.print("Preu: " + q.get_precio() + "\n");
            q = q.get_sig();
        }
    }

    public void mostrar_cola_tiempo() {
        Element q = primero();
        while (q != null) {
            System.out.print("Temps: " + q.get_horalleg() + "\n");
            q = q.get_sig();
        }
        System.out.print("\n");
    }
}
