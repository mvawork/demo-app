import {customElement} from "lit/decorators.js";
import {html, LitElement} from "lit";
import '@vaadin/grid';
import '@vaadin/button';
import '@vaadin/text-field';

import styles from './user-list-view.css?inline';
import editActionStyles from '../../../styles/edit-action-styles.css?inline';
import sharedStyles from '../../../styles/shared-styles.css?inline';

@customElement("user-list-view")
class UserListView extends LitElement {

    static get styles() {
        return [sharedStyles, editActionStyles, styles]
    }

    protected render() {
        return html `
            <div class="filters-block">
                <vaadin-text-field id="userId" label="Идентификатор"></vaadin-text-field>
                <vaadin-text-field id="userLogin" label="Логин"></vaadin-text-field>
                <vaadin-text-field id="userName" label="ФИО"></vaadin-text-field>
            </div>
            <div class="action-block">
                <div class="action-block-left">
                    <vaadin-button id="addButton">Добавить</vaadin-button>
                    <vaadin-button id="editButton">Изменить</vaadin-button>
                    <vaadin-button id="deleteButton">Удалить</vaadin-button>
                </div>
                <div class="action-block-right">
                    <vaadin-button id="reportButton">Отчеты</vaadin-button>
                    <vaadin-button id="searchButton">Найти</vaadin-button>
                    <vaadin-button id="clearButton">Очистить</vaadin-button>
                </div>
            </div>
            <div class="content-block">
                <vaadin-grid style="height: 100%; border-radius: 10px; overflow: hidden;" id="dataGrid"></vaadin-grid>
            </div>
        `;
    }

}
