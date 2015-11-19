package com.company.jsontest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        String strjon = "{\n  \"inapptext\": [\n" +
            "    {\n" +
            "      \"locale\": \"en-US\",\n" +
            "      \"changed_text\": [\n" +
            "        {\n" +
            "          \"key\": \"alertTitleCall\",\n" +
            "          \"value\": \"Could not connect\"\n" +
            "        }\n]" +
//            "        {\n" +
//            "          \"key\": \"alertBtnCall\",\n" +
//            "          \"value\": \"Talk to a live agent\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"485\",\n" +
//            "          \"value\": \"Please check the number\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    },\n" +
//            "    {\n" +
//            "      \"locale\": \"en-UK\",\n" +
//            "      \"changed_text\": [\n" +
//            "        {\n" +
//            "\n" +
//            "\"key\": \"alertTitleCall\",\n" +
//            "          \"value\": \"Sorry! Unable to connect\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"alertBtnCall\",\n" +
//            "          \"value\": \"Would you like to contact an agent\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"485\",\n" +
//            "          \"value\": \"Please redial the correct number\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    },\n" +
//            "    {\n" +
//            "      \"locale\": \"es\",\n" +
//            "      \"changed_text\": [\n" +
//            "        {\n" +
//            "          \"key\": \"SIPRegFail\",\n" +
//            "\n" +
//            "  \"value\": \"Falló el registro. Inténtalo más tarde.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"alertBtnCall\",\n" +
//            "          \"value\": \"Habla con un agente\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"485\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"415\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"416\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "\n" +
//            " \"key\": \"420\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"421\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"487\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"481\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"key\": \"482\",\n" +
//            "          \"value\": \"Favor de verificar el número e intentar de nuevo.\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    }\n" +
//            "\n" +
//            " ]\n" +
            "}\n]\n}";

       // TextView output = (TextView)findViewById(R.id.textView1);
        String data = "";

        try{


            JSONObject rootJSONObject =  new JSONObject(strjon);
            JSONArray jsonArray = rootJSONObject.optJSONArray("inapptext");
         //   String[] inapptext =JSONObject.getNames(rootJSONObject);
           // Log.d("JSONARRAY Length",Integer.toString(jsonArray.length()));
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String locale = jsonObject.optString("locale").toString();
                String changed_text = jsonObject.getString("changed_text").toString();
                data += "locale="+locale +"\n" + "changed_text=" + changed_text;
                Log.d("JSONTEST", "data=" + data);

            }
         //   output.setText(data);
        }catch (JSONException e){
            e.printStackTrace();
        }
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
