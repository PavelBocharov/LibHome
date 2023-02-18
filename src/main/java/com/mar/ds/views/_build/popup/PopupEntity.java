package com.mar.ds.views._build.popup;

public interface PopupEntity {

    Long getId();
    default Long getEntityId() {
        return null;
    }
    String getTitle();

}
