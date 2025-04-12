package igu;

import logica.Controladora;
import logica.Joya;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class CargarDatos {
    private JTextField txtNombre;
    private JTextArea txtObs;
    private JTextField txtPeso;
    private JComboBox<String> cmbCategoria;
    private JButton btnLimpiar;
    private JButton btnGuardar;
    private JPanel mainPanel; // Contenedor principal de la ventana
    private JCheckBox siCheckBox;
    private JTextArea textArea1;
    private JTextArea txtInfoPiedra;
    private JPanel fotoPanel;
    private JLabel txtPrecioTotal;
    private JFormattedTextField txtPrecioGramo;

    private Controladora logicaController; // Instancia de la controladora de lógica

    public CargarDatos(JFrame parent) {
        // Crear la pantalla de carga
        LoadingScreen loadingScreen = new LoadingScreen(parent);

        new Thread(() -> {
            loadingScreen.show(); // Mostrar la animación de carga
            try {
                inicializarTodo();

            } finally {
                SwingUtilities.invokeLater(loadingScreen::hide);
            }
        }).start();
    }


    private void inicializarTodo() {
        // Instanciar la controladora de lógica
        logicaController = new Controladora();
        ImageIcon Icon = new ImageIcon(getClass().getResource("/imprimir.png"));
        ImageIcon customIcon = new ImageIcon(Icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        //
        ImageIcon successIcon = new ImageIcon(getClass().getResource("/nino.png"));
        ImageIcon scaledSuccessIcon = new ImageIcon(successIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        txtPrecioTotal.setText("0"); // Valor inicial

        // Listener para txtPeso y txtPrecioGramo
        txtPeso.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                actualizarPrecioTotal();
            }
        });

        txtPrecioGramo.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                actualizarPrecioTotal();
            }
        });


        // Configurar símbolos personalizados para el formato
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\''); // Separador de miles: apóstrofe
        symbols.setDecimalSeparator('.');  // Separador decimal: punto

        // Configurar el formato con los símbolos personalizados
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.###", symbols);
        decimalFormat.setGroupingUsed(true);

        NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
        numberFormatter.setAllowsInvalid(false); // No permitir valores no válidos
        numberFormatter.setValueClass(Double.class);

        txtPrecioGramo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(numberFormatter));


        //Listener para el checkTienePiedra
        siCheckBox.addActionListener(e -> {
            txtInfoPiedra.setVisible(siCheckBox.isSelected());
            textArea1.setVisible(siCheckBox.isSelected());
        });


        // Configurar el botón "Limpiar"
        btnLimpiar.addActionListener(e -> {
            txtNombre.setText("");
            //txtPrecioTotal.setText("");
            txtObs.setText("");
            txtPeso.setText("");
            txtPrecioGramo.setText("");
            cmbCategoria.setSelectedIndex(0);
            txtInfoPiedra.setText("");
            siCheckBox.setSelected(false);
        });

        // Configurar el botón "Guardar"
        btnGuardar.addActionListener(e -> {
            try {
                // Capturar los datos de los campos

                String nombre = txtNombre.getText();
                String categoria = (String) cmbCategoria.getSelectedItem();
                boolean si = siCheckBox.isSelected();
                String infoPiedra = txtInfoPiedra.getText();
                String obs = txtObs.getText();

                // Validaciones
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "El nombre de la joya no puede estar vacío.", "Error de entrada", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/llora.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
                    );
                    return;
                }

                if (categoria.equals("-") || cmbCategoria.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "Debe seleccionar una categoría válida.", "Error de entrada", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/llora.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
                    );
                    return;
                }

                if (obs.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Debe proporcionar una descripción u observaciones.", "Error de entrada", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/llora.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
                    );
                    return;
                }

                // Datos válidos: Continuar con la creación de la joya

                double peso = Double.parseDouble(txtPeso.getText());
                double precioGramo = txtPrecioGramo.getText().isEmpty() ? 0.0 : Double.parseDouble(txtPrecioGramo.getText().replace("'", ""));
                String txtPrecioTotal =  formatearNumero(peso * precioGramo);

                //String precioFormateado = decimalFormat.format(precio);

                // Llamar a la controladora de lógica para guardar los datos
                logicaController.crearJoya(nombre, txtPrecioTotal, peso, categoria, obs, si, infoPiedra);

                // Crear mensaje de éxitox
                String mensaje = "Joya guardada correctamente:\n" +
                        "Nombre: " + nombre + "\n" +
                        "Precio: " + txtPrecioTotal + "\n" +
                        "Peso: " + peso + "\n" +
                        "Categoría: " + categoria + "\n" +
                        "Observaciones: " + obs;

                // Permitir múltiples reimpresiones
                boolean reimprimir = true;
                while (reimprimir) {
                    Object[] opciones = {"Aceptar", "Volver a imprimir"};
                    int opcionSeleccionada = JOptionPane.showOptionDialog(
                            mainPanel,
                            mensaje,
                            "Éxito",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            scaledSuccessIcon,
                            opciones,
                            opciones[0]
                    );

                    if (opcionSeleccionada == 1) {
                        // Acción para "Volver a imprimir"

                        logicaController.volverImprimir(nombre, txtPrecioTotal, peso, categoria, obs, si, infoPiedra);

                        JOptionPane.showMessageDialog(mainPanel,
                                "Imprimiendo la joya nuevamente:\n" + mensaje,
                                "Reimpresión",
                                JOptionPane.INFORMATION_MESSAGE,
                                customIcon);
                    } else {
                        // Salir del bucle si se selecciona "Aceptar"
                        reimprimir = false;
                    }
                }

                // Limpiar los campos después de guardar
                txtNombre.setText("");
                //txtPrecioTotal.setText("null");
                txtPeso.setText("");
                txtObs.setText("");
                //cmbCategoria.setSelectedIndex(0);
                txtInfoPiedra.setText("");
                siCheckBox.setSelected(false);
                txtPrecioGramo.setText("");

            //} catch (NumberFormatException ex) {
                // Manejo de error si los campos de precio o peso no son números válidos
                //JOptionPane.showMessageDialog(mainPanel,
                       // "Error: Precio y peso deben ser valores numéricos.",
                      //  "Error de entrada",
                       // JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/llora.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
                //);
            } catch (Exception ex) {
                // Manejo de errores generales
                JOptionPane.showMessageDialog(mainPanel,
                        "Error al guardar la joya: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(getClass().getResource("/llora.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
                );
            }
        });
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }



    private void actualizarPrecioTotal() {
        try {
            if (!txtPrecioGramo.getText().isEmpty() && !txtPeso.getText().isEmpty()) {
                double precioGramo = Double.parseDouble(txtPrecioGramo.getText().replace("'", ""));
                double peso = Double.parseDouble(txtPeso.getText());

                String precioFormateado = formatearNumero(peso * precioGramo);

                // Actualiza el JLabel con el precio total formateado
                txtPrecioTotal.setText(precioFormateado);
            } else {
                txtPrecioTotal.setText("0"); // Limpia si uno de los campos está vacío
            }
        } catch (Exception ex) {}
    }

    @FunctionalInterface
    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();

        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
    }




    public static String formatearNumero(double numero) {
        // Configurar los símbolos personalizados
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\''); // Separador de miles: apóstrofe
        symbols.setDecimalSeparator('.');  // Separador decimal: punto

        // Configurar el formato del número
        DecimalFormat formatter = new DecimalFormat("#,##0.###", symbols);

        // Formatear el número y devolverlo como String
        return formatter.format(numero);
    }






}

