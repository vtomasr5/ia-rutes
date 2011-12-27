/*
 * Ciudad.java
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

public class Ciudad implements java.io.Serializable {

    String nombre;
    int coord_x;
    int coord_y;

    public Ciudad(String nombre, int cx, int cy) {
        this.nombre = nombre;
        coord_x = cx;
        coord_y = cy;
    }

    public Ciudad() {
        this.nombre = "";
        coord_x = -1;
        coord_y = -1;

    }

    public void pintar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.red);
        g2d.fillOval(coord_x, coord_y, 7, 7);
        g2d.setColor(Color.black);
        g2d.drawString(this.nombre, coord_x - (this.nombre.length() / 2), coord_y - 2);
    }

    public void set_nombre(String nombre_ciudad) {
        nombre = nombre_ciudad;
    }

    public void set_coordx(int cx) {
        coord_x = cx;
    }

    public void set_coordy(int cy) {
        coord_y = cy;
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
}