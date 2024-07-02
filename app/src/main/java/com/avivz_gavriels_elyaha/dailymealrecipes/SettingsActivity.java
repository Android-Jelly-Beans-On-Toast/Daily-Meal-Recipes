package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private final String[] recentHistoryOptions = {"5", "10", "15", "All"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");

        SettingsActivity that = this;

        sp = getSharedPreferences("settings", MODE_PRIVATE);

        // recent recipe history counter
        AutoCompleteTextView recentHistoryAdapter = findViewById(R.id.recentHistoryDropDown);
        recentHistoryAdapter.setText(sp.getString("recentHistory", "5"));
        ArrayAdapter<String> adapterHistory = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, recentHistoryOptions);
        recentHistoryAdapter.setAdapter(adapterHistory);
        recentHistoryAdapter.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("recentHistory", item);
            editor.apply();
        });

        // daily notification time button
        Button timePicker = findViewById(R.id.timePicker);
        // set time picker text from shared preferences
        setTimePickerText(timePicker, sp.getInt("timerHour", 0), sp.getInt("timerMinute", 0));

        timePicker.setOnClickListener(v -> {
            Log.d("at onClick", "my log");
            TimePickerDialog clockPicker = new TimePickerDialog(that, (view, hourOfDay, minute) -> {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("timerHour", hourOfDay);
                editor.putInt("timerMinute", minute);
                editor.apply();
                setTimePickerText(timePicker, hourOfDay, minute);
            }, 0, 0, true);
            clockPicker.show();
        });

        // enable notification switch
        SwitchMaterial notificationSwitch = findViewById(R.id.enableNotificationSwitch);
        notificationSwitch.setChecked(sp.getBoolean("notification", false));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean state = buttonView.isChecked();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("notification", state);
            editor.apply();
        });

        // enable kosher switch
        SwitchMaterial kosherSwitch = findViewById(R.id.kosherSwitch);
        kosherSwitch.setChecked(sp.getBoolean("kosher", false));
        kosherSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean state = buttonView.isChecked();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("kosher", state);
            editor.apply();
        });

        // enable quick switch
        SwitchMaterial quickSwitch = findViewById(R.id.quickSwitch);
        quickSwitch.setChecked(sp.getBoolean("quick", false));
        quickSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean state = buttonView.isChecked();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("quick", state);
            editor.apply();
        });

        // enable low calories switch
        SwitchMaterial lowCaloriesSwitch = findViewById(R.id.lowCaloriesSwitch);
        lowCaloriesSwitch.setChecked(sp.getBoolean("lowCalories", false));
        lowCaloriesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean state = buttonView.isChecked();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("lowCalories", state);
            editor.apply();
        });
    }

    private void setTimePickerText(Button timePicker, int hourOfDay, int minute) {
        // pad with 0s if needed
        String paddedHour = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String paddedMinute = minute < 10 ? "0" + minute : "" + minute;
        String AMPM = hourOfDay < 12 ? "AM" : "PM";

        if (hourOfDay != 0 || minute != 0) {
            String time = getString(R.string.time_format, paddedHour, paddedMinute, AMPM);
            timePicker.setText(time);
        }
    }
}
