package com.example.foodorderingclint.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingclint.R;


public class ProductViewHolder extends RecyclerView.ViewHolder  {
    public TextView textProductName,textProductDescription,textProductPrice,textProductDiscount;
    public ImageView imageView;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=(ImageView) itemView.findViewById(R.id.product_image);
        textProductName=(TextView) itemView.findViewById(R.id.product_name);
        textProductDescription=(TextView) itemView.findViewById(R.id.product_description);
        textProductPrice=(TextView) itemView.findViewById(R.id.product_price);
        textProductDiscount=(TextView) itemView.findViewById(R.id.product_discount);

    }


}

