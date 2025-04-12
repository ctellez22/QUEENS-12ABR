package logica;

import persistencia.ControladoraPersistencia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Controladora {

    private ControladoraPersistencia persistencia;

    // Constructor
    public Controladora() {
        this.persistencia = new ControladoraPersistencia();
    }

    // Métodos de lógica que usan la persistencia


    // Agregar una nueva joya

    public void crearJoya(String nombre, String precio, double peso, String categoria, String observacion, Boolean tienePiedra, String infoPiedra) {

        persistencia.agregarJoya(nombre, precio, peso, categoria, observacion, tienePiedra, infoPiedra);

        // Imprimir la etiqueta
        Impresora impresora = new Impresora();
        // Obtener el ID de la joya recién creada (puedes ajustar esto según tu lógica)

        // Obtener la joya recién creada con el último ID
        Joya nuevaJoya = persistencia.obtenerUltimaJoya();
        String zplData = generarZPLEtiqueta(nuevaJoya.getId(), precio, peso, tienePiedra, infoPiedra, categoria);
        impresora.imprimirEtiqueta(zplData);
    }


    public void volverImprimir(String nombre, String precio, double peso, String categoria, String observacion, Boolean tienePiedra, String infoPiedra) {
        // Imprimir la etiqueta
        Impresora impresora = new Impresora();
        Joya nuevaJoya = persistencia.obtenerUltimaJoya();
        String zplData = generarZPLEtiqueta(nuevaJoya.getId(), precio, peso, tienePiedra, infoPiedra, categoria);
        impresora.imprimirEtiqueta(zplData);
    }

    public void reImprimirDespues(Long id, String nombre, String precio, double peso, String categoria, String observacion, Boolean tienePiedra, String infoPiedra) {
        // Imprimir la etiqueta
        Impresora impresora = new Impresora();
        //Joya nuevaJoya = persistencia.obtenerUltimaJoya();
        String zplData = generarZPLEtiqueta(id, precio, peso, tienePiedra, infoPiedra, categoria);
        impresora.imprimirEtiqueta(zplData);
    }


    // Editar una joya existente
    public void actualizarJoya(Long id, String nombre, String precio, double peso, String categoria, String observacion, Boolean tienePiedra, String infoPiedra, Boolean vendido, LocalDateTime fechaVendida, String estado) {
        if (peso < 0) {
            throw new IllegalArgumentException("El precio y el peso deben ser mayores o iguales a 0.");
        }
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        persistencia.editarJoya(id, nombre, precio, peso, categoria, observacion, tienePiedra, infoPiedra, vendido, fechaVendida, estado);
    }

    // Eliminar una joya por ID
    public void eliminarJoya(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
        persistencia.eliminarJoya(id);
    }

    // Listar todas las joyas
    public List<Joya> obtenerTodasLasJoyas() {
        return persistencia.obtenerTodasLasJoyas();
    }
/*
    // Filtrar joyas según los criterios especificados
    public List<Joya> filtrarJoyass(boolean filterById,
                                   String id,
                                   boolean filterByCategory,
                                   String category,
                                   boolean filterByName,
                                   String name,
                                   boolean filterByNoVendido,

                                   String estado) {
        // Delegar la operación a la persistencia con los filtros apropiados
        return persistencia.filtrarJoyas(
                filterById ? id : null,
                filterByCategory ? category : null,
                filterByName ? name : null,
                filterByNoVendido ? true : null,
                filterByEstado ? estado : null
        );
    }
*/


    public List<Joya> filtrarJoyas(boolean filterById,
                                   String id,
                                   boolean filterByCategory,
                                   String category,
                                   boolean filterByName,
                                   String name,
                                   boolean filterByNoVendido,
                                   List<String> estado) {
        return persistencia.filtrarJoyas(
                filterById ? id : null,
                filterByCategory ? category : null,
                filterByName ? name : null,
                filterByNoVendido ? true : null,
                (estado != null && !estado.isEmpty()) ? estado : null
        );
    }


    // Marcar una joya como vendida
    public void marcarComoVendida(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }

        Joya joya = persistencia.obtenerJoyaPorId(id); // Obtener la joya desde la base de datos
        if (joya == null) {
            throw new IllegalArgumentException("La joya con ID " + id + " no existe.");
        }

        if (joya.isVendido()) {
            throw new IllegalStateException("La joya ya está marcada como vendida.");
        }

        joya.setVendido(true); // Actualizar el atributo "vendido" de la joya
        joya.setFechaVendida(LocalDateTime.now());
        persistencia.editarJoya(joya.getId(), joya.getNombre(), joya.getPrecio(), joya.getPeso(), joya.getCategoria(), joya.getObservacion(), joya.isTienePiedra(), joya.getInfoPiedra(), joya.isVendido(), joya.getFechaVendida(), joya.getEstado());
    }

    // Obtener una joya por ID
    public Joya obtenerJoyaPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
        return persistencia.obtenerJoyaPorId(id);
    }
    //Actualizar fecha verificacion
    public void actualizarFechaVerificacionCategoria(String categoria, LocalDateTime fecha) {
        persistencia.actualizarFechaVerificacionCategoria(categoria, fecha);
    }

    // Obtener la lista de categorías ordenadas por fecha de verificación (de la más antigua a la más reciente)
    public List<CategoriaVerificacion> obtenerCategoriasOrdenadasPorVerificacion() {
        return persistencia.obtenerCategoriasOrdenadasPorFecha();
    }

    //GENERAR ZPL DE ETIQUETA
    public String generarZPLEtiqueta(Long id, String precio, double peso, boolean tienePiedra, String infoPiedra, String categoria) {
        if (tienePiedra && (categoria.equalsIgnoreCase("topos") || categoria.equalsIgnoreCase("topos Esmeralda") || categoria.equalsIgnoreCase("Candongas Hoggies") || categoria.equalsIgnoreCase("Cuellos"))) {
            // Etiqueta especial para "topos" o "topos Esmeralda" con piedra
            return "^XA\n" +
                    "^PW984\n" +
                    "^LL102\n" +
                    "^FO30,28^A0N,24,24^FD  " + precio + "M ^FS\n" +
                    "^FO30,60^A0N,20,20^FD " + infoPiedra + " ^FS\n" +
                    "^FO200,15^BY1,3,50^BCN,50,Y,N^FD" + id + "^FS\n" +
                    "^XZ";
        } else if (categoria.equalsIgnoreCase("topos") || categoria.equalsIgnoreCase("topos Esmeralda") || categoria.equalsIgnoreCase("Topos Doble Servicio") || categoria.equalsIgnoreCase("Candongas Hoggies") || categoria.equalsIgnoreCase("Cuellos")) {
            // Etiqueta para "topos" o "topos Esmeralda" sin piedra
            return "^XA\n" +
                    "^PW984\n" +
                    "^LL102\n" +
                    "^FO30,28^A0N,24,24^FD  " + peso + "M ^FS\n" +
                    "^FO30,58^A0N,19,19^FD" + precio + " ^FS\n" +
                    "^FO150,15^BY1,3,50^BCN,50,Y,N^FD" + id + "^FS\n" +
                    "^XZ";
        } else if (tienePiedra) {
            // Etiqueta estándar para joyas con piedra
            return "^XA\n" +
                    "^PW984\n" +
                    "^LL102\n" +
                    "^FO30,28^A0N,17,17^FD  " + precio + "M ^FS\n" +
                    "^FO30,60^A0N,17,17^FD " + infoPiedra + " ^FS\n" +
                    "^FO425,15^BY1,3,50^BCN,50,Y,N^FD" + id + "^FS\n" +
                    "^XZ";
        } else {
            // Etiqueta estándar
            return "^XA\n" +
                    "^PW984\n" +
                    "^LL102\n" +
                    "^FO30,28^A0N,24,24^FD  " + peso + "Q ^FS\n" +
                    "^FO30,58^A0N,19,19^FD" + precio + " ^FS\n" +
                    "^FO425,15^BY1,3,50^BCN,50,Y,N^FD" + id + "^FS\n" +
                    "^XZ";
        }
    }
}

