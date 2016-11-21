//package com.lianyao.ftf.sdk.inter;
//
//import org.json.JSONObject;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//import retrofit2.http.Path;
//import retrofit2.http.Query;
//
//public interface UserHttpService {
//	@POST("user")
//	Call<JSONObject> register(@Query("ticket") String ticket,
//			@Body JSONObject json);
//
//	@GET("/user/{userid}")
//	Call<JSONObject> information(@Path("userid") String userid,
//			@Query("token") String token);
//
//	@POST("user")
//	Call<JSONObject> refresh(@Query("ticket") String ticket,
//			@Body JSONObject json);
// }
