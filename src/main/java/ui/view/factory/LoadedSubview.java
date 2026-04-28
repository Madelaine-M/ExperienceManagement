package ui.view.factory;

import javafx.scene.Parent;

public record LoadedSubview<T>(Parent root, T controller) {
}
