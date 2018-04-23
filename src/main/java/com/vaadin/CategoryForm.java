package com.vaadin;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

public class CategoryForm extends UI {
    final Button createCategory = new Button("Create category");
    final Grid categoryGrid = new Grid();
    @Override
    protected void init(VaadinRequest vaadinRequest) {

    }
}
