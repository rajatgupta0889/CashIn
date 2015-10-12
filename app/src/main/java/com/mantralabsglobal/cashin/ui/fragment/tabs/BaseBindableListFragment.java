package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;

/**
 * Created by pk on 7/13/2015.
 */
public abstract class BaseBindableListFragment<T> extends BaseBindableFragment<List<T>>{

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> cloneThroughJson(List<T> tList) {
        Gson gson = new Gson();
        List<T> clonedList = new ArrayList<>();
        for(T t : tList )
        {
            String json = gson.toJson(t);
            T clone = (T) gson.fromJson(json, t.getClass());
            clonedList.add(clone);
        }
        return clonedList;
    }
}
