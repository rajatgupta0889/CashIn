package com.mantralabsglobal.cashin.fragment.tabs;

/**
 * Created by pk on 6/27/2015.
 */
public interface Bindable<T> {

    public void bindDataToForm(T value);

    public T getDataFromForm();

    public void setHasError(boolean hasError);

}
