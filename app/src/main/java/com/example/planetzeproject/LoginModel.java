package com.example.planetzeproject;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginModel extends AppCompatActivity {


    private LoginPresenter presenter = new LoginPresenter(this, new LoginActivity());
    FirebaseAuth mAuth;


    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser checkUserStatus() {
        return mAuth.getCurrentUser();
    }

    public void signInEmailPassword(String email, String password, Context context) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            presenter.validSignIn(context);
                        } else {
                            presenter.invalidSignIn(context);
                        }
                    }
                });
    }


}
