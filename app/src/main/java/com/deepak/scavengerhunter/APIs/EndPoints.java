package com.deepak.scavengerhunter.APIs;

public class EndPoints {

    public static final String BASE_URL = "http://192.168.43.236:4000";
    //public static final String BASE_URL = "http://schunterbackend-env.eba-ruvt3tjq.eu-west-2.elasticbeanstalk.com";
    public static final String RESGISTRATION = BASE_URL + "/auth/registration";
    public static final String LOGIN = BASE_URL + "/auth/login";
    public static final String LOGIN_GOOGLE = BASE_URL + "/auth/auth_google";
    public static final String CREATE_HUNT = BASE_URL + "/hunts/create";
    public static final String GET_ALL_HUNT = BASE_URL + "/hunts/get-all";
    public static final String GET_USERS_HUNT = BASE_URL + "/hunts/get-users-all";

    public static final String GET_FILTERS_HUNTS = BASE_URL + "/hunts/filters";
    public static final String GET_HUNT_BY_ID = BASE_URL + "/hunts/get";
    public static final String CREATE_POST = BASE_URL + "/hunts/add_post";
    public static final String USE_LATEST_POST_ENDING_POINT = BASE_URL + "/hunts/endingpoint";
    public static final String CHECK_HUNT_RECORD = BASE_URL+"/records/check";
}
