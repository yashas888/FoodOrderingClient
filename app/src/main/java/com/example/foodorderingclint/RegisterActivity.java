package com.example.foodorderingclint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword,InputEmail;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton=(Button)findViewById(R.id.register_btn);
        InputName=(EditText)findViewById(R.id.register_username_input);
        InputPhoneNumber=(EditText)findViewById(R.id.register_phone_number_input);
        InputPassword=(EditText)findViewById(R.id.register_password_input);
        InputEmail=(EditText)findViewById(R.id.register_email_input);
        loadingBar=new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //         CreateAccountButton.setEnabled(false);
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {


        if (!TextUtils.isEmpty(InputName.getText())) {
            if (!TextUtils.isEmpty(InputPhoneNumber.getText()) && InputPhoneNumber.getText().length() == 10) {
                if (!TextUtils.isEmpty(InputEmail.getText())) {
                    if (!TextUtils.isEmpty(InputPassword.getText()) && InputPassword.getText().length() >= 8) {

                        loadingBar.setTitle("Create Account");
                        loadingBar.setMessage("please wait, while we are checking the credentials");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        String name = InputName.getText().toString();
                        String phone = InputPhoneNumber.getText().toString();
                        String password = InputPassword.getText().toString();
                        String email = InputEmail.getText().toString();

                        ValidatePhoneNumber(name, phone, email, password);

                    } else {
                        InputPassword.setError("Please Write Your Password ");
                        Toast.makeText(this, "Please Write Your Password... ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    InputEmail.setError("Please Write Your email ");
                    Toast.makeText(this, "Please Write Your email... ", Toast.LENGTH_SHORT).show();
                }
            } else {
                InputPhoneNumber.setError("Please Write valid Ph.no");
                Toast.makeText(this, "Please Write valid Ph.no... ", Toast.LENGTH_SHORT).show();
            }
        } else {
            InputName.setError("Please Write Your Name");
            Toast.makeText(this, "Please Write Your Name... ", Toast.LENGTH_SHORT).show();
        }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String email,  final String password) {
        final DatabaseReference Rootref;
        Rootref= FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("User").child(phone).exists())
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("Password",password);
                    userdataMap.put("Name",name);
                    userdataMap.put("Email",email);

                    Rootref.child("User").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Congratulations, your account created sucessfully",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
//                                        Intent intent = new Intent (RegisterActivity.this, LoginActivity.class);
//                                        startActivity(intent);
                                    }else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Network Error please Try again after some time...",Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    CreateAccountButton.setEnabled(true);
                                }
                            });

                }else{
                    Toast.makeText(RegisterActivity.this,"This"+ phone +"number already exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please try again using another phone Number",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}