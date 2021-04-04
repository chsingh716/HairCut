package uk.ac.tees.S0321466;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import java.util.Calendar;

public class registerUser extends AppCompatActivity {

    //global variables
    ImageView profileImg;
    EditText et_firstName, et_lastName, et_email,et_number;
    TextView tv_dateOfBirth,tv_location;
    //buttons global
    Button btn_Register;
    TextView tv_userLogin,tv_adminLogin;
    private String imagePath="";
    private String dfirstName,dlastName,demail,dlocation,ddob,dnumber; //string global variables
    private CountryCodePicker ccp;
    //calendar and date of birth picker
    private DatePickerDialog dpd;
    Calendar cl;

    public static final int MAP_CODE_REQUEST=1111;
    public static final String ADDRESS_USER = "_address";
    private static final int REQUEST_LOCATION=2222;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
          //get instance  of calendar
          cl=Calendar.getInstance();
        //get ids of view components
        profileImg=findViewById(R.id.userImage);
        et_firstName= findViewById(R.id.et_first_name);
        et_lastName= findViewById(R.id.et_last_name);
        et_email= findViewById(R.id.et_email);
        et_number=findViewById(R.id.et_phone);
        tv_dateOfBirth=findViewById(R.id.tv_dob);
        tv_location=findViewById(R.id.tv_location);
        ccp=findViewById(R.id.cpp_countryCode);
        btn_Register=findViewById(R.id.btn_register);

        //register button event listener
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to get view components input data
                getInputFieldsDate();

            }
        });


     //date of birth event listener handler
        tv_dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //
                int day = cl.get(Calendar.DAY_OF_MONTH);
                int month = cl.get(Calendar.MONTH);
                int year = cl.get(Calendar.YEAR);

                dpd = new DatePickerDialog(registerUser.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                        tv_dateOfBirth.setText(day1 + "/" + (month1 + 1)+ "/" + year1);
                    }
                }, year, month, day);
                //datePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");

                dpd.show();
            }
        });

        //Location listener  ///event
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermissions();
            }
        });

    }



    //get input data function
    private void getInputFieldsDate(){
        dfirstName= et_firstName.getText().toString().trim();
        dlastName= et_lastName.getText().toString().trim();
        demail= et_email.getText().toString().trim();
        dnumber= et_number.getText().toString().trim();
        ddob= tv_dateOfBirth.getText().toString().trim();
        dlocation= tv_location.getText().toString().trim();

//        //date validation check
//        if (imagePath.isEmpty()) {
//
//            return;
//        }

        if (dfirstName.isEmpty()) {
            et_firstName.setError("first name is empty");
            return;
        }
        if (dlastName.isEmpty()) {
            et_lastName.setError("last name is empty");
            return;
        }
        if (demail.isEmpty()) {
            et_email.setError("enter vaild email");
            return;
        }
        if (ddob.isEmpty()) {
            tv_dateOfBirth.setError("enter vaild date of birth");
            return;
        }
        if (dnumber.isEmpty()) {
            et_number.setError("enter phone number");
            return;
        }

      dnumber=ccp.getSelectedCountryCodeWithPlus() + dnumber;
        user_model user=new user_model("",dfirstName,dlastName,imagePath,dnumber,demail,dlocation,ddob);

    }

    //get data from previous loaded activity(map activity)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == MAP_CODE_REQUEST) //check error code
            {
            String address = data.getStringExtra(ADDRESS_USER);  //ADDRESS_USER is key to get data from Intent(Map)
            tv_location.setText(address);
        }
    }




    //check location related permissions
    private void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permissions");
                builder.setMessage("Please grant the location permissions");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();
            } else {
                gpsEnable();
            }
        }
    }

    private void gpsEnable() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            AlertMessageNoGps();
        } else {
            Intent intent = new Intent(registerUser.this, map.class);
            startActivityForResult(intent, MAP_CODE_REQUEST);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gpsEnable();
                }
            }
        }
    }

    private void AlertMessageNoGps() {
        final AlertDialog.Builder _builder = new AlertDialog.Builder(this);
        _builder.setMessage("Your GPS is disabled. Do you want to enable?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = _builder.create();
        alert.show();
    }


}