package com.teamvoid.djevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamvoid.djevents.Adapters.LoginAdapter;
import com.teamvoid.djevents.Fragments.SignInFragment;
import com.teamvoid.djevents.Fragments.SignUpFragment;
import com.teamvoid.djevents.R;

public class LoginActivity extends AppCompatActivity implements SignUpFragment.SendUser {

    private static final String TAG = "LoginActivity";

    // Elements
    private TextView tvSignIn, tvSignUp;
    private ImageView ivSignIn, ivSignUp;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Data Binding
        init();
        setUpFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if already logged in
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            this.finish();
        }
    }

    @Override
    public void sendUser(String name, String email) {
        String tag = "android:switcher:" + R.id.viewpagerLogin + ":" + 0;
        SignInFragment signInFragment = (SignInFragment) getSupportFragmentManager().findFragmentByTag(tag);
        assert signInFragment != null;
        signInFragment.displayReceivedData(name, email);
    }

    private void init() {
        tvSignIn = findViewById(R.id.tvLoginSignIn);
        tvSignUp = findViewById(R.id.tvLoginSignUp);
        ivSignIn = findViewById(R.id.ivLoginSignInUnderline);
        ivSignUp = findViewById(R.id.ivLoginSignUpUnderline);
        viewPager = findViewById(R.id.viewpagerLogin);
    }

    private void setUpFragments() {
        Log.d(TAG, "setUpFragments: Setting up fragments...");

        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new SignInFragment());
        adapter.addFragment(new SignUpFragment());

        viewPager.setAdapter(adapter);
        selectSignIn();
        viewPager.setCurrentItem(0);
        setUpTabs();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    selectSignIn();
                else selectSignUp();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUpTabs() {
        tvSignIn.setOnClickListener(view -> {
            selectSignIn();
            viewPager.setCurrentItem(0);
        });

        tvSignUp.setOnClickListener(view -> {
            selectSignUp();
            viewPager.setCurrentItem(1);
        });
    }

    private void selectSignIn() {
        ivSignIn.setVisibility(View.VISIBLE);
        ivSignUp.setVisibility(View.INVISIBLE);
    }

    private void selectSignUp() {
        ivSignUp.setVisibility(View.VISIBLE);
        ivSignIn.setVisibility(View.INVISIBLE);
    }
}