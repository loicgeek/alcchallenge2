package com.loicngou.alcchallenge2;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SIGNUP";
    private FirebaseAuth mAuth;
    private TextInputEditText email_text ;
    private TextInputEditText name_text ;
    private TextInputEditText pass_text ;
    private Button register_btn;
    private Button cancel_btn;
    private FirebaseFirestore db;
    private androidx.core.widget.ContentLoadingProgressBar progress_circular ;
    private TextView text_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Sign Up Page");
            toolbar.setElevation(0.2f);
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayHomeAsUpEnabled(true);
            this.bindData();
            this.progress_circular.setIndeterminate(true);
            this.progress_circular.setVisibility(View.GONE);
            text_error.setVisibility(View.GONE);

            this.mAuth = FirebaseAuth.getInstance();
            this.db= FirebaseFirestore.getInstance();

            this.cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            this.register_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = name_text.getText().toString();
                    String email = email_text.getText().toString();
                    String pass = pass_text.getText().toString();

                    if(TextUtils.isEmpty(name)  || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                        Toast.makeText(getApplicationContext(),"Veuillez Remplir tous les champs",Toast.LENGTH_SHORT).show();
                    } else {
                        SignUpActivity.this.signUp(email.trim(),name,pass.trim());
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void bindData(){
        this.email_text = findViewById(R.id.register_email_text);
        this.name_text = findViewById(R.id.register_name_text);
        this.pass_text = findViewById(R.id.register_password_text);
        this.register_btn = findViewById(R.id.register_btn);
        this.cancel_btn = findViewById(R.id.cancel_btn);
        this.progress_circular = findViewById(R.id.progress_circular);
        this.text_error = findViewById(R.id.text_error);
    }

    private void signUp(String email, String name ,String pass){
        Log.w(TAG,"email:"+email);
        Log.w(TAG,"pass:"+pass);
        this.progress_circular.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "signUpWithEmailAndPass:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progress_circular.setVisibility(View.GONE);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            text_error.setVisibility(View.VISIBLE);
                            Log.w(TAG, "signUpWithEmailAndPass:failure", task.getException());
                            Snackbar.make(findViewById(R.id.register_btn), "Registration Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            Map<String ,Object> data = new HashMap<>();
            data.put("name",this.name_text.getText().toString());
            data.put("email", Objects.requireNonNull(user.getEmail()));
            data.put("user_id",user.getUid());

            this.updateUserData(user,data);
            Toast.makeText(getApplicationContext(),"Already signed in "+user.getEmail()+" "+user.getUid(),Toast.LENGTH_SHORT).show();
            Log.i("details",user.getEmail());
        }else{
            Toast.makeText(getApplicationContext(),"not signed yet",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(FirebaseUser user , Map<String, Object> data){
        this.db.collection("users")
                .document(user.getUid()).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,e.getMessage());
                    }
                });
    }


}
