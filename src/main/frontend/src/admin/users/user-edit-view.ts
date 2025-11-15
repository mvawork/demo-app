import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import '@vaadin/text-field';

@customElement("user-edit-view")
class UserEditView extends LitElement {

    render() {
        return html `
            <div>
                <vaadin-text-field id="userLogin" label="Логин" required ></vaadin-text-field>
                <vaadin-text-field id="userName" label="ФИО" required></vaadin-text-field>
            </div>
        `;
    }

}