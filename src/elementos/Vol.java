/*
 * Vol.java
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

public final class Vol extends JLabel implements java.io.Serializable {

    private int codigo_vuelo;
    private String compañia;
    private float precio;
    private Date hora_salida;
    private Date hora_llegada;
    private Vol siguiente;
    private int o_x;
    private int o_y;
    private int d_x;
    private int d_y;
    
    public Vol() {
        set_codigo(-1);
        set_compañia(null);
        set_precio(0);
        set_horasal(null);
        set_horalleg(null);
        set_sig(null);
    }

    public Vol(int cod, String comp, float precio, Date salida, Date llegada, int ox, int oy, int dx, int dy) {
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
    
    /**
     * Link: http://www.bytemycode.com/snippets/snippet/82/
     * Draws an arrow on the given Graphics2D context
     * @param g The Graphics2D context to draw on
     * @param x The x location of the "tail" of the arrow
     * @param y The y location of the "tail" of the arrow
     * @param xx The x location of the "head" of the arrow
     * @param yy The y location of the "head" of the arrow
     */
    private void drawArrow(Graphics2D g, int x, int y, int xx, int yy) {
        float arrowWidth = 6.0f; //tamany punta fletxa
        float theta = 0.423f;
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        float[] vecLine = new float[2];
        float[] vecLeft = new float[2];
        float fLength;
        float th;
        float ta;
        float baseX, baseY;

        xPoints[ 0] = xx;
        yPoints[ 0] = yy;

        // build the line vector
        vecLine[ 0] = (float) xPoints[ 0] - x;
        vecLine[ 1] = (float) yPoints[ 0] - y;

        // build the arrow base vector - normal to the line
        vecLeft[ 0] = -vecLine[ 1];
        vecLeft[ 1] = vecLine[ 0];

        // setup length parameters
        fLength = (float) Math.sqrt(vecLine[0] * vecLine[0] + vecLine[1] * vecLine[1]);
        th = arrowWidth / (2.0f * fLength);
        ta = arrowWidth / (2.0f * ((float) Math.tan(theta) / 2.0f) * fLength);

        // find the base of the arrow
        baseX = ((float) xPoints[ 0] - ta * vecLine[0]);
        baseY = ((float) yPoints[ 0] - ta * vecLine[1]);

        // build the points on the sides of the arrow
        xPoints[ 1] = (int) (baseX + th * vecLeft[0]);
        yPoints[ 1] = (int) (baseY + th * vecLeft[1]);
        xPoints[ 2] = (int) (baseX - th * vecLeft[0]);
        yPoints[ 2] = (int) (baseY - th * vecLeft[1]);

        g.drawLine(x, y, (int) baseX, (int) baseY);
        g.fillPolygon( xPoints, yPoints, 3 ) ;
    }

    public void pintaVuelo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.darkGray);
        drawArrow(g2d, getO_x(), getO_y(), getD_x(), getD_y());
    }

    public void pintaRuta(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.green);
        drawArrow(g2d, getO_x(), getO_y(), getD_x(), getD_y());
    }

    public void pintaSeleccionada(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.magenta);
        drawArrow(g2d, getO_x(), getO_y(), getD_x(), getD_y());
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

    public void set_sig(Vol sig) {
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

    public Vol get_sig() {
        return (siguiente);
    }

    /**
     * @return the o_x
     */
    public int getO_x() {
        return o_x;
    }

    /**
     * @param o_x the o_x to set
     */
    public void setO_x(int o_x) {
        this.o_x = o_x;
    }

    /**
     * @return the o_y
     */
    public int getO_y() {
        return o_y;
    }

    /**
     * @param o_y the o_y to set
     */
    public void setO_y(int o_y) {
        this.o_y = o_y;
    }

    /**
     * @return the d_x
     */
    public int getD_x() {
        return d_x;
    }

    /**
     * @param d_x the d_x to set
     */
    public void setD_x(int d_x) {
        this.d_x = d_x;
    }

    /**
     * @return the d_y
     */
    public int getD_y() {
        return d_y;
    }

    /**
     * @param d_y the d_y to set
     */
    public void setD_y(int d_y) {
        this.d_y = d_y;
    }
}
