package nnminh.android.watchstore;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import nnminh.android.watchstore.activities.LoginActivity;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.fragments.CartFragment;
import nnminh.android.watchstore.fragments.HomeFragment;
import nnminh.android.watchstore.fragments.ProfileFragment;
import nnminh.android.watchstore.utils.CartBadgeHelper;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                        Fragment selectedFragment = null;
                        int itemId = item.getItemId();
                        if (itemId == R.id.nav_home) {
                            selectedFragment = new HomeFragment();
                        } else if (itemId == R.id.nav_cart) {
                            selectedFragment = new CartFragment();
                        } else if (itemId == R.id.nav_profile) {
                            if (!TokenManager.getInstance(getApplicationContext()).isLoggedIn()) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                return false;
                            } else {
                                selectedFragment = new ProfileFragment();
                            }
                        }
                        return loadFragment(selectedFragment);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        CartBadgeHelper.updateCartBadge(this);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}