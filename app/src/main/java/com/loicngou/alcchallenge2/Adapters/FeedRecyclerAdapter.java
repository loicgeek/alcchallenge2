package com.loicngou.alcchallenge2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loicngou.alcchallenge2.Models.Product;
import com.loicngou.alcchallenge2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedRecyclerAdapter  extends RecyclerView.Adapter<FeedRecyclerAdapter.MyHolder> {

    ArrayList<Product> productArrayList;
    Context context;

    public FeedRecyclerAdapter(ArrayList<Product> productArrayList, Context context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public FeedRecyclerAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecyclerAdapter.MyHolder holder, int position) {
        Product product = this.productArrayList.get(position);
        holder.title.setText(product.getName());
        holder.price.setText(""+product.getPrice());
        holder.desc.setText(product.getDescription());
        Picasso.with(context)
                .load(product.getImageUrl()).fit()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        if(productArrayList !=null){

            return productArrayList.size();
        }else {
            return 0;
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView title,price,desc;
        ImageView productImage;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_card_title);
            price = itemView.findViewById(R.id.product_card_price);
            desc = itemView.findViewById(R.id.product_card_desc);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
