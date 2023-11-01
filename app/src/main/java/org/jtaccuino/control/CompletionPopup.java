package org.jtaccuino.control;

import java.util.UUID;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;

public class CompletionPopup extends PopupControl {

    private final ObservableList<String> completionSuggestions = FXCollections.observableArrayList();
    private IntegerProperty visibleCompletions = new SimpleIntegerProperty(this, "visibleCompletions", 10);

    @SuppressWarnings("serial")
    public static class CompletionEvent extends Event {

        public static final EventType<CompletionEvent> COMPLETION
                = new EventType<>("SUGGESTION" + UUID.randomUUID().toString()); //$NON-NLS-1$

        private final String suggestion;

        public CompletionEvent(String suggestion) {
            super(COMPLETION);
            this.suggestion = suggestion;
        }

        public String getSuggestion() {
            return suggestion;
        }
    }

    @SuppressWarnings("this-escape")
    public CompletionPopup() {
        setAutoFix(true);
        setAutoHide(true);
        setHideOnEscape(true);
    }

    public ObservableList<String> getSuggestions() {
        return completionSuggestions;
    }

    public void show(Node node, Rectangle2D caretCoordiantes) {
        var parent = node.getScene().getWindow();
        var localToScene = node.localToScene(caretCoordiantes.getMaxX(), caretCoordiantes.getMaxY());
        this.show(
                parent,
                parent.getX() + localToScene.getX() + node.getScene().getX(),
                parent.getY() + localToScene.getY() + node.getScene().getY());

    }

    public final void setVisibleCompletions(int value) {
        visibleCompletions.set(value);
    }

    public final int getVisibleCompletions() {
        return visibleCompletions.get();
    }

    public final IntegerProperty visibleCompletionsProperty() {
        return visibleCompletions;
    }

    public final ObjectProperty<EventHandler<CompletionEvent>> onCompletionProperty() {
        return onCompletion;
    }

    public final void setOnCompletion(EventHandler<CompletionEvent> value) {
        onCompletionProperty().set(value);
    }

    public final EventHandler<CompletionEvent> getOnCompletion() {
        return onCompletionProperty().get();
    }
    private ObjectProperty<EventHandler<CompletionEvent>> onCompletion = new ObjectPropertyBase<EventHandler<CompletionEvent>>() {
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        protected void invalidated() {
            setEventHandler(CompletionEvent.COMPLETION, (EventHandler<CompletionEvent>) (Object) get());
        }

        @Override
        public Object getBean() {
            return CompletionPopup.this;
        }

        @Override
        public String getName() {
            return "onSuggestion"; //$NON-NLS-1$
        }
    };

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CompletionPopupSkin(this);
    }

}
