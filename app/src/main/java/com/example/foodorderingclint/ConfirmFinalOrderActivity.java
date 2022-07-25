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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorderingclint.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, EmailEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private Spinner spinner;
    private String totalAmount = "", totalDiscount = "";
    private float Price = 0;
    private ProgressDialog loadingBar;
    private String item = "";
    private TextView totalPrice, TaxPrice, discountPrice, savedAmount;
    Random random = new Random();
    final long randomNumber = random.nextInt((99999 - 10000) + 1) + 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.order_name);
        phoneEditText = (EditText) findViewById(R.id.order_phone_number);
        EmailEditText = (EditText) findViewById(R.id.order_email);
        addressEditText =findViewById(R.id.order_address);
        cityEditText =findViewById(R.id.order_city1);
        loadingBar = new ProgressDialog(this);


        totalPrice = (TextView) findViewById(R.id.confirm_final_order_total_price);
        TaxPrice = (TextView) findViewById(R.id.confirm_final_order_delivery);
        discountPrice = (TextView) findViewById(R.id.confirm_final_order_discount_price);
        savedAmount = (TextView) findViewById(R.id.confirm_final_order_saved_amount);



        totalAmount = getIntent().getStringExtra("Total Price");
        totalDiscount = getIntent().getStringExtra("Discount Price");

        double roundOff = Math.round(Float.parseFloat(totalDiscount) * 100.0) / 100.0;

        discountPrice.setText("₹" + (roundOff));
        float totalFinalAmount = Float.parseFloat(totalDiscount);
        float x = Float.parseFloat(totalDiscount);
        if (x < 249) {
            TaxPrice.setText("₹25");
            Price = totalFinalAmount + 25;
            double roundOff3 = Math.round(Float.parseFloat(String.valueOf(Price)) * 100.0) / 100.0;
            totalPrice.setText("₹" + String.valueOf(roundOff3));

        } else {
            TaxPrice.setText("₹0");
            Price = totalFinalAmount;
            double roundOff3 = Math.round(Float.parseFloat(String.valueOf(Price)) * 100.0) / 100.0;
            totalPrice.setText("₹" + String.valueOf(roundOff3));

        }
        float AmountSaved = Float.parseFloat(totalAmount) - x;
        //float saved = AmountSaved-x;
        double roundOff1 = Math.round(AmountSaved * 100.0) / 100.0;
        savedAmount.setText("You have Saved ₹" + String.valueOf(roundOff1));

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.equals("Select your Table")) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please select your Table", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Placing Order");
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    Check();
                }
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Enter Your Full Name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Enter Your address", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Enter Your City", Toast.LENGTH_SHORT).show();
        }else{
            confirmOrderBtn.setEnabled(false);
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentTime,saveCurrentDate;
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());

        //orderRandomKey=saveCurrentDate+saveCurrentTime;

        final DatabaseReference adminRef= FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products");
        final DatabaseReference AdminOrderRef=FirebaseDatabase.getInstance().getReference().child("Admin Orders").child(String.valueOf(randomNumber));
        final DatabaseReference addressRef=FirebaseDatabase.getInstance().getReference().child("User");


        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone().concat(String.valueOf(randomNumber)));

        HashMap<String , Object> orderMap= new HashMap<>();
        orderMap.put("totalAmount",String.valueOf(Price));
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",Prevalent.currentOnlineUser.getPhone());
        orderMap.put("phoneOrder",phoneEditText.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("area",item);
        orderMap.put("city",cityEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("state","waiting for confirmation");
        orderMap.put("orderId",String.valueOf(randomNumber));
        orderMap.put("DeliveredDate","");
        orderMap.put("DeliveredTime","");

        final HashMap<String,Object> addressMap = new HashMap<>();
        addressMap.put("addressOrder",addressEditText.getText().toString());





        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    addressRef.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(addressMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child("Cart List")
                                        .child("User View")
                                        .child(Prevalent.currentOnlineUser.getPhone())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    Toast.makeText(ConfirmFinalOrderActivity.this, "Order Placed Successfully ", Toast.LENGTH_SHORT).show();

                                                    Intent intent= new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);



                                                    startActivity(intent);
                                                    confirmOrderBtn.setEnabled(true);
                                                }
                                            }
                                        });
                            }
                        }
                    });

                    moveRecord(adminRef,AdminOrderRef);


                }
            }
        });



    }

    private void addressInfoDisplay(final EditText nameEditText, final EditText phoneEditText, final EditText addressEditText) {
        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child("User").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.child("addressOrder").exists()) {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("addressOrder").getValue().toString();

                        nameEditText.setText(name);
                        phoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void moveRecord(final DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            fromPath.removeValue();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }
}