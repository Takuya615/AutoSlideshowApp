package jp.techacademy.tsumura.autoslideshowapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(){
    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer?=null

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }

        play_button.setOnClickListener {
            if (mTimer == null) {
                play_button.text="停止"
                next_button.text=""
                back_button.text=""

                // タイマーの作成
                mTimer = Timer()

                // タイマーの始動
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            if(cursor.moveToNext()){
                                val fieldIndex6 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id6 = cursor.getLong(fieldIndex6)
                                val imageUri6 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id6)

                                imageView.setImageURI(imageUri6)
                            }else if(cursor.moveToFirst()){
                                val fieldIndex7 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id7 = cursor.getLong(fieldIndex7)
                                val imageUri7 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id7)

                                imageView.setImageURI(imageUri7)
                            }
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定

            }else if(mTimer!=null){
                play_button.text="再生"
                next_button.text="次へ"
                back_button.text="戻る"
                mTimer!!.cancel()
                mTimer=null
            }

        }
        next_button.setOnClickListener {
            if(mTimer==null){
                if (cursor.moveToNext()) {
                    val fieldIndex2 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id2 = cursor.getLong(fieldIndex2)
                    val imageUri2 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id2)
                    imageView.setImageURI(imageUri2)

                }else if (cursor.moveToFirst()) {
                    val fieldIndex3 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id3 = cursor.getLong(fieldIndex3)
                    val imageUri3 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id3)
                    imageView.setImageURI(imageUri3)
                }
            }
        }

        back_button.setOnClickListener{
            if (mTimer==null){
                if (cursor.moveToPrevious()){
                    val fieldIndex4 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id4 = cursor.getLong(fieldIndex4)
                    val imageUri4 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id4)
                    imageView.setImageURI(imageUri4)
                } else if (cursor.moveToLast()) {
                    val fieldIndex5 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id5 = cursor.getLong(fieldIndex5)
                    val imageUri5 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id5)
                    imageView.setImageURI(imageUri5)
                }
            }

        }
    }
}