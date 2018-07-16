package com.derickoduor.legrand.Views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.derickoduor.legrand.Interface.ItemClickListener;
import com.derickoduor.legrand.Model.Category;
import com.derickoduor.legrand.Model.Product;
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

public class Products extends AppCompatActivity {

    String tag;

    RecyclerView productsRv;
    Toolbar toolbar;
    TextView error;

    HttpURLConnection conn;
    URL url;

    GridLayoutManager glm;

    List<Product> productList;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Legrand Shopping");
        setSupportActionBar(toolbar);

        productList=new ArrayList<>();

        tag=getIntent().getStringExtra("tag");
        //Toast.makeText(getApplicationContext(),tag,Toast.LENGTH_LONG).show();

        productsRv=(RecyclerView)findViewById(R.id.productsRv);

        error=(TextView)findViewById(R.id.error);


        new ProductsTask().execute(tag);
    }

    public class ProductsTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://10.0.2.2/legrand/index.php/home/android_get_products/"+strings[0]);
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
            //error.setText(s);
            try{
                JSONObject response=new JSONObject(s);
                JSONArray products=response.getJSONArray("products");
                for (int i=0;i<products.length();i++){
                    JSONObject product=products.getJSONObject(i);
                    //productList.add(new Product(product.getInt("id"),product.getString("name"),product.getString("description"),product.getString("tag"),product.getString("gender"),"http://10.0.2.2/legrand/index.php/../images/"+product.getString("picture"),product.getDouble("price"),product.getInt("quantity")));
                    productList.add(new Product(product.getInt("id"),
                            product.getString("name"),
                            "http://10.0.2.2/legrand/index.php/../images/"+product.getString("picture"),
                            product.getDouble("price"))
                    );
                }
                productsRv.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                adapter=new ProductsAdapter(productList,getApplicationContext());
                productsRv.hasFixedSize();
                productsRv.setAdapter(adapter);
            }catch (JSONException e){
                error.setText(e.getMessage()+"\n "+s);
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }

    public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

        List<Product> list;
        Context context;

        public ProductsAdapter(List<Product> list, Context context) {
            this.list = list;
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            ItemClickListener itemClickListener;
            TextView product_name;
            ImageView product_image;
            TextView product_price;

            public MyViewHolder(View itemView) {
                super(itemView);
                product_name=(TextView)itemView.findViewById(R.id.product_name);
                product_image=(ImageView)itemView.findViewById(R.id.product_image);
                product_price=(TextView)itemView.findViewById(R.id.product_price);

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
            View view= LayoutInflater.from(context).inflate(R.layout.products_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Product p=list.get(position);

            holder.product_name.setText(p.getName());
            holder.product_price.setText("Kshs. "+Double.toString(p.getPrice()));
            Picasso.with(getApplicationContext()).load(p.getPicture()).into(holder.product_image);
            //Glide.with(getApplicationContext()).load(category.getPicture()).into(holder.cat_image);

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent i=new Intent(context,ProductView.class);
                    i.putExtra("id",Integer.toString(p.getId()));
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
