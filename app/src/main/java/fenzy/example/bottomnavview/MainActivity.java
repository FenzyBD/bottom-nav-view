package fenzy.example.bottomnavview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.bottom.navview.KBottomNavView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KBottomNavView kBottomNavView = findViewById(R.id.kNavigationBar);

        kBottomNavView.OnItemSelectedListener((id, position) -> {
            System.out.println(position);
        });
    }
}