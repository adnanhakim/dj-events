package com.teamvoid.djevents.Network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("notification/events")
    Call<Integer> sendEventNotification(@Field("title") String title,
                                        @Field("body") String body,
                                        @Field("eventId") String eventId,
                                        @Field("topic") String topic);
}
