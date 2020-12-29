package com.deepak.scavengerhunter.activities.APIs.ServiceInterfaces;


import com.deepak.scavengerhunter.activities.APIs.EndPoints;
import com.deepak.scavengerhunter.activities.APIs.ServiceModals.UserRegistrationResponse;
import com.deepak.scavengerhunter.activities.classes.Modals.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RegistrationService {


    @POST(EndPoints.RESGISTRATION)
    Call<User> UserRegistration(@Query("name") String name,
                                @Query("email") String email,
                                @Query("password") String password,
                                @Query("googleid") String googleid,
                                @Query("facebookid") String facebookid,
                                @Query("image_url") String image_url,
                                @Query("mode") String mode);

}
