package persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import jakarta.persistence.Query;
import logica.Joya;

import java.io.Serializable;
import java.util.List;

public class JoyaJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    // Constructor para inicializar el EntityManagerFactory
    public JoyaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("miUnidadDePersistencia");
    }

    // Método para obtener el EntityManager
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Método para crear una nueva Joya
    public void create(Joya joya) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(joya);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Método para editar una Joya existente
    public void edit(Joya joya) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(joya);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Método para eliminar una Joya por su ID
    public void delete(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Joya joya = em.find(Joya.class, id); // Busca la Joya por ID
            if (joya != null) {
                em.remove(joya); // Elimina la entidad
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Método para buscar una Joya por su ID usando SQL
    public Joya find(Long id) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM Joya WHERE id = :id", Joya.class);
            query.setParameter("id", id);
            return (Joya) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // Método para obtener todas las Joyas usando SQL
    public List<Joya> findAll() {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM Joya", Joya.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }



    // Método para obtener todas las Joyas ordenadas por ID en orden descendente
    public List<Joya> findAllOrderedByIdDesc() {
        EntityManager em = getEntityManager();
        try {
            // Consulta ordenada por ID en orden descendente
            return em.createQuery("SELECT j FROM Joya j ORDER BY j.id ASC", Joya.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    // Método para filtrar joyas dinámicamente usando SQL

    public List<Joya> filtrarJoyas(String id, String categoria, String nombre, Boolean noVendido, List<String> estado) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM Joya WHERE 1=1");

            if (id != null && !id.isEmpty()) {
                sql.append(" AND id = :id");
            }
            if (categoria != null && !categoria.isEmpty()) {
                sql.append(" AND categoria = :categoria");
            }
            if (nombre != null && !nombre.isEmpty()) {
                sql.append(" AND LOWER(nombre) LIKE :nombre");
            }
            if (noVendido != null) {
                sql.append(" AND vendido = :vendido");
            }
            if (estado != null && !estado.isEmpty()) {
                sql.append(" AND estado IN (:estado)");
            }


            Query query = em.createNativeQuery(sql.toString(), Joya.class);

            if (id != null && !id.isEmpty()) {
                query.setParameter("id", Long.parseLong(id));
            }
            if (categoria != null && !categoria.isEmpty()) {
                query.setParameter("categoria", categoria);
            }
            if (nombre != null && !nombre.isEmpty()) {
                query.setParameter("nombre", "%" + nombre.toLowerCase() + "%");
            }
            if (noVendido != null) {
                query.setParameter("vendido", noVendido ? 0 : 1); // 0: No vendido, 1: Vendido
            }
            if (estado != null && !estado.isEmpty()) {
                query.setParameter("estado", estado); // Pasar lista directamente
            }

            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al filtrar joyas: " + e.getMessage());
            return List.of(); // Devuelve una lista vacía en caso de error
        } finally {
            em.close();
        }
    }



    public Joya obtenerUltimaJoya() {
        EntityManager em = getEntityManager();
        try {
            // Consulta JPQL para obtener la joya con el ID más alto
            return em.createQuery("SELECT j FROM Joya j ORDER BY j.id DESC", Joya.class)
                    .setMaxResults(1) // Limitar el resultado a 1
                    .getSingleResult();
        } catch (Exception e) {
            System.err.println("Error al obtener la última joya: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }



}
