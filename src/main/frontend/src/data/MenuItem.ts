export interface MenuItem {
    id: string;
    title: string;
    url: string;
    active: boolean;
    subItems?: MenuItem[]
}
