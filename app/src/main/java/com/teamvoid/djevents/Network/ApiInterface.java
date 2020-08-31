package com.teamvoid.djevents.Network;

import com.teamvoid.djevents.Models.NotificationResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("notifications/events")
    Call<NotificationResponse> sendEventNotification(@Field("title") String title,
                                                     @Field("body") String body,
                                                     @Field("eventId") String eventId,
                                                     @Field("topic") String topic);

    @FormUrlEncoded
    @POST("notifications/posts")
    Call<NotificationResponse> sendPostNotification(@Field("title") String title,
                                                    @Field("body") String body,
                                                    @Field("postId") String postId,
                                                    @Field("topic") String topic);
}
