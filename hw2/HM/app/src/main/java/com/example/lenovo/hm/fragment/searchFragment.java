package com.example.lenovo.hm.fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.hm.R;
import com.example.lenovo.hm.meizi.MeiziAdapter;
import com.example.lenovo.hm.meizi.MeiziFactory;
import com.example.lenovo.hm.meizi.user;

import java.util.List;

public class searchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.search_layout, container,false);
        List<user> meizis = MeiziFactory.createMeizis(10);
        Context context = this.getActivity();
        MeiziAdapter adapter = new MeiziAdapter(context,meizis);

        //MeiziAdapter adapter = new MeiziAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        /*RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );*/
        RecyclerView recyclerView =  v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }
}
