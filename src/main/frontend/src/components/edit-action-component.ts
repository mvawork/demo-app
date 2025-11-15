import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import '@vaadin/button';
import styles from "./edit-action-component.css?inline";

@customElement("edit-action-component")
class EditActionComponent extends LitElement {

    static get styles() {
        return [styles]
    }

    render() {
        return html`
            <div class="edit-action-panel">
                <vaadin-button id="saveButton">Сохранить</vaadin-button>
                <vaadin-button id="cancelButton">Отмена</vaadin-button>
            </div>
        `;
    }

}