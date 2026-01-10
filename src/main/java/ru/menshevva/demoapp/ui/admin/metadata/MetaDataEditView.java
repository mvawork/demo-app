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
import com.vaadin.flow.data.provider.DataProvider;
import ru.menshevva.demoapp.dto.ChangeStatus;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;

import java.util.Comparator;

public class MetaDataEditView extends VerticalLayout {

    private final Binder<ReferenceData> binder = new Binder<>();
    private final Tabs tabs;
    private VerticalLayout mainView;
    private TextArea sqlView;
    private HorizontalLayout fieldView;
    private final MetaDataFieldEditDialog fieldEditDialog = new MetaDataFieldEditDialog();
    private ReferenceData editValue;
    private Grid<ReferenceFieldData> fieldsGrid;
    private ReferenceFieldData editFieldValue;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

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
        var actionBlock = new VerticalLayout();
        this.addButton = new Button("Добавить");
        addButton.setWidthFull();
        addButton.addClickListener(event -> {
            var value = new ReferenceFieldData();
            value.setChangeStatus(ChangeStatus.ADD);
            fieldEditDialog.setValue(value, () -> {
                editValue.getMetaDataFieldsList().add(value);
                fieldsGrid.getDataProvider().refreshAll();
            });
        });
        this.editButton = new Button("Изменить");
        editButton.setWidthFull();
        editButton.addClickListener(event -> {
                    if (editFieldValue != null) {
                        if (editFieldValue.getChangeStatus() == ChangeStatus.UNCHANGED) {
                            editFieldValue.setChangeStatus(ChangeStatus.MODIFIED);
                        }
                        fieldEditDialog.setValue(editFieldValue, () -> fieldsGrid.getDataProvider().refreshAll());
                    }
                }
        );
        this.deleteButton = new Button("Удалить");
        deleteButton.addClickListener(event -> {
            if (editFieldValue != null) {
                if (editFieldValue.getChangeStatus() == ChangeStatus.ADD) {
                    editValue.getMetaDataFieldsList().remove(editFieldValue);
                } else {
                    editFieldValue.setChangeStatus(ChangeStatus.DELETED);
                }
                setSelectedField(null);
                fieldsGrid.getDataProvider().refreshAll();
            }
        });
        deleteButton.setWidthFull();

        this.fieldView = new HorizontalLayout();
        this.fieldsGrid = new Grid<ReferenceFieldData>();
        fieldsGrid.addColumn(ReferenceFieldData::getFieldName)
                .setHeader("Имя поля");
        fieldsGrid.addColumn(ReferenceFieldData::getFieldTitle)
                .setHeader("Наименование поля");
        fieldsGrid.addColumn(f -> Boolean.TRUE.equals(f.getFieldKey()) ? "Да" : "Нет")
                .setHeader("Ключевое поле");
        fieldsGrid.addColumn(ReferenceFieldData::getFieldLength)
                .setHeader("Щирина поля");
        fieldsGrid.addColumn(ReferenceFieldData::getFieldOrder)
                .setHeader("Позиция");
        fieldsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        fieldsGrid.addSelectionListener(e -> {
            setSelectedField(e.getFirstSelectedItem().orElse(null));
        });

        actionBlock.add(addButton, editButton, deleteButton);
        actionBlock.setWidth(100, Unit.PIXELS);
        fieldView.add(fieldsGrid, actionBlock);
        fieldView.setSizeFull();
        fieldView.setFlexGrow(1, fieldsGrid);
        this.fieldView.setVisible(false);
    }

    private void setSelectedField(ReferenceFieldData value) {
        this.editFieldValue = value;
        editButton.setEnabled(editFieldValue != null);
        deleteButton.setEnabled(editFieldValue != null);
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
        this.editValue = value;
        binder.readBean(value);
        var dataProvider = DataProvider.ofCollection(value.getMetaDataFieldsList());
        dataProvider.setSortComparator((f1, f2) -> Integer.compare(f1.getFieldOrder(), f2.getFieldOrder()));
        dataProvider.setFilter(v -> v.getChangeStatus() != ChangeStatus.DELETED);

        fieldsGrid.setDataProvider(dataProvider);
        setSelectedField(null);
    }

    public void getValue(ReferenceData value) throws ValidationException {
        binder.writeBean(value);
    }

}
