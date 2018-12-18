package com.live.almel.poststestassignment.data.managers;

import com.live.almel.poststestassignment.data.network.RestService;
import com.live.almel.poststestassignment.data.network.ServiceGenerator;
import com.live.almel.poststestassignment.data.network.res.Details;
import com.live.almel.poststestassignment.data.network.res.Post;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Alexey on 26.11.16.
 */

public class DataManager {
    private static DataManager INSTANCE = null;


    private RestService mRestService;

    public DataManager() {
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    //region ============ Network ===========

    public Call<List<Post>> getPostsList() {
        return mRestService.getPostsList();
    }

    public Call<List<Details>> getAuthors() {
        return mRestService.getAuthors();
    }
    //endregion

}
