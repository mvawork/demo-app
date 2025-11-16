import {customElement, property} from "lit/decorators.js";
import {html, LitElement} from "lit";
import styles from './client-layout.css?inline';
import sharedStyles from '../../styles/shared-styles.css?inline';


@customElement("client-layout")
class ClientLayout extends LitElement {

    @property({type: Array})
    menuItems: any = undefined;

    static get styles() {
        return [sharedStyles, styles];
    }

    protected render() {
        return html`
            <div class="content-block">
                <slot></slot>
            </div>
        `;
    }

}