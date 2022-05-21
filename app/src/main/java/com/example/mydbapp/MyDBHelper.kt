package com.example.mydbapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView

class MyDBHelper(val context: Context) : SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION) {
    companion object{
        val DB_NAME = "mydb.db"
        val DB_VERSION = 1
        val TABLE_NAME = "products"
        val PID = "pid"
        val PNAME = "pname"
        val PQUANTITY = "pquantity"
    }

    fun getAllRecord(){
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor= db.rawQuery(strsql,null)
        showRecord(cursor)
        cursor.close()
        db.close()
    }

    private fun showRecord(cursor: Cursor) {
        cursor.moveToFirst() // 처음위치로
        val attrcount = cursor.columnCount //질의에 따라 컬럼 갯수가 다름
        val activity =context as MainActivity
        activity.binding.tableLayout.removeAllViewsInLayout() //전체를 지움
        //타이틀 만들기
        val tablerow = TableRow(activity)
        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT)
        tablerow.layoutParams = rowParam
        val viewParam = TableRow.LayoutParams(0,100,1f)
        for(i in 0 until attrcount){
            val textView = TextView(activity)
            textView.layoutParams = viewParam
            textView.text = cursor.getColumnName(i)
            textView.setBackgroundColor(Color.LTGRAY)
            textView.textSize = 15.0f
            textView.gravity = Gravity.CENTER
            tablerow.addView(textView)
        }
        activity.binding.tableLayout.addView(tablerow)
        if(cursor.count ==0) return
        //레코드 추가하기
        do{
            val row = TableRow(activity)
            row.layoutParams = rowParam
            row.setOnClickListener{
                for(i in 0 until attrcount){
                    val textView = row.getChildAt(i) as TextView//한줄에 있는 child 객체를 가져옴
                    when(textView.tag){
                        0-> activity.binding.pIdEdit.setText(textView.text)
                        1-> activity.binding.pNameEdit.setText(textView.text)
                        2-> activity.binding.pQuantityEdit.setText(textView.text)
                    }
                }
            }
            for(i in 0 until attrcount){
                val textView = TextView(activity)
                textView.tag = i
                textView.layoutParams = viewParam
                textView.text = cursor.getString(i)
                textView.textSize = 13.0f
                textView.gravity = Gravity.CENTER
                row.addView(textView)
            }
            activity.binding.tableLayout.addView(row) // 한 사이클이 돌면 하나의 레코드를 읽어서 추가시켜줌
        }while (cursor.moveToNext())
    }

    fun insertProduct(product: Product):Boolean{
        val values = ContentValues()
        values.put(PNAME, product.pName)
        values.put(PQUANTITY, product.pQuantity)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME("+
                "$PID integer primary key autoincrement, " + //자동으로 값이 증가
                "$PNAME text, "+
                "$PQUANTITY integer);"
        db!!.execSQL(create_table) //db가 null아니면 생성
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val drob_table ="drop table if exists $TABLE_NAME;"
        db!!.execSQL(drob_table)
        onCreate(db)
    }

    //select * from product where name = 'pid';
    fun findProduct(name: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $PNAME='$name';"
        val db = readableDatabase
        val cursor= db.rawQuery(strsql,null)
        val flag = cursor.count != 0
        showRecord(cursor)
        cursor.close()
        db.close()
        return flag
    }
    //select * from product where pid = 'pid';
    fun deleteProduct(pid: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $PID='$pid';"
        val db = writableDatabase
        val cursor= db.rawQuery(strsql,null) //있는지 찾는작업
        val flag = cursor.count != 0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME,"$PID=?",arrayOf(pid))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun updateProduct(product: Product): Boolean {
        val pid = product.pId
        val strsql = "select * from$TABLE_NAME where $PID='$pid';"
        val db = writableDatabase
        val cursor= db.rawQuery(strsql,null) //있는지 찾는작업
        val flag = cursor.count != 0
        if(flag){
            cursor.moveToFirst()
            val values = ContentValues()
            values.put(PNAME, product.pName)
            values.put(PQUANTITY, product.pQuantity)
            db.update(TABLE_NAME, values, "$PID=?",arrayOf(pid.toString())) //pid값이 이값이 경우 업데이트 하겠다.
        }
        cursor.close()
        db.close()
        return flag
    }

    //select * from product where pname like '김%';
    fun findProduct2(name: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $PNAME like'$name%';"
        val db = readableDatabase
        val cursor= db.rawQuery(strsql,null)
        val flag = cursor.count != 0
        showRecord(cursor)
        cursor.close()
        db.close()
        return flag
    }
}