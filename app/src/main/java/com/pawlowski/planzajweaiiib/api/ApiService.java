package com.pawlowski.planzajweaiiib.api;

import com.pawlowski.planzajweaiiib.api.LessonResponseModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("865/events")
    Single<List<LessonResponseModel>> getLessonsFromServer(@Query("start")String start, @Query("end")String end);


}