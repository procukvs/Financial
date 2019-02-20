package dbWork;
import java.util.*;
import java.text.*;
/*
 *   версія роботи з ДБ через прості класи 
 *     ---dbAccess ----
 *    класи Client, AccountOp, CreditOp, DeposOp, 
 *          Current, Instant, Movement, Account, Amount
 *    набір методів для виборки окремих екземплярів
 *         takeClient, takeCurrent, takeInstant, ......
 *    набір методів для змін 
 *      кожний метод - реалізує подію
 *      beginClient, putClient, takeClient, moveClient 
 *           
 */

public class Main {

	private DbAccess db =null; 
	Main(){
		db = new DbAccess();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main wk = new Main();
		wk.work();
	}
	private void work(){
		System.out.println("Begin work wth Financial.");
		if (db.connectionDb()){
			boolean res;
			ArrayList ap = null;
			int newPer = 0;
			
			//res=db.beginClient(buildSqlDate("2012-02-01"), "Іванов");
			//System.out.println("new Client " + res);
			//buildSqlDate("2012-02-06"), 1, (float)341.11
			//buildSqlDate("2012-02-09"), 1, (float)110
			//buildSqlDate("2012-02-05"), 1, (float)10
			//buildSqlDate("2012-02-06"), 1, (float)200
			//buildSqlDate("2012-02-08"), 1, (float)20
			//buildSqlDate("2012-02-08"), 1, (float)20
			res = db.iswfPutClient(buildSqlDate("2012-02-08"), 1, (float)20);
			if (res) System.out.println("iswfPutClient: " + db.getMsgError());
            if (res) {
            	res = db.putClient(buildSqlDate("2012-02-08"), 1, (float)20);
            }
            System.out.println("res = " + res);	
         	db.disConnect();
		}
		else System.out.println("No connection to DB financial");	
	}
	
	static java.sql.Date buildSqlDate(String day) {
		java.sql.Date sqlDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = dateFormat.parse(day);
			sqlDate = new java.sql.Date(date.getTime());
			
		} catch (Exception e) {
			System.out.println("buildSqlDate> " + e.getMessage());}	
		return sqlDate;
		
	}
}
