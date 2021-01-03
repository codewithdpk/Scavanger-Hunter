package com.deepak.scavengerhunter.activities.APIs;

 public class EndPoints {

    public static final String BASE_URL = "http://192.168.43.236:4000";
     //public static final String BASE_URL = "http://schunterbackend-env.eba-ruvt3tjq.eu-west-2.elasticbeanstalk.com";

     public static final String RESGISTRATION = BASE_URL+ "/auth/registration";
     public static final String LOGIN = BASE_URL+"/auth/login";

    public static final String CREATE_HUNT = BASE_URL+"/hunts/create";

}
