package com.derickoduor.legrand.Views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.derickoduor.legrand.Controller.CategoriesAdapter;
import com.derickoduor.legrand.Interface.ItemClickListener;
import com.derickoduor.legrand.Model.Category;
import com.derickoduor.legrand.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout barLayout;
    RecyclerView cat_rv;
    FloatingActionButton cart_btn;

    HttpURLConnection conn;
    URL url;

    GridLayoutManager glm;

    List<Category> categoryList;
    CategoriesAdapter1 adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //error=(TextView)findViewById(R.id.error);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("LeGrand Shopping");
        setSupportActionBar(toolbar);

        cat_rv=(RecyclerView)findViewById(R.id.categoriesRv);
        cart_btn=(FloatingActionButton)findViewById(R.id.cart_btn);

        categoryList=new ArrayList<>();

        new CategoriesTask().execute();

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Cart.class));
            }
        });
    }

    public class CategoriesTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://10.0.2.2/legrand/index.php/home/android_get_categories");
                conn=(HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(25000);

                int responseCode=conn.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    InputStream is=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    StringBuilder result=new StringBuilder();
                    String line=null;

                    while ((line=reader.readLine())!=null){
                        result.append(line);
                    }
                    return result.toString();
                }else{
                    return "unsuccessful";
                }
            }catch (Exception e){
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject response=new JSONObject(s);
                JSONArray categories=response.getJSONArray("categories");
                for (int i=0;i<categories.length();i++){
                    JSONObject category=categories.getJSONObject(i);
                    categoryList.add(new Category(category.getInt("id"),category.getString("name"),"http://10.0.2.2/legrand/index.php/../images/"+category.getString("picture")));
                }
                cat_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                adapter1=new CategoriesAdapter1(categoryList,getApplicationContext());
                cat_rv.hasFixedSize();
                cat_rv.setAdapter(adapter1);
            }catch (JSONException e){
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }

    public class CategoriesAdapter1 extends RecyclerView.Adapter<CategoriesAdapter1.MyViewHolder> {

        List<Category> categoryList;
        Context context;

        public CategoriesAdapter1(List<Category> categoryList, Context context) {
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
            Picasso.with(getApplicationContext()).load(category.getPicture()).into(holder.cat_image);
            //Glide.with(getApplicationContext()).load(category.getPicture()).into(holder.cat_image);

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                Intent i=new Intent(context,Products.class);
                i.putExtra("tag",category.getName());
                startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }
    }
}
