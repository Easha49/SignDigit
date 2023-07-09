package com.example.signdigitrecognition;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View ;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.Button ;
import android.widget.DatePicker ;
import android.widget.EditText ;
import android.widget.ProgressBar ;
import android.widget.RadioButton ;
import android.widget.RadioGroup ;
import android.widget.Spinner ;
import android.widget.Toast ;
import androidx.annotation.NonNull ;
import androidx.appcompat.app.AppCompatActivity ;
import com.google.android.gms.tasks.OnCompleteListener ;
import com.google.android.gms.tasks.Task ;
import com.google.firebase.auth.FirebaseAuth ;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ;
import com.google.firebase.auth.FirebaseAuthUserCollisionException ;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException ;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.database.DataSnapshot ;
import com.google.firebase.database.DatabaseError ;
import com.google.firebase.database.DatabaseReference ;
import com.google.firebase.database.FirebaseDatabase ;
import com.google.firebase.database.Query ;
import com.google.firebase.database.ValueEventListener ;
import com.google.firebase.firestore.DocumentReference ;
import com.google.firebase.firestore.FirebaseFirestore ;

import java.text.SimpleDateFormat;
import java.util.Calendar ;
import java.util.HashMap ;
import java.util.Locale ;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile, editTextRegisterPwd ,
            editTextRegisterConfirmPwd , editTextUsername ;
    private ProgressBar progressBar;
    Spinner stateSpinner , districtSpinner ;
    private String selectedDistrict , selectedDivision;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog datePickerDialog;
    private static final String TAG="RegisterActivity";
    FirebaseFirestore firebaseFirestore ;
    Boolean all_ok = false ;
    Float ratingApp = 0.00F ;
    ArrayAdapter<CharSequence> stateAdapter , districtAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar ().hide ();
        setContentView (R.layout.activity_sign_up);
        //getSupportActionBar().setTitle("Register");
        Toast.makeText (SignUp.this, "Register Now!", Toast.LENGTH_LONG).show ();
        progressBar = findViewById (R.id.progressbBar);
        editTextRegisterFullName = findViewById (R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById (R.id.editText_register_email);
        editTextRegisterDoB = findViewById (R.id.editText_register_dob);
        editTextRegisterMobile = findViewById (R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById (R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById (R.id.editText_register_confirm_password);
        editTextUsername = findViewById (R.id.editText_register_username);
        //dateFormatter = new SimpleDateFormat ("dd/MM/yyyy", Locale.getDefault());
        initDatePicker ();
        // SPINNER SECTION ----------------------------------------------:e --------------------------------

        //State Spinner Initialisation
        stateSpinner = findViewById(R.id.spinner_indian_states);    //Finds a view that was identified by the android:id attribute in xml

        //Populate ArrayAdapter using string array and a spinner layout that we will define
        stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_BD_states, R.layout.spinner_layout);

        // Specify the layout to use when the list of choices appear
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(stateAdapter);            //Set the adapter to the spinner to populate the State Spinner

        //When any item of the stateSpinner is selected
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Define City Spinner but we will populate the options through the selected state
                //districtSpinner = findViewById(R.id.spinner_BD_districts);
                selectedDivision = stateSpinner.getSelectedItem().toString();      //Obtain the selected State

                int parentID = parent.getId();
                if (parentID == R.id.spinner_indian_states){
                    switch (selectedDivision){
                        case "Select Your State": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_default_Districts, R.layout.spinner_layout);
                            break;
                        case "Dhaka": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Dhaka_districts, R.layout.spinner_layout);
                            break;
                        case "Chattogram": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Chattogram_districts, R.layout.spinner_layout);
                            break;
                        case "Khulna": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Khulna_districts, R.layout.spinner_layout);
                            break;
                        case "Mymensingh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Mymensingh_districts, R.layout.spinner_layout);
                            break;
                        case "Sylhet": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Sylhet_districts, R.layout.spinner_layout);
                            break;
                        case "Rajshahi": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Rajshahi_districts, R.layout.spinner_layout);
                            break;
                        case "Barisal": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_Barisal_districts, R.layout.spinner_layout);
                            break;
                        default:  break;
                    }
             /*       districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     // Specify the layout to use when the list of choices appears
                    districtSpinner.setAdapter(districtAdapter);        //Populate the list of Districts in respect of the State selected

                    //To obtain the selected District from the spinner
                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDistrict = districtSpinner.getSelectedItem().toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });*/
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // ############################################################################################################################


        editTextUsername.setOnFocusChangeListener ((view, hasFocus) -> {     // to check the username is new or old / if old then break else continue //
            if (!hasFocus) {
                checkUser ();
            }
        });

        //Radio button for gender
        //radioGroupRegisterGender = findViewById (R.id.radio_group_register_gender);
        //radioGroupRegisterGender.clearCheck ();

