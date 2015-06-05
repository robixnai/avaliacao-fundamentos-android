package com.example.administrador.myapplication.controllers;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrador.myapplication.R;
import com.example.administrador.myapplication.models.entities.User;
import com.example.administrador.myapplication.models.persistence.UserRepository;
import com.example.administrador.myapplication.util.AppUtil;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextLogin, mEditTextPass;
    private Button mButtonLogin;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_material);

        this.bindElements();

        // Change typeface for the password field
        mEditTextLogin.setTypeface(Typeface.DEFAULT);
        mEditTextPass.setTransformationMethod(new PasswordTransformationMethod());

        mButtonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        });
    }

    private void bindElements() {
        mEditTextLogin = AppUtil.get(findViewById(R.id.editTextLogin));
        mEditTextPass = AppUtil.get(findViewById(R.id.editTextPass));
        mButtonLogin = (Button) findViewById(R.id.buttonLogin);
    }

    private void validateUser() {
        boolean isValid = this.verifyMandatoryFields(mEditTextLogin, mEditTextPass);

        if (isValid) {
            mUser = UserRepository.getInstance().getUser(mEditTextLogin.getText().toString(), mEditTextPass.getText().toString());
            if (mUser != null) {
                startActivity(new Intent(MainActivity.this, ServiceOrderListActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.lbl_error_login, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean verifyMandatoryFields(EditText... fields) {
        boolean isValid = true;
        for (EditText field : fields) {
            field.setError(null);
            if (TextUtils.isEmpty(field.getText())) {
                field.setError(getString(R.string.msg_mandatory));
                if (isValid) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }

}
