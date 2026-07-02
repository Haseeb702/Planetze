package com.example.planetzeproject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;

import org.mockito.MockedStatic;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)

public class ExampleUnitTest {

    @Mock
    LoginActivity view;

    @Mock
    LoginModel model;


    @Test
    public void testEmailEmpty() {
        String email = null;
        try (MockedStatic<TextUtils> textUtilsMockedStatic = mockStatic(TextUtils.class)) {
            textUtilsMockedStatic.when(() -> TextUtils.isEmpty(email)).thenReturn(true);
            LoginPresenter presenter = new LoginPresenter(model, view);
            assertTrue(presenter.emailEmpty(email));
        }
    }

    @Test
    public void testPasswordEmpty() {
        String password = null;
        try (MockedStatic<TextUtils> textUtilsMockedStatic = mockStatic(TextUtils.class)) {
            textUtilsMockedStatic.when(() -> TextUtils.isEmpty(password)).thenReturn(true);
            LoginPresenter presenter = new LoginPresenter(model, view);
            assertTrue(presenter.passwordEmpty(password));
        }
    }

    @Test
    public void SignIn() {
        String email = "test1@gmail.com";
        String password = "123456";
        Context mockContext = mock(Context.class);
        LoginPresenter presenter = new LoginPresenter(model, view);

        presenter.signIn(email, password, mockContext);

        verify(model).signInEmailPassword((email), (password), (mockContext));
    }

    @Test
    public void validSignIn() {
        Context context = mock(Context.class);
        LoginPresenter presenter = new LoginPresenter(model, view);

        presenter.validSignIn(context);

        verify(view).login(context);

    }

    @Test
    public void invalidSignIn() {
        Context context = mock(Context.class);
        LoginPresenter presenter = new LoginPresenter(model, view);

        presenter.invalidSignIn(context);

        verify(view).invalidLogin(context);

    }

    @Test
    public void checkLoggedIn() {
        FirebaseUser mock = mock(FirebaseUser.class);
        LoginPresenter presenter = new LoginPresenter(model, view);

        presenter.checkLoggedIn();

        verify(model).checkUserStatus();

    }

}
