package com.example.foodorderingclint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodorderingclint.Model.Cart;
import com.example.foodorderingclint.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CustomerOrderedProducts extends AppCompatActivity {

    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;
    private String userID="",user="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_ordered_products);

        userID=getIntent().getStringExtra("Oid");
        assert userID != null;
        user=userID.substring(10,15);

        productsList= findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        cartListRef= FirebaseDatabase.getInstance().getReference().child("Admin Orders").child(user);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options= new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef,Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.txtProductQuantity.setText("Quantity ="+model.getQuantity());
                //holder.txtProductPrice.setText("Price =  ₹"+model.getPrice());
                holder.txtProductName.setText(model.getPname());
                holder.remove.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);
                Picasso.get().load(model.getImage()).into(holder.cartProductImg);
                String str=model.getPrice();
                String numberOnly= str.replaceAll("[^0-9]", "");

                String str1=model.getDiscount();
                String discount= str1.replaceAll("[^0-9]", "");


                String str2=model.getQuantity();
                String quantity= str2.replaceAll("[^0-9-.]", "");


                double oneTypeProductTPrice= ((Double.parseDouble(numberOnly)) * (Double.parseDouble(quantity)));
                holder.txtProductPrice.setText("MRP =  ₹"+oneTypeProductTPrice);

                double oneTypeDiscount=(oneTypeProductTPrice-(oneTypeProductTPrice*(Double.parseDouble(discount)/100)));
                holder.txtProductDiscount.setText(String.valueOf("Offer Price = ₹"+oneTypeDiscount));
//                overTotalPrice=overTotalPrice+oneTypeDiscount;
//                txtTotalAmount.setText("Total Price = ₹"+String.valueOf(overTotalPrice));

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder= new CartViewHolder(view);
                return holder;
            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}