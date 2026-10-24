package com.psd2png.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.documentfile.provider.DocumentFile
import java.util.Locale

class MainActivity : Activity() {
 private lateinit var status: TextView
 private lateinit var list: LinearLayout
 private var root: DocumentFile? = null
 private val files = mutableListOf<DocumentFile>()
 private val pickRoot = 101
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); window.statusBarColor=Color.rgb(17,25,40); window.navigationBarColor=Color.rgb(17,25,40); buildUi() }
 private fun buildUi() {
  val scroll=ScrollView(this); val page=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(24,32,24,24); setBackgroundColor(Color.rgb(17,25,40)) }; scroll.addView(page); setContentView(scroll)
  page.addView(TextView(this).apply { text="PSD2PNG"; textSize=30f; setTextColor(Color.WHITE); gravity=Gravity.CENTER; typeface=android.graphics.Typeface.DEFAULT_BOLD })
  page.addView(TextView(this).apply { text="تبدیل گروهی فایل‌های PSD به PNG"; textSize=15f; setTextColor(Color.rgb(177,192,215)); gravity=Gravity.CENTER; setPadding(0,8,0,24) })
  val choose=Button(this).apply { text="انتخاب پوشه فلش یا حافظه"; setOnClickListener { val i=Intent(Intent.ACTION_OPEN_DOCUMENT_TREE); startActivityForResult(i,pickRoot) } }; page.addView(choose, match())
  status=TextView(this).apply { text="ابتدا پوشه اصلی را انتخاب کن."; textSize=15f; setTextColor(Color.WHITE); setPadding(4,20,4,16) }; page.addView(status)
  val scan=Button(this).apply { text="جست‌وجوی فایل‌های PSD"; setOnClickListener { scanFiles() } }; page.addView(scan,match())
  list=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(0,12,0,12) }; page.addView(list)
  page.addView(TextView(this).apply { text="فایل‌های اصلی تغییری نمی‌کنند. انتخاب پوشه مقصد و تبدیل PNG در نسخه بعدی تکمیل می‌شود."; textSize=12f; setTextColor(Color.rgb(177,192,215)); setPadding(0,16,0,0) })
 }
 private fun match()=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin=10 }
 override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) { super.onActivityResult(requestCode,resultCode,data); if(requestCode==pickRoot && resultCode==RESULT_OK) { val uri=data?.data ?: return; contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION); root=DocumentFile.fromTreeUri(this,uri); status.text="پوشه انتخاب شد؛ برای یافتن فایل‌ها جست‌وجو را بزن." } }
 private fun scanFiles() { val r=root ?: run { status.text="اول پوشه ورودی را انتخاب کن."; return }; files.clear(); list.removeAllViews(); status.text="در حال جست‌وجوی زیرپوشه‌ها…"; Thread { fun walk(dir:DocumentFile) { dir.listFiles().forEach { f -> if(f.isDirectory) walk(f) else if(f.name?.lowercase(Locale.ROOT)?.endsWith(".psd")==true) files.add(f) } }; try { walk(r); runOnUiThread { status.text="${files.size} فایل PSD پیدا شد."; files.forEach { f -> list.addView(TextView(this).apply { text="• ${f.name}"; setTextColor(Color.WHITE); textSize=13f; setPadding(8,8,8,8) }) }; if(files.isEmpty()) Toast.makeText(this,"فایلی پیدا نشد",Toast.LENGTH_SHORT).show() } } catch(e:Exception) { runOnUiThread { status.text="خطا در خواندن پوشه: ${e.message}" } } }.start() }
}
