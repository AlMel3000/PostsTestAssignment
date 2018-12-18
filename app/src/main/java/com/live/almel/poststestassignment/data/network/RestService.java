package com.live.almel.poststestassignment.data.network;

import com.live.almel.poststestassignment.data.network.res.Details;
import com.live.almel.poststestassignment.data.network.res.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestService {


    @GET("posts")
    Call<List<Post>> getPostsList();

    @GET("users")
    Call<List<Details>> getAuthors();
}