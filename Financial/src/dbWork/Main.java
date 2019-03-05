package dbWork;
import java.util.*;
import java.text.*;
import java.time.LocalDate;
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
			boolean res= true;
			ArrayList ap = null;
			int newPer = 0;
			
			// res=db.beginClient(buildSqlDate("2012-02-05"), "Петренко");
			//System.out.println("new Client " + res);
			//buildSqlDate("2012-02-06"), 1, (float)341.11
			//buildSqlDate("2012-02-09"), 1, (float)110
			//buildSqlDate("2012-02-05"), 1, (float)10
			//buildSqlDate("2012-02-06"), 1, (float)200
			//buildSqlDate("2012-02-08"), 1, (float)20
			//buildSqlDate("2012-02-08"), 1, (float)20
			/*
			res = db.iswfPutClient(buildSqlDate("2012-02-08"), 1, (float)20);
			if (res) System.out.println("iswfPutClient: " + db.getMsgError());
            if (res) {
            	res = db.putClient(buildSqlDate("2012-02-08"), 1, (float)20);
            }
            */
			Execution ex = new Execution(db);
			//ex.initial();
			//res = ex.putClient(LocalDate.of(2012, 02, 10),1, 1000);
			//System.out.println("res = " + res);	
			//res = false;
			
			//ap = ex.iswfPutClient(LocalDate.of(2012,2,6), 8, (float)341.11);
			/*
			ap = ex.iswfPutClient(LocalDate.of(2012,2,9), 8, (float)110);
			if (!ap.isEmpty()) {
				for(int i=0; i<ap.size();i++) System.out.println("iswfPutClient: " + ap.get(i));
			}
			else res = ex.putClient(LocalDate.of(2012,2,9), 8, (float)110);
			*/
			/*
			ap = ex.iswfBeginDeposit(LocalDate.of(2012,2,12), 1, (float)300, (float)16.45, LocalDate.of(2012,2,24) );
			if (!ap.isEmpty()) {
				for(int i=0; i<ap.size();i++) System.out.println("iswfBeginDeposit: " + ap.get(i));
			}
			else res = ex.beginDeposit(LocalDate.of(2012,2,12), 1, (float)300, (float)16.45, LocalDate.of(2012,2,24));	
			*/
			
			ap = ex.iswfCloseDeposit(LocalDate.of(2012,2,24), 2);
			if (!ap.isEmpty()) {
				for(int i=0; i<ap.size();i++) System.out.println("iswfCloseDeposit: " + ap.get(i));
			}
			else res = ex.closeDeposit(LocalDate.of(2012,2,24),2);		
				
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
