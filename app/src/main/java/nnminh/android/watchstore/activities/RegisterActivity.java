package nnminh.android.watchstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.RegisterRequest;
import nnminh.android.watchstore.models.AuthResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, confirmPasswordInput, firstNameInput, lastNameInput;
    private EditText dayInput, monthInput, yearInput, phoneInput;
    private RadioGroup genderRadioGroup;
    private Button registerButton;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        initializeViews();
        
        // Set up login text click listener
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the register activity
        });

        registerButton.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        confirmPasswordInput = findViewById(R.id.editTextConfirmPassword);
        firstNameInput = findViewById(R.id.editTextFirstName);
        lastNameInput = findViewById(R.id.editTextLastName);
        phoneInput = findViewById(R.id.editTextPhone);
        dayInput = findViewById(R.id.editTextDay);
        monthInput = findViewById(R.id.editTextMonth);
        yearInput = findViewById(R.id.editTextYear);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        registerButton = findViewById(R.id.buttonRegister);
        loginText = findViewById(R.id.text_login);
    }

    private boolean validateInput() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String day = dayInput.getText().toString().trim();
        String month = monthInput.getText().toString().trim();
        String year = yearInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
            phone.isEmpty() || day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("^[0-9]{10}$")) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate date
        try {
            int dayNum = Integer.parseInt(day);
            int monthNum = Integer.parseInt(month);
            int yearNum = Integer.parseInt(year);

            if (dayNum < 1 || dayNum > 31 || monthNum < 1 || monthNum > 12 || yearNum < 1900 || yearNum > 2024) {
                Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for date", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String day = dayInput.getText().toString().trim();
        String month = monthInput.getText().toString().trim();
        String year = yearInput.getText().toString().trim();
        
        // Format date as YYYY-MM-DD
        String dateOfBirth = String.format("%s-%02d-%02d", year, Integer.parseInt(month), Integer.parseInt(day));
        
        // Get selected gender
        String gender = "";
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioMale) {
            gender = "MALE";
        } else if (selectedId == R.id.radioFemale) {
            gender = "FEMALE";
        } else if (selectedId == R.id.radioOther) {
            gender = "OTHER";
        }

        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        RegisterRequest registerRequest = new RegisterRequest(email, password, firstName, lastName, dateOfBirth, gender, phone);
        Call<AuthResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    TokenManager.getInstance(getApplicationContext()).saveToken(response.body().getData().getAccess_token());
                    TokenManager.getInstance(getApplicationContext()).saveUser(response.body().getData().getUser());
                    finish();
                } else {
                    String errorMessage = "Registration failed";
                    if (response.errorBody() != null) {
                        try {
                            JSONObject errorJson = new JSONObject(response.errorBody().string());
                            errorMessage = errorJson.optString("message", errorMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}