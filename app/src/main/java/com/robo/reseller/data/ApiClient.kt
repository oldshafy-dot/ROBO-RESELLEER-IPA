package com.robo.reseller.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object ApiClient {
    const val BASE_URL="https://deploy-straight-up.lovable.app/api/public/"
    private val client=OkHttpClient()
    private val json="application/json".toMediaType()
    private var token:String?=null
    fun init(context:Context){ token=context.getSharedPreferences("robo",0).getString("token",null) }
    fun loggedIn()=token!=null
    fun logout(context:Context){ token=null; context.getSharedPreferences("robo",0).edit().remove("token").apply() }
    private suspend fun call(path:String, method:String="GET", body:JSONObject?=null, auth:Boolean=true):JSONObject=withContext(Dispatchers.IO){
        val b=body?.toString()?.toRequestBody(json)
        val r=Request.Builder().url(BASE_URL+path).header("Accept","application/json").header("Content-Type","application/json").apply{ if(auth&&token!=null) header("Authorization","Bearer $token") }.method(method,b).build()
        client.newCall(r).execute().use { res -> val raw=res.body?.string().orEmpty(); if(!res.isSuccessful){ if(res.code==401) token=null; throw Exception(try{JSONObject(raw).optString("error",raw)}catch(_:Exception){raw.ifEmpty{"Request failed"}})}; JSONObject(raw) }
    }
    private fun user(o:JSONObject)=ApiUser(o.getString("id"),o.getString("username"),o.getString("role"),o.optDouble("balance"))
    suspend fun login(context:Context,u:String,p:String):ApiUser{ val o=call("auth/login","POST",JSONObject().put("username",u).put("password",p),false); token=o.getString("token"); context.getSharedPreferences("robo",0).edit().putString("token",token).apply(); return user(o.getJSONObject("user")) }
    suspend fun me()=user(call("me").getJSONObject("user"))
    suspend fun products():List<Product>{ val a=call("products").getJSONArray("products"); return (0 until a.length()).map{ val x=a.getJSONObject(it); val c=x.optJSONArray("categories")?:JSONArray(); Product(x.getString("id"),x.getString("name"),x.optString("description").takeIf{it.isNotEmpty()},x.optString("logo_url").takeIf{it.isNotEmpty()},x.optString("version").takeIf{it.isNotEmpty()},x.optString("download_url").takeIf{it.isNotEmpty()},(0 until c.length()).map{j->val z=c.getJSONObject(j);Category(z.getString("id"),z.getString("name"),if(z.isNull("duration_days"))null else z.optInt("duration_days"),z.optInt("price_cents"),z.optInt("available_count"))}) } }
    suspend fun keys():List<KeyRecord>{ val a=call("my-keys").getJSONArray("keys"); return (0 until a.length()).map{val x=a.getJSONObject(it);KeyRecord(x.getString("id"),x.getString("key_value"),x.getString("status"),x.optString("issued_at").takeIf{it.isNotEmpty()},x.getString("product_name"),x.getString("category_name"))} }
    suspend fun updates():List<PanelUpdate>{ val a=call("updates").getJSONArray("updates"); return (0 until a.length()).map{val x=a.getJSONObject(it);PanelUpdate(x.optString("id").takeIf{it.isNotEmpty()},x.getString("product_name"),x.getString("version"),x.getString("title"),x.optString("patch_notes").takeIf{it.isNotEmpty()},x.optString("download_url").takeIf{it.isNotEmpty()},x.optString("release_date").takeIf{it.isNotEmpty()},if(x.isNull("is_latest"))null else x.optBoolean("is_latest"))} }
    suspend fun getKey(categoryId:String)=call("get-key","POST",JSONObject().put("category_id",categoryId)).let{GetKeyResponse(it.getString("key"),it.getDouble("balance"),it.optString("transaction_id").takeIf{v->v.isNotEmpty()})}
}
