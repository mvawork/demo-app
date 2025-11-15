import {html, LitElement} from "lit";
import {customElement, property} from "lit/decorators.js";
import '@vaadin/button';
import '@vaadin/icon';
import './components/calendar-panel';
import styles from './main-layout.css?inline';
import sharedStyles from '../styles/shared-styles.css?inline';

@customElement('main-layout')
class MainLayout extends LitElement {

    @property({type: String})
    userFio: string = '';

    static get styles() {
        return [sharedStyles, styles];
    }

    render() {
        return html`
            <div class="main-layout">
                <div class="top-panel">
                    <div class="top-panel-layout">
                        <div class="user-photo-wrapper">
                            <div class="user-photo"></div>
                        </div>
                        <div class="info">
                            <div class="info-layout">
                                <div class="info-layout-left">
                                    <div class="top-panel-user">
                                        <p class="top-panel-user-label">Вы вошли в систему как:</p>
                                        <p class="top-panel-user-fio">${this.userFio}</p>
                                    </div>
                                    <p class="top-panel-logout" id="logout" title="Выйти"></p>
                                </div>
                                <div class="info-layout-right">
                                    <div class="info-layout-right-content">
                                        <vaadin-icon id="appProcessLog" icon="vaadin:bell"></vaadin-icon>
                                        <calendar-panel></calendar-panel>
                                        <vaadin-icon id="about" icon="vaadin:info-circle"></vaadin-icon>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="content-block">
                    <slot></slot>
                </div>
            </div>
        `;
    }
}
