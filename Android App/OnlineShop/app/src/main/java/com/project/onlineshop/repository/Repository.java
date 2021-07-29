package com.project.onlineshop.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.project.onlineshop.BuildConfig;
import com.project.onlineshop.api.ApiEndpointInterface;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.api.ServerReport;
import com.project.onlineshop.model.ByteArrayToBase64TypeAdapter;
import com.project.onlineshop.model.MyObjectBox;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    private static ApiEndpointInterface apiEndpoint = null;
    private static GlobalStorage globalStorage = null;

    private static Box<User> userBox = null;
    private static Box<Poster> posterBox = null;

    private static User current_user = null;

    private static Boolean isInitialized = false;

    private static String api_url;

    private Repository() {}

    public static void init(Application application, long user_id, String server_ip, String server_port) {
        if( isInitialized )
            return;

        api_url = "http://" + server_ip + ":" + server_port + "/";

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
                .setLenient()
                .create();

        apiEndpoint = new Retrofit.Builder()
                .baseUrl(api_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiEndpointInterface.class);

        BoxStore boxStore = MyObjectBox.builder()
                .androidContext(application.getApplicationContext())
                .build();

        userBox = boxStore.boxFor(User.class);
        posterBox = boxStore.boxFor(Poster.class);

        current_user = userBox.get(user_id);
        globalStorage = (GlobalStorage)application.getApplicationContext();

        isInitialized = true;
    }

    /* -----------  Registration Methods  ----------- */

    public static Result<User> loginUser(String email, String password) {
        try {
            User user = apiEndpoint.loginUser(email, password).execute().body();

            if( user.email != null ) {
                userBox.put(user);
                for( Poster poster : user.posters ) {
                    poster.setOwner(user);
                    posterBox.put(poster);
                }

                posterBox.put(user.bookmarks);

                userBox.put(user);
                current_user = user;
            }

            return new Result.Success<>(user);
        }
        catch (IOException e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<Response> signUp(String name, String family, String phone_number, String email, String password) {
        try {
            return new Result.Success<>(apiEndpoint.signUp(name, family, phone_number, email, password).execute().body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<ServerReport> getReport() {
        try {
            return new Result.Success<>(
                    apiEndpoint.getReport(getCurrentUser().email, getCurrentUser().password)
                            .execute()
                            .body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<Response> resendPassword(String email) {
        try {
            return new Result.Success<>(apiEndpoint.resendPassword(email).execute().body());

        } catch (IOException e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<Response> changePassword(String old_password, String new_password) {
        try {
            Response response = apiEndpoint.changePassword(
                    getCurrentUser().email, old_password, new_password).execute().body();

            if( response.getResult() ) {
                User user = getCurrentUser();
                user.password = new_password;
                changeCurrentUser(user);
            }
            return new Result.Success<>(response);
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<Response> removeAccount(Long user_id) {
        try {
            return new Result.Success<>(apiEndpoint.removeAccount(
                    user_id,
                    getCurrentUser().email,
                    getCurrentUser().password).execute().body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    /* -----------  User Related Methods  ----------- */

    public static Result<Response> updateUserToServer(User user) {
        try {
            user.posters = null;
            user.bookmarks = null;
            return new Result.Success<>(apiEndpoint.updateUser(user).execute().body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<List<User>> getUsers(String search, String sort_option, int pageIndex, int amount) {
        try {
            return new Result.Success<>(
                    apiEndpoint.getUsers(search.trim(), sort_option.toLowerCase().trim(), pageIndex, amount)
                            .execute().body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    /* -----------  Poster Related Methods  ----------- */

    public static Result<Response> addPoster(Poster poster) {
        try {
            poster.owner = null;
            Response response = apiEndpoint
                    .addPoster(getCurrentUser().email, getCurrentUser().password, poster)
                    .execute()
                    .body();

            if( response.getResult() ) {
                User user = getCurrentUser();
                userBox.attach(user);
                userBox.put(user);
                poster.setOwner(user);
                user.posters.add(poster);
                posterBox.put(poster);
                userBox.put(user);
            }

            return new Result.Success<>(response);
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<List<Poster>> getPosters(String search, String sort_option, int pageIndex, int amount) {
        try {
            return new Result.Success<>(apiEndpoint.getPosters(search, sort_option, pageIndex, amount).execute().body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<Response> updatePoster(Poster poster) {
        try {
            poster.owner = null;
            Response response = apiEndpoint.updatePoster(getCurrentUser().email,
                    getCurrentUser().password, poster).execute().body();

            if( response.getResult() && getCurrentUser().hasPoster(poster) ) {
                User user = getCurrentUser();
                poster.setOwner(user);
                Poster ex_poster = posterBox.get(poster.id);
                user.posters.remove(ex_poster);
                user.posters.add(poster);
                posterBox.put(poster);
                changeCurrentUser(user);
            }

            return new Result.Success<>(response);
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    public static Result<Response> removePoster(Poster poster) {
        try {
            Response response = apiEndpoint.removePoster(
                            poster.id,
                            getCurrentUser().email,
                            getCurrentUser().password)
                            .execute().body();

            if( response.getResult() ) {
                User user = getCurrentUser();
                user.posters.remove(poster);
                posterBox.remove(poster.id);
                changeCurrentUser(user);
            }

            return new Result.Success<>(response);
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    // Returns true if bookmark status was toggled
    public static Boolean toggleBookmark(Poster poster) {
        try {
            Response response = apiEndpoint.toggleBookmark(
                    getCurrentUser().email,
                    getCurrentUser().password,
                    poster.id).execute().body();

            if( response != null && response.getResult() ) {
                User user = getCurrentUser();

                if(user.isBookmarked(poster)) {
                    user.bookmarks.remove(poster);

                    if(!user.hasPoster(poster))
                        posterBox.remove(poster);
                }
                else {
                    userBox.attach(user);
                    user.bookmarks.add(poster);
                    posterBox.put(poster);
                }

                changeCurrentUser(user);
            }

            return response.getResult();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static Result<User> getPosterOwner(Long poster_id) {
        try {
            return new Result.Success<>(apiEndpoint.getPosterOwner(poster_id).execute().body());
        }
        catch (Exception e) {
            return new Result.Error<>(e);
        }
    }

    /* -----------  Local Responsibilities  ----------- */

    public static User getCurrentUser() {
        if(current_user == null)
            return null;
        return current_user.handleNulls();
    }

    public static void changeCurrentUser(User user) {
        globalStorage.saveUser(user.id);
        userBox.put(user);
        current_user = user;
    }

    public static void cleanData() {
        posterBox.removeAll();
        userBox.removeAll();
        globalStorage.cleanData();
        current_user = null;
    }
}
