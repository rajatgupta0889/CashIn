package com.mantralabsglobal.cashin.ui.fragment.tabs;

/**
 * Created by pk on 6/27/2015.
 */
public interface Bindable<T> {

    public void bindDataToForm(T value);

    public T getDataFromForm(T base);

}
