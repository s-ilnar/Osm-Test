package ru.ilnarsoultanov.osmsnapshottest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import ru.ilnarsoultanov.osmsnapshottest.view.adapter.SnapsShotAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int mapWidth, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mapWidth = displayMetrics.widthPixels;
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        update();
    }

    private void update() {
        recyclerView.setAdapter(new SnapsShotAdapter(getApplicationContext(), mapWidth, height));

    }
}
