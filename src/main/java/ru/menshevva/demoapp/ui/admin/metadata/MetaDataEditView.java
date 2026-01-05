package ru.menshevva.demoapp.ui.admin.metadata;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;

public class MetaDataEditView extends VerticalLayout {

    private final Binder<ReferenceData> binder = new Binder<>();
    private final Tabs tabs;
    private VerticalLayout mainView;
    private TextArea sqlView;
    private HorizontalLayout fieldView;

    public MetaDataEditView() {
        initMainView();
        initSqlView();
        initFieldView();
        this.tabs = new Tabs();
        var mainTab = new Tab("Таблица");
        var sqlTab = new Tab("SQL запрос");
        var fieldTab = new Tab("Описание полей");
        tabs.add(mainTab, sqlTab, fieldTab);
        tabs.addSelectedChangeListener(e -> {
            mainView.setVisible(e.getSelectedTab() == mainTab);
            sqlView.setVisible(e.getSelectedTab() == sqlTab);
            fieldView.setVisible(e.getSelectedTab() == fieldTab);
        });
        add(tabs, mainView, sqlView, fieldView);
        tabs.setSelectedTab(mainTab);
        setSizeFull();
    }

    private void initFieldView() {
        this.fieldView = new HorizontalLayout();
        var grid = new Grid<ReferenceFieldData>();
        grid.addColumn(ReferenceFieldData::getFieldName)
                .setHeader("Имя поля");
        grid.addColumn(ReferenceFieldData::getFieldTitle)
                .setHeader("Наименование поля");
        grid.addColumn(ReferenceFieldData::getFieldLength)
                .setHeader("Щирина поля");
        grid.addColumn(ReferenceFieldData::getFieldOrder)
                .setHeader("Позиция");

        var actionBlock = new VerticalLayout();
        var addButton = new Button("Добавить");
        addButton.setWidthFull();
        var editButton = new Button("Изменить");
        editButton.setWidthFull();
        var deleteButton = new Button("Удалить");
        deleteButton.setWidthFull();
        actionBlock.add(addButton, editButton, deleteButton);
        actionBlock.setWidth(100, Unit.PIXELS);
        fieldView.add(grid, actionBlock);
        fieldView.setSizeFull();
        fieldView.setFlexGrow(1, grid);
        this.fieldView.setVisible(false);
    }

    private void initSqlView() {
        this.sqlView = new TextArea("SQL запрос для просмотра данных");
        binder.forField(sqlView).bind(ReferenceData::getTableSQL, ReferenceData::setTableSQL);
        sqlView.setSizeFull();
        this.sqlView.setVisible(false);
    }

    private void initMainView() {
        this.mainView = new VerticalLayout();
        TextField schemaName = new TextField("Имя схемы");
        schemaName.setWidth(100, Unit.PERCENTAGE);
        binder.forField(schemaName)
                .bind(ReferenceData::getSchemaName, ReferenceData::setSchemaName);
        TextField tableName = new TextField("Имя таблицы");
        tableName.setWidth(100, Unit.PERCENTAGE);
        binder.forField(tableName)
                .bind(ReferenceData::getTableName, ReferenceData::setTableName);
        mainView.add(schemaName, tableName);
    }

    public void setValue(ReferenceData value) {
        binder.readBean(value);
    }

    public void getValue(ReferenceData value) throws ValidationException {
        binder.writeBean(value);
    }

}
