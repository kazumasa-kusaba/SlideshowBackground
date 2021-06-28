package xyz.kusaba.slideshowbackground.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import xyz.kusaba.slideshowbackground.SlideshowBackground;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlideshowBackground slideshowBackground = this.findViewById(R.id.slideshowBackground);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_00);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_01);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_02);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_03);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_04);

        Button buttonPlay = this.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideshowBackground.play();
            }
        });

        Button buttonPause = this.findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideshowBackground.pause();
            }
        });

        Button buttonStop = this.findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideshowBackground.stop();
            }
        });
    }
}