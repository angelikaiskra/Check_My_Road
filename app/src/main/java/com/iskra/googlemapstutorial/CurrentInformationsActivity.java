package com.iskra.googlemapstutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;


public class CurrentInformationsActivity extends AppCompatActivity {

    ListView eventListView;
    TextView noDataText;
    LoadListView loadListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_informations);

        eventListView = (ListView) findViewById(R.id.list_view_info);
        noDataText = (TextView) findViewById(R.id.no_data);

        loadListView = new LoadListView(this, getLoaderManager(), null, eventListView, noDataText);
        loadListView.doWork();
    }


}
