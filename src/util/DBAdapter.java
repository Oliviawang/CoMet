package util;

import java.util.ArrayList;

import phpAPI.uploadOnlineScore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
private final static String CreateTalk = "create table talk ("+
	"id text primary key not null, "+
	"title text, " + 
	"speaker text," +
	"affiliation text, " +
	"date text, " +
	"begintime text, " +
	"endtime text, " +
	"location text," +
	"bookmarkno text,"+
	"viewno text,"+
	"emailno text,"+
	"ownerid text,"+
	"ownername text,"+
	"pubtime text,"+
	"description text,"+
	"bookmarked text," +
	"recommended text," +
	"dateid text,"+
	"year text,"+
	"picurl text,"+
	"rating text)";

private final static String CreateMyRating = "create table myrating (" +
"id text not null," +
"userid text not null," +
"rating text, "+
"primary key(id, userid))";
private final static String CreateMyBookmarked = "create table mybookmarked ("+
"id text not null,"+
"userid text not null,"+
"primary key(id, userid))";
private final static String CreateMyRecommended = "create table myrecommended ("+
"id text not null,"+
"userid text not null,"+
"primary key(id, userid))";
private final static String CreateUser = "create table user" +
		"(ID text primary key)";
private final static String CreateSeries = "create table series" +
		"(id text primary key," +
		"name text," +
		"bookmark text," +
		"cate text," +
		"description text)";
private final static String CreateGroup = "create table groups" +
"(id text primary key," +
"name text," +
"bookmark text," +
"cate text," +
"description text)";
//create my online friend
private final static String CreateOnlineFriend="create table myonlinefriend" +
"(userid text primary key," +
"user0_id text not null," +
"name text)";

