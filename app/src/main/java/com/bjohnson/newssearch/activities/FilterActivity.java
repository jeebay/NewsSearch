package com.bjohnson.newssearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bjohnson.newssearch.R;
import com.bjohnson.newssearch.models.Filters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Calendar myCalendar = Calendar.getInstance();
    Filters filters;
    TextView tvBeforeDate;
    EditText etBeforeDate;
    Spinner spinnerViewSortOptions;
    CheckBox cbArts;
    CheckBox cbArtsLeisure;
    CheckBox cbAnyTime;
    Button btnUpdateFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filters = (Filters) getIntent().getSerializableExtra("filters");

        setupViews();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setEtBeforeDate();
    }

    public void setupViews() {
        spinnerViewSortOptions = (Spinner) findViewById(R.id.spinnerSortOrder);
        tvBeforeDate = (TextView) findViewById(R.id.tvBeforeDate);
        etBeforeDate = (EditText) findViewById(R.id.etBeforeDate);
        cbArts = (CheckBox) findViewById(R.id.checkbox_arts);
        cbArtsLeisure = (CheckBox) findViewById(R.id.checkbox_art_leisure);
        cbAnyTime = (CheckBox) findViewById(R.id.cbAnyTime);
        btnUpdateFilters = (Button) findViewById(R.id.btnUpdateFilters);

        // Try initialize the calendar date field based on the Intent
        if (!TextUtils.isEmpty(filters.getBeginDate())) {
            try {
                // try to parse the date from filters settings
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
                Date date = sdf.parse(filters.getBeginDate());
                myCalendar.setTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // if begin date is empty the set anytime checkbox to checked
            cbAnyTime.setChecked(true);
        }
        // Set the date edit text based on the calendar
        setEtBeforeDate();

        // Set the spinner to the Intent value
        setSpinnerToValue(spinnerViewSortOptions, filters.getSortOrder());

        // Set the news desk checks based on the values in the Intent
        setCheckboxValues(filters.getNewsDesk());

        // Set up listeners for everything
        etBeforeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(FilterActivity.this, FilterActivity.this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnUpdateFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beforeDate = "";
                String dateFormat = "yyyyMMdd";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

                if (!cbAnyTime.isChecked()) beforeDate = sdf.format(myCalendar.getTime());

                // Set all the datas
                filters.setSortOrder(spinnerViewSortOptions.getSelectedItem().toString());
                filters.setBeginDate(beforeDate);
                filters.setNewsDesk(getNewsDesks());

                // Add data to intent and finish
                Intent data = new Intent();
                data.putExtra("filters", filters);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private ArrayList<String> getNewsDesks() {
        ArrayList<String> newsDesks = new ArrayList<String>();

        if (cbArts.isChecked()) newsDesks.add("Arts");
        if (cbArtsLeisure.isChecked()) newsDesks.add("Arts & Leisure");

        return newsDesks;
    }

    private void setCheckboxValues(ArrayList<String> newsDesk) {
        for (String check : newsDesk) {
            switch (check) {
                case "Arts":
                    cbArts.setChecked(true);
                    break;
                case "Arts & Leisure":
                    cbArtsLeisure.setChecked(true);
                    break;
            }
        }
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    public void setEtBeforeDate() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        etBeforeDate.setText(sdf.format(myCalendar.getTime()));
    }
}
