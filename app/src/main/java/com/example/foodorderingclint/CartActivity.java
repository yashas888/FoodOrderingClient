package com.example.foodorderingclint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorderingclint.Model.Cart;
import com.example.foodorderingclint.Prevalent.Prevalent;
import com.example.foodorderingclint.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtMsg1, txtMsg2, txtTotalAmount;
    private double overTotalPrice = 0, overallTotalDiscount = 0;
    private DatabaseReference productRef, RemoveListRef;
    private String ID = "";
    private String amt = "";
    private int itemcount = 0;
    private long count = 0;
    //char cart[];
    //private List<Cart> AdminOrderData;
    private String orderRandomKey;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    public static final List<String> cartList = new ArrayList<>();
    private double oneTypeDiscount = 0, oneTypeProductTPrice = 0;
    private String numberOnly = "", discount = "", quantity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("CART");

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg2 = (TextView) findViewById(R.id.msg2);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child("pid");


        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(amt));
                intent.putExtra("Discount Price", String.valueOf(overallTotalDiscount));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //CheckOrderState();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class).build();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").addValueEventListener(eventListener);


        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @NonNull
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder holder, int position, @NonNull final Cart model) {

                Picasso.get().load(model.getImage()).into(holder.cartProductImg);

                holder.txtProductName.setText(model.getPname());

                String str = model.getPrice();
                numberOnly = str.replaceAll("[^0-9]", "");

                String str1 = model.getDiscount();
                discount = str1.replaceAll("[^0-9]", "");

                String str2 = model.getQuantity();
                quantity = str2.replaceAll("[^0-9]", "");

                oneTypeProductTPrice = ((Double.parseDouble(numberOnly)) * (Double.parseDouble(quantity)));
                oneTypeDiscount = (oneTypeProductTPrice - (oneTypeProductTPrice * (Double.parseDouble(discount) / 100)));
                double roundOff1 = Math.round(oneTypeDiscount * 100.0) / 100.0;

                if (itemcount < count) {
                    overallTotalDiscount = overallTotalDiscount + oneTypeDiscount;
                    overTotalPrice = overTotalPrice + oneTypeProductTPrice;
//                    double roundOff1 = Math.round(overTotalPrice * 100.0) / 100.0;
                    amt = String.valueOf(overTotalPrice);
                    txtTotalAmount.setText("Total Price = ₹" + amt);

                }
                holder.txtProductPrice.setText("MRP =  ₹" + oneTypeProductTPrice);

                holder.txtProductDiscount.setText("Discount Price = ₹" + String.valueOf(roundOff1));

                holder.txtProductQuantity.setText("Quantity =" + model.getQuantity());


                itemcount++;
                // Toast.makeText(CartActivity.this, "," + String.valueOf(count), Toast.LENGTH_SHORT).show();

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ref.child("AdminOrders").child(Prevalent.currentOnlineUser.getPhone()).child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cartList.remove(model.getPid());
                                    cartListRef.child("Admin View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    cartListRef.child("User View")
                                                            .child(Prevalent.currentOnlineUser.getPhone())
                                                            .child("Products")
                                                            .child(model.getPid())
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(CartActivity.this, "Item Removed SuccessFully", Toast.LENGTH_SHORT).show();

                                                                        Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        });
                    }
                });


            }
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                NextProcessBtn.setVisibility(View.VISIBLE);
                txtMsg2.setVisibility(View.INVISIBLE);
                //Toast.makeText(CartActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();

                return holder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();


    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}