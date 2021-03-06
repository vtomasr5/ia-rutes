/*
 * Finestra.java
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
package graficos;

import elementos.Ciutat;
import elementos.Element;
import elementos.Mapa_mon;
import elementos.Vol;
import estructuras.Coa_ciutats;
import estructuras.Fitxer;
import estructuras.LlistaVols;
import estructuras.Pila_ciutats;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Finestra extends javax.swing.JFrame {

    /** Creates new form Finestra */
    public Finestra() {
        initComponents();
        initLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(popupMenu);
        lblCoord.setText("X: - Y: -");
//        TextoSalida.setVisible(false);
        inicializar_matriz_vuelos();
        init();
        inicializar_matriz_distancias();
    }
   
    private String origen, destino = "";
    private int codigo = 1;
    private static final int max_ciudades = 100;
    private int ciutats_creades = 0;
    private Ciutat[] ciutats = new Ciutat[max_ciudades];
//    private Vol[] vuelos;
    private LlistaVols[][] llistaVols = new LlistaVols[max_ciudades][max_ciudades];
    private Mapa_mon mundo;
    private JPanel PanelMundo;
    private double[][] distancies = new double[max_ciudades][max_ciudades];
    private JDialog DCreaVuelo;
    private JDialog DCreaCiudad;
    private JDialog DAbout;
    private JDialog DSalidaCosteTiempo;
    private JDialog DGenAle;
    private JDialog DLlistarVols;
    
    public static void mostrarMissatgeError(JComponent parent, Exception e) {
        final JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Sans-Serif", Font.PLAIN, 11));
        textArea.setEditable(false);
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        textArea.setText(writer.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));

        JOptionPane.showMessageDialog(parent, scrollPane, "Ha ocorregut un error.", JOptionPane.ERROR_MESSAGE);
    }
    
    private void initLookAndFeel() {
        String lookAndFeel;
        String osname = System.getProperty("os.name").toLowerCase();
        
        if (osname.equals("linux")) {
            lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        } else if (osname.startsWith("windows")) {
            lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        } else if (osname.startsWith("mac")) {
            lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        } else {
            lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        }

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            mostrarMissatgeError(null, e);
        }
    }    
    
    private void print(String str) {
        //TextoSalida.append(str);
    }

    private void pinta_mapa() {
        for (int i = 0; i < ciutats_creades; i++) {
            for (int j = 0; j < ciutats_creades; j++) {
                llistaVols[i][j].pinta_vuelo();
            }
        }
    }

    private void mostra_vols() {
        for (int i = 0; i < ciutats_creades; i++) {
            for (int j = 0; j < ciutats_creades; j++) {
                llistaVols[i][j].imprimir();
            }
        }
    }

    public void reiniciarTabla() {
        for (int j = 0; j < ResultadosCoste.getRowCount(); j++) {
            ResultadosCoste.setValueAt("", j, 0);
            ResultadosCoste.setValueAt("", j, 1);
            ResultadosCoste.setValueAt("", j, 2);
            ResultadosCoste.setValueAt("", j, 3);
            ResultadosCoste.setValueAt(null, j, 4);
        }
        for (int j = 0; j < ResultadosTiempo.getRowCount(); j++) {
            ResultadosTiempo.setValueAt("", j, 0);
            ResultadosTiempo.setValueAt("", j, 1);
            ResultadosTiempo.setValueAt("", j, 2);
            ResultadosTiempo.setValueAt("", j, 3);
            ResultadosTiempo.setValueAt(null, j, 4);
        }
    }

    /**
     * Cerca la posició de dins l'array Ciutats.
     * @param nombre_ciudad
     * @return la posició o index de la ciutat pasada per parametre dins l'array de Ciutats.
     */
    private int posicion_ciudad_str(String nombre_ciudad) {
        boolean encontrada = false;
        int posicion = 0;
        while (encontrada == false && posicion <= ciutats_creades) {
            if (ciutats[posicion].getnombre().equals(nombre_ciudad)) {
                encontrada = true;
            } else {
                posicion++;
            }
        }
        if (encontrada == true) {
            return posicion;
        } else {
            return -1;
        }
    }

    /**
     * Cerca la posició dins l'array de ciutats.
     * @param nombre_ciudad
     * @return 
     */
    private int posicion_ciudad(String nombre_ciudad) {
        boolean encontrada = false;
        int posicion = 0;
        if (nombre_ciudad.equals("1")) {
            nombre_ciudad = origen;
        } else {
            nombre_ciudad = destino;
        }
        if (!destino.equals("")) {
            while (encontrada == false && posicion <= ciutats_creades) {
                if (ciutats[posicion].getnombre().equals(nombre_ciudad)) {
                    encontrada = true;
                } else {
                    posicion++;
                }
            }
            if (encontrada == true) {
                return posicion;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private void inicializar_matriz_vuelos() {
        for (int i = 0; i < max_ciudades; i++) {
            for (int j = 0; j < max_ciudades; j++) {
                llistaVols[i][j] = new LlistaVols();
            }
        }
    }

    private void inicializar_matriz_distancias() {
        for (int i = 0; i < max_ciudades; i++) {
            for (int j = 0; j < max_ciudades; j++) {
                distancies[i][j] = 0;
            }
        }
    }

    private void actualizar_matriz_distancias() {
        int x1, x2, y1, y2;
        double distancia;
        for (int i = 0; i < ciutats_creades; i++) {
            x1 = ciutats[i].getcx();
            y1 = ciutats[i].getcy();
            for (int j = 0; j < ciutats_creades; j++) {
                x2 = ciutats[j].getcx();
                y2 = ciutats[j].getcy();
                if (x1 > x2 && y1 > y2) {
                    distancia = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                } else if (x1 > x2 && y1 < y2) {
                    distancia = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y2 - y1, 2));
                } else if (x1 < x2 && y1 > y2) {
                    distancia = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y1 - y2, 2));
                } else {
                    distancia = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                }
                distancies[i][j] = distancia;
            }
        }
    }
    
    private void GuardarFichero() throws FileNotFoundException, IOException {
        JFileChooser FC = new JFileChooser();
        int res = FC.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File nom = FC.getSelectedFile();
            if (nom != null) {
                FileOutputStream fos = new FileOutputStream(nom);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                Fitxer fi = new Fitxer(ciutats, llistaVols, ciutats_creades);
                out.writeObject(fi);
            }
        }
    }

    private void AbrirFichero() throws IOException, ClassNotFoundException {
        JFileChooser FC = new JFileChooser();
//        FC.setFileFilter(new FileNameExtensionFilter("Binaris", "bin"));
        FC.showOpenDialog(null);
        File selFile = FC.getSelectedFile();
        if (selFile != null) {
            FileInputStream fis = new FileInputStream(selFile);
            ObjectInputStream in = new ObjectInputStream(fis);
            Fitxer fi = (Fitxer) in.readObject();
            ciutats = fi.getCiudad();
            llistaVols = fi.getVuelos();
            ciutats_creades = fi.getCiudadescreadas();
            mundo.setCiudad(ciutats);
            mundo.setVuelos(llistaVols);
            mundo.setNumciudades(ciutats_creades);
            for (int i = 0; i < ciutats_creades; i++) {
                OrigenComboBox.addItem(ciutats[i].getnombre());
                OvueloComboBox.addItem(ciutats[i].getnombre());
            }
            repaint();
        }
    }

