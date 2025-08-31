package com.mar.libhome.view;

import java.util.UUID;

public interface PopupEntity {

    UUID getId();

    default Long getEntityId() {
        return null;
    }

    String getTitle();

}
