package com.zxlee.timeaxisview.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.zxlee.timeaxisview.OnItemInfoClickListener;
import com.zxlee.timeaxisview.RecordEntity;
import com.zxlee.timeaxisview.SubSection;
import com.zxlee.timeaxisview.TimeAxisView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimeAxisView timeAxisView = (TimeAxisView) findViewById(R.id.timeAxisView);
        TimeAxisView timeAxisView2 = (TimeAxisView) findViewById(R.id.timeAxisView2);
        //TODO Test Datas
        timeAxisView.setData(getTestData());
        timeAxisView2.setData(getTestData2());

        timeAxisView.setOnItemInfoClickListener(new OnItemInfoClickListener() {
            @Override
            public void onItemInfoClick(String flyId) {
                showToast(flyId);
            }
        });

        timeAxisView2.setOnItemInfoClickListener(new OnItemInfoClickListener() {
            @Override
            public void onItemInfoClick(String flyId) {
                showToast(flyId);
            }
        });
    }

    private void showToast(String flyId) {
        Toast.makeText(this, flyId, Toast.LENGTH_SHORT).show();
    }

    private RecordEntity getTestData() {
        ArrayList<SubSection> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SubSection entity = new SubSection();
            entity.startTime = "08:09:09";
            entity.endTime = "09:39:59";
            entity.flyID = "flyId:" + i;
            entity.flyStatus = 1;
            list.add(entity);
        }
        RecordEntity testData = new RecordEntity();
        testData.mFlyList = list;
        testData.isOver = true;
        return testData;
    }

    private RecordEntity getTestData2() {
        ArrayList<SubSection> list = new ArrayList<>();

        SubSection entity = new SubSection();
        entity.startTime = "18:19:29";
        entity.endTime = "19:09:50";
        entity.flyID = "flyId:0";
        entity.flyStatus = 1;
        list.add(entity);

        SubSection entity1 = new SubSection();
        entity1.startTime = "18:19:29";
        entity1.endTime = "19:09:50";
        entity1.flyID = "flyId:1";
        list.add(entity1);

        RecordEntity testData = new RecordEntity();
        testData.mFlyList = list;
        testData.isOver = false;
        return testData;
    }
}
