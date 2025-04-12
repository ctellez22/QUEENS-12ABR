package igu;

import logica.Controladora;
import logica.Joya;



import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class VerDatos {
    private JPanel mainPanel;
    private JCheckBox idCheckBox;
    private JTextField txtId;
    private JCheckBox categoriaCheckBox;
    private JComboBox<String> comboBoxCategoria;
    private JCheckBox nombreCheckBox;
    private JTextField txtNombre;
    private JList<Joya> lstResultados; // Cambiado para coincidir con el formulario
    private JPanel panelFoto;
    private JCheckBox noVendidoCheckBox;
    private JComboBox cmbEstado;
    private JCheckBox noAnuladoCheckBox;
    private DefaultListModel<Joya> listModel;

    private Controladora controladora;

    public VerDatos(JFrame parent) {

        LoadingScreen loadingScreen = new LoadingScreen(parent);

        // Mostrar la pantalla de carga mientras se inicializa la vista
        new Thread(() -> {
            loadingScreen.show();
            try {

                inicializarTodo();
                actualizarListaFiltrada();
            } finally {
                SwingUtilities.invokeLater(loadingScreen::hide);
            }
        }).start();
    }




    private void inicializarTodo() {

        // Inicializar la controladora
        controladora = new Controladora();
        // Crear ImageIcon y redimensionar
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/gestion-de-materiales.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Agregar la imagen al JLabel
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);


        // Inicializar el modelo de lista
        listModel = new DefaultListModel<>();
        lstResultados.setModel(listModel);

        // Configurar el renderizado de la lista
        lstResultados.setCellRenderer(new JoyaListCellRenderer());



        // Acción al seleccionar un elemento en la lista
        lstResultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lstResultados.getSelectedValue() != null) {
                mostrarDetalles(lstResultados.getSelectedValue());
                actualizarListaFiltrada();
            }
        });

        // Llamar a agregarListeners para configurar la actualización automática
        agregarListeners();

    }






    private void agregarListeners() {
        // Listener para txtId
        // Listener para txtId: Buscar solo cuando se presione Enter
        txtId.addActionListener(e -> {
            if (idCheckBox.isSelected() && !txtId.getText().isEmpty()) {
                actualizarListaFiltrada();
            }
        });

        // Listener para comboBoxCategoria
        comboBoxCategoria.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                actualizarListaFiltrada();
            }
        });

        // Listener para txtNombre
        txtNombre.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarListaFiltrada();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarListaFiltrada();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarListaFiltrada();
            }
        });
        // Listener para noVendidoCheckBox
        noVendidoCheckBox.addActionListener(e -> actualizarListaFiltrada());
        idCheckBox.addActionListener(e -> actualizarListaFiltrada());
        categoriaCheckBox.addActionListener(e -> actualizarListaFiltrada());
        nombreCheckBox.addActionListener(e -> actualizarListaFiltrada());
        noVendidoCheckBox.addActionListener(e -> actualizarListaFiltrada());

        cmbEstado.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                actualizarListaFiltrada();
            }
        });
    }


    public void actualizarListaFiltradaa() {
        boolean filterById = idCheckBox.isSelected() && !txtId.getText().isEmpty();
        boolean filterByCategory = categoriaCheckBox.isSelected() && comboBoxCategoria.getSelectedItem() != null;
        boolean filterByName = nombreCheckBox.isSelected() && !txtNombre.getText().isEmpty();
        boolean filterByNoVendido = noVendidoCheckBox.isSelected();
        boolean filterByEstado =  cmbEstado.getSelectedItem() != null&& !cmbEstado.getSelectedItem().toString().equalsIgnoreCase("Todos");




        // Verificar si solo está activo el filtro de estado
        boolean isOnlyEstadoFilterActive = filterByEstado && !filterById && !filterByCategory && !filterByName && !filterByNoVendido;
        //boolean isOnlyNoVendidoFilterActive = filterByNoVendido && !filterById && !filterByCategory && !filterByName && !filterByEstado;


/*
        if (isOnlyNoVendidoFilterActive || isOnlyEstadoFilterActive) {
            listModel.clear();
            return;
        }*/
        if (isOnlyEstadoFilterActive) {
            listModel.clear();
            return;
        }


        List<String> estado = new ArrayList<>();
        estado.add(cmbEstado.getSelectedItem().toString());
        List<Joya> filteredJoyas = controladora.filtrarJoyas(
                filterById, txtId.getText(),
                filterByCategory, (String) comboBoxCategoria.getSelectedItem(),
                filterByName, txtNombre.getText(),
                filterByNoVendido,
                //filterByEstado, (String) cmbEstado.getSelectedItem()
                 filterByEstado ? estado : null

        );

        actualizarLista(filteredJoyas);
    }




    public void actualizarListaFiltrada() {
        boolean filterById = idCheckBox.isSelected() && !txtId.getText().isEmpty();
        boolean filterByCategory = categoriaCheckBox.isSelected() && comboBoxCategoria.getSelectedItem() != null;
        boolean filterByName = nombreCheckBox.isSelected() && !txtNombre.getText().isEmpty();
        boolean filterByNoVendido = noVendidoCheckBox.isSelected();
        boolean filterByEstado = cmbEstado.getSelectedItem() != null && !cmbEstado.getSelectedItem().toString().equalsIgnoreCase("Todos");

        // Si solo está activo el filtro de estado, no cargues nada
        if (filterByEstado && !filterById && !filterByCategory && !filterByName && !filterByNoVendido) {
            listModel.clear();
            JOptionPane.showMessageDialog(mainPanel, "Por favor, seleccione más filtros para realizar la búsqueda.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener los resultados filtrados
        List<String> estado = new ArrayList<>();
        estado.add(cmbEstado.getSelectedItem().toString());
        List<Joya> filteredJoyas = controladora.filtrarJoyas(
                filterById, txtId.getText(),
                filterByCategory, (String) comboBoxCategoria.getSelectedItem(),
                filterByName, txtNombre.getText(),
                filterByNoVendido,
                filterByEstado ? estado : null
        );

        actualizarLista(filteredJoyas);
    }

    private void actualizarLista(List<Joya> joyas) {
        listModel.clear();
        for (Joya joya : joyas) {
            listModel.addElement(joya);
        }
    }

    private void mostrarDetalles(Joya joya) {
        JFrame frame = new JFrame("Detalles de Joya");
        DetallesJoya detallesPanel = new DetallesJoya(joya, this);
        frame.setContentPane(detallesPanel.getMainPanel());
        frame.setSize(650, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
}


