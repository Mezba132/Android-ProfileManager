package aiub.android.mezba.profilemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnstart;
    Button btnstop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btnstart = (Button)findViewById(R.id.button);
        this.btnstop  = (Button)findViewById(R.id.button2);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Start Successfull",Toast.LENGTH_SHORT).show();
                startService(new Intent(getApplicationContext(),profilemanager.class));
            }
        });
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Stop Successfull",Toast.LENGTH_SHORT).show();
                stopService(new Intent(getApplicationContext(),profilemanager.class));
            }
        });
    }
}
