import {html, LitElement} from "lit";
import styles from "./not-found-view.css?inline";
import sharedStyles from "../../styles/shared-styles.css?inline";
import {customElement} from "lit/decorators.js";

@customElement("not-found-view")
class NotFoundView extends LitElement {

    static get styles() {
        return [sharedStyles, styles];
    }

    render() {
        return html`
            <div class="content-block">
                <div class="content-left">
                    <h1>Страница не найдена</h1>
                    <a class="btn" href="/" theme="primary">Вернутся на главную</a>
                </div>
                <div class="content-right"> 
                </div>
            </div>
        `;
    }

}