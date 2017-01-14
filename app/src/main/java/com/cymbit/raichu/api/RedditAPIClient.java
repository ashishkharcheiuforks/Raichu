package com.cymbit.raichu.api;

import com.cymbit.raichu.model.ListingsResponse;

import retrofit2.Call;

public class RedditAPIClient {
    public static Call<ListingsResponse> getListings(String subreddit, String sort) {
        return RedditAPI.subreddit.getListings(subreddit, sort);
    }
}