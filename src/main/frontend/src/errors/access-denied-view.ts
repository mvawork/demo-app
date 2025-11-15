import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import styles from "./access-denied-view.css?inline";
import sharedStyles from "../../styles/shared-styles.css?inline";

@customElement("access-denied-view")
class AccessDeniedView extends LitElement {

    static get styles() {
        return [sharedStyles, styles];
    }

    render() {
        return html `
            <div class="block-error">
                <div class="block-error-columns">
                    <div class="block-error-left">
                        <h1 class="error-403">403</h1>
                        <h3 class="error-description">Доступ запрещен</h3>
                    </div>
                    <div class="block-error-image">
                        
                    </div>
                </div>
            </div>
        `;
    }


}