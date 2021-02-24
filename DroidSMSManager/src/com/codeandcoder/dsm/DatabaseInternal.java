package com.codeandcoder.dsm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseInternal {

	private String HISTORY_TABLE = "History";
	private String KEY_HTITLE = "h_title";
	private String KEY_HSUB_TITLE = "h_sub_title";
	private String KEY_HPOSITION_ID = "h_positin_id";

	private String LABLE_TABLE_MESSAGE = "favourite";
	private String KEY_LID = "f_id";
	private String KEY_LABLE = "f_title";
	private String KEY_L_MESSAGE = "f_sub_title";
	private String KEY_L_DATE = "f_positin_id";

	private String HIGHLIGHT_TABLE = "highlight";
	private String KEY_HI_ID = "hi_id";
	private String KEY_HI_TITLE = "hi_title";
	private String KEY_HI_SUB_TITLE = "hi_sub_title";
	private String KEY_HI_POSITION_ID = "hi_positin_id";

	private String MY_NOTE_TABLE = "my_note";
	private String KEY_MY_ID = "my_id";
	private String KEY_MY_TITLE = "my_title";
	private String KEY_MY_SUB_TITLE = "my_sub_title";
	private String KEY_MY_POSITION_ID = "my_positin_id";
	private String KEY_MY_TEXT = "my_text";
	private String KEY_MY_TIME = "my_time";

	private String MY_SMS_OWN_TABLE = "own_data";

	private String MY_IMAGES = "images_data";

	private String DatabaseName = "OWBN1234.db";
	private int DatabaseVersion = 4;

	private DBHelper ourhelper;
	private final Context ourcontext;
	private SQLiteDatabase ourdatabase;

	/*
	 * private String
	 * CREATE_TABLE_HISTORY="CREATE TABLE "+HISTORY_TABLE+" ("+KEY_HTITLE
	 * +" VARCHAR  NOT NULL, "
	 * +KEY_HSUB_TITLE+" TEXT NOT NULL, "+KEY_HPOSITION_ID
	 * +" VARCHAR NOT NULL, primary key("+KEY_HSUB_TITLE+","+KEY_HTITLE+") );";
	 * private String
	 * CREATE_TABLE_HIGHLIGHT="CREATE TABLE "+HIGHLIGHT_TABLE+" ("
	 * +KEY_HI_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
	 * +KEY_HI_TITLE+" VARCHAR NOT NULL, "
	 * +KEY_HI_SUB_TITLE+" TEXT NOT NULL, "+KEY_HI_POSITION_ID
	 * +" TEXT NOT NULL );";
	 * 
	 * private String
	 * CREATE_TABLE_MYNOTE="CREATE TABLE "+MY_NOTE_TABLE+"("+KEY_MY_ID
	 * +" INTEGER PRIMARY KEY AUTOINCREMENT, "
	 * +KEY_MY_TITLE+" VARCHAR NOT NULL, "
	 * +KEY_MY_SUB_TITLE+" VARCHAR NOT NULL, "
	 * +KEY_MY_TEXT+" TEXT NOT NULL, "+KEY_MY_POSITION_ID
	 * +" TEXT NOT NULL, "+KEY_MY_TIME
	 * +" TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')) );";
	 */
	private String CREATE_TABLE_LABEL = "CREATE TABLE "
			+ LABLE_TABLE_MESSAGE
			+ " ("
			+ KEY_LID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, sender varchar not null, sender_number varchar not null, "
			+ KEY_LABLE + " VARCHAR NOT NULL, " + KEY_L_MESSAGE
			+ " TEXT NOT NULL, " + KEY_L_DATE + " int NOT NULL );";
	private String CREATE_IMAGE_DATA = "CREATE TABLE " + MY_IMAGES
			+ "( id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name" + " TEXT,"
			+ "imagedata" + " BLOB," + KEY_MY_TIME
			+ " TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')) );";;
	private String CREATE_OWN_DATABASE = "create table "
			+ MY_SMS_OWN_TABLE
			+ " (_id integer primary key , address text not null, body text not null, date integer not null);";

	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DatabaseName, null, DatabaseVersion);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			/*
			 * db.execSQL(CREATE_TABLE_HISTORY);
			 * db.execSQL(CREATE_TABLE_HIGHLIGHT);
			 * db.execSQL(CREATE_TABLE_FAVOURITE);
			 */
			db
					.execSQL("create table trash (id integer primary key autoincrement, message text not null, sender text not null, date integer not null, senderDetail text not null )");
			db
					.execSQL("create table label (id integer primary key autoincrement, label_name varchar not null , created_date TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')) )");
			db.execSQL(CREATE_TABLE_LABEL);
			db.execSQL(CREATE_IMAGE_DATA);
			db.execSQL(CREATE_OWN_DATABASE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			/*
			 * db.execSQL("DROP TABLE IF EXISTS "+HISTORY_TABLE);
			 * db.execSQL("DROP TABLE IF EXISTS "+FAVOURITE_TABLE);
			 * db.execSQL("DROP TABLE IF EXISTS "+HIGHLIGHT_TABLE);
			 */
			db.execSQL("drop table if exists trash");
			db.execSQL("drop table if exists label");
			db.execSQL("DROP TABLE IF EXISTS " + LABLE_TABLE_MESSAGE);
			db.execSQL("DROP TABLE IF EXISTS " + CREATE_IMAGE_DATA);
			db.execSQL("DROP TABLE IF EXISTS" + CREATE_OWN_DATABASE);
			onCreate(db);
		}

	}

	public DatabaseInternal(Context c) {
		ourcontext = c;
	}

	public DatabaseInternal open() {
		ourhelper = new DBHelper(ourcontext);
		ourdatabase = ourhelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourhelper.close();
	}

	public long CreatTrash(String messge, String sender, String date,
			String senderDetail) {
		ContentValues cv = new ContentValues();
		cv.put("message", messge);
		cv.put("sender", sender);
		cv.put("date", date);
		cv.put("senderDetail", senderDetail);
		return ourdatabase.insert("trash", null, cv);
	}

	public long CreatImage(String name, byte[] image) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("imagedata", image);
		return ourdatabase.insert(MY_IMAGES, null, cv);
	}

	public long CreatLableMessage(String label, String message, String date,
			String sender, String sender_number) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_LABLE, label);
		cv.put(KEY_L_MESSAGE, message);
		cv.put(KEY_L_DATE, date);
		cv.put("sender", sender);
		cv.put("sender_number", sender_number);

		return ourdatabase.insert(LABLE_TABLE_MESSAGE, null, cv);
	}

	public long CreateStore(String address, String body, String date) {
		ContentValues cv = new ContentValues();
		cv.put("address", address);
		cv.put("body", body);
		cv.put("date", date);
		return ourdatabase.insert(MY_SMS_OWN_TABLE, null, cv);
	}

	public Cursor SendData(String match) {
		String sql = "SELECT * FROM own_data where address like '%" + match
				+ "%'  or body like '%" + match + "%';";
		Cursor c = ourdatabase.rawQuery(sql, null);
		return c;
	}

	public long CreateLabel(String Label) {
		ContentValues cv = new ContentValues();
		cv.put("label_name", Label);
		return ourdatabase.insert("label", null, cv);
	}

	public Cursor getLabelMessage(String label, String match) {
		String sql = "select f_id as _id,f_title as ft,sender_number as sn , f_sub_title as st ,sender as ad , f_positin_id as date  from favourite where f_title = '"
				+ label
				+ "' and ( ad like '%"
				+ match
				+ "%' or st like '%"
				+ match + "%' );";
		Cursor c = ourdatabase.rawQuery(sql, null);
		return c;
	}

	public Cursor getTrash() {
		String sql = "select * from trash;";
		Cursor c = ourdatabase.rawQuery(sql, null);
		return c;
	}

	public void deleteTrash(String id) {
		ourdatabase.delete("trash", "id = " + id, null);
	}

	public Cursor getPosition(String date) {
		Cursor c = ourdatabase.query(LABLE_TABLE_MESSAGE, new String[] {
				KEY_LID + " as id", KEY_LABLE + " as label" }, KEY_L_DATE
				+ " = '" + date + "'", null, null, null, null);

		return c;
	}

	public void DeleteFromlabel(String id) {
		ourdatabase.delete(LABLE_TABLE_MESSAGE, KEY_LID + " = " + id, null);
	}

	public void Update(String label, String id) {

		ContentValues cv = new ContentValues();
		cv.put(KEY_LABLE, label);
		ourdatabase.update(LABLE_TABLE_MESSAGE, cv,
				KEY_LID + " = '" + id + "'", null);

	}

	public Cursor getLabel() {
		Cursor c = ourdatabase.query("label", new String[] { "label_name" },
				null, null, null, null, "label_name asc");
		return c;
	}

	public void DeleteLabel(String label) {
		ourdatabase.delete("label", "label_name = '" + label + "'", null);
	}

	public Cursor getDetailImage() {
		String[] whereClause = new String[] { "id", "name", "imagedata",
				"date(my_time) as date" };
		Cursor cursor = ourdatabase.query(MY_IMAGES, whereClause, null, null,
				null, null, null);
		return cursor;
	}

}
