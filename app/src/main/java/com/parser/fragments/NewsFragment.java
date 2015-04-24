package com.parser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parser.DataSource;
import com.parser.processors.NewsFeedProcessor;
import com.parser.processors.Processor;

public class NewsFragment extends BaseDataFragment {

    public static NewsFragment getNewFragment(Bundle params){
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        loadData();
        return view;
    }

    private void loadData(){
        String url = "http://s13.ru/archives/date/2015";
        Processor processor = new NewsFeedProcessor();
        DataSource dataSource = new DataSource(processor, new DataSource.Callbacks() {
            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onLoadDone() {

            }
        });
        dataSource.fillData(url);
    }
}
