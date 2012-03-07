/*
 * LlistaVols.java
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

import elementos.Vol;
import graficos.Ventana;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LlistaVols implements java.io.Serializable {

    private Vol Cap;

    public LlistaVols() {
        Cap = null;
    }

    public Vol primero() {
        return Cap;
    }

    public void insertarnuevo(int cod, String comp, float precio, Date salida, Date llegada, int or_x, int or_y, int de_x, int de_y) {
        Vol q;
        Vol nuevo;
        q = primero();
        nuevo = new Vol(cod, comp, precio, salida, llegada, or_x, or_y, de_x, de_y);
        nuevo.set_sig(q);
        Cap = nuevo;
    }

    public void pinta_vuelo() {
        Vol vue = Cap;
        while (vue != null) {
            vue = siguiente_vuelo(vue);
        }
    }

    public int numero_vuelos() {
        int j = 0;
        Vol q = primero();
        while (q != null) {
            j = j + 1;
            q = q.get_sig();
        }
        return j;
    }

    public Vol obtener_vuelo(int num) {
        Vol q = primero();
        int j = 1;
        while (q != null && j == num) {
            j = j + 1;
            q = q.get_sig();
        }
        return q;
    }

    public Vol mejor_vuelo_coste(Date hora_llegada) {
        Vol q = primero();
        Vol vuelo_elegido = null;
        float coste = Float.MAX_VALUE;
        while (q != null) {
            if (q.get_horasal().after(hora_llegada) && q.get_precio() < coste) {
                coste = q.get_precio();
                vuelo_elegido = q;
            }
            q = q.get_sig();
        }
        return vuelo_elegido;
    }

    public Vol mejor_vuelo_tiempo(Date hora_salida) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Vol q = primero();
        Vol vuelo_elegido = null;
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

    public Vol siguiente_vuelo(Vol p) {
        return (p.get_sig());
    }

    public int recuperar_cod(Vol p) {
        return (p.get_codigo());
    }

    public String recuperar_compañia(Vol p) {
        return (p.get_compañia());
    }

    public float recuperar_precio(Vol p) {
        return (p.get_precio());
    }

    public Date recuperar_hora_salida(Vol p) {
        return (p.get_horasal());
    }

    public Date recuperar_hora_llegada(Vol p) {
        return (p.get_horalleg());
    }

//    public void imprimir() {
//        Vol p;
//        p = Cap;//
////        System.out.print("--- LLISTA DE VOLS ---\n");
//        while (p != null) {
//            System.out.print("Codi: ");
//            System.out.print(recuperar_cod(p) + "\n");
//            System.out.print("Preu: ");
//            System.out.print(recuperar_precio(p) + "\n");
//            System.out.print("Companyia: ");
//            System.out.print(recuperar_compañia(p) + "\n");
//            System.out.print("Hora sortida: ");
//            System.out.print(recuperar_hora_salida(p) + "\n");
//            System.out.print("Hora arribada: ");
//            System.out.print(recuperar_hora_llegada(p) + "\n");
//            System.out.print("-------------------------------------\n");
//            p = p.get_sig();
//        }
//    }
    
    public void imprimir() {
        Vol p = Cap;
        int cod = -2; 
        while (p != null) { 
            if (recuperar_cod(p) != cod) {
                Ventana.textAreaLlistaVols.append("Codi: ");
                Ventana.textAreaLlistaVols.append(recuperar_cod(p)+"\n");
                Ventana.textAreaLlistaVols.append("Preu: ");
                Ventana.textAreaLlistaVols.append(recuperar_precio(p)+"\n");
                Ventana.textAreaLlistaVols.append("Companyia: ");
                Ventana.textAreaLlistaVols.append(recuperar_compañia(p)+"\n");
                Ventana.textAreaLlistaVols.append("Hora sortida: ");
                Ventana.textAreaLlistaVols.append(recuperar_hora_salida(p)+"\n");
                Ventana.textAreaLlistaVols.append("Hora arribada: ");
                Ventana.textAreaLlistaVols.append(recuperar_hora_llegada(p)+"\n");
                Ventana.textAreaLlistaVols.append("-------------------------------------\n");
            }
            cod = recuperar_cod(p);
            p = p.get_sig();
        }
    }

    public boolean Vacia() {
        if (Cap == null) {
            return true;
        } else {
            return false;
        }
    }
}
