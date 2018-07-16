package com.derickoduor.legrand.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.derickoduor.legrand.Interface.ItemClickListener;
import com.derickoduor.legrand.Model.Category;
import com.derickoduor.legrand.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Derick Oduor on 4/8/2018.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    List<Category> categoryList;
    Context context;

    public CategoriesAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemClickListener itemClickListener;
        TextView cat_name;
        ImageView cat_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            cat_name=(TextView)itemView.findViewById(R.id.cat_name);
            cat_image=(ImageView)itemView.findViewById(R.id.cat_image);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.category_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category category=categoryList.get(position);

        holder.cat_name.setText(category.getName());
        Picasso.with(context).load(category.getPicture()).into(holder.cat_image);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                /*Intent i=new Intent(context,Products.class);
                i.putExtra("tag",category.getName());
                startActivity(i);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
