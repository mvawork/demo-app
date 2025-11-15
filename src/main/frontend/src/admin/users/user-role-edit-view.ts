import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import '@vaadin/combo-box';

@customElement("user-role-edit-view")
class UserRoleEditView extends LitElement {

    render() {
        return html `
            <div>
                <vaadin-combo-box id="roleComboBox" label="Роль"></vaadin-combo-box></vaadin-text-field>
            </div>
        `;
    }
}