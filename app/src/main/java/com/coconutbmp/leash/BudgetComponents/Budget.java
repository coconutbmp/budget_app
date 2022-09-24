package com.coconutbmp.leash.BudgetComponents;

import android.app.Activity;
import android.telecom.Call;

import com.coconutbmp.leash.CompletionListener;
import com.coconutbmp.leash.InternetRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Vector;

import javax.security.auth.callback.Callback;

public class Budget extends BudgetComponent{

    Vector<Income> income_list = new Vector<Income>();
    Vector<Liability> liability_list = new Vector<Liability>();
    Vector<Transaction> transaction_list = new Vector<Transaction>();
    InternetRequest ir = new InternetRequest();
    private CompletionListener listener = null;
    private Activity caller = null;

    void setIncomes(String response){
        if(response == null || response.equals("")) return;

        JSONArray ja;
        System.out.println("response = " + response);
        try {
            ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++){
                income_list.add(new Income((JSONObject) ja.get(i)));
            }
            System.out.println("got the array -> " + ja.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Vector<Income> getIncomes() { return income_list; };

    void setLiabilities(String response){
        JSONArray ja;
        //System.out.println(response);
        try {
            ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++){
                liability_list.add(new Liability(this, (JSONObject) ja.get(i)));
            }
            //System.out.println("got the array -> " + ja.toString());
            if(listener!=null){
                caller.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.processCompletion(true);
                        listener = null;
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            if(listener!=null){
                caller.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.processCompletion(false);
                        listener = null;
                    }
                });
            }
        }
    }

    public Vector<Liability> getLiabilities() { return liability_list; };

    void setTransactions(String response){
        if(response.equals("")){
            System.out.println("no response");
            return;
        }
        JSONArray ja;
        System.out.println(response + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

        try {
            ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++){
                transaction_list.add(new Transaction((JSONObject) ja.get(i)));
            }
            System.out.println("got the array -> " + ja.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Vector<Transaction> getTransactions() { return transaction_list; };

    public void refreshLiabilities(Activity caller, CompletionListener listener) throws Exception{
        this.listener = listener;
        this.caller = caller;
        JSONObject params = new JSONObject();
        params.put("budgetid", this.getJsonRep().get("budget_ID"));
        ir.doRequest(
                InternetRequest.std_url+"get_liabilities.php",
                null,
                params,
                this::setLiabilities
        );
    }

    @Override
    public void initialize() throws Exception{
        JSONObject params = new JSONObject();
        //System.out.println(this.getJsonRep().get("budget_ID") + " " + this.getJsonRep().get("budget_Name"));
        params.put("budgetid", this.getJsonRep().get("budget_ID"));
        ir = new InternetRequest();

        //get income
        params = new JSONObject();
        params.put("budgetid", this.getJsonRep().get("budget_ID"));
        ir.doRequest(
                InternetRequest.std_url+"get_income.php",
                null,
                params,
                this::setIncomes
        );
        //get liabilities
        params = new JSONObject();
        params.put("budgetid", this.getJsonRep().get("budget_ID"));
        ir.doRequest(
                InternetRequest.std_url+"get_liabilities.php",
                null,
                params,
                this::setLiabilities
        );
        //get transactions
        params = new JSONObject();
        params.put("budgetid", getJsonRep().get("budget_ID"));
        //todo: add parameters for transaction request
        ir.doRequest(
                InternetRequest.std_url+"get_all_transactions.php",
                null,
                params,
                this::setTransactions
        );
    }

    public Budget(JSONObject json_rep){
        super(json_rep);


    }



}
