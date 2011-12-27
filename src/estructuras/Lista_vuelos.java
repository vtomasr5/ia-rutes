/*
 * Lista_vuelos.java
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

import elementos.Vuelo;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;

public class Lista_vuelos {

    private Vuelo Cap;

    public Lista_vuelos() {
        Cap = null;
    }

    public Vuelo primero() {
        return Cap;
    }

    public void insertarnuevo(int cod, String comp, float precio, Date salida, Date llegada, int or_x, int or_y, int de_x, int de_y) {
        Vuelo q;
        Vuelo nuevo;
        q = primero();
        nuevo = new Vuelo(cod, comp, precio, salida, llegada, or_x, or_y, de_x, de_y);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public void pinta_vuelo(JLabel mundo) {
        Vuelo vue = Cap;
        while (vue != null) {
            vue = siguiente_vuelo(vue);
        }
    }

    public int numero_vuelos() {
        int j = 0;
        Vuelo q = primero();
        while (q != null) {
            j = j + 1;
            q = q.get_sig();
        }
        return j;
    }

    public Vuelo obtener_vuelo(int num) {
        Vuelo q = primero();
        int j = 1;
        while (q != null && j == num) {
            j = j + 1;
            q = q.get_sig();
        }

        return q;
    }

    public Vuelo mejor_vuelo_coste(Date hora_llegada) {
        Vuelo q = primero();
        Vuelo vuelo_elegido = null;
        float coste = 100000000;
        while (q != null) {
            if (q.get_horasal().after(hora_llegada) && q.get_precio() < coste) {
                coste = q.get_precio();
                vuelo_elegido = q;
            }
            q = q.get_sig();
        }
        return vuelo_elegido;
    }

    public Vuelo mejor_vuelo_tiempo(Date hora_salida) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Vuelo q = primero();
        Vuelo vuelo_elegido = null;
        Date tiempo = df.parse("31/12/2100 00:00");
        while (q != null) {
            if (q.get_horasal().after(hora_salida) && q.get_horalleg().before(tiempo)) {
                tiempo = q.get_horalleg();
                vuelo_elegido = q;
            }
            q = q.get_sig();
        }
        return vuelo_elegido;
    }

    public Vuelo siguiente_vuelo(Vuelo p) {
        return (p.get_sig());
    }

    public int recuperar_cod(Vuelo p) {
        return (p.get_codigo());
    }

    public String recuperar_compañia(Vuelo p) {
        return (p.get_compañia());
    }

    public float recuperar_precio(Vuelo p) {
        return (p.get_precio());
    }

    public Date recuperar_hora_salida(Vuelo p) {
        return (p.get_horasal());
    }

    public Date recuperar_hora_llegada(Vuelo p) {
        return (p.get_horalleg());
    }

    public void imprimir() {
        Vuelo tmp = new Vuelo();
        tmp = Cap;
        System.out.print("--- LLISTA ---\n");
        while (tmp != null) {
            System.out.print("     Companyia: ");
            System.out.print(recuperar_compañia(tmp) + "\n");
            System.out.print("     Codi: ");
            System.out.print(recuperar_cod(tmp) + "\n");
            System.out.print("     Preu: ");
            System.out.print(recuperar_precio(tmp) + "\n");
            System.out.print("     Hora sortida: ");
            System.out.print(recuperar_hora_salida(tmp) + "\n");
            System.out.print("     Hora arribada: ");
            System.out.print(recuperar_hora_llegada(tmp) + "\n");
            System.out.print("-----------------------------\n");
            tmp = tmp.get_sig();
        }
        System.out.println("");
    }

    public boolean Vacia() {
        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }
}
