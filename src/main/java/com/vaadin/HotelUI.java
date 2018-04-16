package com.vaadin;

import javax.servlet.annotation.WebServlet;

import com.classes.Hotel;
import com.classes.HotelService;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class HotelUI extends UI {

    final HorizontalLayout contentLayout = new HorizontalLayout();
    final HotelService hotelService = HotelService.getInstance();
    final Grid<Hotel> hotelGrid = new Grid<>(Hotel.class);
    final TextField filterName = new TextField("Name filter");
    final TextField filterAddress = new TextField("Address filter");
    final Panel panel = new Panel();
    final VerticalLayout verticalLayout = new VerticalLayout();
    final Button addHotel = new Button("Add Hotel");
    final Button delHotel = new Button("Delete Hotel");
    private HotelEditForm form = new HotelEditForm(this);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        form.setVisible(false);
        initGrid();

        filterName.addValueChangeListener(e -> updateFilterByName());
        filterName.setValueChangeMode(ValueChangeMode.LAZY);

        filterAddress.addValueChangeListener(e -> updateFilterByAddress());

        verticalLayout.addComponents(filterName, filterAddress, addHotel,delHotel);
        delHotel.setEnabled(false);
        delHotel.addClickListener(e -> {
            Hotel delCandidate = hotelGrid.getSelectedItems().iterator().next();
            hotelService.delete(delCandidate);
            delHotel.setEnabled(false);
            updateList();
            form.setVisible(false);
        });
        panel.setContent(verticalLayout);
        contentLayout.addComponents(hotelGrid, panel, form);
        setContent(contentLayout);
        addHotel.addClickListener(e -> form.setHotel(new Hotel()));
    }

    void updateFilterByAddress() {
        List<Hotel> hotelList = hotelService.findAddress(filterAddress.getValue()) ;
        hotelGrid.setItems(hotelList);
    }

    void updateFilterByName() {
        List<Hotel> hotelList = hotelService.findName(filterName.getValue()) ;
        hotelGrid.setItems(hotelList);
    }

    private void initGrid() {
        hotelGrid.setItems(hotelService.findAll());
        hotelGrid.removeColumn("url");
        hotelGrid.setColumnOrder("name", "address", "rating", "category");
        hotelGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null){
                delHotel.setEnabled(true);
                form.setHotel(e.getValue());
            }
        });
        hotelGrid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>Link at booking.com</a>",
                new HtmlRenderer()).setCaption("Web Site");
        hotelGrid.setRowHeight(50);
    }

    void updateList() {
        List<Hotel> hotelList = hotelService.findAll(filterName.getValue()) ;
        hotelGrid.setItems(hotelList);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = HotelUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
