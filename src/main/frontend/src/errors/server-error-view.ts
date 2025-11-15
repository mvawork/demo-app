import {html, LitElement} from "lit";

import styles from "./server-error-view.css?inline";
import {customElement} from "lit/decorators.js";

@customElement("server-error-view")
class ServerErrorView extends LitElement {

    static get styles() {
        return [styles];
    }

    render() {
        return html `
            <div class="error-page">
                <h1>Ошибка приложения</h1>
                <p>Извините, что-то пошло не так.</p>
                <a href="/">Перейти на главную страницу</a>
            </div>
        `;
    }

}