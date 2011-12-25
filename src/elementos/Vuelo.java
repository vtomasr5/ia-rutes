/*
 * Vuelo.java
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Date;
import javax.swing.JLabel;

public final class Vuelo extends JLabel {

    int codigo_vuelo;
    String compañia;
    float precio;
    Date hora_salida;
    Date hora_llegada;
    Vuelo siguiente;
    Ciudad ciudad;
    int o_x, o_y, d_x, d_y; // borrar!

    public Vuelo() {
        set_codigo(-1);
        set_compañia(null);
        set_precio(0);
        set_horasal(null);
        set_horalleg(null);
        set_sig(null);
    }

    public Vuelo(int cod, String comp, float precio, Date salida, Date llegada, int ox, int oy, int dx, int dy) {
        set_codigo(cod);
        set_compañia(comp);
        set_precio(precio);
        set_horasal(salida);
        set_horalleg(llegada);
        set_sig(null);
        this.o_x = ox;
        this.o_y = oy;
        this.d_x = dx;
        this.d_y = dy;
    }
    
    public Vuelo(int cod, String comp, float precio, Date salida, Date llegada, Ciudad ciu) {
        set_codigo(cod);
        set_compañia(comp);
        set_precio(precio);
        set_horasal(salida);
        set_horalleg(llegada);
        set_sig(null);
        set_ciudad(ciu);
    }    

    public void pintaVuelo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.drawLine(o_x, o_y, d_x, d_y);
    }

    public void pintaRuta(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.green);
        g2d.drawLine(o_x, o_y, d_x, d_y);
    }

    public void pintaSeleccionada(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.magenta);
        g2d.drawLine(o_x, o_y, d_x, d_y);
    }

    public void set_codigo(int cod) {
        codigo_vuelo = cod;
    }

    public void set_compañia(String comp) {
        compañia = comp;
    }

    public void set_precio(float precio) {
        this.precio = precio;
    }

    public void set_horasal(Date hora) {
        hora_salida = hora;
    }

    public void set_horalleg(Date hora) {
        hora_llegada = hora;
    }

    public void set_sig(Vuelo sig) {
        siguiente = sig;
    }

    public int get_codigo() {
        return (codigo_vuelo);
    }

    public String get_compañia() {
        return (compañia);
    }

    public float get_precio() {
        return (precio);
    }

    public Date get_horasal() {
        return (hora_salida);
    }

    public Date get_horalleg() {
        return (hora_llegada);
    }

    public Vuelo get_sig() {
        return (siguiente);
    }

    private void set_ciudad(Ciudad ciu) {
        ciudad = ciu;
    }
    
    private Ciudad get_ciudad() {
        return ciudad;
    }
}
