package de.pianoman911.mapengine.core.util;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtil {

    public static List<Component> inlineComponent(Component component) {
        component = component.compact();
        if (component.children().isEmpty()) {
            return List.of(component);
        }

        List<Component> components = new ArrayList<>();
        inlineComponent0(components, component);
        return components;
    }

    private static void inlineComponent0(List<Component> components, Component component) {
        components.add(component.children(List.of()));
        for (Component child : component.children()) {
            inlineComponent0(components, child.applyFallbackStyle(component.style()));
        }
    }
}
