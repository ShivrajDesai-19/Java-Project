package com.petcare.model;

/**
 * Unit 2 Syllabus Concept: Interface
 * Enables polymorphic search across entities.
 */
public interface Searchable {
    boolean matchesSearch(String keyword);
}
