package com.live.almel.poststestassignment.data.storage;

import android.os.Parcel;
import android.os.Parcelable;

import com.live.almel.poststestassignment.data.network.res.Post;


/**
 * data transfer object
 */
public class PostDTO implements Parcelable {

    public static final Creator<PostDTO> CREATOR = new Creator<PostDTO>() {
        @Override
        public PostDTO createFromParcel(Parcel in) {
            return new PostDTO(in);
        }

        @Override
        public PostDTO[] newArray(int size) {
            return new PostDTO[size];
        }
    };
    private String mPostTitle;
    private String mPostBody;
    private int mAuthorId;

    public PostDTO(Post postData) {
        mAuthorId = postData.getUserId();
        mPostTitle = postData.getTitle();
        mPostBody = postData.getBody();
    }

    private PostDTO(Parcel in) {
        mAuthorId = in.readInt();
        mPostTitle = in.readString();
        mPostBody = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAuthorId);
        dest.writeString(mPostTitle);
        dest.writeString(mPostBody);
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public String getPostTitle() {
        return mPostTitle;
    }

    public String getPostBody() {
        return mPostBody;
    }

}