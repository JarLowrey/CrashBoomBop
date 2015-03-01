package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//@author James Lowrey
//BEFORE RETRIEVING DATA FROM A CURSOR, MUST CALL c.moveToFirst(), c.next(), ETC IN ORDER FOR CURSOR TO LOAD DATA
//When querying using a string, the string must be wrapped in quotations!!! EX) return mDb.delete(DATABASE_TABLE1, KEY_NAME + " = " + "\"" + name + "\"", null) > 0;


public abstract class DbAdapter {
	
    public static String DATABASE_NAME = "crashBoomBop.db";
    public static final int DATABASE_VERSION = 2;
    public static final String GAME_STATS_DB_TABLE = "GameStatsTable";

    private static DatabaseHelper ourHelper;
    public Context ctx;
    public SQLiteDatabase mDb;
    
    //GameStatsDbAdapter columns
    public static final String KEY_ROWID = "_id";	
    public static final String KEY_SCORE = "score";    
    public static final String KEY_RESPONSE_TIME_THIS_GAME = "responseTime";
    public static final String KEY_TIME = "totalTime";    
    public static final String KEY_NUM_CASES = "totalCases";
    public static final String KEY_NUM_CASES_PASSED = "totalCasesPassed";
    public static final String KEY_NUM_BONUS_CASES = "totalBonusCases";
    public static final String KEY_NUM_BONUS_POINTS = "totalBonusPoints";
    public static final String KEY_NUM_COMBOS = "totalCombos";
    public static final String KEY_NUM_COMBOS_PASSED = "totalCombosPassed";
    public static final String KEY_HIGHEST_STREAK="highestStreak";
    public String[] gameStatsColumns = new String[] {
    		KEY_ROWID,
    		KEY_SCORE,KEY_RESPONSE_TIME_THIS_GAME,KEY_TIME,
    		KEY_NUM_CASES,KEY_NUM_CASES_PASSED,
    		KEY_NUM_BONUS_CASES,KEY_NUM_BONUS_POINTS, 
    		KEY_NUM_COMBOS, KEY_NUM_COMBOS_PASSED,
    		KEY_HIGHEST_STREAK
    };
    
    public static final String GAME_STATS_TABLE= "CREATE TABLE IF NOT EXISTS "+GAME_STATS_DB_TABLE+" (" +
            KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SCORE+" INTEGER, "+
            KEY_RESPONSE_TIME_THIS_GAME +" REAL, " + 
            KEY_TIME +" REAL, " + 
            KEY_NUM_CASES +" INTEGER, " + 
            KEY_NUM_CASES_PASSED +" INTEGER, " + 
            KEY_NUM_BONUS_CASES +" INTEGER, " + 
            KEY_NUM_BONUS_POINTS +" INTEGER, " + 
            KEY_NUM_COMBOS +" INTEGER, " + 
            KEY_NUM_COMBOS_PASSED +" INTEGER, " + 
            KEY_HIGHEST_STREAK+" INTEGER );";
    
     public static class DatabaseHelper extends SQLiteOpenHelper{
    	 public static DatabaseHelper getInstance(Context context) {
    		// http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
    	    if (ourHelper == null) {
    	      ourHelper = new DatabaseHelper(context.getApplicationContext());
    	    }
    	    return ourHelper;
    	 }
    	    
        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GAME_STATS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + GAME_STATS_DB_TABLE);
            onCreate(db);
        }
    }

    public DbAdapter(Context mctx){   
        ctx = mctx;
        ourHelper = ourHelper.getInstance(ctx);
    }

    public DbAdapter open() throws SQLException{
    	ourHelper = DatabaseHelper.getInstance(ctx);
    	mDb = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        if(mDb.isOpen())
            ourHelper.close();
    }
}