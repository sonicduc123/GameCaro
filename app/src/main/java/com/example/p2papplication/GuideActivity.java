package com.example.p2papplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    private  OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton buttonOnboardingAction;
    MediaPlayer soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnboardingAction);
        soundClick = MediaPlayer.create(GuideActivity.this, R.raw.click1);

        setupOnboardingItems();

        final ViewPager2 onboardingViewPager = findViewById(R.id.onBoardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundClick.start();
                if(onboardingViewPager.getCurrentItem()+1 < onboardingAdapter.getItemCount()) {
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem itemHello = new OnboardingItem();
        itemHello.setTitle("Queo Căm To XO Game !!!");
        itemHello.setDescription("");
        itemHello.setImage(R.drawable.hello);

        OnboardingItem itemguide1 = new OnboardingItem();
        itemguide1.setTitle("Game có 2 chế độ chơi nè hee !!");
        itemguide1.setDescription("Notice: Máy đánh never die. Cân nhắc khi chơi !!!.");
        itemguide1.setImage(R.drawable.home);

        OnboardingItem itemguide2 = new OnboardingItem();
        itemguide2.setTitle("Cách chơi với người lạ ơi nè hee !!");
        itemguide2.setDescription("- Bấm 'REFRESH' nếu muốn tìm phòng. \n" +
                "- Bấm 'CREATE ROOM' để tạo phòng. \n   " +
                "Hết ròi đó !!! Đụng là trụng thôi.");
        itemguide2.setImage(R.drawable.room);

        OnboardingItem itemguide3 = new OnboardingItem();
        itemguide3.setTitle("Luật chơi nè hee !!");
        itemguide3.setDescription("- Mỗi người chỉ được đánh 1 nước khi tới lượt. \n " +
                "- Nếu đánh đủ 5 nước liên tiếp mà không bị chặn 2 đầu là Win. \n" +
                " Chú ý: Chỉ có 20s để suy nghĩ\n Hơn thua ráng chịu :3 \n" +
                "- Có cái thanh máu ở trên. Hết máu là say good byee nà.");
        itemguide3.setImage(R.drawable.ingame);

        OnboardingItem itemguide4 = new OnboardingItem();
        itemguide4.setTitle("Hết ròi !!!");
        itemguide4.setDescription("Game made by: \n" +
                "18120308 - Nguyễn Tấn Đạt \n" +
                "18120347 - Ngô Hải Hà\n" +
                "18120325 - Phạm Anh Đức\n" +
                "18120344 - Nguyễn Trường Duy");
        itemguide4.setImage(R.drawable.friend);



        onboardingItems.add(itemHello);
        onboardingItems.add(itemguide1);
        onboardingItems.add(itemguide2);
        onboardingItems.add(itemguide3);
        onboardingItems.add(itemguide4);


        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i =0; i<indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }
    private void setCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for(int i = 0; i<childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if(i==index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == onboardingAdapter.getItemCount() - 1) {
            buttonOnboardingAction.setText("Home");
        }
        else {
            buttonOnboardingAction.setText("Next");
        }
    }
}