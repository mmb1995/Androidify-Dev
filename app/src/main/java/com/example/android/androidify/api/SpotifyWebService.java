package com.example.android.androidify.api;

import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.api.models.AlbumWrapper;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.ArtistCursorPager;
import com.example.android.androidify.api.models.ArtistTrackWrapper;
import com.example.android.androidify.api.models.ArtistsPager;
import com.example.android.androidify.api.models.Pager;
import com.example.android.androidify.api.models.Playlist;
import com.example.android.androidify.api.models.PlaylistSimple;
import com.example.android.androidify.api.models.PlaylistTrack;
import com.example.android.androidify.api.models.PublicUser;
import com.example.android.androidify.api.models.Recommendations;
import com.example.android.androidify.api.models.RelatedArtistsWrapper;
import com.example.android.androidify.api.models.SearchResultsPager;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.api.models.TrackWrapper;
import com.example.android.androidify.api.models.TracksPager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

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

    @GET("me/player/recently-played?limit=30")
    Call<Pager<TrackWrapper>> getRecentlyPlayed();

    @GET("tracks/{id}")
    Call<Track> getTrack(@Path("id") String id);

    @GET("me/tracks/contains")
    Call<Boolean[]> containsTrack(@Query("ids") String ids);

    /** Search endpoints **/

    @GET("search?type=artist&limit=10")
    Call<ArtistsPager> searchForArtists(@Query("q") String query);

    @GET("search?type=track&limit=10")
    Call<TracksPager> searchForTracks(@Query("q") String query);

    @GET("search?type=track,artist,album&limit=10")
    Call<SearchResultsPager> getSearchResults(@Query("q") String query);

    @GET("search?limit=30")
    Call<SearchResultsPager> getSearchResultsByType(@Query("q") String query, @Query("type") String type);

    /** Album endpoints **/

    @GET("me/albums/contains")
    Call<Boolean[]> containsAlbum(@Query("ids") String ids);

    @GET("albums/{id}?market=from_token")
    Call<Album> getAlbum(@Path("id") String id);

    @GET("albums/{id}/tracks")
    Call<Pager<Track>> getAlbumTracks(@Path("id") String id);

    /** Library endpoints **/

    @GET
    Call<Pager<TrackWrapper>> getSavedTracks(@Url String url);

    @GET("me/tracks")
    Call<Pager<TrackWrapper>> getSavedTracks(@Query("offset") long offset, @Query("limit") int limit);

    @GET("me/albums")
    Call<Pager<AlbumWrapper>> getSavedAlbums(@Query("offset") long offset, @Query("limit") int limit);

    @GET
    Call<Pager<AlbumWrapper>> getSavedAlbums(@Url String url);

    @GET("me/following?type=artist")
    Call<ArtistCursorPager> getFollowedArtists(@Query("limit") int limit);

    @GET
    Call<ArtistCursorPager> getFollowedArtists(@Url String url);

    /********
     * User *
     ********/

    @GET("me")
    Call<PublicUser> getUserProfile();


    /** PlaylistBase endpoints **/

    @GET("playlists/{id}")
    Call<Playlist> getPlaylist(@Path("id") String id);

    @GET
    Call<Pager<PlaylistSimple>> getUserPlaylists(@Url String url);

    @GET("me/playlists")
    Call<Pager<PlaylistSimple>> getUserPlaylists(@Query("offset") long offset, @Query("limit") int limit);

    @GET("playlists/{id}/tracks")
    Call<Pager<PlaylistTrack>> getPlaylistTracks(@Path("id") String id, @Query("limit") int limit);

    @GET
    Call<Pager<PlaylistTrack>> getAdditionalPlaylistTracks(@Url String url);

    @GET("playlists/{id}/followers/contains")
    Call<Boolean[]> areFollowingPlaylist(@Path("id") String playlistId, @Query("ids") String ids);

    @Headers("Content-Type: application/json")
    @PUT("playlists/{id}/followers")
    Call<Void> followPlaylist(@Path("id") String id);

    @DELETE("playlists/{id}/followers")
    Call<Void> unfollowPlaylist(@Path("id") String id);

    /** Recommendations endpoints **/

    @GET("recommendations")
    Call<Recommendations> getRecommendations(@QueryMap Map<String, Object> options);

    /** Put requests **/

    @PUT("me/albums")
    Call<Void> saveAlbum(@Query("ids") String ids);

    @PUT("me/following?type=artist")
    Call<Void> followArtist(@Query("ids") String ids);

    @PUT("me/tracks")
    Call<Void> saveTracks(@Query("ids") String ids);

    /** Delete requests **/

    @DELETE("me/albums")
    Call<Void> removeAlbum(@Query("ids") String ids);

    @DELETE("me/tracks")
    Call<Void> removeTracks(@Query("ids") String ids);

    @DELETE("me/following?type=artist")
    Call<Void> unfollowArtist(@Query("ids") String ids);

}
