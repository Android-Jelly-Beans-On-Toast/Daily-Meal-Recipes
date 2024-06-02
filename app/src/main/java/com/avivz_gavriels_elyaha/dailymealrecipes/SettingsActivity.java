package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private final String[] recentHistoryOptions = {"5", "10", "15", "All"};
    private final String[] recipeCategories = {"Breakfast", "Lunch", "Dinner", "Dessert"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SettingsActivity that = this;
        // recent recipe history counter
        AutoCompleteTextView recentHistoryAdapter = findViewById(R.id.recentHistoryDropDown);
        recentHistoryAdapter.setText(this.recentHistoryOptions[0]);
        ArrayAdapter<String> adapterHistory = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, recentHistoryOptions);
        recentHistoryAdapter.setAdapter(adapterHistory);
        recentHistoryAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(SettingsActivity.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // recipe categories
        AutoCompleteTextView categoryAdapter = findViewById(R.id.categoryDropDown);
        categoryAdapter.setText(this.recipeCategories[0]);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, recipeCategories);
        categoryAdapter.setAdapter(adapterCategory);
        categoryAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(SettingsActivity.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        // enable notification switch
        Switch notificationSwitch = findViewById(R.id.enableNotificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                Toast.makeText(SettingsActivity.this, "Notification state: " + state, Toast.LENGTH_SHORT).show();
            }
        });
        // enable kosher switch
        Switch kosherSwitch = findViewById(R.id.kosherSwitch);
        kosherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                Toast.makeText(SettingsActivity.this, "Notification state: " + state, Toast.LENGTH_SHORT).show();
            }
        });
        // enable quick switch
        Switch quickSwitch = findViewById(R.id.quickSwitch);
        quickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                Toast.makeText(SettingsActivity.this, "Notification state: " + state, Toast.LENGTH_SHORT).show();
            }
        });
        // enable low calories switch
        Switch lowCaloriesSwitch = findViewById(R.id.lowCaloriesSwitch);
        lowCaloriesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = buttonView.isChecked();
                Toast.makeText(SettingsActivity.this, "Notification state: " + state, Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton timePicker = findViewById(R.id.timePicker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("at onClick", "my log");
                TimePickerDialog clockPicker = new TimePickerDialog(that, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Toast.makeText(that, hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
                    }
                }, 0, 0, true);
                clockPicker.show();
            }
        });
    }
}

