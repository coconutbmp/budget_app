package com.coconutbmp.leash.BudgetComponents;

import android.app.Activity;
import android.graphics.Color;
import android.telecom.Call;

import com.coconutbmp.leash.CompletionListener;
import com.coconutbmp.leash.Data;
import com.coconutbmp.leash.InternetRequest;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Vector;

import javax.security.auth.callback.Callback;

public class Budget extends BudgetComponent{

    Vector<Income> income_list = new Vector<Income>();
    Vector<Liability> liability_list = new Vector<Liability>();
    Vector<Transaction> transaction_list = new Vector<Transaction>();
    InternetRequest ir = new InternetRequest();
    //private CompletionListener listener = null;
    //private Activity caller = null;

    /**
     * @param response
     * populate Incomes
     */
    void setIncomes(String response){
        income_list.clear();
        if(response == null || response.equals("")){
            Data.respond(false);
            return;
        }

        JSONArray ja;
        //System.out.println("response = " + response);
        try {
            ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++){
                income_list.add(new Income((JSONObject) ja.get(i)));
            }
            //System.out.println("got the array -> " + ja.toString());
        }catch (Exception e){
            Data.respond(false);
            e.printStackTrace();
        }

        Data.respond(true);

    }

    public Vector<Income> getIncomes() { return income_list; };

    /**
     * @param response
     * populate Liabilities
     */
    void setLiabilities(String response){
        liability_list.clear();
        JSONArray ja;
        //System.out.println(response);
        try {
            ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++){
                liability_list.add(new Liability(this, (JSONObject) ja.get(i)));
            }
        }catch (Exception e){
            e.printStackTrace();
            Data.respond(false);
        }

        Data.respond(true);
    }

    public Vector<Liability> getLiabilities() { return liability_list; };

    /**
     * @param response
     * populate Transactions
     */
    void setTransactions(String response){
        transaction_list.clear();
        if(response.equals("")){
            Data.respond(true);
            //System.out.println("no response");
            return;
        }
        JSONArray ja;
        //System.out.println(response + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");

        try {
            ja = new JSONArray(response);
            for (int i = 0; i < ja.length(); i++){
                transaction_list.add(new Transaction((JSONObject) ja.get(i)));
            }
            //System.out.println("got the array -> " + ja.toString());
        }catch (Exception e){
            Data.respond(false);
            e.printStackTrace();
        }
        Data.respond(true);
    }

    public Vector<Transaction> getTransactions() { return transaction_list; };

    /**
     * @throws Exception
     * refresh liabilities after creation
     *
     */
    public void refreshLiabilities() throws Exception{
        JSONObject params = new JSONObject();
        params.put("budgetid", this.getJsonRep().get("budget_ID"));
        ir.doRequest(
                InternetRequest.std_url+"get_liabilities.php",
                null,
                params,
                this::setLiabilities
        );
    }

    /**
     * initialization function
     * @throws Exception
     */
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

    private void accumulate(LineDataSet subject, ArrayList<ArrayList<Entry>> lists){
        float total=0f, x=0f;

        Entry hold = new Entry(0f,0f);
        float min = Float.MAX_VALUE;

        ArrayList<Entry> used = new ArrayList<>();

        while (hold != null){
            x = hold.getX();
            total += hold.getY();
            used.add(hold);

            subject.addEntry(new Entry(x, total));

            hold = null;
            min = Float.MAX_VALUE;
            for(ArrayList<Entry> list: lists){
                for(Entry e: list){
                    System.out.println("--{" + e.getX() + "," + e.getY() + "}--");
                    if(e.getX() >= x && e.getX() < min && !used.contains(e)) {

                        hold = e;
                        min = e.getX();
                    }
                }
            }
        }
        subject.addEntry(new Entry(1f, total));
    }

    /**
     * Get Line data between start and end dates
     * @param start_date
     * @param end_date
     * @return
     */
    public LineData getPeriodSummary(LocalDate start_date, LocalDate end_date){
        LineData data = new LineData();

        float total_income = 0f, total_expense = 0f;
        float x = 0f;


        LineDataSet income_set = new LineDataSet(new ArrayList<>(), "Income");
        income_set.setLineWidth(2f);
        income_set.setFillAlpha(50);
        income_set.setFillColor(Color.rgb(55,234,100));
        income_set.setDrawFilled(true);
        income_set.setCircleColor(Color.rgb(55,234,100));
        income_set.setColor(Color.rgb(55,234,100));

        LineDataSet expense_set = new LineDataSet(new ArrayList<>(), "Liability");
        expense_set.setLineWidth(2f);
        expense_set.setFillAlpha(50);
        expense_set.setFillColor(Color.rgb(255,34,100));
        expense_set.setDrawFilled(true);
        expense_set.setCircleColor(Color.rgb(255,34,100));
        expense_set.setColor(Color.rgb(255,34,100));

        ArrayList<ArrayList<Entry>> income_sets = new ArrayList<>();
        ArrayList<ArrayList<Entry>> liability_sets = new ArrayList<>();

        for (int i = 0; i < income_list.size(); i++){
            income_sets.add(income_list.get(i).getPeriodSummary(start_date, end_date));
        }

        accumulate(income_set, income_sets);

        for (int i = 0; i < liability_list.size(); i++){
            liability_sets.add(liability_list.get(i).getPeriodSummary(start_date, end_date));
        }

        accumulate(expense_set, liability_sets);

        data.addDataSet(income_set);
        data.addDataSet(expense_set);

        return data;
    }


}
