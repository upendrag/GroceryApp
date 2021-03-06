package com.grocery.app.tabswipe.utilities;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grocery.app.tabswipe.R;
import com.grocery.app.tabswipe.adapters.BuyAdapter;
import com.grocery.app.tabswipe.adapters.BuyDetailViewAdapter;
import com.grocery.app.tabswipe.adapters.PostAdapter;
import com.grocery.app.tabswipe.models.Post;
import com.grocery.app.tabswipe.models.RequestorDetails;
import com.grocery.app.tabswipe.models.DataModel;
import com.grocery.app.tabswipe.models.Requestor;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.apache.commons.lang.StringUtils;
import org.parceler.guava.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManipulationUtilities {
    public static HashMap<String, DataModel> myDataset = new HashMap<String, DataModel>();
    static HashMap<String, DataModel> myItems = new HashMap<String, DataModel>();
    static HashMap<String, RequestorDetails> requestorDetails = new HashMap<>();
    public static PostAdapter mPostAdapter;
    public static BuyAdapter mBuyAdapter;
    public static BuyDetailViewAdapter mBuyerDetailsAdapter;
    //item-id, buyerslist
    public static HashMap<String, RequestorDetails> buyerDetails = new HashMap<>();
    private static Gson gson = new Gson();


    public static void initializeBuyerItems(Context ctx) {
        R.raw r = new R.raw();
        Field[] fields = R.raw.class.getFields();
        for(Field f: fields){
            if(!f.getName().contains("buy") && !f.getName().contains("post")){
                f.setAccessible(true);
                try {
                    int id = (Integer) f.get(r);
                    String json = getStringJson(ctx.getResources().openRawResource(id));
                    RequestorDetails rd = gson.fromJson(json, RequestorDetails.class);
                    buyerDetails.put(rd.getItm_name(), rd);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public static void initializeData(Context ctx) {
        String json = getStringJson(ctx.getResources().openRawResource(R.raw.buy));
        List<DataModel> dm = gson.fromJson(json, DataModel.getListType());
        for(DataModel d: dm){
            myDataset.put(d.getItemName(), d);
        }
    }

    private static String getStringJson(InputStream io) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(io));
        try {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                out.append(line);
                out.append(" ");
            }
            bf.close();
            return out.toString();   //Prints the string content read from input stream
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<DataModel> getDataSet() {
        ArrayList<DataModel> objs = new ArrayList<DataModel>(myDataset.values());
        return objs;
    }

    public static ArrayList<DataModel> getMyItems() {
        return new ArrayList<>(myItems.values());
    }

    public static void addToMyDataSet(String itemName, DataModel d){
        if (myDataset.containsKey(itemName)) {
            int q = Integer.parseInt(myDataset.get(itemName).getQuantity()) + Integer.parseInt(d.getQuantity());
            myDataset.get(itemName).setQuantity(String.valueOf(q));
            mBuyAdapter.notifyDataSetChanged();
            addToMyItems(itemName, d);

        } else {
            DataModel data = new DataModel(itemName, d.getItm_desc(), d.getQuantity());
            myDataset.put(itemName,data );
            mBuyAdapter.add(data);
            addToMyItems(itemName, data);
            mBuyAdapter.notifyItemInserted(mBuyAdapter.getItemCount());
        }
    }

    public static void addToMyItems(String itemName, DataModel d) {
        if (myItems.containsKey(itemName)) {
            int q = Integer.parseInt(myItems.get(itemName).getQuantity()) + Integer.parseInt(d.getQuantity());
            myItems.get(itemName).setQuantity(String.valueOf(q));
            mPostAdapter.notifyDataSetChanged();

        } else {
            DataModel data = new DataModel(itemName, d.getItm_desc(),d.getQuantity());
            myItems.put(itemName, data);
            mPostAdapter.add(data);
            mPostAdapter.notifyItemInserted(mPostAdapter.getItemCount());

        }
    }

    public static void removeFromMyItems(String itemName) {
        int q = Integer.parseInt(myItems.get(itemName).getQuantity()) - 1;
        if (q == 0) {
            myItems.remove(itemName);
        } else {
            myItems.get(itemName).setQuantity(String.valueOf(q));
        }
        mBuyAdapter.remove(myItems.get(itemName));
        mPostAdapter.remove(myItems.get(itemName));
    }

    public static void deleteFromMyItems(String itemName, int pos, boolean deleteFlag) {
        int q = Integer.parseInt(myItems.get(itemName).getQuantity());
        int f = Integer.parseInt(myDataset.get(itemName).getQuantity());

        if (deleteFlag) {
            myDataset.get(itemName).setQuantity(String.valueOf(f - q));
            myItems.get(itemName).setQuantity("0");
            mPostAdapter.remove(myItems.get(itemName));
            mBuyAdapter.remove(myItems.get(itemName));
            myItems.remove(itemName);
        } else {
            myDataset.get(itemName).setQuantity(String.valueOf(f - 1));
            myItems.get(itemName).setQuantity(String.valueOf(q - 1));
            mPostAdapter.remove(myItems.get(itemName));
            mBuyAdapter.remove(myItems.get(itemName));
            if(Integer.parseInt(myItems.get(itemName).getQuantity()) < 1){
                myItems.remove(itemName);
            }
        }
        mBuyAdapter.notifyDataSetChanged();
    }

    public static RequestorDetails getBuyerDetailsForItemName(String itemName) {
        if(buyerDetails.containsKey(itemName))
        return buyerDetails.get(itemName);
        else return null;
    }

    public static boolean areAllItemsLocked(String itemName) {
        if(buyerDetails.containsKey(itemName))
        return buyerDetails.get(itemName).areAllItemsLocked();
        else return false;
    }
    public static boolean areAllItemsBought(String itemName) {
        if(buyerDetails.containsKey(itemName))
            return buyerDetails.get(itemName).areAllItemsBought();
        else return true;
    }
    public static void setAllItemsLocked(String itemName) {
        if(buyerDetails.containsKey(itemName))
            buyerDetails.get(itemName).areAllItemsLocked();
    }
    public static boolean setAllItemsBought(String itemName) {
        if(buyerDetails.containsKey(itemName))
            return buyerDetails.get(itemName).areAllItemsBought();
        else return true;
    }


}

