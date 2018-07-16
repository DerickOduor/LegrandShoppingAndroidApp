package com.derickoduor.legrand.Controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.derickoduor.legrand.Model.Product;
import com.derickoduor.legrand.R;

import java.util.List;

/**
 * Created by Derick Oduor on 4/9/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    Context context;
    List<Product> cartList;

    public CartAdapter(Context context, List<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView cart_item_name,cart_item_price;

        public MyViewHolder(View itemView) {
            super(itemView);
            cart_item_name=(TextView)itemView.findViewById(R.id.cart_item_name);
            cart_item_price=(TextView)itemView.findViewById(R.id.cart_item_price);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.MyViewHolder holder, int position) {
        final Product product=cartList.get(position);
        holder.cart_item_price.setText(Double.toString(product.getPrice()));
        holder.cart_item_name.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
