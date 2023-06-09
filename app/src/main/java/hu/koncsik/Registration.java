package hu.koncsik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import hu.koncsik.adapter.UserItem;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = Registration.class.getName();
    private static final String PREF_KEY = Registration.class.getPackage().toString();
    private static final int SECRET_KEY = 69;

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();


        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordAgainEditText);


        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userNameEditText.setText(userName);
        passwordEditText.setText(password);
        passwordConfirmEditText.setText(password);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("users");

        Log.i(LOG_TAG, "onCreate");
    }


    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "Not equals password");
            Toast.makeText(this, "Not equals password",  Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(LOG_TAG, "Registration:  " + userName + ", email: " + email);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {
                        Log.d(LOG_TAG, "User created successfully");
                        String emailLowerCase = email.toLowerCase();
                        Log.d(LOG_TAG, "Emial lower case: " + emailLowerCase);
                        mItems.add(new UserItem(userName, emailLowerCase, new Date()));
                        startCheat();
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        Log.d(LOG_TAG, "User wasn't created successfully:", task.getException());
                        Toast.makeText(Registration.this, "Password should be at least 6 characters!", Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Log.d(LOG_TAG, "User wasn't created successfully:", task.getException());
                        Toast.makeText(Registration.this, "Account already exist!", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(LOG_TAG, "User wasn't created successfully: ", task.getException());
                        Toast.makeText(Registration.this, "Unknown error", Toast.LENGTH_LONG).show();
                    }

                }
        });

    }

    public void cancel(View view) {
        finish();
    }

    private void startCheat() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}