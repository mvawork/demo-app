import {html, LitElement} from "lit";
import {state, customElement, property} from "lit/decorators.js";
import styles from './main-menu-layout.css?inline';
import sharedStyles from '../styles/shared-styles.css?inline';
import '@vaadin/progress-bar';
import {MenuItem} from './data/MenuItem';


@customElement("menu-layout")
class MainMenuLayout extends LitElement {

    @property({type: Array})
    menuItems: any = undefined;

    @state()
    hoveredItem: string | undefined = undefined;

    static get styles() {
        return [sharedStyles, styles];
    }

    render() {
        return html`
            <div class="navigation-panel">
                <div class="menu-items">
                    ${this.menuItems ? this.menuItems.map((item: MenuItem) => this.renderMenuItem(item)) : html``}
                </div>
            </div>
            <div class="content-block">
                <slot></slot>
            </div>
        `;
    }


    private renderMenuItem(item: MenuItem) {
        return html `
            <a router-link id=${item.id} class="switcher" ?is-active=${item.active} href=${item.url}
               @mouseover=${() => this.handleMouseOver(item.id)}
               @mouseleave=${() => this.handleMouseLeave()} >
                <span class="switcher-icon"> </span>
                ${item.title}
            </a>
        `;
    }

    private handleMouseOver(itemId: string) {
        this.hoveredItem = itemId;

    }

    private handleMouseLeave() {
        this.hoveredItem = undefined;
    }
}