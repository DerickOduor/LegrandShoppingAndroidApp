package com.derickoduor.legrand.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.derickoduor.legrand.Controller.CartAdapter;
import com.derickoduor.legrand.Model.GrandDatabase;
import com.derickoduor.legrand.Model.Product;
import com.derickoduor.legrand.R;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    RecyclerView cartRv;
    FloatingActionButton clear_cart;

    List<Product> cartList;
    CartAdapter adapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        loginPrefs=getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        editor=loginPrefs.edit();

        if(loginPrefs.getBoolean(PREFREMEMBER,true)){
            login_name=loginPrefs.getString(PREFUSERNAME,"abc");
            login_phone=loginPrefs.getInt(PREFPHONE,1);
            login_id=loginPrefs.getInt(PREFID,1);
        }

        cartRv=(RecyclerView)findViewById(R.id.cartList);

        if(loginPrefs.getBoolean(PREFREMEMBER,true)){
            login_id=loginPrefs.getInt(PREFID,1);
            cartList=new GrandDatabase(this).getCartLogged(login_id);
        }else{
            cartList=new GrandDatabase(this).getCart();
        }

        adapter=new CartAdapter(this,cartList);
        cartRv.setLayoutManager(new LinearLayoutManager(this));
        cartRv.hasFixedSize();
        cartRv.setAdapter(adapter);

        clear_cart=(FloatingActionButton)findViewById(R.id.clearCart);
        clear_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GrandDatabase(Cart.this).cleanCart();
                startActivity(new Intent(Cart.this,Home.class));
            }
        });
    }
}
