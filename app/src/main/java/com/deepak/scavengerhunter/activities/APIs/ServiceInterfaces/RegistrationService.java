package com.deepak.scavengerhunter.activities.APIs.ServiceInterfaces;


import com.deepak.scavengerhunter.activities.APIs.EndPoints;
import com.deepak.scavengerhunter.activities.APIs.ServiceModals.UserRegistrationResponse;
import com.deepak.scavengerhunter.activities.classes.Modals.RegisterUser;
import com.deepak.scavengerhunter.activities.classes.Modals.User;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RegistrationService {


    @POST(EndPoints.RESGISTRATION)
    Call<User> UserRegistration(@Body RegisterUser body);

}
