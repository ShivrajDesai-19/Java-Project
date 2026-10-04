package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import java.util.List;

/**
 * Unit 2 Syllabus Concept: Generic Interface
 * Defines standard CRUD contracts for database access layers.
 */
public interface GenericDAO<T> {
    void insert(T entity) throws DatabaseException;
    void update(T entity) throws DatabaseException;
    void delete(int id) throws DatabaseException;
    T findById(int id) throws DatabaseException;
    List<T> findAll() throws DatabaseException;
}
