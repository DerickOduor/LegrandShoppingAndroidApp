package com.derickoduor.legrand.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.derickoduor.legrand.Model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Derick Oduor on 4/8/2018.
 */

public class GrandDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="HotSauceCart";
    private static final String CART_DETAILS="CartDetails";
    private static final String CART_ID="id";
    private static final String PRODUCT_ID="product_id";
    private static final String PRODUCT_NAME="product_name";
    private static final String PRODUCT_PRICE="price";
    private static final String PRODUCT_DISCOUNT="discount";
    private static final String PRODUCT_QTY="qty";
    private static final String CUSTOMER_ID="customer_id";

    Context context;

    public GrandDatabase(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table_OrderDetails="CREATE TABLE "+CART_DETAILS+"("+
                CART_ID+" INTEGER PRIMARY KEY,"+PRODUCT_ID+" INTEGER,"+PRODUCT_NAME+" TEXT,"+
                PRODUCT_PRICE+" DOUBLE,"+PRODUCT_DISCOUNT+" DOUBLE,"+PRODUCT_QTY+" INTEGER,"+
                CUSTOMER_ID+" TEXT"+")";
        db.execSQL(Create_Table_OrderDetails);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CART_DETAILS);

        onCreate(db);
    }

    public List<Product> getCart(){
        SQLiteDatabase db=this.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        String[] sqlSelect={PRODUCT_ID,PRODUCT_QTY,PRODUCT_NAME,PRODUCT_PRICE,PRODUCT_DISCOUNT};

        queryBuilder.setTables(CART_DETAILS);
        Cursor c=queryBuilder.query(db,sqlSelect,null,null,null,null,null);

        final List<Product> result=new ArrayList<>();
        if(c.moveToFirst()){
            do{
                /*result.add(new Order(c.getInt(c.getColumnIndex(PRODUCT_ID)),
                        c.getInt(c.getColumnIndex(PRODUCT_QTY)),
                        c.getDouble(c.getColumnIndex(PRODUCT_PRICE)),
                        c.getDouble(c.getColumnIndex(PRODUCT_DISCOUNT)),
                        c.getString(c.getColumnIndex(PRODUCT_NAME)))
                );*/
            }while (c.moveToNext());
        }

        return result;
    }

    public List<Product> getCartLogged(int login_id){
        SQLiteDatabase db=this.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        String[] sqlSelect={PRODUCT_ID,PRODUCT_QTY,PRODUCT_NAME,PRODUCT_PRICE,PRODUCT_DISCOUNT};

        String query="SELECT * FROM "+CART_DETAILS+" WHERE "+CUSTOMER_ID+"="+Integer.toString(login_id);
        Cursor cursor = db.rawQuery(query, null);

        /*queryBuilder.setTables(CART_DETAILS);
        Cursor c=queryBuilder.query(db,sqlSelect,null,null,null,null,null);*/

        final List<Product> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(new Product(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(PRODUCT_PRICE)))
                );
            }while (cursor.moveToNext());
        }

        return result;
    }

    public void addCart(Product product){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(PRODUCT_ID,product.getId());
        values.put(PRODUCT_NAME,product.getName());
        values.put(PRODUCT_PRICE,product.getPrice());
        values.put(PRODUCT_QTY,product.getQuantity());
        //values.put(CUSTOMER_ID,login_id);

        db.insert(CART_DETAILS,null,values);
        db.close();
        Toast.makeText(context,"Added to cart: "+product.getName(),Toast.LENGTH_LONG).show();
    }

    public void addCartLogged(Product order, int login_id){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(PRODUCT_ID,order.getId());
        values.put(PRODUCT_NAME,order.getName());
        values.put(PRODUCT_PRICE,order.getPrice());
        values.put(PRODUCT_QTY,order.getQuantity());
        values.put(CUSTOMER_ID,login_id);

        db.insert(CART_DETAILS,null,values);
        db.close();
        Toast.makeText(context,"Added to cart: "+order.getName(),Toast.LENGTH_LONG).show();
    }

    public void cleanCart(){
        SQLiteDatabase db=this.getReadableDatabase();

        db.execSQL("DELETE FROM "+CART_DETAILS);
        Toast.makeText(context,"Cart cleared!",Toast.LENGTH_LONG).show();
    }
}
