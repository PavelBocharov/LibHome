package com.mar.ds.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("LibHome: login")
public class LoginPage extends AppLayout {

    private final LoginForm login = new LoginForm();

    public LoginPage() {
        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.addClassName("login-view");
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        verticalLayout.add(login);

        setContent(verticalLayout);
    }

}
