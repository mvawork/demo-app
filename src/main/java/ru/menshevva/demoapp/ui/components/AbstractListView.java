package ru.menshevva.demoapp.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.security.SecurityUtils;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

public abstract class AbstractListView<T, F> extends Composite <VerticalLayout> implements EditActionCallback,
        CallbackDataProvider.FetchCallback<T, F>, CallbackDataProvider.CountCallback<T, F> {


    private final ListViewAbstractSearchService<T, F> searchService;
    private final ConfigurableFilterDataProvider<T, Void, F> dataProvider;
    private final String editRole;

    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    
    private T selectedItem;

    public AbstractListView(ListViewAbstractSearchService<T, F> searchService,
                            String editRole) {
        this.searchService = searchService;
        this.editRole = editRole;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(this, this)
                .withConfigurableFilter();

        selectedItem = null;
        var filterBlock = initFilterBlock();
        if (filterBlock != null) {
            getContent().add(filterBlock);
        }
        var actionBlock = initActionBlock();
        if (actionBlock != null) {
            getContent().add(actionBlock);
        }
        var dataGrid = new Grid<T>();
        initGrid(dataGrid);
        setButtonState(selectedItem);
        getContent().add(dataGrid);
        getContent().setSizeFull();
    }
    
    protected void initGrid(Grid<T> dataGrid) {
        dataGrid.addSelectionListener(event -> {
            selectedItem = event.getFirstSelectedItem().orElse(null);
            setButtonState(selectedItem);
        });
        dataGrid.setDataProvider(dataProvider);
    }

    protected void setButtonState(T selectedItem) {
        var r = SecurityUtils.checkPermission(editRole);
        var f = selectedItem != null;
        addButton.setEnabled(r);
        editButton.setEnabled(r && f);
        deleteButton.setEnabled(r && f);
    }

    protected Component initActionBlock() {
        var actionBlock = new HorizontalLayout();
        this.addButton = new Button("Добавить");
        this.editButton = new Button("Изменить");
        this.deleteButton = new Button("Удалить");
        addButton.addClickListener(event -> this.addItem());
        editButton.addClickListener(event -> this.editItem());
        deleteButton.addClickListener(event -> this.deleteItem());
        actionBlock.add(addButton, editButton, deleteButton);
        return actionBlock;
    }

    protected void deleteItem() {
        
    }

    protected void editItem() {
    }

    protected void addItem() {
        
    }

    protected Component initFilterBlock() {
        return null;
    }

    @Override
    public int count(Query<T, F> query) {
        return searchService.getCount(query);
    }

    @Override
    public Stream<T> fetch(Query<T, F> query) {
        return searchService.fetch(query);
    }

    @Override
    public void ok() {

    }
}
