package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private final String[] recentHistoryOptions = {"5", "10", "15", "All"};
    private final String[] recipeCategories = {"Breakfast", "Lunch", "Dinner", "Dessert"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsActivity that = this;

        sp = getSharedPreferences("settings", MODE_PRIVATE);

        // recent recipe history counter
        AutoCompleteTextView recentHistoryAdapter = findViewById(R.id.recentHistoryDropDown);
        recentHistoryAdapter.setText(sp.getString("recentHistory", "5"));
        ArrayAdapter<String> adapterHistory = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, recentHistoryOptions);
        recentHistoryAdapter.setAdapter(adapterHistory);
        recentHistoryAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("recentHistory", item);
                editor.apply();
            }
        });

        // recipe categories
        AutoCompleteTextView categoryAdapter = findViewById(R.id.categoryDropDown);
        categoryAdapter.setText(sp.getString("category", "Breakfast"));
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, recipeCategories);
        categoryAdapter.setAdapter(adapterCategory);
        categoryAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("category", item);
                editor.apply();

            }
        });
        // enable notification switch
        Switch notificationSwitch = findViewById(R.id.enableNotificationSwitch);
        notificationSwitch.setChecked(sp.getBoolean("notification", false));
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("notification", state);
                editor.apply();
            }
        });
        // enable kosher switch
        Switch kosherSwitch = findViewById(R.id.kosherSwitch);
        kosherSwitch.setChecked(sp.getBoolean("kosher", true));
        kosherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("kosher", state);
                editor.apply();
            }
        });
        // enable quick switch
        Switch quickSwitch = findViewById(R.id.quickSwitch);
        quickSwitch.setChecked(sp.getBoolean("quick", false));
        quickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("quick", state);
                editor.apply();
            }
        });
        // enable low calories switch
        Switch lowCaloriesSwitch = findViewById(R.id.lowCaloriesSwitch);
        lowCaloriesSwitch.setChecked(sp.getBoolean("lowCalories", false));
        lowCaloriesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("lowCalories", state);
                editor.apply();
            }
        });
        Button timePicker = findViewById(R.id.timePicker);
        timePicker.setText(sp.getInt("timerHour", 0) + ":" + sp.getInt("timerMinute", 0));

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("at onClick", "my log");
                TimePickerDialog clockPicker = new TimePickerDialog(that, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SharedPreferences.Editor editor = sp.edit();


                        editor.putInt("timerHour", hourOfDay);
                        editor.putInt("timerMinute", minute);
                        editor.apply();

                        timePicker.setText(sp.getInt("timerHour", 0) + ":" + sp.getInt("timerMinute", 0));
                    }
                }, 0, 0, true);
                clockPicker.show();
            }
        });
    }
}

