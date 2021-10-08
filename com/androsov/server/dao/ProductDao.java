package com.androsov.server.dao;

import com.androsov.server.products.exceptions.ManagementException;

import java.util.Comparator;
import java.util.List;

/**
 * The whole point of DAO is to abstract from the implementation details of interacting with product collections.
 * Provides the ability to create changes in collections like ADDED, UPDATED and REMOVED, as well as commit these changes.
 *
 * @param <P> Product in a form that is easy to use for ProductDao implementation.
 * @param <InvokatorName> Nickname of user in a form that is easy to use for ProductDao implementation.
 */
public interface ProductDao<P, InvokatorName> {
    List<P> getList();
    void add(P p, InvokatorName name) throws ManagementException;
    void update(P p, InvokatorName name) throws ManagementException;
    void remove(P p, InvokatorName name) throws ManagementException;
    void sort(Comparator<P> comparator);
    void commit();
}
