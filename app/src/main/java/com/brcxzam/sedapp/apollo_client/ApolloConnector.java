package com.brcxzam.sedapp.apollo_client;

import android.content.Context;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy;
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApolloConnector {

    public static final String IP = "192.168.1.15";

    private static final String PORT = "3000";

    private static final String BASE_URL = "http://"+IP+":"+PORT+"/graphql";

    public static ApolloClient setupApollo(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build();
    }

    public static ApolloClient setupApollo(Context context) {

        final String token = new Token(context).getToken();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .addHeader("Authorization",token)
                                .method(original.method(),original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

//        // Create the ApolloSqlHelper. Please note that if null is passed in as the name, you will get an in-memory
//        // Sqlite database that will not persist across restarts of the app.
//        ApolloSqlHelper apolloSqlHelper = ApolloSqlHelper.create(context, "db_name");
//
//        // Create NormalizedCacheFactory
//        NormalizedCacheFactory cacheFactory = new SqlNormalizedCacheFactory(apolloSqlHelper);
//
//        // Create the cache key resolver, this example works well when all types have globally unique ids.
//        CacheKeyResolver resolver =  new CacheKeyResolver() {
//            @NotNull @Override
//            public CacheKey fromFieldRecordSet(@NotNull ResponseField field, @NotNull Map<String, Object> recordSet) {
//                return formatCacheKey((String) recordSet.get("id"));
//            }
//
//            @NotNull @Override
//            public CacheKey fromFieldArguments(@NotNull ResponseField field, @NotNull Operation.Variables variables) {
//                return formatCacheKey((String) field.resolveArgument("id", variables));
//            }
//
//            private CacheKey formatCacheKey(String id) {
//                if (id == null || id.isEmpty()) {
//                    return CacheKey.NO_KEY;
//                } else {
//                    return CacheKey.from(id);
//                }
//            }
//        };

        // Build the Apollo Client
        return ApolloClient.builder()
                .serverUrl(BASE_URL)
//                .normalizedCache(cacheFactory, resolver)
                .okHttpClient(okHttpClient)
                .build();
    }

}
