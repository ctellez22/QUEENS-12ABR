package igu;

import logica.CategoriaVerificacion;
import logica.Controladora;
import logica.Joya;
import persistencia.ControladoraPersistencia;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class GroupBy {
    private JCheckBox todasLasJoyasCheckBox;
    private JComboBox<String> comboBoxCategoria;
    private JButton btnContar;
    private JList<Joya> listaJoyas; // Lista de Joya en lugar de String
    private JPanel mainPanel;
    private JButton btnVerificar;
    private JScrollPane panelScrollVerificados;
    private JScrollBar scrollBar1;


    private Controladora controladora; // Instancia de la capa lógica
    private ControladoraPersistencia controladoraPersistencia;

    private DefaultListModel<Joya> listModel; // Modelo de datos para la lista
    private boolean enVerificacion = false; // Para controlar si estamos en modo verificación
    private JDialog dialogVerificacion; // Referencia al cuadro de diálogo de verificación
    private String categoriaSeleccionadaActual;



    public GroupBy(JFrame parent) {
        // Crear la pantalla de carga
        LoadingScreen loadingScreen = new LoadingScreen(parent);

        new Thread(() -> {
            loadingScreen.show(); // Mostrar la animación de carga
            try {
                // Inicializar el modelo de lista y otros componentes
                controladora = new Controladora();
                listModel = new DefaultListModel<>();
                listaJoyas.setModel(listModel);


                // Configurar renderizado para la lista
                listaJoyas.setCellRenderer(new JoyaListCellRenderer());

                // Cargar todas las joyas inicialmente
                SwingUtilities.invokeLater(() -> {
                    cargarCategoriasVerificacion();
                    // También puedes llamar a cargarTodasLasJoyas() si es necesario
                });
                //cargarTodasLasJoyas();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loadingScreen.hide(); // Ocultar la animación de carga
            }
        }).start();




        // Acción para "Todas las Joyas"
        todasLasJoyasCheckBox.addActionListener(e -> {
            if (!enVerificacion && todasLasJoyasCheckBox.isSelected()) {
                cargarTodasLasJoyas();
                //checkBoxCategoria.setSelected(false);
                comboBoxCategoria.setEnabled(false);
            }
        });

        // Acción para "Categoría"
        comboBoxCategoria.addActionListener(e -> {
            if (!enVerificacion) {
                cargarJoyasPorCategoria((String) comboBoxCategoria.getSelectedItem());
                todasLasJoyasCheckBox.setSelected(false);
            }
        });

        // Acción para el botón "Contar"
        /*
        btnContar.addActionListener(e -> {
            int cantidad = listModel.getSize();
            JOptionPane.showMessageDialog(mainPanel, "Total de joyas: " + cantidad, "Conteo", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/inversor.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        });
        */



        btnContar.addActionListener(e -> {
            int cantidad = listModel.getSize();

            // Prepara el ícono que quieras mostrar
            ImageIcon icon = new ImageIcon(
                    new ImageIcon(getClass().getResource("/inversor.png"))
                            .getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH)
            );

            // Definir las opciones a mostrar
            Object[] opciones = { "OK", "Marcar todo verificado" };

            int seleccion = JOptionPane.showOptionDialog(
                    mainPanel,
                    "Total de joyas: " + cantidad,
                    "Conteo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    opciones,
                    opciones[0]  // Opción por defecto
            );

            // Revisar la opción que seleccionó el usuario
            if (seleccion == 1) {
                // "Marcar todo verificado"
                //verificarTodo();
                controladora.actualizarFechaVerificacionCategoria(categoriaSeleccionadaActual,LocalDateTime.now() );
            }
            // Si seleccion == 0 ó -1, no hacemos nada (simplemente “OK” o cerrar diálogo)
        });







        // Acción para el botón "Verificar"
        btnVerificar.addActionListener(e -> {
            if (!enVerificacion) {
                bloquearOpciones();
                iniciarVerificacion();
            }else{
                dialogVerificacion.setVisible(true);
            }
        });
        listaJoyas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = listaJoyas.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Joya joyaSeleccionada = listModel.getElementAt(index);
                        mostrarDetallesJoya2(joyaSeleccionada);
                    }
                }
            }
        });

    }

    private void cargarTodasLasJoyas() {
        List<String> estado = Arrays.asList("disponible", "prestado");

        List<Joya> joyasNoVendidas = controladora.filtrarJoyas(
                false,
                null,                // No filtrar por ID
                false,
                null,                // No filtrar por categoría
                false,
                null,                // No filtrar por nombre
                true,
                estado);
        actualizarLista(joyasNoVendidas);
    }

    private void cargarJoyasPorCategoria(String categoriaSeleccionada) {
        // Imprime la categoría que se está cargando
        //System.out.println("Cargando joyas para la categoría: '" + categoriaSeleccionada + "'");

        // Asigna la categoría actual para poder utilizarla luego al finalizar la verificación
        categoriaSeleccionadaActual = categoriaSeleccionada;

        // Filtrar joyas por categoría y que no estén vendidas
        List<String> estado = Arrays.asList("disponible", "prestado");
        List<Joya> joyasPorCategoriaNoVendidas = controladora.filtrarJoyas(
                false, null,                // No filtrar por ID
                true, categoriaSeleccionada, // Filtrar por categoría
                false, null,                // No filtrar por nombre
                true,
                estado
        );

        // Imprime la cantidad de joyas encontradas para esa categoría
        //System.out.println("Joyas encontradas para la categoría '" + categoriaSeleccionada + "': " + joyasPorCategoriaNoVendidas.size());

        actualizarLista(joyasPorCategoriaNoVendidas);
    }


    private void actualizarLista(List<Joya> joyas) {
        listModel.clear();
        for (Joya joya : joyas) {
            listModel.addElement(joya);
        }
    }
    private void mostrarDetallesJoya2(Joya joya) {
        JDialog detallesDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(mainPanel), "Detalles de la Joya", true);
        detallesDialog.setSize(650, 900);
        detallesDialog.setLocationRelativeTo(mainPanel);
        DetallesJoya detallesJoya = new DetallesJoya(joya, null);
        detallesDialog.add(detallesJoya.getMainPanel());
        detallesDialog.setVisible(true);
    }


    private void iniciarVerificacion() {
        enVerificacion = true;

        if (dialogVerificacion == null) {
            // Crear el cuadro de diálogo si no existe
            dialogVerificacion = new JDialog((JFrame) SwingUtilities.getWindowAncestor(mainPanel), "Verificación de Joyas", true);
            dialogVerificacion.setSize(600, 400);
            dialogVerificacion.setLocationRelativeTo(mainPanel);

            // Panel principal del diálogo
            JPanel dialogPanel = new JPanel(new BorderLayout());

            // Panel de entrada de ID
            JPanel inputPanel = new JPanel(new BorderLayout());
            JLabel lblInfo = new JLabel("Ingrese el ID de la joya y presione Enter:");
            JTextField txtInputId = new JTextField();
            inputPanel.add(lblInfo, BorderLayout.NORTH);
            inputPanel.add(txtInputId, BorderLayout.CENTER);

            // Panel para mostrar los detalles de la joya
            JPanel detallesPanel = new JPanel();
            detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));
            detallesPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la Joya"));

            // Agregar los paneles al diálogo
            dialogPanel.add(inputPanel, BorderLayout.NORTH);
            dialogPanel.add(new JScrollPane(detallesPanel), BorderLayout.CENTER);

            dialogVerificacion.add(dialogPanel);

            // Listener para verificar el ID al presionar Enter
            txtInputId.addActionListener(e -> {
                String inputId = txtInputId.getText().trim();
                if (!inputId.isEmpty()) {
                    verificarYMostrarJoya(inputId, detallesPanel);
                    txtInputId.setText(""); // Limpiar el campo para el siguiente ID
                }
            });
            // Listener para cerrar el cuadro de diálogo al presionar Escape
            dialogVerificacion.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ESCAPE"), "closeDialog");
            dialogVerificacion.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cerrarVerificacion(); // Cierra el cuadro sin destruirlo
                }
            });
        }

        dialogVerificacion.setVisible(true); // Reutilizar el cuadro si ya existe
    }




    private void cerrarVerificacion() {
        if (dialogVerificacion != null) {
            dialogVerificacion.setVisible(false); // Ocultar el cuadro sin destruirlo
            enVerificacion = false;
            desbloquearOpciones(); // Desbloquear las opciones si es necesario
        }
    }




    private JPanel crearTarjetaAtributo(String titulo, String valor) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBackground(new Color(255, 255, 255)); // Fondo blanco
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), // Borde redondeado
                BorderFactory.createEmptyBorder(10, 15, 10, 15) // Espaciado interno
        ));
        tarjeta.setMaximumSize(new Dimension(450, 50));

        JLabel lblTitulo = new JLabel(titulo + ": ");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(80, 80, 80));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblValor.setForeground(new Color(50, 50, 50));

        tarjeta.add(lblTitulo, BorderLayout.WEST);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private void verificarYMostrarJoya(String id, JPanel detallesPanel) {
        boolean encontrada = false;

        // Iterar sobre el modelo de la lista
        for (int i = 0; i < listModel.getSize(); i++) {
            Joya joya = listModel.getElementAt(i);

            // Verificar si el ID coincide
            if (joya.getId().toString().equals(id)) {
                encontrada = true;

                // Actualizar los detalles de la joya en el panel
                detallesPanel.removeAll();
                detallesPanel.add(crearTarjetaAtributo("ID", String.valueOf(joya.getId())));
                detallesPanel.add(crearTarjetaAtributo("Nombre", joya.getNombre()));
                detallesPanel.add(crearTarjetaAtributo("Categoría", joya.getCategoria()));
                detallesPanel.add(crearTarjetaAtributo("Peso", joya.getPeso() + " gramos"));
                detallesPanel.add(crearTarjetaAtributo("Precio", "$" + joya.getPrecio()));
                detallesPanel.add(crearTarjetaAtributo("Tiene Piedra", joya.isTienePiedra() ? "Sí 💎" : "No 🪨"));
                if (joya.isTienePiedra()) {
                    detallesPanel.add(crearTarjetaAtributo("Información de Piedra", joya.getInfoPiedra()));
                }
                detallesPanel.add(crearTarjetaAtributo("Observación", joya.getObservacion()));
                detallesPanel.revalidate();
                detallesPanel.repaint();

                // Eliminar la joya de la lista
                listModel.remove(i);
                break; // Salir del bucle después de encontrar y procesar
            }
        }

        if (!encontrada) {
            JOptionPane.showMessageDialog(mainPanel, "La joya con ID " + id + " no se encuentra en la lista.", "No Encontrada", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/llora.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        }

        // Comprobar si la lista está vacía
        if (listModel.getSize() == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Todas las joyas han sido verificadas y la lista está vacía.", "Lista Completa", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/nino.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            // Actualiza la fecha de verificación para la categoría
            if (categoriaSeleccionadaActual != null) {
                System.out.println("Actualizando categoría: '" + categoriaSeleccionadaActual + "' con fecha " + LocalDateTime.now());
                controladora.actualizarFechaVerificacionCategoria(categoriaSeleccionadaActual, LocalDateTime.now());
            } else {
                System.out.println("categoriaSeleccionadaActual es null");
            }
        }
    }


    private void bloquearOpciones() {
        todasLasJoyasCheckBox.setEnabled(false);
        //checkBoxCategoria.setEnabled(false);
        comboBoxCategoria.setEnabled(false);
    }

    private void desbloquearOpciones() {
        todasLasJoyasCheckBox.setEnabled(true);
        //checkBoxCategoria.setEnabled(true);
        comboBoxCategoria.setEnabled(true);
    }



    private void cargarCategoriasVerificacion() {
        List<CategoriaVerificacion> categorias = controladora.obtenerCategoriasOrdenadasPorVerificacion();
        JPanel panelCategorias = new JPanel();
        panelCategorias.setLayout(new BoxLayout(panelCategorias, BoxLayout.Y_AXIS));

        for (CategoriaVerificacion cat : categorias) {
            long dias = ChronoUnit.DAYS.between(cat.getUltimaFechaVerificacion(), LocalDateTime.now());
            // Ícono: ✗ si >20 días, ✓ si <=20 días.
            String icono = (dias > 20) ? "\u2717 " : "\u2713 ";
            String texto = icono + cat.getNombreCategoria() + " - " + dias + " días sin verificación";
            JLabel lblCategoria = new JLabel(texto);
            lblCategoria.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblCategoria.setForeground(Color.BLACK); // Texto en negro

            // Colores suaves (pastel)
            Color bgColor = (dias > 20) ? new Color(255, 204, 204)   // Pastel rojo
                    : new Color(204, 255, 204);  // Pastel verde
            lblCategoria.setOpaque(true);
            lblCategoria.setBackground(bgColor);



            panelCategorias.add(lblCategoria);
            panelCategorias.add(Box.createRigidArea(new Dimension(0, 5))); // Espacio entre etiquetas
        }

        panelScrollVerificados.setViewportView(panelCategorias);
    }




    public JPanel getMainPanel() {
        return mainPanel;
    }
}
