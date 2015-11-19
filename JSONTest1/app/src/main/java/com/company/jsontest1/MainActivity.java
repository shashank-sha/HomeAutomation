package com.company.jsontest1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView output = (TextView) findViewById(R.id.textView1);
        String strJson = "\"\n" +
                "      {\n" +
                "         \\\"Employee\\\" :[\n" +
                "         {\n" +
                "            \\\"id\\\":\\\"01\\\",\n" +
                "            \\\"name\\\":\\\"Gopal Varma\\\",\n" +
                "            \\\"salary\\\":\\\"500000\\\"\n" +
                "         },\n" +
                "         {\n" +
                "            \\\"id\\\":\\\"02\\\",\n" +
                "            \\\"name\\\":\\\"Sairamkrishna\\\",\n" +
                "            \\\"salary\\\":\\\"500000\\\"\n" +
                "         },\n" +
                "         {\n" +
                "            \\\"id\\\":\\\"03\\\",\n" +
                "            \\\"name\\\":\\\"Sathish kallakuri\\\",\n" +
                "            \\\"salary\\\":\\\"600000\\\"\n" +
                "         }\n" +
                "         ] \n" +
                "      }\"";
        String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(strJson);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("Employee");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = Integer.parseInt(jsonObject.optString("id").toString());
                String name = jsonObject.optString("name").toString();
                float salary = Float.parseFloat(jsonObject.optString("salary").toString());

                data += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
            }
            output.setText(data);
        } catch (JSONException e) {e.printStackTrace();}
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