//create onlinegame
private final static String CreatOnlineGame="create table onlinegames"+"(col_id text primary key,"+"userid text,"+"speaker text,"+"title text,"+
"desc text)"; 
private final static String CreateGame2 ="create table game2 ("+
"user1_id text,"+"user2_id text,"+"col_id text,"+"score text,"+"end_time text,"+"launch_time text,"+"primary key(user1_id,user2_id,col_id))";
private final static String CreateGame2User ="create table game2User("+
"user1_id text,"+"score text,"+"end_time text,"+"title text,"+"col_id text,"+"detail text,"+"name text,"+"primary key(user1_id,col_id))";
private final static String CreateTotalScore ="create table TotalScore("+
"user_id text,"+"onlinescore text,"+"offlinescore text,"+"primary key(user_id))";	
private final static String CreateGame1 ="create table game1("+
"user2_id text,"+"user1_id text,"+"col_id text,"+"title text,"+"speaker text,"+"detail text,"+"flag text,"+"score text,"+"endtime text,"+"primary key(flag,user2_id,col_id))";	
public DBAdapter(Context ctx) {
		this.mCtx = ctx;
		
	}

	public DBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
	//insert user2 game2
	public long insertGame2User(game2receive g) {
		ContentValues values = new ContentValues();
		values.put("col_id", g.colID);
		values.put("user1_id", g.user1ID);
		values.put("detail", g.detail);
		values.put("title", g.title);
	values.put("name", g.name);
		return mDb.insert("game2User", null, values);
	}
	//insert Game2 to user2
	public long insertGame2(String uid1,String uid2,String colid, long time){
		ContentValues values=new ContentValues();
		values.put("user1_id",uid1);
		values.put("user2_id", uid2);
		values.put("col_id", colid);
		values.put("launch_time", time);
		return mDb.insert("game2", null, values);
		
		
	}
	//random online game
	public long insertOnlineGame(onlinegameTopic t,String uid)
	{
		
		ContentValues values=new ContentValues();
		values.put("col_id",t.id);
		values.put("userid","1");
		values.put("title", t.title);
		values.put("speaker", t.name);
		//values.put("speaker",t.speakername);
		values.put("desc", t.des);
		return mDb.insert("onlinegames", null, values);
	}
	
	//insert online friend
	
	
	public long insertMyOnlineFriend(Onlinefriend l,String uid)
	{
		
		ContentValues values=new ContentValues();
		values.put("userid", l.userID);
		values.put("user0_id",uid);
		values.put("name",l.name);
	
		return mDb.insert("myonlinefriend", null, values);
	}
	//Papers
	public long insertTalk(Talk t) {
		ContentValues values = new ContentValues();
		values.put("id", t.id);
		values.put("title", t.title);
		values.put("speaker", t.speaker);
		values.put("affiliation", t.affiliation);
		values.put("date", t.date);
		values.put("begintime", t.begintime);
		values.put("endtime", t.endtime);
		values.put("location", t.location);
		values.put("emailno", t.emailno);
		values.put("bookmarkno", t.bookmarkno);
		values.put("viewno", t.viewno);
		values.put("ownerid", t.ownerid);
		values.put("ownername", t.ownername);
		values.put("pubtime", t.pubtime);
		values.put("bookmarked", t.bookmarked);
		values.put("recommended", t.recommended);
		values.put("description",t.description);
		values.put("dateid",t.dateid);
		values.put("rating",t.rating);
		values.put("year", t.year);
		values.put("picurl", t.picurl);
		return mDb.insert("talk", null, values);
	}
	
	public long insertRecommendedTalk(Talk t) {
		ContentValues values = new ContentValues();
		values.put("id", t.id);
		values.put("title", t.title);
		values.put("speaker", t.speaker);
		values.put("affiliation", t.affiliation);
		values.put("date", t.date);
		values.put("begintime", t.begintime);
		values.put("endtime", t.endtime);
		values.put("location", t.location);
		values.put("emailno", t.emailno);
		values.put("bookmarkno", t.bookmarkno);
		values.put("viewno", t.viewno);
		values.put("ownerid", t.ownerid);
		values.put("ownername", t.ownername);
		values.put("pubtime", t.pubtime);
		values.put("bookmarked", t.bookmarked);
		values.put("recommended", "yes");
		values.put("description",t.description);
		values.put("dateid",t.dateid);
		values.put("rating",t.rating);
		values.put("year", t.year);
		values.put("picurl", t.picurl);
		return mDb.insert("talk", null, values);
	}
	
	public long insertBookmarkedTalk(Talk t) {
		ContentValues values = new ContentValues();
		values.put("id", t.id);
		values.put("title", t.title);
		values.put("speaker", t.speaker);
		values.put("affiliation", t.affiliation);
		values.put("date", t.date);
		values.put("begintime", t.begintime);
		values.put("endtime", t.endtime);
		values.put("location", t.location);
		values.put("emailno", t.emailno);
		values.put("bookmarkno", t.bookmarkno);
		values.put("viewno", t.viewno);
		values.put("ownerid", t.ownerid);
		values.put("ownername", t.ownername);
		values.put("pubtime", t.pubtime);
		values.put("bookmarked", "yes");
		values.put("recommended", t.recommended);
		values.put("description",t.description);
		values.put("dateid",t.dateid);
		values.put("rating",t.rating);
		values.put("year", t.year);
		values.put("picurl", t.picurl);
		return mDb.insert("talk", null, values);
	}
	public long insertMyBookmarked(Talk t, String userid){
		ContentValues values = new ContentValues();
		values.put("id", t.id);
		values.put("userid", userid);
		return mDb.insert("mybookmarked", null, values);
	}
	public long insertMyRecommended(Talk t, String userid){
		ContentValues values = new ContentValues();
		values.put("id", t.id);
		values.put("userid", userid);
		return mDb.insert("myrecommended", null, values);
	}
	public int updateOnlineScore(Score s, String userid){
		ContentValues values = new ContentValues();
		values.put("user_id", userid);
		values.put("onlinescore", s.onlineScore);
		values.put("offlinescore", s.offlineScore);
		return mDb.update("TotalScore", values, "user_id='" +userid+"'", null);
	}

	public long insertOnlineScore1(String userid){
		ContentValues values = new ContentValues();
	//	Cursor cursor = mDb.rawQuery("select user_id" + "from TotalScore ",null);
	
		values.put("user_id", userid);
		values.put("onlinescore", 0);
		values.put("offlinescore", 0);
		return mDb.insert("TotalScore", null, values);
	}
	public long insertTotalScore(Score s,String userid){
		ContentValues values = new ContentValues();
	//	Cursor cursor = mDb.rawQuery("select user_id" + "from TotalScore ",null);
	
		values.put("user_id", s.userID);
		values.put("onlinescore", s.onlineScore);
		values.put("offlinescore",s.offlineScore );
		return mDb.insert("TotalScore", null, values);
	}
	public long insertOfflineScore(String userid){
		ContentValues values = new ContentValues();
		Cursor cursor = mDb.rawQuery("select user_id" + "from TotalScore ",null);
		if(cursor.getString(0)==null){
		values.put("user_id", userid);
		values.put("onlinescore", 0);
		values.put("offlinescore", 0);
}		return mDb.insert("TotalScore", null, values);
	}
	//insert my online friend
	/*public long insertMyonlineFriend(Authorization a, String name){
		ContentValues values = new ContentValues();
		values.put("id", a.onlineID.toString());
		values.put("name", name);
		return mDb.insert("myonlinefriend", null, values);
	}*/
	//delete online friend
	public int deleteTotalScore(){
		return mDb.delete("TotalScore", null, null);
	}
	public int deleteOnlineGame(){
		return mDb.delete("onlinegames", null, null);
	}
	public int deleteGame2User(){
		return mDb.delete("game2User", null, null);
	}
	public int deleteGame2(){
		return mDb.delete("game2", null, null);
	}
	public int deleteOnlineFriend1() {
		// TODO Auto-generated method stub
		return mDb.delete("myonlinefriend", null, null);
	}
	
	public int daleteTalk()
	{
		return mDb.delete("talk", null, null);
	}
	public int daleteMyBookmarked(String userid)
	{
		return mDb.delete("mybookmarked", "userid='"+userid+"'", null);
	}
	public int deleteMyBookmarked(Talk t,String userid)
	{
		return mDb.delete("mybookmarked", "userid='"+userid+"' and id='"+t.id+"'", null);
	}
	public int daleteMyRecommended(String userid)
	{
		return mDb.delete("myrecommended", "userid='"+userid+"'", null);
	}
	public int deleteScoreRecord(String uid,String uid1,String colid)
	{
		return mDb.delete("game2User", "user1_id='"+uid1+"'and col_id='"+colid+"'", null);
	}
	public int deleteGame1Guess(String uid)
	{
		return mDb.delete("game1", "user2_id='"+uid+"'", null);
	}
	public int updateOnlineGame(onlinegameTopic l,String uid){
		ContentValues values = new ContentValues();
		values.put("col_id", l.id);
		values.put("userid", "1");
		values.put("title", l.title);
		values.put("speaker", l.name);
		//values.put("speaker", l.speakername);
		values.put("desc", l.des);


		return mDb.update("onlinegames", values, "userid='" +uid+"'", null);
		
	}
	public int updateGame1Score(String uid, String user1ID, String id, String score,long time, String flag){
		ContentValues values = new ContentValues();
		values.put("col_id", id);
		values.put("user1_id",user1ID);
		values.put("user2_id", uid);
		values.put("endtime", time);
		//values.put("speaker", l.speakername);
		values.put("score",score );
		values.put("flag", flag);


		return mDb.update("game1", values, "user2_id='" +uid+"'and col_id='"+id+"'and flag='"+flag+"'", null);
		
	}
	public int updateTotalScore(String uid,float onlinescore,float offlinescore){
		ContentValues values = new ContentValues();
		Cursor cursor = mDb.rawQuery("select onlinescore,offlinescore" + "from TotalScore where user_id="+uid+"",null);
        cursor.moveToFirst();
		values.put("user_id", uid);
		values.put("onlinescore",cursor.getString(0)+onlinescore);
    	values.put("offlinescore",cursor.getString(1)+offlinescore);
    	values.putAll(values);
		return mDb.update("TotalScore", values, "user_id='" +uid+"'", null);
		
	}
	
	public int updateOnlineScore(String uid,String user0id,String colID, double score,long time){
		ContentValues values = new ContentValues();
		values.put("col_id", colID);
		values.put("user1_id", user0id);
	//	values.put("user0_id", user0id);
		values.put("score",score);
		//values.put("speaker", l.speakername);
		values.put("end_time", time);

		return mDb.update("game2User", values, "user1_id='" +user0id+"' and col_id='"+colID+"'", null);
		
	}
	
	
	public int updateGame2User(game2receive l,String uid){
		ContentValues values = new ContentValues();
		values.put("col_id", l.colID);
		values.put("user1_id", l.user1ID);
		values.put("title", l.title);
		values.put("detail", l.detail);
		values.put("name",l.name);
		//values.put("speaker", l.speakername);
	//	values.put("desc", l.des);


		return mDb.update("game2User", values, null, null);
		
	}
	public int updateGame2(game2 l,String uid){
		ContentValues values = new ContentValues();
		values.put("col_id", l.colID);
		values.put("user1_id", l.user1ID);
		values.put("user2_id", l.user2ID);
		values.put("launch_time", l.launchtime);
		//values.put("title", l.title);
		//values.put("speaker", l.name);
		//values.put("speaker", l.speakername);
		//values.put("detail", l.detail);


		return mDb.update("game2", values, "user2_id='" +uid+"'", null);
		
	}
	//update game1 guess
	public int updateGame1Guess(game1 g,String uid){
		ContentValues values = new ContentValues();
		if(g.user1ID!=null){
		values.put("user1_id",g.user1ID);}
		values.put("title",g.title);
		values.put("detail", g.detail);
		values.put("speaker", g.speaker);
		values.put("flag",g.flag);
		values.put("col_id", g.colID);
		values.put("user2_id", uid);
		return mDb.update("game1", values, null, null);
		
	}
	public int updateonlineFriend(Onlinefriend l,String uid){
	ContentValues values = new ContentValues();
	values.put("userid", l.userID);
	values.put("name", l.name);

     values.put("user0_id", uid);
	return mDb.update("myonlinefriend", values, "userid='" +l.userID+"'", null);
	
}
	public int updateTotalScore(Score l,String uid){
		ContentValues values = new ContentValues();
		values.put("user_id", l.userID);
		values.put("onlinescore",l.onlineScore);
		values.put("offlinescore",l.offlineScore);

	   
		return mDb.update("TotalScore", values, "userid='" +l.userID+"'", null);
		
	}


	public int updateTalk(Talk t){
		ContentValues values = new ContentValues();
		values.put("emailno", t.emailno);
		values.put("bookmarkno", t.bookmarkno);
		values.put("viewno", t.viewno);
		return mDb.update("talk", values, "id='" + t.id+"'", null);
		
	}
	public int updateTalkDes(String des, String id){
		ContentValues values = new ContentValues();
		values.put("description", des);	
		return mDb.update("talk", values, "id='" + id+"'", null);
		
	}
	public int updateRecommendTalk(Talk t){
		ContentValues values = new ContentValues();
		values.put("recommended", "yes");
		return mDb.update("talk", values, "id='" + t.id+"'", null);
		
	}
	
	public int updateBookmarkedTalk(Talk t){
		ContentValues values = new ContentValues();
		values.put("bookmarked", "yes");
		return mDb.update("talk", values, "id='" + t.id+"'", null);
		
	}
	
	public int updateRatedTalk(Talk t, String userid, String rating){
		ContentValues values = new ContentValues();
		values.put("rating", rating);
		return mDb.update("myrating", values, "id='" + t.id+"' and userid = '"+userid+"' ", null);
	}
	
	public int updateTalkByBookmark(Talk t, String bookmark) {
		ContentValues vals = new ContentValues();
		vals.put("bookmarked", bookmark);
		return mDb.update("talk", vals, "id='" + t.id+"'", null);
	}
	
	public int updateTalkByRating(Talk t, String rating) {
		ContentValues vals = new ContentValues();
		vals.put("rating", rating);
		return mDb.update("talk", vals, "id='" + t.id+"'", null);
	}
	
	public int updateToDefaultBookmark() {
		ContentValues vals = new ContentValues();
		vals.put("bookmarked", "no");
		return mDb.update("talk", vals, "bookmarked='yes'", null);
	}
	public int updateToDefaultRating() {
		ContentValues vals = new ContentValues();
		vals.put("rating", "no");
		return mDb.update("talk", vals, "rating<>'no'", null);
	}
	public int updateToDefaultRecommend() {
		ContentValues vals = new ContentValues();
		vals.put("recommended", "no");
		return mDb.update("talk", vals, "recommended='yes'", null);
	}

	public long insertRatedTalk(Talk t, String userid, String rating) {
		ContentValues values = new ContentValues();
		values.put("id", t.id);
		values.put("userid", userid);
		values.put("rating", rating);
		return mDb.insert("myrating", null, values);
	}
	
