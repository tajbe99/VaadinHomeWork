package com.vaadin;

import javax.servlet.annotation.WebServlet;

import com.classes.Category;
import com.classes.CategoryService;
import com.classes.Hotel;
import com.classes.HotelService;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.*;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.Json;
import elemental.json.JsonValue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

    final HorizontalLayout workWitGrid = new HorizontalLayout();
    final HorizontalLayout gridAndEditiongFiled = new HorizontalLayout();
    final HotelService hotelService = HotelService.getInstance();
    final CategoryService categoryService = CategoryService.getInstance();
    final Grid<Hotel> hotelGrid = new Grid<>(Hotel.class);
    final Grid<Category> categoryGrid = new Grid<>(Category.class);
    final TextField filterName = new TextField();
    final TextField filterAddress = new TextField();
    final Panel panel = new Panel();
    final VerticalLayout verticalLayout = new VerticalLayout();
    final Button addHotel = new Button("Add Hotel");
    final Button delHotel = new Button("Delete Hotel");
    final Button delCategory = new Button("Delete Category");
    final Button addCategory = new Button("Add Category");
    final Button editCategory = new Button("Edit Category");
    private HotelEditForm form = new HotelEditForm(this);
    final MenuBar bar = new MenuBar();
    final VerticalLayout categoryView = new VerticalLayout();
    final HorizontalLayout categoryGridEditorView = new HorizontalLayout();


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        MenuBar.Command command = new MenuBar.Command() {
            MenuBar.MenuItem prevois = null;
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                if (menuItem.getText() == "Hotel") setHotelView();
                if (menuItem.getText() == "Category") setCategoryView();
                if (prevois != null)
                    prevois.setStyleName(null);
                menuItem.setStyleName("highlight");
                prevois = menuItem;
            }
        };
        MenuBar.MenuItem hotelItem = bar.addItem("Hotel",VaadinIcons.BUILDING, command);
        MenuBar.MenuItem categoryItem = bar.addItem("Category",VaadinIcons.BUILDING, command);
        configurCategoryView();
        configurHotelView();
    }

    private void configurCategoryView() {
        categoryGrid.setItems(categoryService.findAllCategories());
        categoryGrid.setColumnOrder("name");
        categoryGrid.removeColumn("id");
        categoryGrid.removeColumn("persisted");
        categoryGridEditorView.addComponents(addCategory,delCategory,editCategory);
        categoryView.addComponents(bar,categoryGridEditorView,categoryGrid);
        setContent(categoryView);

    }

    private void setCategoryView(){
        categoryView.addComponents(bar,categoryGridEditorView,categoryGrid);
        setContent(categoryView);
    }

    private void setHotelView(){
        verticalLayout.addComponents(bar, workWitGrid, gridAndEditiongFiled);
        setContent(verticalLayout);
    }

    private void configurHotelView() {
        form.setVisible(false);
        form.setMargin(false);
        initGrid();
        bar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);

        filterName.addValueChangeListener(e -> updateFilterByName());
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterName.setPlaceholder("Name filter");
        filterAddress.setPlaceholder("Address filter");

        filterAddress.addValueChangeListener(e -> updateFilterByAddress());
        workWitGrid.addComponents(filterName, filterAddress, addHotel,delHotel);
        gridAndEditiongFiled.addComponents(hotelGrid, form);
        verticalLayout.addComponents(bar, workWitGrid, gridAndEditiongFiled);
        delHotel.setEnabled(false);
        delHotel.addClickListener(e -> {
            Hotel delCandidate = hotelGrid.getSelectedItems().iterator().next();
            hotelService.delete(delCandidate);
            delHotel.setEnabled(false);
            updateList();
            form.setVisible(false);
        });
        setContent(verticalLayout);
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
        Grid.Column<Hotel, ?> operatesColumn = hotelGrid.getColumn("operatesFrom");
        operatesColumn.setRenderer(new TextRenderer(){
            @Override
            public JsonValue encode(Object value) {
                    Long longDate = Long.valueOf(value.toString());
                    String result = LocalDate.now().plusDays(longDate).toString();
                    return Json.create(result);
            }
        }) ;
        hotelGrid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>Link at booking.com</a>",
                new HtmlRenderer()).setCaption("Web Site");
        hotelGrid.setRowHeight(50);
        hotelGrid.setWidth("900");
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
