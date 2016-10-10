package com.yuncun.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.yuncun.swipeableweekview.WeekViewAdapter;
import com.yuncun.swipeableweekview.WeekViewSwipeable;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView introtxt = (TextView) findViewById(R.id.helloworld);
        final Context context = this;

        //Sample data
        List<Boolean> record = Arrays.asList(true, false, true, false, true, true);
        introtxt.setText("Dataset: \n " + Arrays.toString(record.toArray()));

        WeekViewSwipeable wvs = (WeekViewSwipeable) findViewById(R.id.calendar_component);
        WeekViewAdapter<Boolean> adapter = new WeekViewAdapter(record) {
            @Override
            public int getStrokeColor(final int index){
                if ((Boolean) get(index)==true){
                    return ContextCompat.getColor(context, R.color.teal);
                } else {
                    return ContextCompat.getColor(context, R.color.darkgray);
                }
            }

            @Override
            public int getFillColor(final int index){
                if ((Boolean) get(index)==true){
                    return ContextCompat.getColor(context, R.color.teal);
                } else {
                    return ContextCompat.getColor(context, R.color.darkgray);
                }
            }

            @Override
            public TextView getTextView(TextView tv, int index){
                if ((Boolean) get(index)==true){
                    tv.setText("Hit");
                } else {
                    tv.setText("Miss");
                }
                return tv;
            }

            @Override
            public View getDayLayout(View dv, final int index){
                dv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Click! on index " + index, Toast.LENGTH_LONG).show();
                    }
                });
                return dv;
            }
        };

        wvs.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
