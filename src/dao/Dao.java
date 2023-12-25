package dao;

import entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface Dao<K,E> {
    boolean delete(K id);
    E save(E ticket);
    E update(E ticket);
    Optional<E> findById(K id);
    List<E> findAll();
}
