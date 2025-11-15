import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import '@vaadin/combo-box';

@customElement("role-privilege-edit-view")
class RolePrivilegeEditView extends LitElement {

    render() {
        return html `
            <div>
                <vaadin-combo-box id="privilegeComboBox" label="Привилегия"></vaadin-combo-box></vaadin-text-field>
            </div>
        `;
    }

}