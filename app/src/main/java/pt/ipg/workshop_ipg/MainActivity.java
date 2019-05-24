package pt.ipg.workshop_ipg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openImageActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, VisionActivity.class);
        startActivity(intent);

        //Toast.makeText(MainActivity.this, "Great success! tens isso OK.", Toast.LENGTH_LONG).show();



    }
}
