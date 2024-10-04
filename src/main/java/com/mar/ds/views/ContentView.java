package com.mar.ds.views;

import com.mar.ds.db.entity.ViewType;
import com.vaadin.flow.component.Component;

public interface ContentView {
    Component getContent();
    ViewType getViewType();
    default void reloadData() {
        // not reload static view
    }
}