//    private void mostrar_ciutats() {
//        int pos = 0;
//        for (int i = 0; i < ciutats_creades; i++) {
//            pos = posicion_ciudad_str(ciutats[i].getnombre());
//            textAreaLlistaVols.append(ciutats[i].getnombre() + ", " + ciutats[i].getcx() + ", " + ciutats[i].getcy() + "\n");
//            textAreaLlistaVols.append(Integer.toString(pos));
//
//        }
//    }

    private void busqueda_prof(String ciudad_origen, String ciudad_destino, int tipus) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Pila_ciutats p = new Pila_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        Element ciudad_actual;
        int nodos_visitados = 0, nodos_expandidos = 0;
        Date hora_llegada_origen = new Date(); 
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat origen1 = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        p.añadir_pila(origen1.getnombre(), origen1.getcx(), origen1.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos);
        // mientras la pila no este vacia
        while (p.Vacia() == false) {
            iteraciones++;
            ciudad_actual = p.quitar_pila();
            nodos_visitados++;
            // comprovamos que la ciudad origen sea distinta a la ciudad destino
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                // obtenemos la posicion de la ciudad actual
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                // recorremos todas las ciudades creadas en el mapa
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    // si no hay vuelo entre esas dos ciudades...
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        //... comprovamos que no sea la misma ciudad.
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            // miramos el siguiente vuelo
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            // para los vuelos desde la ciudad de origen a las demas ciudades del mapa...
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                // ... si la hora salida del vuelo > hora llegada de la ciudad actual
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    // buscamos la posicion de la ciudad
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    // encontramos la ciudad
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    if (tipus == 0) { // tipus = 0 -> busqueda por precio, si tipus = 1 -> busqueda por tiempo
                                        // la añadirmos a la pila
                                        p.añadir_pila(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida);
                                    } else {
                                        if (iteraciones == 1) { // si solo ha hecho una iteración solo hay un vuelo.
                                            p.añadir_pila_tiempo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, sig_vuelo.get_horasal(), sig_vuelo.get_horalleg().getTime() - sig_vuelo.get_horasal().getTime());
                                        } else {
                                            p.añadir_pila_tiempo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_primer_vuelo(), sig_vuelo.get_horalleg().getTime() - ciudad_actual.get_primer_vuelo().getTime());
                                        }
                                    }
                                }
                                // pasamos al siguiente vuelo y así vamos buscando los vuelos segun el criterio de busqueda.
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else { // si la ciudad origen es la misma que la ciudad destino enconces 
                if (tipus == 0) {
                    if (num_vuelos_candidatos == 0) {
                        vuelos_candidatos = ciudad_actual;
                    } else {
                        if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                            vuelos_candidatos = ciudad_actual;
                        }
                    }
                } else {
                    if (num_vuelos_candidatos == 0) {
                        vuelos_candidatos = ciudad_actual;
                    } else {
                        if (vuelos_candidatos.get_tiempo_acum() > ciudad_actual.get_tiempo_acum()) {
                            vuelos_candidatos = ciudad_actual;
                        }
                    }
                }
                num_vuelos_candidatos++;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA EN PROFUNDITAT\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("\nNodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps d'execució: " + campod.toString() + "\n");
        if (tipus == 0) {
            elegir_mejor_alternativa_coste(vuelos_candidatos);
        } else {
            elegir_mejor_alternativa_tiempo_nueva(vuelos_candidatos);
        }

    }

    private void elegir_mejor_alternativa_coste(Element vuelos_candidatos) {
        Element vuelo_elegido = vuelos_candidatos;
        if (vuelo_elegido != null) {
            print(vuelo_elegido.get_precio() + "\n");
            print("Preu total: " + vuelo_elegido.get_precio() + "\n");
            print("MINIM COST" + "\n");
            print("La ruta a seguir es: " + "\n");
            int i = 0;
            // buscamos si el vuelo tiene aluna ciudad destino
            while (!vuelo_elegido.get_ciudades()[i].equals("")) {
                print(vuelo_elegido.get_ciudades()[i] + "\n");
                print(vuelo_elegido.get_ciudades()[i]);
                if (!vuelo_elegido.get_ciudades()[i + 1].equals("")) {
                    print(" + ");
                    ResultadosCoste.setValueAt(vuelo_elegido.get_ciudades()[i], i, 0);
                    ResultadosCoste.setValueAt(vuelo_elegido.get_ciudades()[i + 1], i, 1);
                }
                i++;
            }
            i = 1;
            print("La llista de vols a escollir es: " + "\n");
            int numCiu = 0;
            // mostramos el resultado en la tabla
            while (vuelo_elegido.get_vuelos()[i] != null) {
                print("Codi: " + vuelo_elegido.get_vuelos()[i].get_codigo() + "\n"
                        + "Hora sortida: " + vuelo_elegido.get_vuelos()[i].get_horasal() + "\nHora arribada: "
                        + vuelo_elegido.get_vuelos()[i].get_horalleg() + "\n");
                ResultadosCoste.setValueAt(vuelo_elegido.get_vuelos()[i].get_horasal(), i - 1, 2);
                ResultadosCoste.setValueAt(vuelo_elegido.get_vuelos()[i].get_horalleg(), i - 1, 3);
                ResultadosCoste.setValueAt(vuelo_elegido.get_vuelos()[i].get_precio(), i - 1, 4);
                i++;
                numCiu++;
            }
            Float pre;
            pre = vuelo_elegido.get_precio();
            campoCostetotal.setText(pre.toString());
            mundo.setVuelosRuta(vuelo_elegido.get_vuelos(), numCiu);
            mundo.setModoRuta(1);
//            vuelos = vuelo_elegido.get_vuelos();
            repaint();
        }
        print("\n");
    }

    private void elegir_mejor_alternativa_tiempo_nueva(Element vuelos_candidatos) throws ParseException {
        Element vuelo_elegido = vuelos_candidatos;
        if (vuelo_elegido != null) {
            print(vuelo_elegido.get_precio() + "\n");
            print("MINIM TEMPS" + "\n");
            print("La duració del viatje es: ");
            // calculamos la duración del viaje
            Long tie = (vuelo_elegido.get_horalleg().getTime() - vuelo_elegido.get_primer_vuelo().getTime()) / 3600000;
            campoTiempotoTi.setText(tie.toString());
            print("Preu total: " + vuelo_elegido.get_precio() + "\n");
            print("Temps total: " + tie.toString() + "\n");
            print((vuelo_elegido.get_horalleg().getTime() - vuelo_elegido.get_primer_vuelo().getTime()) / 3600000 + " hores." + "\n");
            print("La ruta a seguir es: " + "\n");
            int i = 0;
            // buscamos si el vuelo tiene aluna ciudad destino
            while (!vuelo_elegido.get_ciudades()[i].equals("")) {
                print(vuelo_elegido.get_ciudades()[i] + "\n");
                print(vuelo_elegido.get_ciudades()[i]);
                if (!vuelo_elegido.get_ciudades()[i + 1].equals("")) {
                    ResultadosTiempo.setValueAt(vuelo_elegido.get_ciudades()[i], i, 0);
                    ResultadosTiempo.setValueAt(vuelo_elegido.get_ciudades()[i + 1], i, 1);
                    print(" + ");
                }
                i++;
            }
            i = 1;
            print("La llista de vols a escollir es: " + "\n");
            int numCiu = 0;
            // mostramos los resultados en la tabla
            while (vuelo_elegido.get_vuelos()[i] != null) {
                print("Codi: " + vuelo_elegido.get_vuelos()[i].get_codigo() + "\n"
                        + "Hora sortida: " + vuelo_elegido.get_vuelos()[i].get_horasal() + "\nHora arribada: "
                        + vuelo_elegido.get_vuelos()[i].get_horalleg() + "\n");
                ResultadosTiempo.setValueAt(vuelo_elegido.get_vuelos()[i].get_horasal(), i - 1, 2);
                ResultadosTiempo.setValueAt(vuelo_elegido.get_vuelos()[i].get_horalleg(), i - 1, 3);
                tie = (vuelo_elegido.get_vuelos()[i].get_horalleg().getTime() - vuelo_elegido.get_vuelos()[i].get_horasal().getTime()) / 3600000;
                tie.toString();
                ResultadosTiempo.setValueAt(tie, i - 1, 4);
                i++;
                numCiu++;
            }
            mundo.setVuelosRuta(vuelo_elegido.get_vuelos(), numCiu);
            mundo.setModoRuta(1);
            Float pre;
            pre = vuelo_elegido.get_precio();
            campoPretoti.setText(pre.toString());
//            vuelos = vuelo_elegido.get_vuelos();
            repaint();
        }
        print("\n");
    }

    private void busqueda_prof_poda_coste(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Pila_ciutats p = new Pila_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        float mejor_coste = Float.MAX_VALUE;
        Vol sig_vuelo;
        boolean ciclo;
        Element ciudad_actual;
        int nodos_visitados = 0, nodos_expandidos = 0;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat origen2 = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        p.añadir_pila(origen2.getnombre(), origen2.getcx(), origen2.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos);
        // mientras la pila de ciudades no este vacia
        while (p.Vacia() == false) {
            iteraciones++;
            // cogemos la primera ciudad
            ciudad_actual = p.quitar_pila();
            nodos_visitados++;
            // si la ciudad origen != ciudad destino
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                // buscamos su posición en la lista
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                // para todas la ciudades creadas en el mapa
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    // si hay vuelo entre la ciudad de origen i alguna ciudad del mapa
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        // comprovamos que el vuelo vuelva a la misma ciudad de partida
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            // para todos los vuelos
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                // miramos si la hora salida > hora llegada de la ciudad i que el precio sea el mas barato
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg()) && (ciudad_actual.get_precio()) < mejor_coste) {
                                    int posicion_array = 0;                                                                       
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    // buscamos la posición de de las ciudad en la lista
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    // agregamos la ciudad a la lista de ciudades y sus vuelos
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    // lo guardamos a la pila
                                    p.añadir_pila(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida);
                                }
                                // vamos a mirar el siguiente vuelo
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                    mejor_coste = ciudad_actual.get_precio();
                } else {
                    if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                        vuelos_candidatos = ciudad_actual;
                        mejor_coste = ciudad_actual.get_precio();
                    }
                }
                num_vuelos_candidatos++;
            }
        }
        tiempo2 = System.nanoTime();
        // calculamos el tiempo de ejecución
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA EN PROFUNDITAT AMB PODA" + "\n");
        print("iteracions"+String.valueOf(iteraciones)+"\n");
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        // mostramos los resultados
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("\nNodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_coste(vuelos_candidatos);
    }

    private void busqueda_prof_poda_tiempo(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Pila_ciutats p = new Pila_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        long mejor_tiempo = Long.MAX_VALUE;
        boolean pasar;
        Vol sig_vuelo;
        boolean ciclo;
        Element ciudad_actual;
        int nodos_visitados = 0, nodos_expandidos = 0;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat origen3 = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        // inicio del calculo del tiempo de ejecución del algoritmo
        tiempo = System.nanoTime();
        // añadimos a la pila los valores iniciales
        p.añadir_pila(origen3.getnombre(), origen3.getcx(), origen3.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos);
        // mientras haya ciudades en la pila
        while (p.Vacia() == false) {
            iteraciones++;
            ciudad_actual = p.quitar_pila();
            nodos_visitados++;
            // si la ciudad actual != ciudad destino
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                // obtenemos su posición en la lista de ciudades
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                // para todas las ciudades creadas en el mapa
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    // si hay vuelos creados entre la ciudad actual y otras ciudades
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        // comprovamos que no haya ciclo
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            // obtenemos el vuelo que sale de la ciudad actual
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            // para todos los vuelos de esa ciudad
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                pasar = false;
                                if (iteraciones == 1) {
                                    pasar = true;
                                } else {
                                    // si la hora llegada de la ciudad actual menos el tiempo del vuelo < mejor tiempo
                                    if ((ciudad_actual.get_horalleg().getTime() - ciudad_actual.get_primer_vuelo().getTime()) < mejor_tiempo) {
                                        pasar = true;
                                    }
                                }
                                // si la hora salida del vuelo > hora llegada de la ciudad actual
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg()) && pasar) {
                                    int posicion_array = 0;                                                                      
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    // buscamos la posición de la ciudad obtenida
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    // guardamos la ciudad y los vuelos
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    if (iteraciones == 1) {
                                        p.añadir_pila_tiempo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, sig_vuelo.get_horasal(), sig_vuelo.get_horalleg().getTime() - sig_vuelo.get_horasal().getTime());
                                    } else {
                                        p.añadir_pila_tiempo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_primer_vuelo(), sig_vuelo.get_horalleg().getTime() - ciudad_actual.get_primer_vuelo().getTime());
                                    }
                                }
                                // miramos el siguiente vuelo
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                // miramos el mejor tiempo de todos los vuelos
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                    mejor_tiempo = ciudad_actual.get_tiempo_acum();
                } else {
                    if (vuelos_candidatos.get_tiempo_acum() > ciudad_actual.get_tiempo_acum()) {
                        vuelos_candidatos = ciudad_actual;
                        mejor_tiempo = ciudad_actual.get_tiempo_acum();
                    }
                }
                num_vuelos_candidatos++;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA EN PROFUNDITAT AMB PODA" + "\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        // mostramos los resultados
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("\nNodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps d'execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_tiempo_nueva(vuelos_candidatos);
    }

    private void busqueda_costo_uniforme_coste(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Coa_ciutats c = new Coa_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        boolean acabar = false;
        int nodos_visitados = 0, nodos_expandidos = 0;
        Element ciudad_actual;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat origen4 = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        // inicio del tiempo de ejecución del algoritmo
        tiempo = System.nanoTime();
        // añadimos a la cola las ciudades y los vuelos creados
        c.añadir_cola_costee(origen4.getnombre(), origen4.getcx(), origen4.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos);
        // mientras la cola no este vacia
        while (c.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = c.quitar_cola_coste();
            nodos_visitados++;
            // si la ciudad actual != ciudad destino
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                // obtenemos la posición de la ciudad actual dentro del vector de ciudades
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                // para cada ciudad creada del mapa
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    // si hay vuelos entre la ciudad actual y las demás ciudades
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        // comprovamos que no haya ciclo
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            // cogemos el primer vuelo
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            // para todos los vuelos entre la ciudad actual y las demás ciudades
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                // si la hora salida del vuelo > hora llegada de la ciudad actual
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    // buscamos la posición de la ciudad obtenida 
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    // guardamos la ciudad y el vuelo
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    // añadimos los resultados a la cola
                                    c.añadir_cola_costee(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida);
                                }
                                // vamos a por el siguiente vuelo
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                } else {
                    if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                        vuelos_candidatos = ciudad_actual;
                    }
                }
                num_vuelos_candidatos++;
                acabar = true;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA DE COST UNIFORME\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        // mostramos los resultados
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("\nNodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps d'execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_coste(vuelos_candidatos);
    }

    private void busqueda_costo_uniforme_tiempo(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Coa_ciutats c = new Coa_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        boolean acabar = false;
        int nodos_visitados = 0, nodos_expandidos = 0;
        Element ciudad_actual;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat ciu_origen = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        // añadimos a la cosa las ciudades y los vuelos
        c.añadir_cola_tiempoo(ciu_origen.getnombre(), ciu_origen.getcx(), ciu_origen.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos, null, 0);
        while (c.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = c.quitar_cola_tiempo();
            nodos_visitados++;
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    if (iteraciones == 1) {
                                        c.añadir_cola_tiempoo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, sig_vuelo.get_horasal(), sig_vuelo.get_horalleg().getTime() - sig_vuelo.get_horasal().getTime());
                                    } else {
                                        c.añadir_cola_tiempoo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_primer_vuelo(), sig_vuelo.get_horalleg().getTime() - ciudad_actual.get_primer_vuelo().getTime());
                                    }
                                }
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                } else {
                    if (vuelos_candidatos.get_tiempo_acum() > ciudad_actual.get_tiempo_acum()) {
                        vuelos_candidatos = ciudad_actual;
                    }
                }
                num_vuelos_candidatos++;
                acabar = true;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA DE COST UNIFORME" + "\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("\nNodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_tiempo_nueva(vuelos_candidatos);
    }

    private void busqueda_vecino_mas_proximo(String ciudad_origen, String ciudad_destino, int tipus) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Pila_ciutats p = new Pila_ciutats();
        Coa_ciutats cola_ordenacion_local;
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        int nodos_visitados = 0, nodos_expandidos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        boolean acabar = false;
        Element ciudad_actual;
        Element proxima_ciudad;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat origen6 = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        p.añadir_pila(origen6.getnombre(), origen6.getcx(), origen6.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos);
        while (p.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = p.quitar_pila();
            nodos_visitados++;
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                cola_ordenacion_local = new Coa_ciutats();
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;                                    
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    if (tipus == 0) {
                                        cola_ordenacion_local.añadir_cola_coste(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida);
                                    } else {
                                        if (iteraciones == 1) {
                                            cola_ordenacion_local.añadir_cola_tiempo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, sig_vuelo.get_horasal(), sig_vuelo.get_horalleg().getTime() - sig_vuelo.get_horasal().getTime());
                                        } else {
                                            cola_ordenacion_local.añadir_cola_tiempo(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_primer_vuelo(), sig_vuelo.get_horalleg().getTime() - ciudad_actual.get_primer_vuelo().getTime());
                                        }
                                    }
                                }
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
                while (cola_ordenacion_local.Vacia() == false) {
                    proxima_ciudad = cola_ordenacion_local.quitar_cola_ultimo();
                    nodos_expandidos++;
                    p.añadir_pila_tiempo(proxima_ciudad.getnombre(), proxima_ciudad.getcx(), proxima_ciudad.getcy(), proxima_ciudad.get_horalleg(), proxima_ciudad.get_precio(), proxima_ciudad.get_ciudades(), proxima_ciudad.get_vuelos(), proxima_ciudad.get_primer_vuelo(), proxima_ciudad.get_tiempo_acum());
                }
            } else {
                if (tipus == 0) {
                    if (num_vuelos_candidatos == 0) {
                        vuelos_candidatos = ciudad_actual;
                    } else {
                        if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                            vuelos_candidatos = ciudad_actual;
                        }
                    }
                } else {
                    if (num_vuelos_candidatos == 0) {
                        vuelos_candidatos = ciudad_actual;
                    } else {
                        if (vuelos_candidatos.get_tiempo_acum() > ciudad_actual.get_tiempo_acum()) {
                            vuelos_candidatos = ciudad_actual;
                        }
                    }
                }
                acabar = true;
                num_vuelos_candidatos++;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA DEL VEÍ MÉS PRÒXIM\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("\nNodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        if (tipus == 0) {
            elegir_mejor_alternativa_coste(vuelos_candidatos);
        } else {
            elegir_mejor_alternativa_tiempo_nueva(vuelos_candidatos);
        }

    }

    private int vuelos_desde_ciudad(String ciudad_actual, String ciudad_destino) {
        if (ciudad_actual.equals(ciudad_destino)) {
            return 1000;
        } else {
            int num_vuelos = 0;
            for (int j = 0; j < ciutats_creades; j++) {
                num_vuelos = num_vuelos + llistaVols[posicion_ciudad(ciudad_actual)][j].numero_vuelos();
            }
            return num_vuelos;
        }
    }

    private void busqueda_a_estrella_cost(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Coa_ciutats c = new Coa_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        Vol sig_vuelo;
        boolean ciclo;
        int nodos_visitados = 0, nodos_expandidos = 0;
        boolean acabar = false;
        Element ciudad_actual;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat ciu_origen = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        c.añadir_cola_distanciaa(ciu_origen.getnombre(), ciu_origen.getcx(), ciu_origen.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos, 0);
        while (c.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = c.quitar_cola_dist();
            nodos_visitados++;
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    c.añadir_cola_distanciaa(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_precio() + ((float) distancies[pos_ciudad_or][posicion_ciudad(ciudad_destino)] * 2));
                                }
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                vuelos_candidatos = ciudad_actual;
                acabar = true;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA DE A* 1º HEURISTICA" + "\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("Nodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_coste(vuelos_candidatos);
    }

    private void busqueda_a_estrella_temps(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Coa_ciutats c = new Coa_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        int nodos_visitados = 0, nodos_expandidos = 0;
        boolean acabar = false;
        Element ciudad_actual;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat ciu_origen = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        c.añadir_cola_tiempoo(ciu_origen.getnombre(), ciu_origen.getcx(), ciu_origen.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos, null, 0);
        while (c.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = c.quitar_cola_dist();
            nodos_visitados++;
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    if (iteraciones == 1) {
                                        nodos_expandidos++;
                                        c.añadir_cola_distanciaa2(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, sig_vuelo.get_horasal(), distancies[pos_ciudad_or][posicion_ciudad(ciudad_destino)] * 120000);
                                    } else {
                                        nodos_expandidos++;
                                        c.añadir_cola_distanciaa2(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_primer_vuelo(), (sig_vuelo.get_horasal().getTime() - ciudad_actual.get_primer_vuelo().getTime()) + (distancies[pos_ciudad_or][posicion_ciudad(ciudad_destino)] * 120000));
                                    }
                                }
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                } else {
                    if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                        vuelos_candidatos = ciudad_actual;
                    }
                }
                num_vuelos_candidatos++;
                acabar = true;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA EN A* 1º HEURISTICA\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("Nodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_tiempo_nueva(vuelos_candidatos);
    }

    private void busqueda_a_estrella2_cost(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Coa_ciutats c = new Coa_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        int nodos_visitados = 0, nodos_expandidos = 0;
        boolean acabar = false;
        Element ciudad_actual;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat ciu_origen = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        c.añadir_cola_distanciaa(ciu_origen.getnombre(), ciu_origen.getcx(), ciu_origen.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos, 0);
        while (c.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = c.quitar_cola_dist();
            nodos_visitados++;
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    nodos_expandidos++;
                                    c.añadir_cola_distanciaa(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_precio() + sig_vuelo.get_precio() - (vuelos_desde_ciudad(ciutats[i].getnombre(), ciudad_destino) * 0.14));
                                }
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                } else {
                    if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                        vuelos_candidatos = ciudad_actual;
                    }
                }
                num_vuelos_candidatos++;
                acabar = true;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA EN A* 2º HEURISTICA\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("Nodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_coste(vuelos_candidatos);
    }

    private  void busqueda_a_estrella2_temps(String ciudad_origen, String ciudad_destino) throws ParseException {
        double tiempo, tiempo2, tiempo3;
        Coa_ciutats c = new Coa_ciutats();
        int pos_ciudad_or;
        int iteraciones = 0;
        int num_vuelos_candidatos = 0;
        Vol sig_vuelo;
        boolean ciclo;
        int nodos_visitados = 0, nodos_expandidos = 0;
        boolean acabar = false;
        Element ciudad_actual;
        Date hora_llegada_origen = new Date();
        String[] lista_ciudades = new String[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_ciudades[i] = "";
        }
        Vol[] lista_vuelos = new Vol[max_ciudades];
        for (int i = 0; i < max_ciudades; i++) {
            lista_vuelos[i] = null;
        }
        Element vuelos_candidatos = null;
        Ciutat origen12 = ciutats[posicion_ciudad_str(ciudad_origen)];
        lista_ciudades[0] = ciudad_origen;
        lista_vuelos[0] = null;
        Vol[] lista_vuelos_obtenida = new Vol[max_ciudades];
        String[] lista_ciudades_obtenida = new String[max_ciudades];
        nodos_expandidos++;
        
        tiempo = System.nanoTime();
        c.añadir_cola_tiempoo(origen12.getnombre(), origen12.getcx(), origen12.getcy(), hora_llegada_origen, 0, lista_ciudades, lista_vuelos, null, 0);
        while (c.Vacia() == false && !acabar) {
            iteraciones++;
            ciudad_actual = c.quitar_cola_dist();
            nodos_visitados++;
            if (!ciudad_actual.getnombre().equals(ciudad_destino)) {
                pos_ciudad_or = posicion_ciudad_str(ciudad_actual.getnombre());
                for (int i = ciutats_creades - 1; i >= 0; i--) {
                    if (llistaVols[pos_ciudad_or][i].Vacia() == false) {
                        ciclo = hay_ciclo(ciudad_actual.get_ciudades(), ciutats[i].getnombre());
                        if (ciclo == false) {
                            sig_vuelo = llistaVols[pos_ciudad_or][i].primero();
                            for (int z = 0; z < llistaVols[pos_ciudad_or][i].numero_vuelos(); z++) {
                                if (sig_vuelo.get_horasal().after(ciudad_actual.get_horalleg())) {
                                    int posicion_array = 0;
                                    System.arraycopy(ciudad_actual.get_vuelos(), 0, lista_vuelos_obtenida, 0, ciudad_actual.get_vuelos().length);
                                    System.arraycopy(ciudad_actual.get_ciudades(), 0, lista_ciudades_obtenida, 0, ciudad_actual.get_ciudades().length);
                                    while (!lista_ciudades_obtenida[posicion_array].equals("")) {
                                        posicion_array++;
                                    }
                                    lista_ciudades_obtenida[posicion_array] = ciutats[i].getnombre();
                                    lista_vuelos_obtenida[posicion_array] = sig_vuelo;
                                    if (iteraciones == 1) {
                                        nodos_expandidos++;
                                        c.añadir_cola_distanciaa2(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, sig_vuelo.get_horasal(), -vuelos_desde_ciudad(ciutats[i].getnombre(), ciudad_destino) * 3000);
                                    } else {
                                        nodos_expandidos++;
                                        c.añadir_cola_distanciaa2(ciutats[i].getnombre(), ciutats[i].getcx(), ciutats[i].getcy(), sig_vuelo.get_horalleg(), ciudad_actual.get_precio() + sig_vuelo.get_precio(), lista_ciudades_obtenida, lista_vuelos_obtenida, ciudad_actual.get_primer_vuelo(), (sig_vuelo.get_horasal().getTime() - ciudad_actual.get_primer_vuelo().getTime()) - (vuelos_desde_ciudad(ciutats[i].getnombre(), ciudad_destino) * 3000));//120000*0,07
                                    }
                                }
                                sig_vuelo = llistaVols[pos_ciudad_or][i].siguiente_vuelo(sig_vuelo);
                            }
                        }
                    }
                }
            } else {
                if (num_vuelos_candidatos == 0) {
                    vuelos_candidatos = ciudad_actual;
                } else {
                    if (vuelos_candidatos.get_precio() > ciudad_actual.get_precio()) {
                        vuelos_candidatos = ciudad_actual;
                    }
                }
                num_vuelos_candidatos++;
                acabar = true;
            }
        }
        tiempo2 = System.nanoTime();
        tiempo3 = (tiempo2 - tiempo);
        print("\nCERCA EN A* 2º HEURISTICA" + "\n");
        print(String.valueOf(iteraciones));
        print("Temps invertit: " + (tiempo3 / Math.pow(10, 9)) + " segons." + "\n");
        print("Nodes visitats: " + nodos_visitados + "\n");
        print("Nodes expandits: " + nodos_expandidos + "\n");
        print("El mínim cost per anar de: " + ciudad_origen + " a " + ciudad_destino + " es: ");
        Double t = tiempo3 / Math.pow(10, 9);
        Integer campo;
        Float campod;
        campod = t.floatValue();
        campo = nodos_visitados;
        campoNodovis.setText(campo.toString());
        campoNodovisTi.setText(campo.toString());
        print("Nodes visitats: " + campo.toString() + " , ");
        campo = nodos_expandidos;
        campoNodoexp.setText(campo.toString());
        campoNodoexTi.setText(campo.toString());
        print("Nodes expandits: " + campo.toString() + " , ");
        campoTiempoeje.setText(campod.toString());
        campoTiempoejeTi.setText(campod.toString());
        print("Temps execució: " + campod.toString() + "\n");
        elegir_mejor_alternativa_tiempo_nueva(vuelos_candidatos);
    }
    
    private boolean hay_ciclo(String[] lista_ciudades, String ciudad_futura) {
        for (int i = 0; i < max_ciudades; i++) {
            if (lista_ciudades[i].equals(ciudad_futura)) {
                return true;
            }
        }
        return false;
    }

    private void añadir_ciudad_auto(int numero_ciudades_crear) {
        final int max_coord_x = mundo.getIcon().getIconWidth() - 8;
        final int max_coord_y = mundo.getIcon().getIconHeight() - 8;
        int coord_x;
        int coord_y;
        for (int j = 0; j < numero_ciudades_crear; j++) {
            coord_x = (int) (Math.random() * max_coord_x);
            coord_y = (int) (Math.random() * max_coord_y);
            String nombre_ciudad1 = "Ciutat-" + String.valueOf(j);
            ciutats[ciutats_creades] = new Ciutat(nombre_ciudad1, coord_x, coord_y);
            ciutats_creades = ciutats_creades + 1;
            OrigenComboBox.addItem(ciutats[ciutats_creades - 1].getnombre());
            OvueloComboBox.addItem(ciutats[ciutats_creades - 1].getnombre());
            if (ciutats_creades - 1 != 0) {
                DestinoComboBox.addItem(ciutats[ciutats_creades - 1].getnombre());
            }
        }
        mundo.setNumciudades(ciutats_creades);
        repaint();
    }

    private void añadir_vuelo_auto(int numero_vuelos_crear) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        float precio;
        Date fechaActual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy ");
        String Fecha = formato.format(fechaActual);
        String hora_salida;
        String hora_llegada;
        String hora;
        String minuto;
        String minuto2;
        double distancia;
        int posicion_x;
        int posicion_y;
        for (int j = 0; j < numero_vuelos_crear; j++) {
            String compañia = "Companyia-" + String.valueOf(j);
            hora = String.valueOf((int) (Math.random() * 24 + 720));
            if (hora.equals("12")) {
                hora = "11";
                minuto = "60";
            } else {
                minuto = "00";
            }
            posicion_x = 0;
            posicion_y = 0;
            while (posicion_x == posicion_y) {
                posicion_x = (int) (Math.random() * ciutats_creades);
                posicion_y = (int) (Math.random() * ciutats_creades);
            }
            minuto = Integer.toString(Integer.valueOf(minuto) + (int) (Math.random() * 60));
            distancia = distancies[posicion_x][posicion_y];
            minuto2 = String.valueOf(Integer.parseInt(minuto) + (int) (distancia * 2));
            precio = (int) (distancia * 2);
            hora_salida = Fecha + hora + ":" + minuto;
            hora_llegada = Fecha + hora + ":" + minuto2;
            for (int i = 0; i < 5; i++) {
                llistaVols[posicion_x][posicion_y].insertarnuevo(j, compañia, precio, df.parse(hora_salida), df.parse(hora_llegada), ciutats[posicion_x].getcx(), ciutats[posicion_x].getcy(), ciutats[posicion_y].getcx(), ciutats[posicion_y].getcy());
                minuto = Integer.toString(Integer.parseInt(minuto) + 1440);
                minuto2 = Integer.toString(Integer.parseInt(minuto2) + 1440);
                hora_salida = Fecha + hora + ":" + minuto;
                hora_llegada = Fecha + hora + ":" + minuto2;
            }
        }
    }

    private void conectar_todas_ciudades() throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        float precio;
        Date fechaActual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy ");
        String Fecha = formato.format(fechaActual);
        String hora_salida;
        String hora_llegada;
        String hora;
        String minuto;
        String minuto2;
        double distancia;
        int posicion_x;
        int posicion_y;
        for (int j = 0; j < ciutats_creades; j++) {
            for (int k = 0; k < ciutats_creades; k++) {
                if (j != k) {
                    String compañia = "Companyia-" + String.valueOf(j);
                    hora = String.valueOf((int) (Math.random() * 24) + 720);
                    if (hora.equals("12")) {
                        hora = "11";
                        minuto = "60";
                    } else {
                        minuto = "00";
                    }
                    posicion_x = j;
                    posicion_y = k;
                    minuto = Integer.toString(Integer.valueOf(minuto) + (int) (Math.random() * 60));
                    distancia = distancies[posicion_x][posicion_y];
                    minuto2 = String.valueOf(Integer.parseInt(minuto) + (int) (distancia * 2));
                    precio = (int) (distancia * 2);
                    hora_salida = Fecha + hora + ":" + minuto;
                    hora_llegada = Fecha + hora + ":" + minuto2;
                    for (int i = 0; i < 5; i++) {
                        llistaVols[posicion_x][posicion_y].insertarnuevo(j, compañia, precio, df.parse(hora_salida), df.parse(hora_llegada), ciutats[posicion_x].getcx(), ciutats[posicion_x].getcy(), ciutats[posicion_y].getcx(), ciutats[posicion_y].getcy());
                        minuto = Integer.toString(Integer.parseInt(minuto) + 1440);
                        minuto2 = Integer.toString(Integer.parseInt(minuto2) + 1440);
                        hora_salida = Fecha + hora + ":" + minuto;
                        hora_llegada = Fecha + hora + ":" + minuto2;
                    }
                }
            }
        }
    }

    private void mundoMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            Integer numx = evt.getX();
            Integer numy = evt.getY();
            coordenadax.setText(numx.toString());
            coordenaday.setText(numy.toString());
            popupMenu.show(PanelMundo, evt.getX(), evt.getY());
        } else {
            lblCoord.setFont(new Font("Lucida Grande", Font.BOLD, 14));
            lblCoord.setForeground(Color.red);
            Integer numx = evt.getX();
            Integer numy = evt.getY();
            coordenadax.setText(numx.toString());
            coordenaday.setText(numy.toString());
        }
    }

    private void init() {
        mundo = new Mapa_mon(ciutats, llistaVols);
        PanelMundo = new JPanel();
        javax.swing.GroupLayout PanelMundoLayout = new javax.swing.GroupLayout(PanelMundo);
        PanelMundo.setLayout(PanelMundoLayout);
        PanelMundoLayout.setHorizontalGroup(
                PanelMundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(PanelMundoLayout.createSequentialGroup().addComponent(mundo).addContainerGap(24, Short.MAX_VALUE)));
        PanelMundoLayout.setVerticalGroup(
                PanelMundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(PanelMundoLayout.createSequentialGroup().addComponent(mundo).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        ScrollMundo.setViewportView(PanelMundo);
        mundo.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mundoMouseClicked(evt);
            }
        });
        
        mundo.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lblCoord.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
                lblCoord.setForeground(Color.black);
                lblCoord.setText("X: " + e.getX() + " Y: " + e.getY());
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CritVuelo = new javax.swing.ButtonGroup();
        PanelGenAle = new javax.swing.JPanel();
        CiuGen = new javax.swing.JTextField();
        label_ciugen = new javax.swing.JLabel();
        label_numvuelos = new javax.swing.JLabel();
        personalizado = new javax.swing.JRadioButton();
        todos_con_todos = new javax.swing.JRadioButton();
        texto_numvuelos = new javax.swing.JTextField();
        CreaGenAle = new javax.swing.JButton();
        btGenAleCancelar = new javax.swing.JButton();
        PanelSalidaCoste = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ResultadosCoste = new javax.swing.JTable();
        Paneldatoscoste = new javax.swing.JPanel();
        labelNodovis = new javax.swing.JLabel();
        labelNodoexp = new javax.swing.JLabel();
        labelTiempoeje = new javax.swing.JLabel();
        campoNodovis = new javax.swing.JTextField();
        campoTiempoeje = new javax.swing.JTextField();
        campoNodoexp = new javax.swing.JTextField();
        campoCostetotal = new javax.swing.JTextField();
        labelCosteTotal = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        tipo_busqueda = new javax.swing.ButtonGroup();
        PanelCreaCiudad = new javax.swing.JPanel();
        label_x = new javax.swing.JLabel();
        label_coordenadas = new javax.swing.JLabel();
        label_nombre_ciudad = new javax.swing.JLabel();
        nombre_ciudad = new javax.swing.JTextField();
        label_y = new javax.swing.JLabel();
        coordenadax = new javax.swing.JTextField();
        coordenaday = new javax.swing.JTextField();
        CreaCiudad = new javax.swing.JButton();
        btCrearCiutatCancelar = new javax.swing.JButton();
        PanelCreaVuelo = new javax.swing.JPanel();
        DvueloComboBox = new javax.swing.JComboBox();
        HoraLlegada = new javax.swing.JTextField();
        HoraSalida = new javax.swing.JTextField();
        OvueloComboBox = new javax.swing.JComboBox();
        PrecioVuelo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CreaDestino = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        CreaOrigen = new javax.swing.JLabel();
        nombre_compañia = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        BotonCreavuelo = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btCreaVueloCancelar = new javax.swing.JButton();
        Criterio = new javax.swing.ButtonGroup();
        PanelSalidaTiempo = new javax.swing.JPanel();
        Paneldatostiempo = new javax.swing.JPanel();
        campoNodovisTi = new javax.swing.JTextField();
        campoNodoexTi = new javax.swing.JTextField();
        campoTiempoejeTi = new javax.swing.JTextField();
        labelNodogen1 = new javax.swing.JLabel();
        labelNodoexp1 = new javax.swing.JLabel();
        labelTiempoeje1 = new javax.swing.JLabel();
        labelTiempototalTi = new javax.swing.JLabel();
        campoTiempotoTi = new javax.swing.JTextField();
        labelpretoti = new javax.swing.JLabel();
        campoPretoti = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        ResultadosTiempo = new javax.swing.JTable();
        btPanelSalidaTiempoSortir = new javax.swing.JButton();
        PanelAbout = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        btAboutSortir = new javax.swing.JButton();
        popupMenu = new javax.swing.JPopupMenu();
        popupMenuItemCrearCiutat = new javax.swing.JMenuItem();
        popupMenuItemCrearVol = new javax.swing.JMenuItem();
        PanellLlistarVols = new javax.swing.JPanel();
        btLlistarVols = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        textAreaLlistaVols = new javax.swing.JTextArea();
        PanelBusquedas = new javax.swing.JPanel();
        profundidad_poda = new javax.swing.JRadioButton();
        precio_minimo = new javax.swing.JRadioButton();
        vecino_proximo = new javax.swing.JRadioButton();
        tiempo_minimo = new javax.swing.JRadioButton();
        busqueda_tipo = new javax.swing.JLabel();
        coste_uniforme = new javax.swing.JRadioButton();
        criterio_busqueda = new javax.swing.JLabel();
        a_estrella = new javax.swing.JRadioButton();
        OrigenComboBox = new javax.swing.JComboBox();
        profundidad = new javax.swing.JRadioButton();
        Busqueda = new javax.swing.JLabel();
        DestinoComboBox = new javax.swing.JComboBox();
        Destino = new javax.swing.JLabel();
        Origen = new javax.swing.JLabel();
        a_estrella2 = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        ScrollMundo = new javax.swing.JScrollPane();
        BotonBuscar = new javax.swing.JButton();
        toobar = new javax.swing.JToolBar();
        tbObrir = new javax.swing.JButton();
        tbGuardar = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btTbCrearCiutat = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        lblCoord = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        Menubusquedas = new javax.swing.JMenu();
        MenuNuevo = new javax.swing.JMenuItem();
        menuItemObrir = new javax.swing.JMenuItem();
        menuItemGuardar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        MenuSotirItem = new javax.swing.JMenuItem();
        Menucrear = new javax.swing.JMenu();
        Menucreaciudad = new javax.swing.JMenuItem();
        Menucreavuelo = new javax.swing.JMenuItem();
        Menugenale = new javax.swing.JMenuItem();
        MenuLlistarVols = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        CiuGen.setText("2");

        label_ciugen.setText("Número de ciutats:");

        label_numvuelos.setText("Número de vols:");

        CritVuelo.add(personalizado);
        personalizado.setSelected(true);
        personalizado.setText("Personalitzat");
        personalizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                personalizadoActionPerformed(evt);
            }
        });

        CritVuelo.add(todos_con_todos);
        todos_con_todos.setText("Tots amb tots");
        todos_con_todos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todos_con_todosActionPerformed(evt);
            }
        });

        texto_numvuelos.setText("1");

        CreaGenAle.setText("Generar");
        CreaGenAle.setSelected(true);
        CreaGenAle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreaGenAleActionPerformed(evt);
            }
        });

        btGenAleCancelar.setText("Cancel·lar");
        btGenAleCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGenAleCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelGenAleLayout = new javax.swing.GroupLayout(PanelGenAle);
        PanelGenAle.setLayout(PanelGenAleLayout);
        PanelGenAleLayout.setHorizontalGroup(
            PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGenAleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelGenAleLayout.createSequentialGroup()
                        .addComponent(personalizado, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(todos_con_todos, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                        .addGap(102, 102, 102))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelGenAleLayout.createSequentialGroup()
                        .addComponent(btGenAleCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CreaGenAle))
                    .addGroup(PanelGenAleLayout.createSequentialGroup()
                        .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_ciugen)
                            .addComponent(label_numvuelos))
                        .addGap(12, 12, 12)
                        .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(texto_numvuelos, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                            .addComponent(CiuGen, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))))
                .addContainerGap())
        );
        PanelGenAleLayout.setVerticalGroup(
            PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGenAleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_ciugen)
                    .addComponent(CiuGen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_numvuelos)
                    .addComponent(texto_numvuelos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(personalizado)
                    .addComponent(todos_con_todos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelGenAleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CreaGenAle)
                    .addComponent(btGenAleCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ResultadosCoste.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Origen", "Destí", "Hora sortida", "Hora arribada", "Preu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ResultadosCoste.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultadosCosteMouseClicked(evt);
            }
        });
        ResultadosCoste.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                ResultadosCosteAncestorRemoved(evt);
            }
        });
        jScrollPane2.setViewportView(ResultadosCoste);
        ResultadosCoste.getColumnModel().getColumn(0).setResizable(false);
        ResultadosCoste.getColumnModel().getColumn(1).setResizable(false);
        ResultadosCoste.getColumnModel().getColumn(4).setResizable(false);

        labelNodovis.setText("Nodes visitats:");

        labelNodoexp.setText("Nodes expandits:");

        labelTiempoeje.setText("Temps d'execució:");

        campoNodovis.setEditable(false);
        campoNodovis.setPreferredSize(new java.awt.Dimension(70, 27));

        campoTiempoeje.setEditable(false);
        campoTiempoeje.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        campoTiempoeje.setPreferredSize(new java.awt.Dimension(90, 27));

        campoNodoexp.setEditable(false);
        campoNodoexp.setPreferredSize(new java.awt.Dimension(70, 27));
        campoNodoexp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoNodoexpActionPerformed(evt);
            }
        });

        campoCostetotal.setEditable(false);
        campoCostetotal.setPreferredSize(new java.awt.Dimension(60, 27));

        labelCosteTotal.setText("Cost total:");

        javax.swing.GroupLayout PaneldatoscosteLayout = new javax.swing.GroupLayout(Paneldatoscoste);
        Paneldatoscoste.setLayout(PaneldatoscosteLayout);
        PaneldatoscosteLayout.setHorizontalGroup(
            PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaneldatoscosteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTiempoeje)
                    .addComponent(labelNodovis))
                .addGap(18, 18, 18)
                .addGroup(PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(campoNodovis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(campoTiempoeje, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelNodoexp, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCosteTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoCostetotal, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(campoNodoexp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PaneldatoscosteLayout.setVerticalGroup(
            PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaneldatoscosteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoNodovis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNodovis)
                    .addComponent(labelNodoexp)
                    .addComponent(campoNodoexp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PaneldatoscosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTiempoeje)
                    .addComponent(campoTiempoeje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCosteTotal)
                    .addComponent(campoCostetotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Sortir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelSalidaCosteLayout = new javax.swing.GroupLayout(PanelSalidaCoste);
        PanelSalidaCoste.setLayout(PanelSalidaCosteLayout);
        PanelSalidaCosteLayout.setHorizontalGroup(
            PanelSalidaCosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSalidaCosteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelSalidaCosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Paneldatoscoste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        PanelSalidaCosteLayout.setVerticalGroup(
            PanelSalidaCosteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSalidaCosteLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Paneldatoscoste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        label_x.setText("X:");

        label_coordenadas.setText("Coordenades en el mapa:");

        label_nombre_ciudad.setText("Nom ciutat:");

        nombre_ciudad.setColumns(8);

        label_y.setText("Y:");

        coordenadax.setColumns(4);

        coordenaday.setColumns(4);

        CreaCiudad.setText("Crear ciutat");
        CreaCiudad.setSelected(true);
        CreaCiudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreaCiudadActionPerformed(evt);
            }
        });

        btCrearCiutatCancelar.setText("Cancel·lar");
        btCrearCiutatCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCrearCiutatCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelCreaCiudadLayout = new javax.swing.GroupLayout(PanelCreaCiudad);
        PanelCreaCiudad.setLayout(PanelCreaCiudadLayout);
        PanelCreaCiudadLayout.setHorizontalGroup(
            PanelCreaCiudadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCreaCiudadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCreaCiudadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_coordenadas)
                    .addGroup(PanelCreaCiudadLayout.createSequentialGroup()
                        .addComponent(label_nombre_ciudad)
                        .addGap(18, 18, 18)
                        .addComponent(nombre_ciudad))
                    .addGroup(PanelCreaCiudadLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(label_x, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coordenadax, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_y)
                        .addGap(8, 8, 8)
                        .addComponent(coordenaday, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCreaCiudadLayout.createSequentialGroup()
                        .addComponent(btCrearCiutatCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CreaCiudad)))
                .addContainerGap())
        );
        PanelCreaCiudadLayout.setVerticalGroup(
            PanelCreaCiudadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCreaCiudadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCreaCiudadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_nombre_ciudad)
                    .addComponent(nombre_ciudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(label_coordenadas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelCreaCiudadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_x)
                    .addComponent(coordenaday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coordenadax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_y))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelCreaCiudadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CreaCiudad)
                    .addComponent(btCrearCiutatCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        DvueloComboBox.setPreferredSize(new java.awt.Dimension(75, 20));
        DvueloComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DvueloComboBoxActionPerformed(evt);
            }
        });

        HoraLlegada.setText("01:00");

        HoraSalida.setText("00:00");

        OvueloComboBox.setPreferredSize(new java.awt.Dimension(75, 20));
        OvueloComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OvueloComboBoxActionPerformed(evt);
            }
        });

        PrecioVuelo.setColumns(4);
        PrecioVuelo.setText("0");

        jLabel2.setText("Hora arribada:");

        jLabel4.setText("€");

        CreaDestino.setText("Destí:");

        jLabel3.setText("Preu:");

        CreaOrigen.setText("Origen:");

        nombre_compañia.setColumns(8);

        jLabel11.setText("Nom companyia:");

        BotonCreavuelo.setText("Crear vol");
        BotonCreavuelo.setSelected(true);
        BotonCreavuelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCreavueloActionPerformed(evt);
            }
        });

        jLabel1.setText("Hora sortida:");

        btCreaVueloCancelar.setText("Cancel·lar");
        btCreaVueloCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCreaVueloCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelCreaVueloLayout = new javax.swing.GroupLayout(PanelCreaVuelo);
        PanelCreaVuelo.setLayout(PanelCreaVueloLayout);
        PanelCreaVueloLayout.setHorizontalGroup(
            PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCreaVueloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCreaVueloLayout.createSequentialGroup()
                        .addComponent(CreaOrigen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(OvueloComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CreaDestino)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DvueloComboBox, 0, 179, Short.MAX_VALUE))
                    .addGroup(PanelCreaVueloLayout.createSequentialGroup()
                        .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nombre_compañia)
                            .addComponent(HoraSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PanelCreaVueloLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(77, 77, 77))
                            .addGroup(PanelCreaVueloLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)))
                        .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(HoraLlegada, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(PrecioVuelo, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCreaVueloLayout.createSequentialGroup()
                        .addComponent(btCreaVueloCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonCreavuelo)))
                .addContainerGap())
        );
        PanelCreaVueloLayout.setVerticalGroup(
            PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCreaVueloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OvueloComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(CreaOrigen)
                    .addComponent(CreaDestino)
                    .addComponent(DvueloComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HoraSalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HoraLlegada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombre_compañia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PrecioVuelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelCreaVueloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonCreavuelo)
                    .addComponent(btCreaVueloCancelar))
                .addContainerGap())
        );

        campoNodovisTi.setEditable(false);
        campoNodovisTi.setPreferredSize(new java.awt.Dimension(70, 27));

        campoNodoexTi.setEditable(false);
        campoNodoexTi.setPreferredSize(new java.awt.Dimension(70, 27));

        campoTiempoejeTi.setEditable(false);
        campoTiempoejeTi.setPreferredSize(new java.awt.Dimension(80, 27));

        labelNodogen1.setText("Nodes visitats:");

        labelNodoexp1.setText("Nodes expandits:");

        labelTiempoeje1.setText("Temps d'execució:");

        labelTiempototalTi.setText("Temps total:");

        campoTiempotoTi.setEditable(false);
        campoTiempotoTi.setPreferredSize(new java.awt.Dimension(90, 27));

        labelpretoti.setText("Preu total:");

        campoPretoti.setEditable(false);

        ResultadosTiempo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Origen", "Destí", "Hora sortida", "Hora arribada", "Temps de vol"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ResultadosTiempo.setMinimumSize(new java.awt.Dimension(75, 72));
        ResultadosTiempo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultadosTiempoMouseClicked(evt);
            }
        });
        ResultadosTiempo.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                ResultadosTiempoAncestorRemoved(evt);
            }
        });
        jScrollPane3.setViewportView(ResultadosTiempo);
        ResultadosTiempo.getColumnModel().getColumn(1).setResizable(false);
        ResultadosTiempo.getColumnModel().getColumn(4).setResizable(false);

        btPanelSalidaTiempoSortir.setText("Sortir");
        btPanelSalidaTiempoSortir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPanelSalidaTiempoSortirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PaneldatostiempoLayout = new javax.swing.GroupLayout(Paneldatostiempo);
        Paneldatostiempo.setLayout(PaneldatostiempoLayout);
        PaneldatostiempoLayout.setHorizontalGroup(
            PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaneldatostiempoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btPanelSalidaTiempoSortir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PaneldatostiempoLayout.createSequentialGroup()
                        .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelNodogen1)
                            .addComponent(labelNodoexp1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(campoNodovisTi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(campoNodoexTi, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelTiempoeje1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(campoTiempoejeTi, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelTiempototalTi)
                            .addComponent(labelpretoti))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(campoPretoti)
                            .addComponent(campoTiempotoTi, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        PaneldatostiempoLayout.setVerticalGroup(
            PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PaneldatostiempoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoPretoti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(campoTiempoejeTi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(campoNodovisTi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTiempoeje1)
                        .addComponent(labelpretoti)
                        .addComponent(labelNodogen1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(campoNodoexTi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelNodoexp1))
                    .addGroup(PaneldatostiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(campoTiempotoTi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTiempototalTi)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btPanelSalidaTiempoSortir)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout PanelSalidaTiempoLayout = new javax.swing.GroupLayout(PanelSalidaTiempo);
        PanelSalidaTiempo.setLayout(PanelSalidaTiempoLayout);
        PanelSalidaTiempoLayout.setHorizontalGroup(
            PanelSalidaTiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Paneldatostiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PanelSalidaTiempoLayout.setVerticalGroup(
            PanelSalidaTiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Paneldatostiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelAbout.setName("PanelAbout"); // NOI18N

        jLabel5.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Cerques: Rutes Aèries");

        jLabel6.setText("Autor:");

        jTextField1.setEditable(false);
        jTextField1.setText("Vicenç Juan Tomàs Montserrat");

        jLabel7.setText("Llicència:");

        jTextField2.setEditable(false);
        jTextField2.setText("GPLv3");

        jLabel8.setText("Repositori:");

        jTextField3.setEditable(false);
        jTextField3.setText("https://github.com/vtomasr5/ia-rutes");

        jLabel9.setText("Assignatura:");

        jTextField4.setEditable(false);
        jTextField4.setText("Inteligència Artificial. UIB 2011/12");

        btAboutSortir.setText("Sortir");
        btAboutSortir.setSelected(true);
        btAboutSortir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAboutSortirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelAboutLayout = new javax.swing.GroupLayout(PanelAbout);
        PanelAbout.setLayout(PanelAboutLayout);
        PanelAboutLayout.setHorizontalGroup(
            PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAboutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                    .addComponent(btAboutSortir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                    .addGroup(PanelAboutLayout.createSequentialGroup()
                        .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))))
                .addContainerGap())
        );
        PanelAboutLayout.setVerticalGroup(
            PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAboutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btAboutSortir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        popupMenuItemCrearCiutat.setText("Crear ciutat");
        popupMenuItemCrearCiutat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenucreaciudadActionPerformed(evt);
            }
        });
        popupMenu.add(popupMenuItemCrearCiutat);

        popupMenuItemCrearVol.setText("Crear vol");
        popupMenuItemCrearVol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenucreavueloActionPerformed(evt);
            }
        });
        popupMenu.add(popupMenuItemCrearVol);

        btLlistarVols.setText("Llistar tots els vols");
        btLlistarVols.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLlistarVolsActionPerformed(evt);
            }
        });

        textAreaLlistaVols.setColumns(20);
        textAreaLlistaVols.setEditable(false);
        textAreaLlistaVols.setRows(5);
        jScrollPane4.setViewportView(textAreaLlistaVols);

        javax.swing.GroupLayout PanellLlistarVolsLayout = new javax.swing.GroupLayout(PanellLlistarVols);
        PanellLlistarVols.setLayout(PanellLlistarVolsLayout);
        PanellLlistarVolsLayout.setHorizontalGroup(
            PanellLlistarVolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanellLlistarVolsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanellLlistarVolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btLlistarVols, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanellLlistarVolsLayout.setVerticalGroup(
            PanellLlistarVolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanellLlistarVolsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btLlistarVols)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pràctica IA. UIB 2011/12. Rutes aèries.");
        setLocationByPlatform(true);

        PanelBusquedas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tipo_busqueda.add(profundidad_poda);
        profundidad_poda.setText("Profunditat amb poda");

        Criterio.add(precio_minimo);
        precio_minimo.setSelected(true);
        precio_minimo.setText("Preu mínim");

        tipo_busqueda.add(vecino_proximo);
        vecino_proximo.setText("Veí més pròxim");

        Criterio.add(tiempo_minimo);
        tiempo_minimo.setText("Temps mínim");

        busqueda_tipo.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        busqueda_tipo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        busqueda_tipo.setText("Tipus de cerques");

        tipo_busqueda.add(coste_uniforme);
        coste_uniforme.setText("Cost uniforme");

        criterio_busqueda.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        criterio_busqueda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        criterio_busqueda.setText("Criteri de cerca");

        tipo_busqueda.add(a_estrella);
        a_estrella.setText("A* 1ª heurística");

        OrigenComboBox.setMinimumSize(new java.awt.Dimension(25, 18));
        OrigenComboBox.setPreferredSize(new java.awt.Dimension(75, 20));
        OrigenComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OrigenComboBoxActionPerformed(evt);
            }
        });

        tipo_busqueda.add(profundidad);
        profundidad.setSelected(true);
        profundidad.setText("Profunditat");

        Busqueda.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        Busqueda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Busqueda.setText("Rutes");

        DestinoComboBox.setPreferredSize(new java.awt.Dimension(75, 20));
        DestinoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DestinoComboBoxActionPerformed(evt);
            }
        });

        Destino.setText("Destí:");

        Origen.setText("Origen:");

        tipo_busqueda.add(a_estrella2);
        a_estrella2.setText("A* 2ª heurística");

        javax.swing.GroupLayout PanelBusquedasLayout = new javax.swing.GroupLayout(PanelBusquedas);
        PanelBusquedas.setLayout(PanelBusquedasLayout);
        PanelBusquedasLayout.setHorizontalGroup(
            PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusquedasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addGroup(PanelBusquedasLayout.createSequentialGroup()
                        .addGroup(PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Origen)
                            .addComponent(Destino))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OrigenComboBox, 0, 153, Short.MAX_VALUE)
                            .addComponent(DestinoComboBox, 0, 153, Short.MAX_VALUE)))
                    .addComponent(Busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(busqueda_tipo, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(criterio_busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(precio_minimo)
                    .addComponent(tiempo_minimo)
                    .addComponent(profundidad)
                    .addComponent(profundidad_poda)
                    .addComponent(coste_uniforme)
                    .addComponent(vecino_proximo)
                    .addComponent(a_estrella)
                    .addComponent(a_estrella2))
                .addContainerGap())
        );
        PanelBusquedasLayout.setVerticalGroup(
            PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusquedasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Busqueda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Origen)
                    .addComponent(OrigenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelBusquedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Destino)
                    .addComponent(DestinoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(busqueda_tipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(profundidad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(profundidad_poda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(coste_uniforme)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vecino_proximo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(a_estrella)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(a_estrella2)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(criterio_busqueda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(precio_minimo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tiempo_minimo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        BotonBuscar.setText("Iniciar cerca");
        BotonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBuscarActionPerformed(evt);
            }
        });

        toobar.setFloatable(false);
        toobar.setRollover(true);

        tbObrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/open.png"))); // NOI18N
        tbObrir.setText("Obrir");
        tbObrir.setFocusable(false);
        tbObrir.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        tbObrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemObrirActionPerformed(evt);
            }
        });
        toobar.add(tbObrir);

        tbGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/save.png"))); // NOI18N
        tbGuardar.setText("Guardar");
        tbGuardar.setFocusable(false);
        tbGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        tbGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemGuardarActionPerformed(evt);
            }
        });
        toobar.add(tbGuardar);
        toobar.add(jSeparator5);

        btTbCrearCiutat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/ciutat.jpg"))); // NOI18N
        btTbCrearCiutat.setText("Crear ciutat");
        btTbCrearCiutat.setFocusable(false);
        btTbCrearCiutat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btTbCrearCiutat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenucreaciudadActionPerformed(evt);
            }
        });
        toobar.add(btTbCrearCiutat);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/vol.png"))); // NOI18N
        jButton3.setText("Crear vol");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenucreavueloActionPerformed(evt);
            }
        });
        toobar.add(jButton3);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/random.png"))); // NOI18N
        jButton4.setText("Gen. Aleatoris");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenugenaleActionPerformed(evt);
            }
        });
        toobar.add(jButton4);
        toobar.add(jSeparator6);

        lblCoord.setText("X: 0 Y: 0");
        toobar.add(lblCoord);

        Menubusquedas.setBackground(new java.awt.Color(238, 238, 238));
        Menubusquedas.setText("Arxiu");

        MenuNuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        MenuNuevo.setText("Reset");
        MenuNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuNuevoActionPerformed(evt);
            }
        });
        Menubusquedas.add(MenuNuevo);

        menuItemObrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemObrir.setText("Obrir...");
        menuItemObrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemObrirActionPerformed(evt);
            }
        });
        Menubusquedas.add(menuItemObrir);

        menuItemGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemGuardar.setText("Guardar...");
        menuItemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemGuardarActionPerformed(evt);
            }
        });
        Menubusquedas.add(menuItemGuardar);
        Menubusquedas.add(jSeparator3);

        MenuSotirItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        MenuSotirItem.setText("Sortir");
        MenuSotirItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuSotirItemActionPerformed(evt);
            }
        });
        Menubusquedas.add(MenuSotirItem);

        jMenuBar1.add(Menubusquedas);

        Menucrear.setBackground(new java.awt.Color(238, 238, 238));
        Menucrear.setText("Accions");

        Menucreaciudad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        Menucreaciudad.setText("Crear ciutat");
        Menucreaciudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenucreaciudadActionPerformed(evt);
            }
        });
        Menucrear.add(Menucreaciudad);

        Menucreavuelo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        Menucreavuelo.setText("Crear vol");
        Menucreavuelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenucreavueloActionPerformed(evt);
            }
        });
        Menucrear.add(Menucreavuelo);

        Menugenale.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        Menugenale.setText("Generar aleatoris");
        Menugenale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenugenaleActionPerformed(evt);
            }
        });
        Menucrear.add(Menugenale);

        MenuLlistarVols.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        MenuLlistarVols.setText("Llistar vols");
        MenuLlistarVols.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuLlistarVolsActionPerformed(evt);
            }
        });
        Menucrear.add(MenuLlistarVols);

        jMenuBar1.add(Menucrear);

        jMenu1.setBackground(new java.awt.Color(238, 238, 238));
        jMenu1.setText("Ajuda");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Sobre...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toobar, javax.swing.GroupLayout.DEFAULT_SIZE, 1031, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelBusquedas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BotonBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScrollMundo, javax.swing.GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toobar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelBusquedas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonBuscar)
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addComponent(ScrollMundo))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OrigenComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OrigenComboBoxActionPerformed
        DestinoComboBox.removeAllItems();
        JComboBox combo = (JComboBox) evt.getSource();
        String nombre = (String) combo.getSelectedItem();
        origen = nombre;
        for (int i = 0; i < ciutats_creades; i++) {
            if (!ciutats[i].getnombre().equals(nombre)) {
                DestinoComboBox.addItem(ciutats[i].getnombre());
            }
        }
    }//GEN-LAST:event_OrigenComboBoxActionPerformed

    private void BotonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBuscarActionPerformed
