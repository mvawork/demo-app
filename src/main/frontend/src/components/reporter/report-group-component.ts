import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';
import sharedStyles from 'Frontend/src/styles/shared-styles.css?inline';
import styles from './report-group-component.css?inline';
import '@vaadin/button';
import '@vaadin/grid';

@customElement('report-group-component')
class ReportGroupComponent extends LitElement {

    static get styles() {
        return [sharedStyles, styles]
    }

    render() {
        return html`
            <vaadin-grid id="dataGrid"></vaadin-grid>
            <div>
                <vaadin-button id="runButton">Сформировать</vaadin-button>
                <vaadin-button id="cancelButton">Отмена</vaadin-button>
            <div>
        `;
    }
}
