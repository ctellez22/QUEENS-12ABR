package persistencia;


import logica.CategoriaVerificacion;
import logica.Joya;

import java.time.LocalDateTime;
import java.util.List;

public class ControladoraPersistencia {

    private JoyaJpaController joyaController;
    private CategoriaVerificacionJpaController catVerifController;
    // Constructor
    public ControladoraPersistencia() {
        this.joyaController = new JoyaJpaController();
        this.catVerifController = new CategoriaVerificacionJpaController();
    }

    // Métodos para interactuar con JoyaJpaController

    // Crear una nueva Joya
    public void agregarJoya(String nombre, String precio, double peso, String categoria, String observacion, boolean tienePiedra, String infoPiedra) {
        Joya nuevaJoya = new Joya(nombre, precio, peso, categoria, observacion, tienePiedra, infoPiedra);
        joyaController.create(nuevaJoya);
        System.out.println("Joya agregada: " + nuevaJoya);
    }


    // Editar una Joya existente
    public void editarJoya(Long id, String nombre, String precio, double peso, String categoria, String observacion, boolean tienePiedra, String infoPiedra, Boolean vendido, LocalDateTime fechaVendida, String estado) {
        Joya joya = joyaController.find(id);
        if (joya != null) {
            joya.setNombre(nombre);
            joya.setPrecio(precio);
            joya.setPeso(peso);
            joya.setCategoria(categoria);
            joya.setObservacion(observacion);
            joya.setTienePiedra(tienePiedra);
            joya.setInfoPiedra(infoPiedra);
            joya.setVendido(vendido);
            joya.setFechaVendida(fechaVendida);
            joya.setEstado(estado);
            joyaController.edit(joya);
            System.out.println("Joya editada: " + joya);
        } else {
            System.out.println("Joya con ID " + id + " no encontrada.");
        }
    }

    // Eliminar una Joya
    public void eliminarJoya(Long id) {
        Joya joya = joyaController.find(id);
        if (joya != null) {
            joyaController.delete(id);
            System.out.println("Joya eliminada con ID: " + id);
        } else {
            System.out.println("Joya con ID " + id + " no encontrada.");
        }
    }

    // Listar todas las Joyas
    public List<Joya> obtenerTodasLasJoyas() {
        return joyaController.findAll();
    }

    public List<Joya> obtenerTodasLasJoyasByIdDes() {
        return joyaController.findAllOrderedByIdDesc();
    }




    // Buscar una Joya por ID
    public Joya obtenerJoyaPorId(Long id) {
        Joya joya = joyaController.find(id);
        if (joya != null) {
            return joya;
        } else {
            System.out.println("Joya con ID " + id + " no encontrada.");
            return null;
        }
    }
    //Filtrar Joyas
    public List<Joya> filtrarJoyas(String id, String categoria, String nombre, Boolean noVendido, List<String> estado) {
        return joyaController.filtrarJoyas(id, categoria, nombre, noVendido, estado);
    }
    //Obtener  la ultima joya
    public Joya obtenerUltimaJoya() {
        return joyaController.obtenerUltimaJoya();
    }

    // Método para actualizar o insertar la fecha de verificación de una categoría
    public void actualizarFechaVerificacionCategoria(String nombreCategoria, LocalDateTime fechaVerificacion) {
        CategoriaVerificacion catVerif = catVerifController.findByNombre(nombreCategoria);
        if (catVerif != null) {
            // Si ya existe, actualizamos la fecha
            catVerif.setUltimaFechaVerificacion(fechaVerificacion);
            catVerifController.edit(catVerif);
        } else {
            // Si no existe, creamos uno nuevo
            catVerif = new CategoriaVerificacion(nombreCategoria, fechaVerificacion);
            catVerifController.create(catVerif);
        }
        System.out.println("Registro de verificación actualizado para la categoría: " + nombreCategoria);
    }

    // Método para obtener las categorías ordenadas por fecha de verificación (de la más antigua a la más reciente)
    public List<CategoriaVerificacion> obtenerCategoriasOrdenadasPorFecha() {
        return catVerifController.findAllOrderedByFechaVerificacion();
    }
}
