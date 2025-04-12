package Prueba;

import jakarta.persistence.*;

public class PersistenceTest {
    public static void main(String[] args) {
        // Crear el EntityManagerFactory usando el nombre de la unidad de persistencia definida en persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadDePersistencia");
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("Conexión exitosa a la base de datos");
        } finally {
            em.close();
            emf.close();
        }
    }
}
