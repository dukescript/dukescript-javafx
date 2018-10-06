package javafx.webui;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

final class ConstantValue<T> implements ObservableValue<T> {
    private final T value;

    ConstantValue(T value) {
        this.value = value;
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("addListener");
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("removeListener");
    }

}
