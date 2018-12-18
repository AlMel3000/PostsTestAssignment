package com.live.almel.poststestassignment.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.live.almel.poststestassignment.R;
import com.live.almel.poststestassignment.data.managers.DataManager;
import com.live.almel.poststestassignment.data.network.res.Post;
import com.live.almel.poststestassignment.data.storage.PostDTO;
import com.live.almel.poststestassignment.ui.PostsAdapter;
import com.live.almel.poststestassignment.utils.CustomSnackBar;
import com.live.almel.poststestassignment.utils.NetworkStatusChecker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<Post> mPosts;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        mDataManager = DataManager.getInstance();

        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPosts();
    }

    /**
     * load posts from server and offer to contact support on failure
     */
    private void loadPosts() {

        if (!NetworkStatusChecker.isNetworkAvailable(this)) {
            showSnackbar(getString(R.string.failure_try_later), null, null);
            return;
        }

        Call<List<Post>> call = mDataManager.getPostsList();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull final Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    loadSuccess(response.body());
                } else {
                    View.OnClickListener sendMailToSupportListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                ShareCompat.IntentBuilder.from(PostsListActivity.this)
                                        .setType("message/rfc822")
                                        .addEmailTo(getString(R.string.english_speaking_support))
                                        .setSubject(getString(R.string.failed_to_fetch_data) + response.code())
                                        .setText(getString(R.string.failed_to_fetch_data) + response.code())
                                        .setChooserTitle(getString(R.string.chooser_email))
                                        .startChooser();

                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.apps_are_not_installed_to_send_mail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    showSnackbar(getString(R.string.contact_support), getString(R.string.support), sendMailToSupportListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, Throwable t) {
                showSnackbar(getString(R.string.failure_try_later), null, null);
            }
        });
    }

    /**
     * builds item list with RecyclerView
     *
     * @param posts data (array) loaded from API
     */
    private void loadSuccess(List<Post> posts) {
        mPosts = posts;
        try {
            PostsAdapter postsAdapter = new PostsAdapter(mPosts, (position) -> {
                PostDTO postDTO = new PostDTO(mPosts.get(position));
                Intent profileIntent = new Intent(PostsListActivity.this, DetailsActivity.class);
                profileIntent.putExtra(DetailsActivity.POST_KEY, postDTO);
                startActivity(profileIntent);
            });
            mRecyclerView.setAdapter(postsAdapter);
        } catch (NullPointerException e) {
            Log.e("AdapterException", e.toString());
        }
    }

    /**
     * provides brief feedback about an operation through a message at the bottom of the screen.
     *
     * @param message
     * @param actionName
     * @param listener
     */
    private void showSnackbar(String message, String actionName, final View.OnClickListener listener) {
        View containerLayout = findViewById(R.id.container_layout);
        CustomSnackBar.showCustomSnackbar(containerLayout, message, actionName, listener);
    }
}
