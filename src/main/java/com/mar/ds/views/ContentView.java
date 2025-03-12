package com.mar.ds.views;

import com.mar.ds.utils.FileUtils;
import com.vaadin.flow.component.Component;

public interface ContentView {

    Component getContent();

    FileUtils.ViewTypeDto getViewType();

    default void reloadData() {
        // not reload static view
    }
}
