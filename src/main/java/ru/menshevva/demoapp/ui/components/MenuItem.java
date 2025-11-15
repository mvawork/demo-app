package ru.menshevva.demoapp.ui.components;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    private String id;
    private String title;
    private String url;
    private boolean active;
}
