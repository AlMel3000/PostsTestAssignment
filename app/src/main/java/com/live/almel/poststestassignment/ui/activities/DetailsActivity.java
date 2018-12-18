package com.live.almel.poststestassignment.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.live.almel.poststestassignment.R;
import com.live.almel.poststestassignment.data.managers.DataManager;
import com.live.almel.poststestassignment.data.network.res.Details;
import com.live.almel.poststestassignment.data.storage.PostDTO;
import com.live.almel.poststestassignment.utils.CustomSnackBar;
import com.live.almel.poststestassignment.utils.NetworkStatusChecker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    private final static String TAG = DetailsActivity.class.getSimpleName();
    public final static String POST_KEY = "postData";

    Toolbar mToolbar;
    int mAuthorId = 0;
    String mPostTitle;
    Details mAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        initPostData();

        loadAuthors();
    }

    /**
     * receives data from postsListActivity and fills views with it
     */
    private void initPostData() {
        PostDTO postDto = getIntent().getParcelableExtra(POST_KEY);

        TextView titleTextView = findViewById(R.id.title_tv);
        mPostTitle = postDto.getPostTitle();
        titleTextView.setText(mPostTitle);

        TextView bodyTextView = findViewById(R.id.body_tv);
        bodyTextView.setText(postDto.getPostBody());

        mAuthorId = postDto.getAuthorId();
    }

    /**
     * load posts from server and offer to contact support on failure
     */
    private void loadAuthors() {

        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<List<Details>> call = DataManager.getInstance().getAuthors();
            call.enqueue(new Callback<List<Details>>() {
                @Override
                public void onResponse(@NonNull Call<List<Details>> call, @NonNull final Response<List<Details>> response) {
                    if (response.isSuccessful()) {
                        loadSuccess(response.body());
                    } else {
                        View.OnClickListener sendMailToSupportListener = v -> sendEmail(
                                getString(R.string.english_speaking_support),
                                getString(R.string.failed_to_fetch_data) + response.code(),
                                getString(R.string.failed_to_fetch_data) + response.code(),
                                getString(R.string.chooser_email));
                        showSnackbar(getString(R.string.contact_support), getString(R.string.support), sendMailToSupportListener);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Details>> call, Throwable t) {
                    showSnackbar(getString(R.string.failure_try_later), null, null);
                }
            });
        } else {
            showSnackbar(getString(R.string.failure_try_later), null, null);
        }
    }

    /**
     * @param authors
     */
    private void loadSuccess(List<Details> authors) {
        if (authors == null) {
            Log.w(TAG, "loadSuccess: authors is null");
            return;
        }

        for (Details author : authors) {
            if (author.getId().equals(mAuthorId)) {
                setupSpecificAuthor(author);
                return;
            }
        }
    }

    private void setupSpecificAuthor(Details author) {
        mToolbar.setTitle(author.getName());
        mAuthor = author;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_posts_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.action_email:
                sendEmail(
                        mAuthor.getEmail(),
                        getString(R.string.from_post_app),
                        getString(R.string.dear) + mAuthor.getName() + getString(R.string.email_purpose_description) + mPostTitle,
                        getString(R.string.chooser_email));
                break;

            case R.id.action_maps:
                String latitude = mAuthor.getAddress().getGeo().getLat();
                String longitude = mAuthor.getAddress().getGeo().getLng();
                String label = mAuthor.getName();
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                try {
                    startActivity(geoIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this,
                            getString(R.string.maps_not_installed),
                            Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * provides brief feedback about an operation through a message at the bottom of the screen.
     *
     * @param message
     * @param actionName
     * @param listener
     */
    private void showSnackbar(String message, String actionName, final View.OnClickListener listener) {
        View containerLayout = findViewById(R.id.details_ll);
        CustomSnackBar.showCustomSnackbar(containerLayout, message, actionName, listener);
    }


    /**
     * opens email app with filled email, subject and part of body
     *
     * @param email
     * @param subject
     * @param text
     * @param chooserTitle
     */
    private void sendEmail(String email, String subject, String text, String chooserTitle) {
        try {
            ShareCompat.IntentBuilder.from(DetailsActivity.this)
                    .setType("message/rfc822")
                    .addEmailTo(email)
                    .setSubject(subject)
                    .setText(text)
                    .setChooserTitle(chooserTitle)
                    .startChooser();

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    R.string.apps_are_not_installed_to_send_mail,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
