package com.example.foodorderingclint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorderingclint.Model.CustomerOrders;
import com.example.foodorderingclint.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomerOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private String user="",phone="";
    private String time="";
    private String currentTime="";
    private int endTime=0,startTime=0,difference=0,orderTime=0;
    private String saveCurrentTime="00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_orders);

        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders");


        ordersList=findViewById(R.id.customer_orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Calendar calForDate=Calendar.getInstance();



        FirebaseRecyclerOptions<CustomerOrders> options = new FirebaseRecyclerOptions.Builder<CustomerOrders>()
                .setQuery(ordersRef.orderByChild("phone").equalTo(Prevalent.currentOnlineUser.getPhone()),CustomerOrders.class)
                .build();

        FirebaseRecyclerAdapter<CustomerOrders, CustomerOrdersViewHolders> adapter= new FirebaseRecyclerAdapter<CustomerOrders, CustomerOrdersViewHolders>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerOrdersViewHolders holder, final int position, @NonNull final CustomerOrders model) {
                holder.userName.setText("Name: "+ model.getName());
                holder.userPhoneNumber.setText("Phone: "+ model.getPhone());
                holder.userTotalPrice.setText("Total Amount= ₹"+ model.getTotalAmount());
                holder.userDateTime.setText("Ordered at "+ model.getDate()+ " "+ model.getTime());
                holder.userShippingAddress.setText("Shipping Address: " + model.getAddress() + ", "+model.getArea()+", "+ model.getCity());
                holder.userOrderStatus.setText("Status :"+model.getState());

                holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uID= getRef(holder.getAdapterPosition()).getKey();
                        Intent intent = new Intent (CustomerOrdersActivity.this, CustomerOrderedProducts.class);
                        intent.putExtra("Oid",uID );
                        startActivity(intent);


                    }
                });

                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string1 = model.getTime().substring(0,2);
                        //String number = string1.substring(0,2);
                        startTime =Integer.valueOf(string1);



                        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");
                        saveCurrentTime=simpleDateFormat.format(calForDate.getTime());
                        currentTime=saveCurrentTime.substring(0,2);
                        difference= Integer.valueOf(currentTime)-startTime;
                        if(difference<0){
                            try {
                                orderTime = ((24 - startTime) + (Integer.valueOf(currentTime) - 0));
                            }catch (Exception e){
                                Toast.makeText(CustomerOrdersActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            orderTime=difference;
                        }
                        if(orderTime<=2) {
                            String uID = getRef(holder.getAdapterPosition()).getKey();
                            RemoveOrder(uID);
                            String OID = uID.substring(10, 15);
                            DatabaseReference AdminOrderRef = FirebaseDatabase.getInstance().getReference().child("Admin Orders").child(OID);
                            AdminOrderRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(CustomerOrdersActivity.this, "Order has been cancelled", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CustomerOrdersActivity.this,CustomerOrdersActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(CustomerOrdersActivity.this, "Sorry...! Order cannot be cancelled", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            @NonNull
            @Override
            public CustomerOrdersViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                return new CustomerOrdersViewHolders(view);
            }
        };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class CustomerOrdersViewHolders extends RecyclerView.ViewHolder{

        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress,userOrderStatus;
        public Button ShowOrdersBtn, cancel;

        public CustomerOrdersViewHolders(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.order_user_name);
            userPhoneNumber=itemView.findViewById(R.id.order_Phone_number);
            userTotalPrice=itemView.findViewById(R.id.order_total_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            ShowOrdersBtn=itemView.findViewById(R.id.show_All_products_btn);
            userOrderStatus=itemView.findViewById(R.id.order_status);
            cancel=itemView.findViewById(R.id.cancle);
        }
    }

    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }
}