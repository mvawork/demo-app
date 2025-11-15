import {customElement} from "lit/decorators.js";
import {html, LitElement} from "lit";

@customElement("privileges-edit-view")
class PrivilegesEditView extends LitElement {

    render() {
        return html `
            <div>
                <vaadin-text-field id="privilegesName" label="Наименование"></vaadin-text-field></vaadin-text-field>
                <vaadin-text-field id="privilegesDescription" label="Описание"></vaadin-text-field></vaadin-text-field>
            </div>
        `;
    }

}