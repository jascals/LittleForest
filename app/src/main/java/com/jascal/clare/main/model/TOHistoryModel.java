package com.jascal.clare.main.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jascal.clare.Constant;
import com.jascal.clare.bean.HistoryEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author No.47 create at 2017/9/1.
 */
public class TOHistoryModel {
    private int month;
    private int day;
    private String key;
    private String v;
    private String url;
    private TOHistoryModel.OnResponse callback;
    private List<HistoryEvent> eventList = new ArrayList<>();

    public TOHistoryModel(TOHBuilder tohBuilder) {
        this.month = tohBuilder.month;
        this.day = tohBuilder.day;
        this.v = tohBuilder.v;
        this.key = tohBuilder.key;
        this.url = tohBuilder.url;
        this.callback = tohBuilder.callBack;
    }

    public void getHistoryToday() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();
        TOHistoryModel.HistoryOfToadyService accessTakenService = retrofit.create(TOHistoryModel.HistoryOfToadyService.class);
        Call<ResponseBody> call = accessTakenService.getHistoryOfToady(key, v, month, day);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                JSONObject object = null;
                try {
                    object = new JSONObject(response.body().string());
                    if (object.getInt(Constant.HISTORY_OF_TODAY_ERROR_CODE) == Constant.HISTORY_OF_TODAY_SUCCESS_CODE) {
                        eventList = gson.fromJson(
                                object.getString(Constant.HISTORY_OF_TODAY_SUCCESS_RESULT),
                                new TypeToken<List<HistoryEvent>>() {
                                }.getType());
                        callback.onSuccess(eventList);
                    } else {
                        callback.onFail(object.getString(Constant.HISTORY_OF_TODAY_REASON));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public interface HistoryOfToadyService {
        @GET(Constant.HISTORY_OF_TODAY_ACTION)
        Call<ResponseBody> getHistoryOfToady(@Query("key") String key, @Query("v") String v, @Query("month") int month, @Query("day") int day);
    }

    public interface OnResponse {
        void onSuccess(List<HistoryEvent> data);

        void onFail(String reason);
    }

    public static class TOHBuilder {
        private String key;
        private String v;
        private String url;
        private String action;
        private int month;
        private int day;
        private TOHistoryModel.OnResponse callBack;

        public TOHBuilder(int month, int day) {
            this.month = month;
            this.day = day;
        }

        public TOHBuilder setVersion(String v) {
            this.v = v;
            return this;
        }

        public TOHBuilder setKey(String key) {
            this.key = key;
            return this;
        }

        public TOHBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public TOHBuilder setAction(String action) {
            this.action = action;
            return this;
        }

        public TOHBuilder setCallback(TOHistoryModel.OnResponse callBack) {
            this.callBack = callBack;
            return this;
        }

        public TOHistoryModel build() {
            return new TOHistoryModel(this);
        }
    }
}
