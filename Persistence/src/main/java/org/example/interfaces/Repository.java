package org.example.interfaces;

public interface Repository<T, ID> {

    /**
     * Saves an entity to the database.
     * @param entity the entity to save
     */
    void save(T entity);

    /**
     * Deletes an entity from the database by its identifier.
     * @param id the identifier of the entity to delete
     */
    void delete(ID id);

    /**
     * Deletes all entities from the database.
     */
    void deleteAll();

    /**
     * Updates an existing entity in the database.
     * @param entity the entity to update
     */
    void update(T entity);

    /**
     * Checks if an entity with the given identifier exists in the database.
     * @param id the identifier to check
     * @return true if an entity with the given identifier exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Finds an entity in the database by its identifier.
     * @param id the identifier of the entity to find
     * @return the found entity, or null if not found
     */
    T findById(ID id);

    /**
     * Finds all entities in the database.
     * @return the found entities, or null if not found
     */
    Iterable<T> findAll();

    /**
     * Returns the total number of entities in the database.
     * @return the total number of entities
     */
    long count();
}
