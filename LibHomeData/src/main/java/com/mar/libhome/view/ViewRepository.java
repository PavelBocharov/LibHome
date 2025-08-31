package com.mar.libhome.view;

import java.util.List;

public interface ViewRepository<E extends PopupEntity> {

    List<E> findAll();
    E save(E entity);
    E delete(E entity);

}
