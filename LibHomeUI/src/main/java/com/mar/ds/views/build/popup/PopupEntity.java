package com.mar.ds.views.build.popup;

public interface PopupEntity {

    Long getId();

    default Long getEntityId() {
        return null;
    }

    String getTitle();

}
