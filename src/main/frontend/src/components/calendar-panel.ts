import {html, LitElement, PropertyValues} from "lit";
import {customElement, query} from "lit/decorators.js";
import styles from "./calendar-panel.css?inline";
import "@vaadin/icon";


@customElement("calendar-panel")
class CalendarPanel extends LitElement {

    static get styles() {
        return [styles];
    }

    protected render() {
        return html`
            <vaadin-icon icon="vaadin:calendar"></vaadin-icon>
            <span class="day" id="dayId"></span>
            <span class="month">
                <span id="monthId"></span>
                <br/>
                <span id="weekDayId"></span>
            </span>
            <span class="time" id="timeId"></span>
        `;
    }

    @query("#dayId")
    dayId: HTMLSpanElement | undefined;

    @query("#monthId")
    monthId: HTMLSpanElement | undefined;

    @query("#weekDayId")
    weekDayId: HTMLSpanElement | undefined;

    @query("#timeId")
    timeId: HTMLSpanElement | undefined;

    protected firstUpdated(_changedProperties: PropertyValues) {
        super.firstUpdated(_changedProperties);
        this.startClock()
    }

    static months = [
        "января",
        "февраля",
        "марта",
        "апреля",
        "мая",
        "июня",
        "июля",
        "августа",
        "сентября",
        "октября",
        "ноября",
        "декабря"
    ];

    static days = ['вс.', 'пн.', 'вт.', 'ср.', 'чт.', 'пт.', 'сб.'];

    startClock() {
        const d: Date = new Date();
        let day: number | string = d.getDate();
        let hours: number | string = d.getHours();
        let minutes: number | string = d.getMinutes();
        if (day <= 9) day = '0' + day;
        if (hours <= 9) hours = '0' + hours;
        if (minutes <= 9) minutes = '0' + minutes;
        if (this.dayId) this.dayId.textContent = day.toString();
        if (this.monthId) this.monthId.textContent = CalendarPanel.months[d.getMonth()];
        if (this.weekDayId) this.weekDayId.textContent = CalendarPanel.days[d.getDay()];
        if (this.timeId) this.timeId.textContent = hours + ":" + minutes;
        setTimeout(this.startClock.bind(this), 1000);
    }

}