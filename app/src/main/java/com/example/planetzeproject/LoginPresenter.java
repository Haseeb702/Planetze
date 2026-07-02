package com.example.planetzeproject;

import android.content.Context;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;



public class LoginPresenter {


    private LoginModel model;
    private LoginActivity view;


    public LoginPresenter(LoginModel model, LoginActivity view) {
        this.model = model;
        this.view = view;
    }


    public boolean emailEmpty(String email) {
        return (TextUtils.isEmpty(email));
    }


    public boolean passwordEmpty(String password) {
        return (TextUtils.isEmpty(password));
    }


    public void signIn(String email, String password, Context context) {
        model.signInEmailPassword(email, password, context);
    }

    public void validSignIn(Context context) {
        view.login(context);
    }

    public void invalidSignIn(Context context) {
        view.invalidLogin(context);
    }

    public FirebaseUser checkLoggedIn() {
        return model.checkUserStatus();
    }
}
