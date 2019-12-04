package com.brcxzam.sedapp;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

class ApolloConnector {

    private static final String BASE_URL = "http://192.168.50.43:3001/graphql";

    public static ApolloClient setupApollo(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build();
    }

}
