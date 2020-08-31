package com.teamvoid.djevents.Network;

import retrofit2.Callback;

public class ApiRequest {
    private static ApiRequest instance;

    public static ApiRequest getInstance() {
        if (instance == null)
            instance = new ApiRequest();
        return instance;
    }

    public void sendEventNotification(String title, String body, String eventId, String topic, Callback<Integer> callback) {
        ApiClient.getClient().create(ApiInterface.class).sendEventNotification(title, body, eventId, topic).enqueue(callback);
    }
}
