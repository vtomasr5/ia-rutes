/*
 * MapaMundo.java
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

import estructuras.Lista_vuelos;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MapaMundo extends JLabel {

    Ciudad[] ciu;
    Lista_vuelos[][] matriz;
    Vuelo[] vuelos;
    int ciu_creadas = 0, ori = -1, des = -1, modoRuta = 0, numCiu = 0, seleccionado = 0;
    ImageIcon img;

    public MapaMundo(Ciudad[] ciu, Lista_vuelos[][] matriz) {
        this.matriz = matriz;
        this.ciu = ciu;
        img = new ImageIcon("src/imagenes/mapa.jpg");
        this.setIcon(img);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img.getImage(), 0, 0, this);
        for (int i = 0; i < ciu_creadas; i++) {
            for (int j = 0; j < ciu_creadas; j++) {
                if (i != j) {
                    if (matriz[i][j].primero() != null) {
                        matriz[i][j].primero().pintaVuelo(g);
                    }
                }
            }
        }
        for (int i = 0; i < ciu_creadas; i++) {
            ciu[i].pintar(g);
        }
        if (modoRuta == 1 || modoRuta == 2) {
            for (int i = 1; i <= numCiu; i++) {
                vuelos[i].pintaRuta(g);
            }
            if (modoRuta == 2) {
                vuelos[seleccionado].pintaSeleccionada(g);
            }
        }
    }

    public void setVuelos(Lista_vuelos[][] vuelos) {
        this.matriz = vuelos;
    }

    public void setCiudad(Ciudad[] ciudad) {
        this.ciu = ciudad;
    }

    public void setNumciudades(int num) {
        this.ciu_creadas = num;
    }

    public void setVuelosRuta(Vuelo[] vuelo, int numvuelos) {
        this.vuelos = vuelo;
        this.numCiu = numvuelos;
    }

    public void setModoRuta(int modo) {
        this.modoRuta = modo;
    }

    public void setSeleccionado(int selec) {
        this.seleccionado = selec;
    }

}
