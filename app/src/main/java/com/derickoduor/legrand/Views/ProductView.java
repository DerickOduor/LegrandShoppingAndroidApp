package com.derickoduor.legrand.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derickoduor.legrand.Controller.ImageSliderAdapter;
import com.derickoduor.legrand.Model.GrandDatabase;
import com.derickoduor.legrand.Interface.CartTaskCompleted;
import com.derickoduor.legrand.Interface.ProductTaskCompleted;
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

public class ProductView extends AppCompatActivity implements ProductTaskCompleted,CartTaskCompleted{

    String id;
    int qty;

    Toolbar toolbar;
    CollapsingToolbarLayout collapsing;
    AppBarLayout barLayout;
    //ImageView product_img;
    TextView product_name,product_price,product_description,quantity,qty_available;
    FloatingActionButton cart_btn;
    TextView error;
    ViewPager image_slider;
    ImageButton addBtn,removeBtn;

    HttpURLConnection conn;
    URL url;
    Context context;

    GridLayoutManager glm;

    ProductTaskCompleted productTaskCompleted;
    CartTaskCompleted cartTaskCompleted;
    ImageSliderAdapter sliderAdapter;

    SharedPreferences loginPrefs;
    SharedPreferences.Editor editor;

    public final String PREF_NAME="LoginPrefs";
    static final String PREFUSERNAME="LoginUser";
    static final String PREFPASSWORD="LoginPassword";
    static final String PREFID="LoginId";
    static final String PREFPHONE="LoginPhone";
    static final String PREFREMEMBER="LoginRemember";

    String login_name="",login_password="";
    int login_id=0,login_phone=0;
    List<Product> cart;
    String[] product_images={};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        id=getIntent().getStringExtra("id");

        loginPrefs=getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        editor=loginPrefs.edit();
        cart=new ArrayList<>();

        if(loginPrefs.getBoolean(PREFREMEMBER,true)){
            login_name=loginPrefs.getString(PREFUSERNAME,"abc");
            login_phone=loginPrefs.getInt(PREFPHONE,1);
            login_id=loginPrefs.getInt(PREFID,1);
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        collapsing=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        cart_btn=(FloatingActionButton)findViewById(R.id.cartBtn);
        //product_img=(ImageView)findViewById(R.id.product_img);
        product_name=(TextView)findViewById(R.id.product_name);
        product_price=(TextView)findViewById(R.id.price_product);
        product_description=(TextView)findViewById(R.id.product_description);
        addBtn=(ImageButton) findViewById(R.id.addBtn);
        removeBtn=(ImageButton) findViewById(R.id.removeBtn);
        quantity=(TextView)findViewById(R.id.qty);
        qty_available=(TextView)findViewById(R.id.qty_available);
        image_slider=(ViewPager)findViewById(R.id.imageSlider);

        /*sliderAdapter=new ImageSliderAdapter(this,product_images);
        image_slider.setAdapter(sliderAdapter);*/

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CartTask(ProductView.this).execute(id);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q=Integer.parseInt(quantity.getText().toString());
                int qty_a=Integer.parseInt(qty_available.getText().toString());
                if(q==qty_a||q>qty_a){
                    Toast.makeText(getApplicationContext(),"Quantity available is"+qty_a ,Toast.LENGTH_LONG).show();
                }else{
                    int n_q=q+1;
                    quantity.setText(Integer.toString(n_q));
                    //Snackbar.make(v,n_q,Snackbar.LENGTH_LONG).show();
                }
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q=Integer.parseInt(quantity.getText().toString());
                if(q==1||q<1){
                    Toast.makeText(getApplicationContext(),"Must be at least 1!",Toast.LENGTH_LONG).show();
                }else{
                    int n_q=q-1;
                    quantity.setText(Integer.toString(n_q));
                }
            }
        });
        new ProductTask(ProductView.this).execute(id);
    }

    @Override
    public void onProductTaskCompleted(int i,String[] product_images) {
        qty_available.setText(Integer.toString(i));
        sliderAdapter=new ImageSliderAdapter(this,product_images);
        image_slider.setAdapter(sliderAdapter);
    }

    @Override
    public void onCartTaskCompleted(Product product) {
        if(loginPrefs.getBoolean(PREFREMEMBER,true)){
            login_id=loginPrefs.getInt(PREFID,1);
            new GrandDatabase(this).addCartLogged(product,login_id);
        }else{
            new GrandDatabase(this).addCart(product);
        }
        Toast.makeText(getApplicationContext(),"Added to cart: "+product.getName(),Toast.LENGTH_LONG).show();
    }

    public class ProductTask extends AsyncTask<String,String,String> {

        Context c;

        public ProductTask(Context mContext){
            this.c=mContext;
            productTaskCompleted=(ProductTaskCompleted)c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://10.0.2.2/legrand/index.php/home/android_get_product/"+strings[0]);
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
                JSONArray product=response.getJSONArray("products");
                for (int i=0;i<product.length();i++){
                    JSONObject product_detail=product.getJSONObject(i);
                    collapsing.setTitle(product_detail.getString("name"));
                    product_name.setText(product_detail.getString("name"));
                    product_price.setText(Double.toString(product_detail.getDouble("price")));
                    product_description.setText(product_detail.getString("description"));
                    /*Picasso.with(getApplicationContext()).
                            load("http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture")).
                            into(product_img);*/
                    String product_images[]={"http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture"),
                            "http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_2"),
                            "http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_3"),
                            "http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_4"),
                            "http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_5")
                    };
                    /*product_images[0]="http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture");
                    product_images[1]="http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_2");
                    product_images[2]="http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_3");
                    product_images[3]="http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_4");
                    product_images[4]="http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture_5");*/
                    productTaskCompleted.onProductTaskCompleted(product_detail.getInt("quantity"),product_images);
                }
            }catch (JSONException e){
                error.setText(e.getMessage()+"\n "+s);
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public class CartTask extends AsyncTask<String,String,String> {

        Context c;

        public CartTask(Context mContext){
            this.c=mContext;
            cartTaskCompleted=(CartTaskCompleted)c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://10.0.2.2/legrand/index.php/home/android_get_product/"+strings[0]);
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
                JSONArray product=response.getJSONArray("products");
                Product p;
                for (int i=0;i<product.length();i++){
                    JSONObject product_detail=product.getJSONObject(i);
                    collapsing.setTitle(product_detail.getString("name"));
                    product_name.setText(product_detail.getString("name"));
                    product_price.setText(Double.toString(product_detail.getDouble("price")));
                    product_description.setText(product_detail.getString("description"));
                    p=new Product(product_detail.getInt("id"),
                            product_detail.getString("name"),
                            product_detail.getString("description"),
                            product_detail.getString("tag"),
                            product_detail.getString("gender"),
                            "http://10.0.2.2/legrand/index.php/../images/"+product_detail.getString("picture"),
                            product_detail.getDouble("price"),
                            product_detail.getInt("quantity")
                    );

                    cartTaskCompleted.onCartTaskCompleted(p);
                }
            }catch (JSONException e){
                error.setText(e.getMessage()+"\n "+s);
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
