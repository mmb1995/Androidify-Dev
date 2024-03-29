package com.example.android.androidify.api;

import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.ArtistTrackWrapper;
import com.example.android.androidify.api.models.ArtistsPager;
import com.example.android.androidify.api.models.Pager;
import com.example.android.androidify.api.models.RelatedArtistsWrapper;
import com.example.android.androidify.api.models.SearchResultsPager;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.api.models.TrackWrapper;
import com.example.android.androidify.api.models.TracksPager;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpotifyWebService {
    @GET("artists/{id}")
    Call<Artist> getArtist(@Path("id") String artistId);

    @GET("artists/{id}/top-tracks?country=from_token")
    Call<ArtistTrackWrapper> getArtistTopTracks(@Path("id") String artistId);

    @GET("artists/{id}/related-artists")
    Call<RelatedArtistsWrapper> getRelatedArtists(@Path("id") String artistId);

    @GET("artists/{id}/albums?include_groups=album")
    Call<Pager<Album>> getArtistAlbums(@Path("id") String artistId);

    @GET("me/following/contains?type=artist")
    Call<Boolean[]> isFollowingArtist(@Query("ids") String ids);

    @GET("me/top/artists")
    Call<Pager<Artist>> getTopArtists(@Query("time_range") String range);

    @GET("me/top/artists")
    Call<Pager<Artist>> getTopArtists();

    @GET("me/top/tracks")
    Call<Pager<Track>> getTopTracks();

    @GET("me/top/tracks")
    Call<Pager<Track>> getTopTracks(@Query("time_range") String range);

    @GET("me/player/recently-played")
    Call<Pager<TrackWrapper>> getRecentlyPlayed();

    @GET("me/tracks")
    Call<Pager<TrackWrapper>> getSavedTracks();

    @GET("me/tracks/contains")
    Call<Boolean[]> containsTrack(@Query("ids") String ids);

    @GET("search?type=artist&limit=10")
    Call<ArtistsPager> searchForArtists(@Query("q") String query);

    @GET("search?type=track&limit=10")
    Call<TracksPager> searchForTracks(@Query("q") String query);

    @GET("search?type=track,artist&limit=10")
    Call<SearchResultsPager> getSearchResults(@Query("q") String query);

    @GET("albums/{id}?market=from_token")
    Call<Album> getAlbum(@Path("id") String id);

    @GET("albums/{id}/tracks")
    Call<Pager<Track>> getAlbumTracks(@Path("id") String id);

    /** Put requests **/

    @PUT("me/following?type=artist")
    Call<Void> followArtist(@Query("ids") String ids);

    @PUT("me/tracks")
    Call<Void> saveTracks(@Query("ids") String ids);

    @DELETE("me/tracks")
    Call<Void> removeTracks(@Query("ids") String ids);

    @DELETE("me/following?type=artist")
    Call<Void> unfollowArtist(@Query("ids") String ids);

}
