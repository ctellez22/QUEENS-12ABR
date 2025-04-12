package igu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Principal {
    private JButton cargarDatosButton;
    private JButton verDatosButton;
    private JButton salirButton;
    private JPanel mainPanel; // Contenedor principal de la ventana
    private JButton GROUP_BYButton;
    private JPanel panelFoto;

    public Principal() {

        //Cargar una imagen en panelFoto al iniciar
        cargarImagenEnPanelFoto();


        // Acción para el botón "Cargar Datos"
        cargarDatosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear y mostrar la ventana de cargar datos
                JFrame frame = new JFrame("Cargar Datos");
                frame.setContentPane(new CargarDatos(null).getMainPanel());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cerrar solo esta ventana
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);



            }
        });

        // Acción para el botón "Ver Datos" (implementa según necesidad)
        verDatosButton.addActionListener(e -> {
            JFrame frame = new JFrame("Ver Datos");
            frame.setContentPane(new VerDatos(null).getMainPanel());
            frame.setSize(1400, 1000);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        // Acción para el botón "Contar"
        GROUP_BYButton.addActionListener(e -> {
            JFrame frame = new JFrame("Group By");
            frame.setContentPane(new GroupBy(null).getMainPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // Acción para el botón "Salir"
        salirButton.addActionListener(e -> System.exit(0));



    }
    private void cargarImagenEnPanelFoto() {
        // Ruta de la imagen (ajusta la ruta según la ubicación de tu imagen)
        String imagePath = "/holaa.png";

        try {
            // Cargar y escalar la imagen
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // Crear un JLabel con la imagen
            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Configurar el panelFoto para mostrar la imagen
            panelFoto.setLayout(new BorderLayout());
            panelFoto.add(imageLabel, BorderLayout.CENTER);
            panelFoto.revalidate(); // Asegurar que el panel se actualice
            panelFoto.repaint();   // Redibujar el panel
        } catch (Exception e) {
            // Mostrar un mensaje si no se encuentra la imagen
            JLabel errorLabel = new JLabel("Imagen no disponible");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            panelFoto.setLayout(new BorderLayout());
            panelFoto.add(errorLabel, BorderLayout.CENTER);
            panelFoto.revalidate();
            panelFoto.repaint();
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
