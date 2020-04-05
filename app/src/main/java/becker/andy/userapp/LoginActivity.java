package becker.andy.userapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import becker.andy.userapp.databinding.ActivityLoginBinding;
import becker.andy.userapp.models.User;
import becker.andy.userapp.ui.home.HomeViewModel;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private PrefConfig prefConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);

        prefConfig = new PrefConfig(getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewmodel(viewModel);

        viewModel.getLoginResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.loading.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getProgressbar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.loading.setVisibility(View.VISIBLE);
            }
        });
        

        viewModel.getUserResponse().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                binding.loading.setVisibility(View.GONE);
                viewModel.writeUser(user, prefConfig);
                prefConfig.writeLoginStatus(true);
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


}
