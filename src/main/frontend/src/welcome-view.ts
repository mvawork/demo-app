import {html, LitElement} from "lit";
import {customElement} from "lit/decorators.js";
import styles from './welcome-view.css?inline';
import sharedStyles from '../styles/shared-styles.css?inline';
@customElement("welcome-view")
class WelcomeView extends LitElement {

    static get styles() {
        return [sharedStyles, styles];
    }

    protected render() {
        return html `
            <div class="parent">
                <h1>Добро пожаловать на нашу платформу!</h1>
            </div>
        `;
    }
}