package com.example.mydbapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.mydbapp.databinding.ActivityMainBinding
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var myDBHelper: MyDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDB()
        init()
        getALLRecord()
    }

    private fun initDB() {
        val dbfile =getDatabasePath("mydb.db")
        if(!dbfile.parentFile.exists()){
            dbfile.parentFile.mkdir() //디렉토리 만듦
        }
        if(!dbfile.exists()){
            val file = resources.openRawResource(R.raw.mydb)
            val fileSize = file.available()
            val buffer = ByteArray(fileSize)
            file.read(buffer)
            file.close()
            dbfile.createNewFile()
            val output =FileOutputStream(dbfile)
            output.write(buffer)
            output.close()
        }
    }

    fun getALLRecord(){
        myDBHelper.getAllRecord()
    }
    fun clearEditText(){
        binding.apply {
            pIdEdit.text.clear()
            pNameEdit.text.clear()
            pQuantityEdit.text.clear()
        }
    }
    private fun init(){
        myDBHelper = MyDBHelper(this)
        binding.apply{
            testsql.addTextChangedListener{
                val pname = it.toString()
                val result = myDBHelper.findProduct2(pname)
            }
            insertbtn.setOnClickListener{
                val name =pNameEdit.text.toString()
                val quantity = pQuantityEdit.text.toString().toInt()
                val product = Product(0, name,quantity)
                val result = myDBHelper.insertProduct(product)
                if(result){
                    getALLRecord()
                    Toast.makeText(this@MainActivity,"Data INSERT SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity,"Data INSERT FAILED", Toast.LENGTH_SHORT).show()
                }
                clearEditText()
            }
            findbtn.setOnClickListener{
                val name =pNameEdit.text.toString()
                val result = myDBHelper.findProduct(name)
                if(result){
                    Toast.makeText(this@MainActivity,"RECORD FOUND", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity,"NO MATCH FOUND", Toast.LENGTH_SHORT).show()
                }
            }
            deletebtn.setOnClickListener{
                val pid =pIdEdit.text.toString()
                val result = myDBHelper.deleteProduct(pid)
                if(result){
                    Toast.makeText(this@MainActivity,"Data DELETE SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity,"Data DELETE FAILED", Toast.LENGTH_SHORT).show()
                }
                getALLRecord()
                clearEditText()
            }
            updatebtn.setOnClickListener{
                val pid = pIdEdit.text.toString().toInt()
                val name =pNameEdit.text.toString()
                val quantity = pQuantityEdit.text.toString().toInt()
                val product = Product(pid, name,quantity)
                val result = myDBHelper.updateProduct(product)
                if(result){
                    getALLRecord()
                    Toast.makeText(this@MainActivity,"Data INSERT SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity,"Data INSERT FAILED", Toast.LENGTH_SHORT).show()
                }
                clearEditText()
            }
        }
    }
}