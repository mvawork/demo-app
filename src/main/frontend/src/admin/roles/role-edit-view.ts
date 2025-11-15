import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import '@vaadin/text-field';

@customElement("role-edit-view")
class RoleEditView extends LitElement {

    render() {
        return html `
            <div>
                <vaadin-text-field id="roleName" label="Наименование"></vaadin-text-field></vaadin-text-field>
                <vaadin-text-field id="roleDescription" label="Описание"></vaadin-text-field></vaadin-text-field>
            </div>
        `;
    }

}