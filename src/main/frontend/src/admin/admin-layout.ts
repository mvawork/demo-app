import {customElement, property} from "lit/decorators.js";
import {html, LitElement} from "lit";
import styles from './admin-layout.css?inline';
import sharedStyles from '../../styles/shared-styles.css?inline';


@customElement("admin-layout")
class AdminLayout extends LitElement {

    @property({type: Array})
    menuItems: any = undefined;

    static get styles() {
        return [sharedStyles, styles];
    }

    protected render() {
        return html`
            <div class="menu-block">
                ${this.menuItems ? this.menuItems.map((item: any) => html`
                    <div class="menu-item" ?is-active=${item.active}>
                        <a router-link
                           id=${item.id}
                           class="switcher"
                           href=${item.url + (item.urlParameter ? item.urlParameter : '')}>
                            <span class="menu-item-content">${item.title}</span>
                        </a>
                    </div>
                `) : html``}
            </div>
            <div class="content-block">
                <slot></slot>
            </div>
        `;
    }

}