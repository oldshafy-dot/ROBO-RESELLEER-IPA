package com.robo.reseller.data

data class ApiUser(val id:String,val username:String,val role:String,val balance:Double)
data class LoginResponse(val token:String,val user:ApiUser)
data class MeResponse(val user:ApiUser)
data class Category(val id:String,val name:String,val duration_days:Int?,val price_cents:Int,val available_count:Int)
data class Product(val id:String,val name:String,val description:String?,val logo_url:String?,val version:String?,val download_url:String?,val categories:List<Category>)
data class ProductsResponse(val products:List<Product>)
data class KeyRecord(val id:String,val key_value:String,val status:String,val issued_at:String?,val product_name:String,val category_name:String)
data class KeysResponse(val keys:List<KeyRecord>)
data class PanelUpdate(val id:String?,val product_name:String,val version:String,val title:String,val patch_notes:String?,val download_url:String?,val release_date:String?,val is_latest:Boolean?)
data class UpdatesResponse(val updates:List<PanelUpdate>)
data class GetKeyResponse(val key:String,val balance:Double,val transaction_id:String?)