//get onlinefriends  status
	public String getOnlineStatus(String id)
	{
		String status="";
		Cursor cursor = mDb.query("myonlinefriend", new String[] {"name" }, "userID='"+id+"'",
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			status =  cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return status;
	}
	
	//getTalks
	public String getTalkBookmarkedStatus(String id)
	{
		String status="";
		Cursor cursor = mDb.query("talk", new String[] { "bookmarked" }, "id='"+id+"'",
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			status =  cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return status;
	}
	public String getTalkRecommendedStatus(String id)
	{
		String status="";
		Cursor cursor = mDb.query("talk", new String[] { "recommended" }, "id='"+id+"'",
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			status =  cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return status;
	}
	public String getTalkRating(String id)
	{
		String status="";
		Cursor cursor = mDb.query("talk", new String[] { "rating" }, "id='"+id+"'",
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			status =  cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return status;
	}
	
	
	public Talk getTalkByID(String id){
		Talk t = new Talk();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, t.rating, t.recommended, t.picurl"+" "+
				"from talk t where t.id="+id+" order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.rating = cursor.getString(16);
			t.recommended = cursor.getString(17);
			t.picurl = cursor.getString(18);
			cursor.moveToNext();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return t;
	}

	public ArrayList<Talk> getRatedTalks(String userid){
		ArrayList<Talk> tList = new ArrayList<Talk>();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, b.rating, t.picurl"+" "+
				"from talk t, myrating b where t.id= b.id and b.userid= '"+userid+"' order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Talk t = new Talk();
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.rating = cursor.getString(16);
			t.picurl = cursor.getString(17);
			tList.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return tList;
	}
	public ArrayList<Talk> getMyBookmarkedTalks(String userid){
		ArrayList<Talk> tList = new ArrayList<Talk>();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, t.rating, t.picurl"+" "+
				"from talk t, mybookmarked b where b.userid= '"+userid+"' and t.id = b.id order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Talk t = new Talk();
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.rating = cursor.getString(16);
			t.picurl = cursor.getString(17);
			tList.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return tList;
	}
	public ArrayList<Talk> getMyRecommendedTalks(String userid){
		ArrayList<Talk> tList = new ArrayList<Talk>();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, t.rating, t.picurl"+" "+
				"from talk t, myrecommended b where b.userid= '"+userid+"' and t.id = b.id order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Talk t = new Talk();
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.rating = cursor.getString(16);
			t.picurl = cursor.getString(17);
			tList.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return tList;
	}
	//get online score
	public Score getOnlineScore(String userid){
		Score l = new Score() ;
		Cursor cursor = mDb.rawQuery("select onlinescore " + "from TotalScore where user_id="+ userid+"",null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
		l = new Score();
			l.onlineScore = cursor.getFloat(0);
			
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return l ;
	}
	public Score getTotalScore(String userid){
		Score l = new Score() ;
		Cursor cursor = mDb.rawQuery("select onlinescore,offlinescore " + "from TotalScore where user_id="+ userid+"",null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
		l = new Score();
			l.onlineScore = cursor.getFloat(0);
			l.offlineScore=cursor.getFloat(1);
			
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return l ;
	}
	//get game1 Guess
	public ArrayList<game1> getGame1Guess(String userid){
		ArrayList<game1> List = new ArrayList<game1>();
		Cursor cursor = mDb.rawQuery("select col_id,title,detail,speaker,user1_id,flag " + "from game1 where user2_id="+userid+"",null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			game1 l = new game1();
			l.colID = cursor.getString(0);
			l.title = cursor.getString(1);
			l.detail = cursor.getString(2);
			l.speaker=cursor.getString(3);
			l.user1ID = cursor.getString(4);
			l.flag=cursor.getString(5);
		List.add(l);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return List;
	}
	public ArrayList<game2receive> getonlineGamefromUser(String userid){
		ArrayList<game2receive> onlineList = new ArrayList<game2receive>();
		Cursor cursor = mDb.rawQuery("select col_id,title,detail, user1_id,name " + "from game2User ",null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			game2receive l = new game2receive();
			l.colID = cursor.getString(0);
			l.title = cursor.getString(1);
			l.detail = cursor.getString(2);
			l.user1ID = cursor.getString(3);
			l.name=cursor.getString(4);
			onlineList.add(l);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return onlineList;
	}
	//get online my online friend
	public ArrayList<Onlinefriend> getMyOnlineFriend(String userid){
		ArrayList<Onlinefriend> onlineList = new ArrayList<Onlinefriend>();
		Cursor cursor = mDb.rawQuery("select m.userid,m.name " + "from myonlinefriend m where m.user0_id= '"+userid+"'",null);
	
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Onlinefriend l = new Onlinefriend();
			l.userID = cursor.getString(0);
			l.name = cursor.getString(1);
			
			onlineList.add(l);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return onlineList;
	}
	//get online topics
	public ArrayList<onlinegameTopic> getonlineTopics(String userid){
		ArrayList<onlinegameTopic> onlinetTopics = new ArrayList<onlinegameTopic>();
		Cursor cursor = mDb.rawQuery("select m.col_id,m.title,m.desc,m.speaker " + "from onlinegames m where m.userid= '"+1+"'",null);
	
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			onlinegameTopic t = new onlinegameTopic();
			t.id=cursor.getString(0);
			t.title = cursor.getString(1);
			//t.speakername = cursor.getString(1);
			t.des=cursor.getString(2);
			t.name=cursor.getString(3);
		    onlinetTopics.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return onlinetTopics;
	}
	public ArrayList<Talk> getThisMonthTalks(int dayid_start, int dayid_end, int year){
		ArrayList<Talk> tList = new ArrayList<Talk>();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, t.picurl"+" "+
				"from talk t where t.year ='"+year+"' and t.dateid >= '"+dayid_start+"' and t.dateid <= '"+dayid_end+"' order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Talk t = new Talk();
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.picurl = cursor.getString(16);
			tList.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return tList;
	}
	
	public ArrayList<Talk> getThisWeekTalks(int dayid_start, int dayid_end, int year){
		ArrayList<Talk> tList = new ArrayList<Talk>();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, t.picurl"+" "+
				"from talk t where t.year ='"+year+"' and t.dateid >= '"+dayid_start+"' and t.dateid <= '"+dayid_end+"' order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Talk t = new Talk();
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.picurl = cursor.getString(16);
			tList.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return tList;
	}
	
	public ArrayList<Talk> getTodayTalks(int dayid, int year){
		ArrayList<Talk> tList = new ArrayList<Talk>();
		Cursor cursor = mDb.rawQuery("select t.id, t.title,t.date,t.begintime," +
				"t.endtime, t.speaker, t.affiliation, t.location, t.bookmarkno,"+
				"t.emailno, t.viewno, t.ownerid, t.ownername, t.pubtime, t.bookmarked, t.description, t.picurl"+" "+
				"from talk t where t.year ='"+year+"' and t.dateid ='"+dayid+"' order by t.dateid, t.begintime",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Talk t = new Talk();
			t.id = cursor.getString(0);
			t.title = cursor.getString(1);
			t.date = cursor.getString(2);
			t.begintime = cursor.getString(3);
			t.endtime = cursor.getString(4);
			t.speaker = cursor.getString(5);
			t.affiliation = cursor.getString(6);
			t.location = cursor.getString(7);
			t.bookmarkno = cursor.getString(8);
			t.emailno = cursor.getString(9);
			t.viewno = cursor.getString(10);
			t.ownerid = cursor.getString(11);
			t.ownername = cursor.getString(12);
			t.pubtime = cursor.getString(13);
			t.bookmarked = cursor.getString(14);
			t.description = cursor.getString(15);
			t.picurl = cursor.getString(16);
			tList.add(t);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return tList;
	}
	
	//insert user2 game2
	public long insertGame2FromUser(game2receive g) {
		ContentValues values = new ContentValues();
		values.put("col_id", g.colID);
		values.put("user1_id", g.user1ID);
		values.put("detail", g.detail);
		values.put("title", g.title);
		
	
		return mDb.insert("game2User", null, values);
	}
	//insert game1 guess
	public long insertGame1Guess(game1 g,String uid) {
		ContentValues values = new ContentValues();
		values.put("col_id", g.colID);
		values.put("user1_id", g.user1ID);
		values.put("user2_id", uid);
		values.put("detail", g.detail);
		values.put("title", g.title);
		values.put("speaker", g.speaker);
		values.put("flag", g.flag);
		return mDb.insert("game1", null, values);
	}
	//Series
	public long insertSeries(Series s) {
		ContentValues values = new ContentValues();
		values.put("id", s.id);
		values.put("name", s.name);
		values.put("bookmark", s.bookmarkno);
		values.put("description", s.description);
		values.put("cate", s.cate);
		return mDb.insert("series", null, values);
	}
	public Series getSeriesByID(String id){
		Series s = new Series();
		Cursor cursor = mDb.rawQuery("select s.id, s.name,s.bookmark,s.description" +" "+
				"from series s where s.id="+id,null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			s.id = cursor.getString(0);
			s.name = cursor.getString(1);
			s.bookmarkno = cursor.getString(2);
			s.description = cursor.getString(3);
			cursor.moveToNext();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return s;
	}
	public ArrayList<Series> getSeriesByCate(String cate){
		ArrayList<Series> sList = new ArrayList<Series>();
		Cursor cursor = mDb.rawQuery("select s.id, s.name, s.bookmark, s.description, s.cate " +
				"from series s where s.cate='"+cate+"'",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Series s = new Series();
			s.id = cursor.getString(0);
			s.name = cursor.getString(1);
			s.bookmarkno = cursor.getString(2);
			s.description = cursor.getString(3);
			s.cate = cursor.getString(4);
			sList.add(s);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return sList;
	}
	//Group
	public long insertGroup(Group s) {
		ContentValues values = new ContentValues();
		values.put("id", s.id);
		values.put("name", s.name);
		values.put("bookmark", s.bookmarkno);
		values.put("description", s.description);
		values.put("cate", s.cate);
		return mDb.insert("groups", null, values);
	}
	public Group getGroupByID(String id){
		Group s = new Group();
		Cursor cursor = mDb.rawQuery("select s.id, s.name,s.bookmark,s.description" +" "+
				"from groups s where s.id="+id,null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			s.id = cursor.getString(0);
			s.name = cursor.getString(1);
			s.bookmarkno = cursor.getString(2);
			s.description = cursor.getString(3);
			cursor.moveToNext();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return s;
	}
	public ArrayList<Group> getGroupByCate(String cate){
		ArrayList<Group> gList = new ArrayList<Group>();
		Cursor cursor = mDb.rawQuery("select g.id, g.name, g.bookmark, g.description, g.cate " +
				"from groups g where g.cate='"+cate+"'",null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Group g = new Group();
			g.id = cursor.getString(0);
			g.name = cursor.getString(1);
			g.bookmarkno = cursor.getString(2);
			g.description = cursor.getString(3);
			g.cate = cursor.getString(4);
			gList.add(g);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return gList;
	}
	//User login
	public long insertUser(String ID)
	{
		ContentValues values = new ContentValues();
		values.put("ID", ID);
		return mDb.insert("user", null, values);
	}
	
	public String getUserID()
	{
		String id = "";
		try {
		Cursor cursor = mDb.query("user", new String[] { "ID" }, null,
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			id =  cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		} catch(Exception e)
		{
			System.out.print(e.getMessage());
			System.out.print("bug!");
		}
		
		return id;
	}

	//online user
	public long insertonlineUser(String ID)
	{
		ContentValues values = new ContentValues();
		values.put("UserID", ID);
		return mDb.insert("name", null, values);
	}
	
	public String getonlineUserID()
	{
		String id = "";
		try {
		Cursor cursor = mDb.query("user", new String[] { "ID" }, null,
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			id =  cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		} catch(Exception e)
		{
			System.out.print(e.getMessage());
			System.out.print("bug!");
		}
		
		return id;
	}
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, "CoMet11", null, 2);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CreateTalk);
			db.execSQL(CreateMyBookmarked);
			db.execSQL(CreateMyRecommended);
			db.execSQL(CreateMyRating);
			db.execSQL(CreateUser);
			db.execSQL(CreateSeries);
			db.execSQL(CreateGroup);
			//
			db.execSQL(CreateOnlineFriend);
			db.execSQL(CreatOnlineGame);
			db.execSQL(CreateGame2);
			db.execSQL(CreateGame2User);
db.execSQL(CreateTotalScore);
db.execSQL(CreateGame1);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS talk");
			db.execSQL("DROP TABLE IF EXISTS mybookmarked");
			db.execSQL("DROP TABLE IF EXISTS myrecommended");
			db.execSQL("DROP TABLE IF EXISTS myrating");
			db.execSQL("DROP TABLE IF EXISTS user");
			db.execSQL("DROP TABLE IF EXISTS series");
			db.execSQL("DROP TABLE IF EXISTS groups");
			db.execSQL("DROP TABLE IF EXISTS myonlinefriend");
			db.execSQL("DROP TABLE IF EXISTS onlinegames");
			db.execSQL("DROP TABLE IF EXISTS game2");
			db.execSQL("DROP TABLE IF EXISTS game2User");
			db.execSQL("DROP TABLE IF EXISTS TotalScore");
			db.execSQL("DROP TABLE IF EXISTS game1");
			onCreate(db);
		}
	}
	

	

	
}