//        if (OrigenComboBox.getItemCount() == 0 || DestinoComboBox.getItemCount() == 0) {
        if (ciutats_creades < 2) {
            JOptionPane.showMessageDialog(this, "No hi ha ciutats suficients per cercar.", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            int elemd = DestinoComboBox.getSelectedIndex();
            String lugard = (String) DestinoComboBox.getItemAt(elemd);
            int elemo = OrigenComboBox.getSelectedIndex();
            boolean coste = true;
            String lugaro = (String) OrigenComboBox.getItemAt(elemo);
            pinta_mapa();
            reiniciarTabla();
            if (elemd != -1) {
                if (profundidad.isSelected()) {
                    if (precio_minimo.isSelected()) {
                        try {
                            coste = true;
                            busqueda_prof(lugaro, lugard, 0);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            coste = false;
                            busqueda_prof(lugaro, lugard, 1);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }//tiempo minimo
                } else if (profundidad_poda.isSelected()) {
                    if (precio_minimo.isSelected()) {
                        try {
                            coste = true;
                            busqueda_prof_poda_coste(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            coste = false;
                            busqueda_prof_poda_tiempo(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (coste_uniforme.isSelected()) {
                    if (precio_minimo.isSelected()) {
                        try {
                            coste = true;
                            busqueda_costo_uniforme_coste(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            coste = false;
                            busqueda_costo_uniforme_tiempo(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (vecino_proximo.isSelected()) {
                    if (precio_minimo.isSelected()) {
                        try {
                            coste = true;
                            busqueda_vecino_mas_proximo(lugaro, lugard, 0);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            coste = false;
                            busqueda_vecino_mas_proximo(lugaro, lugard, 1);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (a_estrella.isSelected()) {          //A estrella
                    if (precio_minimo.isSelected()) {
                        coste = true;
                        try {
                            busqueda_a_estrella_cost(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        coste = false;
                        try {
                            busqueda_a_estrella_temps(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    if (precio_minimo.isSelected()) {
                        coste = true;
                        try {
                            busqueda_a_estrella2_cost(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        coste = false;
                        try {
                            busqueda_a_estrella2_temps(lugaro, lugard);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                DSalidaCosteTiempo = new JDialog();
                SwingUtilities.updateComponentTreeUI(PanelSalidaCoste);
                SwingUtilities.updateComponentTreeUI(PanelSalidaTiempo);
                DSalidaCosteTiempo.setModal(true);
                DSalidaCosteTiempo.setTitle("Resultats");
                if (coste) {
                    DSalidaCosteTiempo.setSize(PanelSalidaCoste.getPreferredSize());
                    DSalidaCosteTiempo.add(PanelSalidaCoste);
                } else {
                    DSalidaCosteTiempo.setSize(PanelSalidaTiempo.getPreferredSize());
                    DSalidaCosteTiempo.add(PanelSalidaTiempo);
                }
                DSalidaCosteTiempo.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                DSalidaCosteTiempo.setLocationByPlatform(true);
                DSalidaCosteTiempo.pack();
                DSalidaCosteTiempo.setVisible(true);
            }
        }
    }//GEN-LAST:event_BotonBuscarActionPerformed

    private void CreaCiudadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreaCiudadActionPerformed
        if (nombre_ciudad.getText().equals("") || coordenadax.getText().equals("") || coordenaday.getText().equals("")) {
            JOptionPane.showMessageDialog(DCreaCiudad, "Falten dades per omplir.", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            if ((Integer.parseInt(coordenadax.getText()) < 0) || (Integer.parseInt(coordenaday.getText()) < 0)) {
                JOptionPane.showMessageDialog(DCreaCiudad, "Els valors de X i Y no poden ser negatius.", "Alerta", JOptionPane.WARNING_MESSAGE);    
            } else {
                if ((Integer.parseInt(coordenadax.getText()) > 795) || (Integer.parseInt(coordenaday.getText()) > 430)) {
                    JOptionPane.showMessageDialog(DCreaCiudad, "Els valors de X i Y no poden sortir del mapa. Maxim: X:795 i Y:430", "Alerta", JOptionPane.WARNING_MESSAGE);    
                } else {
                    for (int i = 0; i < ciutats_creades; i++) {
                        if ((ciutats[i].getnombre().equals(nombre_ciudad.getText())) || ((String.valueOf(ciutats[i].getcx()).equals(coordenadax.getText())) && (String.valueOf(ciutats[i].getcy()).equals(coordenaday.getText())))) {
                            JOptionPane.showMessageDialog(DCreaCiudad, "No hi pot haver ciutats amb el mateix nom,\nni amb les mateixes coordenades.", "Alerta", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    Integer x = Integer.parseInt(coordenadax.getText());
                    Integer y = Integer.parseInt(coordenaday.getText());
                    mundo.setModoRuta(0);
                    if (nombre_ciudad.getText() != null) {
                        ciutats[ciutats_creades] = new Ciutat(nombre_ciudad.getText(), x, y);
                        ciutats_creades += 1;
                        mundo.setNumciudades(ciutats_creades);
                        repaint();
                    }
                    OrigenComboBox.addItem(ciutats[ciutats_creades - 1].getnombre());
                    OvueloComboBox.addItem(ciutats[ciutats_creades - 1].getnombre());
                    if (ciutats_creades - 1 != 0) {
                        DestinoComboBox.addItem(ciutats[ciutats_creades - 1].getnombre());
                    }
                    nombre_ciudad.setText("");
//                    DCreaCiudad.dispose();
                    DCreaCiudad.setVisible(false);
                }
            }
        }
    }//GEN-LAST:event_CreaCiudadActionPerformed

    private void DestinoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DestinoComboBoxActionPerformed
        JComboBox combo = (JComboBox) evt.getSource();
        String nombre = (String) combo.getSelectedItem();
        destino = nombre;
    }//GEN-LAST:event_DestinoComboBoxActionPerformed

    private void OvueloComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OvueloComboBoxActionPerformed
        DvueloComboBox.removeAllItems();
        JComboBox combo = (JComboBox) evt.getSource();
        String nombre = (String) combo.getSelectedItem();
        origen = nombre;
        for (int i = 0; i < ciutats_creades; i++) {
            if (!ciutats[i].getnombre().equals(nombre)) {
                DvueloComboBox.addItem(ciutats[i].getnombre());
            }
        }
    }//GEN-LAST:event_OvueloComboBoxActionPerformed

    private void MenucreavueloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenucreavueloActionPerformed
        if (ciutats_creades < 2) {
            JOptionPane.showMessageDialog(DCreaVuelo, "No hi ha ciutats creades. No se poden crear vols.", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            DCreaVuelo = new JDialog();
            SwingUtilities.updateComponentTreeUI(PanelCreaVuelo);
            DCreaVuelo.setTitle("Crea nou vol");
            DCreaVuelo.setModal(true);
            nombre_compañia.setText("");
            HoraLlegada.setText("01:00");
            HoraSalida.setText("00:00");
            PrecioVuelo.setText("0");
            DCreaVuelo.getRootPane().setDefaultButton(BotonCreavuelo);
            DCreaVuelo.setSize(PanelCreaVuelo.getPreferredSize());
            DCreaVuelo.setLocationByPlatform(true);
            if (DvueloComboBox.getSelectedIndex() == -1) {
                for (int i = 1; i < ciutats_creades; i++) {
                    DvueloComboBox.addItem(ciutats[i].getnombre());
                }
            }
            DCreaVuelo.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            DCreaVuelo.add(PanelCreaVuelo);
            DCreaVuelo.pack();
            DCreaVuelo.setVisible(true);
        }
    }//GEN-LAST:event_MenucreavueloActionPerformed

    private void MenucreaciudadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenucreaciudadActionPerformed
        nombre_ciudad.setText("");
        DCreaCiudad = new JDialog();
        SwingUtilities.updateComponentTreeUI(PanelCreaCiudad);
        DCreaCiudad.setTitle("Crear nova ciutat");
        DCreaCiudad.setSize(400, 200);
        DCreaCiudad.setModal(true);
        DCreaCiudad.getRootPane().setDefaultButton(CreaCiudad);
        DCreaCiudad.setLocationByPlatform(true);
        DCreaCiudad.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        DCreaCiudad.add(PanelCreaCiudad);
        DCreaCiudad.pack();
        DCreaCiudad.setVisible(true);
    }//GEN-LAST:event_MenucreaciudadActionPerformed

    private void BotonCreavueloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCreavueloActionPerformed
        if (HoraSalida.getText().equals("") || HoraLlegada.getText().equals("") || PrecioVuelo.getText().equals("") || nombre_compañia.getText().equals("")) {
            JOptionPane.showMessageDialog(DCreaVuelo, "Faltes dades per omplir.", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            if (DvueloComboBox.getItemCount() == 0 || OvueloComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(DCreaVuelo, "No hi ha ciutats creades. No se poden crear vols.", "Alerta", JOptionPane.WARNING_MESSAGE);
            } else {
                if (DvueloComboBox.getSelectedItem().toString().equals(OvueloComboBox.getSelectedItem().toString())) {
                    JOptionPane.showMessageDialog(DCreaVuelo, "No se pot crear un vol a la mateixa ciutat.", "Alerta", JOptionPane.WARNING_MESSAGE);
                } else {
                    if (Float.parseFloat(PrecioVuelo.getText()) < 0) {
                        JOptionPane.showMessageDialog(DCreaVuelo, "El preu d'un vol no pot ser negatiu.", "Alerta", JOptionPane.WARNING_MESSAGE);
                    } else {
                        try {
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                            DateFormat df2 = new SimpleDateFormat("hh:mm");
                            Date fechaActual = new Date();
                            Date fecha_salida;
                            Date fecha_llegada;
                            
                            String dia2;
                            mundo.setModoRuta(0);
                            
                            SimpleDateFormat formato = new SimpleDateFormat("dd");
                            String dia = formato.format(fechaActual);
                            SimpleDateFormat formato2 = new SimpleDateFormat("MM");
                            String mes = formato2.format(fechaActual);
                            SimpleDateFormat formato3 = new SimpleDateFormat("yyyy");
                            String año = formato3.format(fechaActual);
                            
                            int posicion_x = posicion_ciudad("1"); //origen
                            int posicion_y = posicion_ciudad("2"); //desti
//                            print("Posició origen: " + posicion_x + " Posició desti: " + posicion_y);
                            
                            dia = Integer.toString(Integer.parseInt(dia) + 1);
                            dia2 = dia;
                            String hora_lleg = HoraLlegada.getText();
                            String hora_sal = HoraSalida.getText();
                            
                            if (df2.parse(hora_sal).after(df2.parse(hora_lleg))) {
                                dia = Integer.toString(Integer.valueOf(dia) + 1);
                            }

                            fecha_salida = df.parse(dia2 + "/" + mes + "/" + año + " " + hora_sal);
                            fecha_llegada = df.parse(dia + "/" + mes + "/" + año + " " + hora_lleg);
                            
                            if (posicion_y != -1) {
                                int posox = ciutats[posicion_x].getcx();
                                int posoy = ciutats[posicion_x].getcy();
                                int posdx = ciutats[posicion_y].getcx();
                                int posdy = ciutats[posicion_y].getcy();
//                                print("O: " + ciudades[posicion_x].getnombre() + " D: " + ciudades[posicion_y].getnombre());
                                for (int i = 0; i < 6; i++) {
                                      llistaVols[posicion_x][posicion_y].insertarnuevo(codigo, nombre_compañia.getText(), Float.parseFloat(PrecioVuelo.getText()), fecha_salida, fecha_llegada, posox, posoy, posdx, posdy);
                                    dia = Integer.toString(Integer.valueOf(dia) + 1);
                                    dia2 = Integer.toString(Integer.valueOf(dia2) + 1);
                                    fecha_salida = df.parse(dia2 + "/" + mes + "/" + año + " " + hora_sal);
                                    fecha_llegada = df.parse(dia + "/" + mes + "/" + año + " " + hora_lleg);
                                }
                                codigo++;
                                repaint();
                            }
//                            DCreaVuelo.dispose();
                            DCreaVuelo.setVisible(false);
                        } catch (Exception ex) {
                            mostrarMissatgeError(null, ex);
                            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_BotonCreavueloActionPerformed

    private void DvueloComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DvueloComboBoxActionPerformed
        JComboBox combo = (JComboBox) evt.getSource();
        String nombre = (String) combo.getSelectedItem();
        destino = nombre;
    }//GEN-LAST:event_DvueloComboBoxActionPerformed

    private void ResultadosCosteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultadosCosteMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            mundo.setModoRuta(1);
            repaint();
            if (ResultadosCoste.getValueAt(ResultadosCoste.getSelectedRow(), 4) != null) {
                mundo.setModoRuta(2);
                mundo.setSeleccionado(ResultadosCoste.getSelectedRow() + 1);
            }
        }
    }//GEN-LAST:event_ResultadosCosteMouseClicked

    private void ResultadosCosteAncestorRemoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_ResultadosCosteAncestorRemoved
        mundo.setModoRuta(0);
        repaint();
    }//GEN-LAST:event_ResultadosCosteAncestorRemoved

    private void MenugenaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenugenaleActionPerformed
        DGenAle = new JDialog();
        DGenAle.setSize(425, 200);
        SwingUtilities.updateComponentTreeUI(PanelGenAle);
        DGenAle.setModal(true);
        personalizado.setSelected(true);
        texto_numvuelos.setEnabled(true);
        todos_con_todos.setSelected(false);
        CiuGen.setText("2");
        texto_numvuelos.setText("1");
        DGenAle.setTitle("Generar aleatoris");
        DGenAle.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        DGenAle.setLocationByPlatform(true);
        DGenAle.add(PanelGenAle);
        DGenAle.pack();
        DGenAle.setVisible(true);
    }//GEN-LAST:event_MenugenaleActionPerformed

    private void CreaGenAleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreaGenAleActionPerformed
        if (CiuGen.getText().equals("") || texto_numvuelos.getText().equals("")) {
            JOptionPane.showMessageDialog(DGenAle, "Faltes dades per omplir.", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            if (Integer.parseInt(CiuGen.getText()) < 2 || Integer.parseInt(texto_numvuelos.getText()) < 1) {
                JOptionPane.showMessageDialog(DGenAle, "Has d'introduir almenys dues ciutats i un vol", "Alerta", JOptionPane.WARNING_MESSAGE);
            } else {
                añadir_ciudad_auto(Integer.parseInt(CiuGen.getText()));
                actualizar_matriz_distancias();
                if (personalizado.isSelected()) {
                    try {
                        añadir_vuelo_auto(Integer.parseInt(texto_numvuelos.getText()));
                    } catch (ParseException ex) {
                        mostrarMissatgeError(null, ex);
                        Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        conectar_todas_ciudades();
                    } catch (ParseException ex) {
                        mostrarMissatgeError(null, ex);
                        Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    //            DGenAle.dispose();
                DGenAle.setVisible(false);
            }
        }
    }//GEN-LAST:event_CreaGenAleActionPerformed

    private void MenuNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuNuevoActionPerformed
        OrigenComboBox.removeAllItems();
        DestinoComboBox.removeAllItems();
        OvueloComboBox.removeAllItems();
        DvueloComboBox.removeAllItems();
        ciutats = new Ciutat[max_ciudades];        
        ciutats_creades = 0;
        codigo = 1;
        //TextoSalida.setText("");
        inicializar_matriz_vuelos();
        init();
        inicializar_matriz_distancias();
    }//GEN-LAST:event_MenuNuevoActionPerformed

    private void ResultadosTiempoAncestorRemoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_ResultadosTiempoAncestorRemoved
        mundo.setModoRuta(0);
        repaint();
    }//GEN-LAST:event_ResultadosTiempoAncestorRemoved

    private void ResultadosTiempoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultadosTiempoMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            mundo.setModoRuta(1);
            repaint();
            if (ResultadosTiempo.getValueAt(ResultadosTiempo.getSelectedRow(), 4) != null) {
                mundo.setModoRuta(2);
                mundo.setSeleccionado(ResultadosTiempo.getSelectedRow() + 1);
            }
        }
    }//GEN-LAST:event_ResultadosTiempoMouseClicked

    private void MenuSotirItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuSotirItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_MenuSotirItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        DAbout = new JDialog(this);
        SwingUtilities.updateComponentTreeUI(PanelAbout);
        DAbout.setTitle("Sobre...");
        DAbout.setModal(true);
        DAbout.setLocationByPlatform(true);
        DAbout.setPreferredSize(new Dimension(430, 290));
        DAbout.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        DAbout.add(PanelAbout);
        DAbout.pack();
        DAbout.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    
    private void btGenAleCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGenAleCancelarActionPerformed
//        DGenAle.dispose();
        DGenAle.setVisible(false);
    }//GEN-LAST:event_btGenAleCancelarActionPerformed

    private void btCrearCiutatCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCrearCiutatCancelarActionPerformed
//        DCreaCiudad.dispose();
        DCreaCiudad.setVisible(false);
    }//GEN-LAST:event_btCrearCiutatCancelarActionPerformed

    private void btCreaVueloCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCreaVueloCancelarActionPerformed
//        DCreaVuelo.dispose();
        DCreaVuelo.setVisible(false);
    }//GEN-LAST:event_btCreaVueloCancelarActionPerformed

    private void btAboutSortirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAboutSortirActionPerformed
//        DAbout.dispose();
        DAbout.setVisible(false);
    }//GEN-LAST:event_btAboutSortirActionPerformed

    private void btPanelSalidaTiempoSortirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPanelSalidaTiempoSortirActionPerformed
//        DSalidaCosteTiempo.dispose();
        campoNodoexTi.setText("0");
        campoNodovisTi.setText("0");
        campoPretoti.setText("0");
        campoTiempoejeTi.setText("0");
        campoTiempotoTi.setText("0");
        DSalidaCosteTiempo.setVisible(false);
    }//GEN-LAST:event_btPanelSalidaTiempoSortirActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        DSalidaCosteTiempo.dispose();
        campoNodoexp.setText("0");
        campoNodovis.setText("0");
        campoTiempoeje.setText("0");
        campoCostetotal.setText("0");
        DSalidaCosteTiempo.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void todos_con_todosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todos_con_todosActionPerformed
        texto_numvuelos.setText("1");
        texto_numvuelos.setEnabled(false);
    }//GEN-LAST:event_todos_con_todosActionPerformed

    private void personalizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_personalizadoActionPerformed
        texto_numvuelos.setEnabled(true);
    }//GEN-LAST:event_personalizadoActionPerformed

    private void menuItemObrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemObrirActionPerformed
        try {
            AbrirFichero();
        } catch (IOException ex) {
            mostrarMissatgeError(null, ex);
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            mostrarMissatgeError(null, ex);
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuItemObrirActionPerformed

    private void menuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGuardarActionPerformed
        try {
            GuardarFichero();
        } catch (FileNotFoundException ex) {
            mostrarMissatgeError(null, ex);
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            mostrarMissatgeError(null, ex);
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuItemGuardarActionPerformed

    private void MenuLlistarVolsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuLlistarVolsActionPerformed
        if (ciutats_creades < 2) {
            JOptionPane.showMessageDialog(this, "No hi ha ciutats suficients.", "Alerta", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DLlistarVols = new JDialog();
        DLlistarVols.setSize(588, 364);
        SwingUtilities.updateComponentTreeUI(PanellLlistarVols);
        DLlistarVols.setModal(true);
        DLlistarVols.setTitle("Llistat de tots els vols");
        DLlistarVols.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        DLlistarVols.setLocationByPlatform(true);
        DLlistarVols.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent we) {
                mostra_vols();
            }

            @Override
            public void windowClosing(WindowEvent we) {
            }

            @Override
            public void windowClosed(WindowEvent we) {
                textAreaLlistaVols.setText("");
            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });
        
        DLlistarVols.add(PanellLlistarVols);
        DLlistarVols.pack();
        DLlistarVols.setVisible(true);
    }//GEN-LAST:event_MenuLlistarVolsActionPerformed

    private void btLlistarVolsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLlistarVolsActionPerformed
        textAreaLlistaVols.setText("");
        mostra_vols();
  }//GEN-LAST:event_btLlistarVolsActionPerformed

    private void campoNodoexpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoNodoexpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoNodoexpActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] Main) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
//                System.setProperty("apple.laf.useScreenMenuBar", "true");
                new Finestra().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonBuscar;
    private javax.swing.JButton BotonCreavuelo;
    private javax.swing.JLabel Busqueda;
    private javax.swing.JTextField CiuGen;
    private javax.swing.JButton CreaCiudad;
    private javax.swing.JLabel CreaDestino;
    private javax.swing.JButton CreaGenAle;
    private javax.swing.JLabel CreaOrigen;
    private javax.swing.ButtonGroup CritVuelo;
    private javax.swing.ButtonGroup Criterio;
    private javax.swing.JLabel Destino;
    private javax.swing.JComboBox DestinoComboBox;
    private javax.swing.JComboBox DvueloComboBox;
    private javax.swing.JTextField HoraLlegada;
    private javax.swing.JTextField HoraSalida;
    private javax.swing.JMenuItem MenuLlistarVols;
    private javax.swing.JMenuItem MenuNuevo;
    private javax.swing.JMenuItem MenuSotirItem;
    private javax.swing.JMenu Menubusquedas;
    private javax.swing.JMenuItem Menucreaciudad;
    private javax.swing.JMenu Menucrear;
    private javax.swing.JMenuItem Menucreavuelo;
    private javax.swing.JMenuItem Menugenale;
    private javax.swing.JLabel Origen;
    private javax.swing.JComboBox OrigenComboBox;
    private javax.swing.JComboBox OvueloComboBox;
    private javax.swing.JPanel PanelAbout;
    private javax.swing.JPanel PanelBusquedas;
    private javax.swing.JPanel PanelCreaCiudad;
    private javax.swing.JPanel PanelCreaVuelo;
    private javax.swing.JPanel PanelGenAle;
    private javax.swing.JPanel PanelSalidaCoste;
    private javax.swing.JPanel PanelSalidaTiempo;
    private javax.swing.JPanel Paneldatoscoste;
    private javax.swing.JPanel Paneldatostiempo;
    private javax.swing.JPanel PanellLlistarVols;
    private javax.swing.JTextField PrecioVuelo;
    private javax.swing.JTable ResultadosCoste;
    private javax.swing.JTable ResultadosTiempo;
    private javax.swing.JScrollPane ScrollMundo;
    private javax.swing.JRadioButton a_estrella;
    private javax.swing.JRadioButton a_estrella2;
    private javax.swing.JButton btAboutSortir;
    private javax.swing.JButton btCreaVueloCancelar;
    private javax.swing.JButton btCrearCiutatCancelar;
    private javax.swing.JButton btGenAleCancelar;
    private javax.swing.JButton btLlistarVols;
    private javax.swing.JButton btPanelSalidaTiempoSortir;
    private javax.swing.JButton btTbCrearCiutat;
    private javax.swing.JLabel busqueda_tipo;
    private javax.swing.JTextField campoCostetotal;
    private javax.swing.JTextField campoNodoexTi;
    private javax.swing.JTextField campoNodoexp;
    private javax.swing.JTextField campoNodovis;
    private javax.swing.JTextField campoNodovisTi;
    private javax.swing.JTextField campoPretoti;
    private javax.swing.JTextField campoTiempoeje;
    private javax.swing.JTextField campoTiempoejeTi;
    private javax.swing.JTextField campoTiempotoTi;
    private javax.swing.JTextField coordenadax;
    private javax.swing.JTextField coordenaday;
    private javax.swing.JRadioButton coste_uniforme;
    private javax.swing.JLabel criterio_busqueda;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel labelCosteTotal;
    private javax.swing.JLabel labelNodoexp;
    private javax.swing.JLabel labelNodoexp1;
    private javax.swing.JLabel labelNodogen1;
    private javax.swing.JLabel labelNodovis;
    private javax.swing.JLabel labelTiempoeje;
    private javax.swing.JLabel labelTiempoeje1;
    private javax.swing.JLabel labelTiempototalTi;
    private javax.swing.JLabel label_ciugen;
    private javax.swing.JLabel label_coordenadas;
    private javax.swing.JLabel label_nombre_ciudad;
    private javax.swing.JLabel label_numvuelos;
    private javax.swing.JLabel label_x;
    private javax.swing.JLabel label_y;
    private javax.swing.JLabel labelpretoti;
    private javax.swing.JLabel lblCoord;
    private javax.swing.JMenuItem menuItemGuardar;
    private javax.swing.JMenuItem menuItemObrir;
    private javax.swing.JTextField nombre_ciudad;
    private javax.swing.JTextField nombre_compañia;
    private javax.swing.JRadioButton personalizado;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JMenuItem popupMenuItemCrearCiutat;
    private javax.swing.JMenuItem popupMenuItemCrearVol;
    private javax.swing.JRadioButton precio_minimo;
    private javax.swing.JRadioButton profundidad;
    private javax.swing.JRadioButton profundidad_poda;
    private javax.swing.JButton tbGuardar;
    private javax.swing.JButton tbObrir;
    public static javax.swing.JTextArea textAreaLlistaVols;
    private javax.swing.JTextField texto_numvuelos;
    private javax.swing.JRadioButton tiempo_minimo;
    private javax.swing.ButtonGroup tipo_busqueda;
    private javax.swing.JRadioButton todos_con_todos;
    private javax.swing.JToolBar toobar;
    private javax.swing.JRadioButton vecino_proximo;
    // End of variables declaration//GEN-END:variables
}
