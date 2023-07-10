package sg.edu.rp.c326.id22015010.registrationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private EditText inputName;
    private EditText inputMobile;
    private EditText inputGroupSize;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private RadioButton radioSmoking;
    private RadioButton radioNonSmoking;
    private EditText amount;
    private EditText numPax;
    private EditText Discount;
    private CheckBox Svs;
    private CheckBox Gst;
    private TextView totalBill;
    private TextView eachPays;
    private Button split;
    private RadioGroup rgMode;
    private Button submit;
    private Button reset;
    private TextView textReservationInfo;

    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private int selectedHour;
    private int selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputName = findViewById(R.id.editInputName);
        inputMobile = findViewById(R.id.editInputMobile);
        inputGroupSize = findViewById(R.id.numPax);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        radioSmoking = findViewById(R.id.radioSmoking);
        radioNonSmoking = findViewById(R.id.radioNonSmoking);
        Discount = findViewById(R.id.editInputDiscount);
        Svs = findViewById(R.id.cbSvs);
        Gst = findViewById(R.id.cbGst);
        split = findViewById(R.id.split);
        rgMode = findViewById(R.id.rgMode);
        submit = findViewById(R.id.submit);
        reset = findViewById(R.id.reset);
        textReservationInfo = findViewById(R.id.textReservationInfo);
        totalBill = findViewById(R.id.totalBill);
        amount = findViewById(R.id.editInputAmount);
        eachPays = findViewById(R.id.eachPays);
        numPax = findViewById(R.id.numPax);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReservation();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetReservation();
            }
        });

        Calendar calendarView = Calendar.getInstance();
        selectedYear = calendarView.get(Calendar.YEAR);
        selectedMonth = calendarView.get(Calendar.MONTH);
        selectedDay = calendarView.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendarView.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendarView.get(Calendar.MINUTE);
        datePicker.init(selectedYear, selectedMonth, selectedDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = monthOfYear;
                selectedDay = dayOfMonth;
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
            }
        });
        // Set default to 1 Jan 2023
        calendarView.set(2023, Calendar.JANUARY, 1, 18, 30, 0);
        datePicker.init(calendarView.get(Calendar.YEAR), calendarView.get(Calendar.JANUARY),
                calendarView.get(Calendar.DAY_OF_MONTH), null);
        // Set default time to 6:30 PM
        timePicker.setCurrentHour(18);
        timePicker.setCurrentMinute(30);

        // Perform calculations and generate reservation details
        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount.getText().toString().trim().length() != 0 && numPax.getText().toString().trim().length() != 0) {
                    double origAmt = Double.parseDouble(amount.getText().toString());
                    double newAmt = origAmt;

                    if (Gst.isChecked()) {
                        newAmt += origAmt * 0.07; // Add 7% GST (corrected from 8%)
                    }
                    if (Svs.isChecked()) {
                        newAmt += origAmt * 0.1; // Add 10% service charge
                    }
                    if (Svs.isChecked() && Gst.isChecked()) {
                        newAmt += origAmt * 0.17; // Add 17% (10% service charge + 7% GST)
                    }
                    // Apply discount
                    if (Discount.getText().toString().trim().length() != 0) {
                        newAmt *= 1 - Double.parseDouble(Discount.getText().toString()) / 100;
                    }

                    String mode = " in cash";
                    if (rgMode.getCheckedRadioButtonId() == R.id.radioPayNow) {
                        mode = " via PayNow to 912345678";
                    }

                    totalBill.setText("Total Bill: $" + String.format("%.2f", newAmt));

                    int numPerson = Integer.parseInt(numPax.getText().toString());
                    if (numPerson != 1) {
                        eachPays.setText("Each Pays: $" + String.format("%.2f", newAmt / numPerson) + mode);
                    } else {
                        eachPays.setText("Each Pays: $" + String.format("%.2f", newAmt) + mode);
                    }
                }
            }
        });
    }

    private void confirmReservation() {
        // Get the input values
        String name = inputName.getText().toString();
        String mobile = inputMobile.getText().toString();
        String groupSize = inputGroupSize.getText().toString();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is 0-based, so add 1
        int year = datePicker.getYear();
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        boolean isSmokingArea = radioSmoking.isChecked();
        boolean hasSvs = Svs.isChecked();
        boolean hasGst = Gst.isChecked();
        String discount = Discount.getText().toString();
        String paymentMode = getPaymentMode();

        // Display the reservation details
        String reservationDetails = "Details below:\n\n" +
                "Name: " + name + "\n" +
                "Mobile: " + mobile + "\n" +
                "Group Size: " + groupSize + "\n" +
                "Booking Date: " + day + "/" + month + "/" + year + "\n" +
                "Booking Time: " + hour + ":" + String.format("%02d", minute) + "\n" +
                "Smoking Area: " + (isSmokingArea ? "Yes" : "No") + "\n" +
                "Service Charge (SVS): " + (hasSvs ? "Yes" : "No") + "\n" +
                "Goods and Services Tax (GST): " + (hasGst ? "Yes" : "No") + "\n" +
                "Discount: " + discount + "\n" +
                "Payment Mode: " + paymentMode + "\n";

        textReservationInfo.setText(reservationDetails);
        Toast.makeText(this, "Confirmed reservation", Toast.LENGTH_SHORT).show();
    }

    private String getPaymentMode() {
        int selectedId = rgMode.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (radioButton != null) {
            return radioButton.getText().toString();
        }
        return "";
    }

    private void resetReservation() {
        // Reset all input fields
        inputName.setText("");
        inputMobile.setText("");
        inputGroupSize.setText("");

        // Reset date to default (1 Jan 2023)
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JANUARY, 1);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Reset time to default (6:30 PM)
        timePicker.setCurrentHour(18);
        timePicker.setCurrentMinute(30);

        radioSmoking.setChecked(false);
        radioNonSmoking.setChecked(false);
        Discount.setText("");
        Svs.setChecked(false);
        Gst.setChecked(false);
        rgMode.clearCheck();
        textReservationInfo.setText("");
    }
}