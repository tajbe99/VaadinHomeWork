package com.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CategoryService {

    private static CategoryService instance;
    private static final HashMap<Long, Category> categoryMap = new HashMap<>();
    private long nextId = 0;

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
            instance.ensureTestData();
        }
        return instance;
    }


    public synchronized void save(Category category) {
        if (category == null) {
            new RuntimeException("Error");
            return;
        }
        if (category.getId() == null) {
            category.setId(nextId++);
        }
        try {
            category = category.clone();
        } catch (CloneNotSupportedException e) {
            new RuntimeException("Error");
        }
        categoryMap.put(category.getId(), category);
    }

    public synchronized void delete(Category category) {
        categoryMap.remove(category.getId());
    }

    public static synchronized Set<Category> findAllCategories() {
        Set<Category> result = new HashSet<>();
        try {
            for (Category c : categoryMap.values())
                result.add(c.clone());
        } catch (CloneNotSupportedException e) {
            new RuntimeException("Error");
        }
        return result;
    }

    public void ensureTestData() {
        if (findAllCategories().isEmpty()) {
            for (HotelCategory category : HotelCategory.values()) {
                Category c = new Category();
                c.setName(category.toString());
                save(c);
            }
        }
    }

}
