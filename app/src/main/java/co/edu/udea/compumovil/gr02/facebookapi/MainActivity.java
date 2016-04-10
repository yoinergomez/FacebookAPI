package co.edu.udea.compumovil.gr02.facebookapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    Context context = this;
    CharSequence text = "Hello toast!";
    int duration = Toast.LENGTH_SHORT;
    Toast toast;
    ProfileTracker profileTracker;
    TextView tvNombre;
    ImageView ivImagen;
    Button btShare;
    AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            URL imageUrl;
            Bitmap imagen = null;
            try {
                imageUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.connect();
                imagen = BitmapFactory.decodeStream(conn.getInputStream());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error cargando la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ivImagen.setImageBitmap(bitmap);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                text = "Logeado";
                toast = Toast.makeText(context, text, duration);
                toast.show();


            }

            @Override
            public void onCancel() {
                text = "onCancel";
                toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onError(FacebookException exception) {
                text = "onError: " + exception.getMessage();
                toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        });

        tvNombre = (TextView) findViewById(R.id.nombre);
        ivImagen = (ImageView) findViewById(R.id.imagen);
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                if (currentProfile != null) {
                    text = currentProfile.getName();
                    tvNombre.setText(text);
                    Uri uri = currentProfile.getProfilePictureUri(100, 100);
                    asyncTask.execute(uri.toString()); //Descargar imagen
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                    currentProfile.getFirstName();
                }
            }
        };

        btShare = (Button) findViewById(R.id.share);
        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.facebook.orca");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "https://sites.google.com/site/udeacomputacionmovil15/");
                shareIntent.setType("text/plain");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
