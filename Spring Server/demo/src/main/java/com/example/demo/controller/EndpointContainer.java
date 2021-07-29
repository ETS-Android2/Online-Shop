package com.example.demo.controller;

public class EndpointContainer {

    private EndpointContainer() {}

    // API Main Endpoint
    private static final String API = "api";

    // Registration Endpoints
    private static final String REG_BRANCH = API + "/auth";
        public static final String LOGIN = REG_BRANCH + "/login";
        public static final String RESEND_PASSWORD = REG_BRANCH + "/resend_password";
        public static final String CHANGE_PASSWORD = REG_BRANCH + "/change_password";
        public static final String REMOVE_ACCOUNT = REG_BRANCH + "/remove_account";
        public static final String SIGNUP = REG_BRANCH + "/signup";
            public static final String CONFIRM_EMAIL = SIGNUP + "/confirm";
        public static final String GET_REPORT = REG_BRANCH + "/get_report";

    // Users Endpoints
    private static final String USER = API + "/user";
        public static final String ADD_USER = USER + "/add";
        public static final String UPDATE_USER = USER + "/update_user";
        public static final String GET_USERS = USER + "/get_users";
        public static final String FILTER_USERS = USER + "/filter";
        public static final String GET_USER_LOGS = USER + "/logs";
        public static final String LIST_USERS = USER + "/list";

    // Products Endpoints
    private static final String POSTER = API + "/poster";
        public static final String ADD_POSTER = POSTER + "/add";
        public static final String GET_POSTERS = POSTER + "/list";
        public static final String DELETE_POSTER = POSTER + "/delete";
        public static final String UPDATE_POSTER = POSTER + "/update_info";
        public static final String TOGGLE_BOOKMARK = USER + "/toggle_bookmark";
        public static final String GET_POSTER_OWNER = POSTER + "/owner";

}
