package xyz.kusaba.slideshowbackground.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import xyz.kusaba.slideshowbackground.SlideshowBackground;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        List<Integer> resIdList = new ArrayList<>();
        resIdList.add(R.drawable.sample_picture_00);
        resIdList.add(R.drawable.sample_picture_01);
        resIdList.add(R.drawable.sample_picture_02);
        resIdList.add(R.drawable.sample_picture_03);
        resIdList.add(R.drawable.sample_picture_04);

        SlideshowBackground slideshowBackground = this.findViewById(R.id.slideshowBackground);
        slideshowBackground.setData(this.getResources(), resIdList);
        slideshowBackground.start();
        */

        SlideshowBackground slideshowBackground = this.findViewById(R.id.slideshowBackground);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_00);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_01);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_02);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_03);
        slideshowBackground.append(this.getResources(), R.drawable.sample_picture_04);
    }
}