///////////////////////////////////////////////////////////////////////////
        Button buttonRegister = findViewById (R.id.button_register);
        buttonRegister.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
              //  int selectGenderId = radioGroupRegisterGender.getCheckedRadioButtonId ();
                //radioButtonRegisterGenderSelected = findViewById (selectGenderId);
                //obtain the entered data
                String userFullname = editTextRegisterFullName.getText ().toString ().trim ();
                String userEmail = editTextRegisterEmail.getText ().toString ().trim ();
                String userdateofBirth = editTextRegisterDoB.getText ().toString ();
                String userPhone = editTextRegisterMobile.getText ().toString ();
                String userPass = editTextRegisterPwd.getText ().toString ().trim ();
                String userConfirmPass = editTextRegisterConfirmPwd.getText ().toString ().trim ();
                String username = editTextUsername.getText ().toString ().trim ();
                String userGender;     //can'T get the value if gender is not selected
                // Checkeing whether any field is empty or not?
                if (TextUtils.isEmpty (userFullname)) {
                    Toast.makeText (SignUp.this, "Please Enter Your Full Name", Toast.LENGTH_LONG).show ();
                    editTextRegisterFullName.setError ("Full Name is Required");
                    editTextRegisterFullName.requestFocus ();
                } else if (TextUtils.isEmpty (userEmail)) {
                    Toast.makeText (SignUp.this, "Please Enter Your E-mail", Toast.LENGTH_LONG).show ();
                    editTextRegisterEmail.setError ("E-mail is Required");
                    editTextRegisterEmail.requestFocus ();
                } else if (!Patterns.EMAIL_ADDRESS.matcher (userEmail).matches ()) {
                    Toast.makeText (SignUp.this, "Please Re-enter Your E-mail", Toast.LENGTH_LONG).show ();
                    editTextRegisterEmail.setError (" Valid E-mail is Required");
                    editTextRegisterEmail.requestFocus ();
                } else if (TextUtils.isEmpty (userdateofBirth)) {
                    Toast.makeText (SignUp.this, "Please Enter Your Date of Birth", Toast.LENGTH_LONG).show ();
                    editTextRegisterDoB.setError ("Birth Date is Required");
                    editTextRegisterDoB.requestFocus ();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId () == -1) {
                    Toast.makeText (SignUp.this, "Please Select Your Gender", Toast.LENGTH_LONG).show ();
                    radioButtonRegisterGenderSelected.setError ("Gender is Required");
                    radioButtonRegisterGenderSelected.requestFocus ();
                } else if (TextUtils.isEmpty (userPhone)) {
                    Toast.makeText (SignUp.this, "Please Enter Your Phone Number", Toast.LENGTH_LONG).show ();
                    editTextRegisterMobile.setError ("Phone Number is Required");
                    editTextRegisterMobile.requestFocus ();
                } else if (userPhone.charAt (0) != '0' && userPass.charAt (1) != '1' && userPhone.length () != 11) {
                    Toast.makeText (SignUp.this, "Please Re-enter Your Phone Number", Toast.LENGTH_LONG).show ();
                    editTextRegisterMobile.setError ("Phone Number Should be 11 digits");
                    editTextRegisterMobile.requestFocus ();
                } else if (TextUtils.isEmpty (userPass)) {
                    Toast.makeText (SignUp.this, "Please Enter Your Password", Toast.LENGTH_LONG).show ();
                    editTextRegisterPwd.setError ("Password is Required");
                    editTextRegisterPwd.requestFocus ();
                } else if (userPass.length () < 6) {
                    Toast.makeText (SignUp.this, "Password Should be at least 6 digits", Toast.LENGTH_LONG).show ();
                    editTextRegisterPwd.setError ("Too weak password");
                    editTextRegisterPwd.requestFocus ();
                } else if (TextUtils.isEmpty (userConfirmPass)) {
                    Toast.makeText (SignUp.this, "Please Confirm Your Password", Toast.LENGTH_LONG).show ();
                    editTextRegisterConfirmPwd.setError ("Confirm Password is required");
                    editTextRegisterConfirmPwd.requestFocus ();
                } else if (!userPass.equals (userConfirmPass)) {
                    Toast.makeText (SignUp.this, "Password Doesn't Match", Toast.LENGTH_LONG).show ();
                    editTextRegisterConfirmPwd.setError ("Confirm Password is required");
                    editTextRegisterConfirmPwd.requestFocus ();
                    //clear the entered password
                    editTextRegisterPwd.clearComposingText ();
                    editTextRegisterConfirmPwd.clearComposingText ();
                } else if (username.length () < 6 || all_ok == true) {
                    Toast.makeText (SignUp.this, "Not a Valid Username ", Toast.LENGTH_LONG).show ();
                    editTextUsername.setError ("Username should be 6 digit long and the first letter should be alphabetic ");
                    editTextUsername.requestFocus ();
                } else {
                    userGender = radioButtonRegisterGenderSelected.getText ().toString ();
                    progressBar.setVisibility (View.VISIBLE);
                    registerUser (userFullname, userEmail, userdateofBirth, userGender, userPhone, userPass, username);
                }
            }
        });
    }
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                editTextRegisterDoB.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + "/" + day + "/" + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    //Users regisretion
    public void registerUser(String userFullname, String userEmail, String userdateofBirth, String userGender, String userPhone, String userPass , String username ) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(userEmail,userPass).addOnCompleteListener((task)-> {

            if(task.isSuccessful()) {
                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task1) {
                        if(task1.isSuccessful())  {
                            Toast.makeText(SignUp.this,"You have registered SuccessFully \n Please Verify your email ID ! ",Toast.LENGTH_SHORT).show();
                            // Storing Data in the Firestore Firebase
                            firebaseFirestore = FirebaseFirestore.getInstance() ;
                            String userid = firebaseAuth.getCurrentUser().getUid() ;
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userid) ;

                            Map<String,Object> user = new HashMap<>() ;
                            user.put("fname" , userFullname) ;
                            user.put("email",userEmail) ;
                            user.put("dateOfBirth",userdateofBirth) ;
                            user.put("gender",userGender) ;
                            user.put("phone",userPhone) ;
                            user.put("username",username) ;
                            user.put("division",selectedDivision) ;
                            user.put("district",selectedDistrict) ;
                            documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG,"onSuccess: user Profile is created for "+userid) ;
                                }
                            });
                            // Storing Data in the Realtime Database
                            ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(userFullname,userEmail,userdateofBirth,userGender,userPhone,username,selectedDivision,selectedDistrict,ratingApp) ;
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users") ;
                            referenceProfile.child(firebaseAuth.getUid()).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    Log.d(TAG,"onSuccess: user Profile is created for " + userid);
                                }
                            });
                            startActivity(new Intent(SignUp.this,Login.class));
                        }
                        else {
                            Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show() ;
                        }
                    }
                });
            }
            else{
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    editTextRegisterPwd.setError("Your Password is too weak; Use mixed letters and numbers");
                    editTextRegisterPwd.requestFocus();
                }  catch (FirebaseAuthInvalidCredentialsException e){
                    editTextRegisterPwd.setError("Invalid email or already in use");
                    editTextRegisterPwd.requestFocus();
                }  catch (FirebaseAuthUserCollisionException e){
                    editTextRegisterPwd.setError("User already registered");
                    editTextRegisterPwd.requestFocus();
                }  catch (Exception e){
                    Log.e(TAG,e.getMessage());
                    Toast.makeText(SignUp.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }


    // to check the username is already in the database .

    private void checkUser() {
        String shortName = editTextUsername.getText().toString();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users") ;
        if (!shortName.isEmpty()) {
            Query query = referenceProfile.orderByChild("username").equalTo(shortName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        editTextUsername.setError("Username already exist");
//                        signup_username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_username, 0, 0, 0);
                        all_ok = false;
                    } else {
                        editTextUsername.setError(null);
                        //editTextUsername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_username, 0, R.drawable.ic_ok, 0);
                        all_ok = true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
        else {
            editTextUsername.setError("Username cannot be empty");
        }
    }

}