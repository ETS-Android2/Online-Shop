package com.project.onlineshop.api;

import com.project.onlineshop.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiEndpointInterface {

    @GET(EndpointContainer.LIST_USERS)
    public Call<List<User>> getUsers();

    // Registration Service
    @POST(EndpointContainer.LOGIN)
    public Call<User> loginUser(@Query("email") String email,@Query("password") String password);

    @GET(EndpointContainer.RESEND_PASSWORD)
    public Call<Response> resendPassword(@Query("email") String email);

    @POST(EndpointContainer.CHANGE_PASSWORD)
    public Call<Response> changePassword(@Query("email") String email,
                                         @Query("old_password") String old_password,
                                         @Query("new_password") String new_password);

    @POST(EndpointContainer.REMOVE_ACCOUNT)
    public Call<Response> removeAccount(@Query("user_id") Long user_id,
                                        @Query("email") String email,
                                        @Query("password") String password);

    @POST(EndpointContainer.SIGNUP)
    public Call<Response> signUp(
            @Query("name") String name,
            @Query("family") String family,
            @Query("phone_number") String phone_number,
            @Query("email") String email,
            @Query("password") String password);

    @POST(EndpointContainer.GET_REPORT)
    public Call<ServerReport> getReport(@Query("email") String admin_email,
                                        @Query("password") String admin_password);

    @POST(EndpointContainer.UPDATE_USER)
    public Call<Response> updateUser(@Body User user);

    @GET(EndpointContainer.GET_USERS)
    public Call<List<User>> getUsers(@Query("search") String search,
                                         @Query("sort_option") String sort_option,
                                         @Query("pageIndex") int pageIndex,
                                         @Query("amount") int amount);

    // User Service
    // Just for admin...
    @GET(EndpointContainer.FILTER_USERS)
    public Call<List<User>> filterUsers(
            @Query("name") String name,
            @Query("family") String family,
            @Query("phone_number") String phone_number,
            @Query("email") String email);

    // TODO : defining args...
    @POST(EndpointContainer.GET_USER_LOGS)
    public Call<List<Log>> getUserLogs(@Query("user_id") Long user_id,
                                       @Query("email") String email,
                                       @Query("password") String password);

    // Poster Service
    @POST(EndpointContainer.ADD_POSTER)
    public Call<Response> addPoster(@Query("email") String email,
                                    @Query("password") String password,
                                    @Body Poster poster);

    @GET(EndpointContainer.GET_POSTERS)
    public Call<List<Poster>> getPosters(@Query("search") String search,
                                         @Query("sort_option") String sort_option,
                                         @Query("pageIndex") int pageIndex,
                                         @Query("amount") int amount);

    @POST(EndpointContainer.DELETE_POSTER)
    public Call<Response> removePoster(@Query("poster_id") Long poster_id,
                                       @Query("email") String email,
                                       @Query("password") String password);

    @POST(EndpointContainer.UPDATE_POSTER)
    public Call<Response> updatePoster(@Query("email") String email,
                                       @Query("password") String password,
                                       @Body Poster poster);

    @POST(EndpointContainer.TOGGLE_BOOKMARK)
    public Call<Response> toggleBookmark(@Query("email") String email,
                                         @Query("password") String password,
                                         @Query("poster_id") Long poster_id);

    @GET(EndpointContainer.GET_POSTER_OWNER)
    public Call<User> getPosterOwner(@Query("poster_id") Long poster_id);
}
