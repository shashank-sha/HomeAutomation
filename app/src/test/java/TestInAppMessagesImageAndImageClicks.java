import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.HandlerThread;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;

import com.zemoso.zinteract.ZinteractSampleApp.Activity3;
import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.Activity5;
import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;
import com.zemoso.zinteract.ZinteractSampleApp.R;
import com.zemosolabs.zinteract.sdk.*;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowImageView;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.util.ActivityController;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;



@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestInAppMessagesImageAndImageClicks extends TestInAppMessagesBaseClass{

    @Test
    public void test(){
        setupPromotionsInitially();
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel1);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel2);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel3);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel4);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel5);
    }

}
