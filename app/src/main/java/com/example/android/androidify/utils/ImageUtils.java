package com.example.android.androidify.utils;

import android.util.Log;
import android.widget.ImageView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.view.CircleImageView;
import com.example.android.androidify.view.SquareImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.DrawableRes;

public class ImageUtils {
    private static final String TAG = "IMAGE_UTILS";

    public static final String SMALL = "small";
    public static final String MEDIUM = "medium";
    public static final String LARGE = "large";


    public static void displayImage(ImageView imageView, List<Image> images) {
        if (images != null && images.size() > 0) {
            String imageUrl = images.get(0).url;
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.color.imageLoadingColor)
                    .into(imageView);
        }
    }

    public static void displayImage(ImageView circleImageView, List<Image> images, @DrawableRes int placeholderId) {
        String imageUrl = getImageUrl(images, LARGE);
        Log.i(TAG, "url = " + imageUrl);
        Log.i(TAG, "placeholder = " + placeholderId);
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(placeholderId)
                    .into(circleImageView);
        } else {
            Picasso.get()
                    .load(placeholderId)
                    .fit()
                    .placeholder(R.drawable.ic_audio)
                    .into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    });
        }
    }

    public static void displayImage(CircleImageView circleImageView, String imageUrl) {
        if (circleImageView != null && imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.imageLoadingColor)
                    .into(circleImageView);
        }
    }

    public static void displayImage(SquareImageView squareImageView, String imageUrl) {
        if (squareImageView!= null && imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .placeholder(R.color.imageLoadingColor)
                    .into(squareImageView);

/*            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.color.imageLoadingColor)
                    .into(squareImageView);*/
        }
    }

    public static void displayImage(ImageView imageView, String imageUrl) {
        if (imageView != null && imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .placeholder(R.color.imageLoadingColor)
                    .into(imageView);
        }
    }

    public static void displayMusicListItemImage(ImageView imageView, String imageUrl) {
        if (imageView != null && imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .resize(64, 64)
                    .placeholder(R.color.imageLoadingColor)
                    .into(imageView);
        }
    }

    public static String getImageUrl(List<Image> images, String size) {
        if (images != null && images.size() > 0) {
            switch (size) {
                case SMALL:
                    return images.get(images.size() - 1).url;
                case MEDIUM:
                    return images.size() > 1 ? images.get(1).url : images.get(0).url;
                case LARGE:
                    Log.i(TAG, "width = " + images.get(0).width);
                    return images.get(0).url;
                default:
                    return images.get(0).url;
            }
        } else {
            return null;
        }
    }



/*    public static void displayImage(ImageView imageView, List<Image> images) {
        if (images != null && images.size() > 0) {
            String imageUrl = images.get(0).url;
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.color.imageLoadingColor)
                    .into(imageView);
        }
    }*/
}
