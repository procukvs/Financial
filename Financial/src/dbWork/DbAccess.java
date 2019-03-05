package dbWork;

import java.sql.*;
import java.util.ArrayList;
import java.time.*;

public class DbAccess {
	private Connection conn = null;
	private Statement s; 
	private ResultSet rs;
	private String sql;
	private String msgError = "";
	DbAccess(){ 
		try  {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception ex) {
           	System.out.println("DbAccess> " + ex.getMessage());
        } 	
	}
	public boolean connectionDb(){
	       try{ 
		      conn = DriverManager.getConnection("jdbc:mysql://localhost/financial?useSSL=false","root","root");
		      s = conn.createStatement();
		      return true;
	        } catch (SQLException e) {
				System.out.println("connectionDB> " + e.getMessage());
				return false;
		    } 
	}
	public void disConnect(){
		 try{
			 conn.close();	
			 conn = null;
	     } catch (SQLException e) {
	    	 System.out.println("disConnect> " + e.getMessage());
	     }  
	}
	
	public String getMsgError() {return msgError;}
	
	private boolean iswfCurrent(Date begin, int idCl) {
		boolean res = true;
		try{
			int idPr=0;
			Date beg = null;
			sql = "select idPr, begin from product where idCl = " + idCl + " and kind = 'current'";
			s.execute(sql);
			rs = s.getResultSet();
			if((rs!=null) && (rs.next())) {
				idPr = rs.getInt(1);
				if (rs.getObject(2)!=null) beg = rs.getDate(2);
			}
			if (idPr==0) {
				//if (!msgError.isEmpty()) msgError +="\n";
				msgError += " \nNot found current product";
				res = false;
			} else	if (begin.compareTo(beg)<0) {
				//if (!msgError.isEmpty()) msgError +="\n";
				msgError += " \nDate operation befor " + beg.toString();
				res = false;
			}
			if (!res)System.out.println("iswfCurrent> " + msgError);
	    } catch (SQLException e) {
	    	res=false;
	    	msgError +="iswfCurrent> " + e.getMessage();
	    	System.out.println("iswfCurrent> " + e.getMessage());
	    }  	
		return res;
	}
	
	public boolean iswfPutClient(Date begin, int idCl, float sum) {
		msgError = "";
		return iswfCurrent(begin,idCl);
	}
	
	public boolean iswfMoveSum(String numb, Date op, String kind, String what, float sum ) {
		// чи можна виконати операцію what ("D"/"C") над рахунком numb типу kind ("act"/"pas") сумою sum і датою op
		boolean res = true;
		if (kind.equals("act") && what.equals("D") || kind.equals("pas") && what.equals("C")) return true;
		try{
			Date last = null;
		
			sql = "select max(day) from amount where number = '" + numb + "' and day <= '" + op + "'";
			s.execute(sql);
			rs = s.getResultSet();
			if((rs!=null) && (rs.next())) {if (rs.getObject(1)!=null) last = rs.getDate(1);}
			String condSum = " sum + " + sum + " <= 0 " ;
			if (what.equals("D")) condSum = "  sum - " + sum + " >= 0 ";
			sql = "select day from amount where " + condSum + " and number = '" + numb + "' and day >= '" + last + "'";
			System.out.println("iswfMoveSum> " + sql);
			s.execute(sql);
			rs = s.getResultSet();
			while((rs!=null) && (rs.next())) {
				res = false;
				msgError += " \nProblem with sum on date " + rs.getDate(1); 
			}
		} catch (SQLException e) {
		    	res=false;
		    	msgError += "iswfMoveSum> " + e.getMessage();
		    	System.out.println("iswfMoveSum> " + e.getMessage());
		}  	
		return res;
	}
	/*
	public boolean isPutClient(Date begin, int idCl, float sum) {
		boolean res = true;
		try{
			msgError = "";
			int idPr=0;
			Date beg = null;
			sql = "select idPr, begin from product where idCl = " + idCl + " and kind = 'current'";
			s.execute(sql);
			rs = s.getResultSet();
			if((rs!=null) && (rs.next())) {
				idPr = rs.getInt(1);
				if (rs.getObject(2)!=null) beg = rs.getDate(2);
			}
			if (idPr==0) {
				if (!msgError.isEmpty()) msgError +="\n";
				msgError += " Not found current product";
				res = false;
			} else	if (begin.compareTo(beg)<0) {
				if (!msgError.isEmpty()) msgError +="\n";
				msgError += " Date operation befor " + beg.toString();
				res = false;
			}
			if (!res)System.out.println("isPutClient> " + msgError);
	    } catch (SQLException e) {
	    	res=false;
	    	System.out.println("isPutClient> " + e.getMessage());
	    }  	
		return res;
	}
	*/	
	// ----------------------------------------------------------------
	public boolean beginClient(Date begin, String name) {
		boolean res = true;
		/*
		int idPr =0;
		String numb ="";
		int idCl = nextId("client");
		if (idCl == 0) return false;
		idPr = nextId("product");
		if (idPr == 0) return false;
		numb = nextNumber("current");
		if (numb.isEmpty()) return false;
		*/
		try {
			conn.setAutoCommit(false);
			try{
				int idCl = nextId("client");
				int idPr = nextId("product");
				String numb = nextNumber("current");
				res = insertClient(idCl, name);
				if (res) res = insertProduct(idPr, idCl, "current", begin);
				if (res) res = insertAccount(numb, idPr, "current", "pas");
				if (res) conn.commit();
				else conn.rollback();
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("beginClient1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("beginClient2> " + e.getMessage());}	
		return res;
	}
	public boolean putClient(Date begin, int idCl, float sum) {
		boolean res = true;
		try {
			conn.setAutoCommit(false);
			try{
				String numbCash = "", numbCl = "";
				int idPr = 0;
				sql = "select idPr from product where idCl = " + idCl + " and kind = 'current'";
				s.execute(sql);
				rs = s.getResultSet();
				if((rs!=null) && (rs.next())) idPr = rs.getInt(1);
				sql = "select number from account  where idPr = " + idPr + " and name = 'current' ";
				s.execute(sql);	rs = s.getResultSet();
				if((rs!=null) && (rs.next()) && (rs.getObject(1)!=null)) numbCl = rs.getString(1);
				sql = "select number from account where name = 'cash' and idPr is NULL";
				s.execute(sql);	rs = s.getResultSet();
				if((rs!=null) && (rs.next()) && (rs.getObject(1)!=null)) numbCash = rs.getString(1);
				//System.out.println("putClient1> " + numbCash + "=>"+ numbCl);
				int idOp = nextId("operation");
				int idM = nextId("movement");
				res = insertOperation(idOp,idPr,begin,"account","put");
				if(res) res = insertAccountOp(idOp,sum,0);
				if(res) res= insertMovement(idM,sum,numbCash,numbCl,idOp);
				modifyAmount(numbCash,begin,-sum);
				modifyAmount(numbCl,begin,sum);
				if(res) conn.commit(); else conn.rollback(); 
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("putClient1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("putClient2> " + e.getMessage());}	
		return res;	
	}
	
	public void modifyAmount(String acc, Date dOp, float sum) throws SQLException {
		Date last = null;
		float begSum = 0;
		//sql =  "select max(B.day), B.sum from amount as B " + 
		//       " where B.number = '" + acc + "' and B.day <= '" + dOp + "' group by B.sum";
		sql =  "select max(day) from amount where number = '" + acc + "' and day <= '" + dOp + "'";
		System.out.println("modifyAmount1 " + sql);
		s.execute(sql);
		rs = s.getResultSet();
		if((rs!=null) && (rs.next()  && (rs.getObject(1)!=null) )) {
			last = rs.getDate(1); 
			if (!last.equals(dOp)) {
				sql = "select sum from amount where number = '" + acc + "' and day = '" + last + "'";
				// System.out.println(sql);
				s.execute(sql);
				rs = s.getResultSet();
			    if((rs!=null) && (rs.next())) begSum = rs.getFloat(1);
			}
			//System.out.println("last = " + last + " dOp = " + dOp + " begSum = " + begSum);
		}
		//System.out.println("(last == null) = " + (last == null) + " (!last.equals(dOp)) = " + (!last.equals(dOp)));
		if ((last == null) || (!last.equals(dOp))) {
			sql = "insert into amount values ('" + acc + "', '" + dOp + "',"  + begSum + ")"; 
			System.out.println("modifyAmount2 " + sql);
			s.executeUpdate(sql);
		}
		String sign = (sum<0? "-" : "+");
		sql = "update amount set sum = sum " + sign + " " + Math.abs(sum) + 
			  " where number = '" + acc + "' and day >= '" + dOp + "'";
		System.out.println("modifyAmount3 " + sql);
		s.executeUpdate(sql);
	}
	
	// --------------------------------------------------------
	public boolean insertClient(int idCl,String name) {
		try{
			sql = "insert into client values (" + idCl +",'" + name + "')";
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e) {
			System.out.println("inserClient> " + e.getMessage());}
		return true;
	}
	public boolean insertProduct(int idPr, int idCl, String kind, Date begin) {
		try{
			sql = "insert into product values (" + idPr + ","  + idCl +",'" + kind + "','" + begin + "')";
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e) {
			System.out.println("inserProduct> " + e.getMessage());}
		return true;
	}
	public boolean insertOperation(int idOp, int idPr, Date dOp, String kind, String type) {
		try{
			sql = "insert into operation values (" + idOp + "," + idPr + ",'"  + dOp +"','" + kind + "','" + type + "')";
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e) {
			System.out.println("insertOperation> " + e.getMessage());}
		return true;
	}
	public boolean insertAccountOp(int idOp, float sum, int idCl) {
		try{
			String idCls = (idCl==0?"NULL":""+idCl);
			sql = "insert into accountOp values (" + idOp + "," + sum + ","  + idCls + ")";
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e) {
			System.out.println("insertAccountOp> " + e.getMessage());}
		return true;
	}
	public boolean insertAccount(String numb, int idPr, String name, String kind) {
		try{
			sql = "insert into account values ('" + numb + "'," + idPr + ",'"  + name +"','" + kind + "')";
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e) {
			System.out.println("inserAccount> " + e.getMessage());}
		return true;
	}	
	public boolean insertMovement(int idM, float sum, String numberD, String numberC, int idOp) {
		try{
			sql = "insert into movement values (" + idM + "," + sum + ",'"  + numberD +"','" + numberC + "'," + idOp + ")";
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e) {
			System.out.println("insertMovement> " + e.getMessage());}
		return true;
	}
	// --------------------- last ---------------------------
	public int last(String table){
		int i = 0;
		String id;
		switch(table) {
		case "client": id = "idCl"; break;
		case "product": id = "idPr"; break;
		case "movement": id = "idM"; break;
		default: id = "idOp"; break;        //operation
		}
		try{
			sql = "select max(" + id + ") from " + table;
			// System.out.println(sql);
			s.execute(sql);
			rs = s.getResultSet();
		    if((rs!=null) && (rs.next()))
		    	   if (rs.getObject(1)!=null) i = rs.getInt(1);
		}
		catch (Exception e) {i = -1; System.out.println("last(" + table + ")> " + e.getMessage());}
		return i;
	}	
	public String lastNumber(String what){
		String res = "0000";
		String cl = Account.evalClass(what);
		try{
			sql = "select max(substr(number,3)) from account where substr(number,1,1)= '" + cl + "'" ;
			s.execute(sql);
			rs = s.getResultSet();
			if((rs!=null) && (rs.next())) 
				if (rs.getString(1)!=null) res = rs.getString(1);   // 
		}
		catch (Exception e) {res = null; System.out.println("lastNumber(" + what + ")> " + e.getMessage());}
		//System.out.println("cl=" + cl + " res = " + res);
		return res;
	}		
	// -------------- take ----------------------------------
	public Client takeClient(int idCl) {
		Client cl = null; 
		try{
			sql = "select name from client where idCl = " + idCl;
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) cl = new Client(idCl,rs.getString(1));
		}
		catch (Exception e) {System.out.println("takeClient> " + e.getMessage());}
		return cl;
	}
	public AccountOp takeAccountOp(int idOp) {
		AccountOp ao = null; 
		try{
			sql = "select idPr, day, type, sum, idCl from operation as Op " +
		          "   left join accountop as Ao on Op.idOp=Ao.idOp " +
				  " where Op.idOp = " + idOp + " and kind = 'account'";
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) 
		    	   ao = new AccountOp(idOp, rs.getInt(1), rs.getDate(2).toLocalDate(), rs.getString(3), rs.getFloat(4), rs.getInt(5));
		}
		catch (Exception e) {System.out.println("takeAccountOp> " + e.getMessage());}
		return ao;
	}
	public Current takeCurrent(int idCl) {
		Current cur = null; 
		try{
			sql = "select idPr, begin from product where idCl = " + idCl + " and kind = 'current'";
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) {
		    	   //Date day = rs.getDate(2);
		    	   //LocalDate lday = day.toLocalDate();
		    	   cur = new Current(rs.getInt(1), idCl, rs.getDate(2).toLocalDate());
		       }
		}
		catch (Exception e) {System.out.println("takeCurrent> " + e.getMessage());}
		return cur;
	}
	public Instant takeInstant(int idPr) {
		Instant ins = null; 
		try{
			sql = "select p.idCl, p.begin, i.kind, i.sum, i.end, i.rate, i.state from product as p " + 
		          " inner join instant as i on (p.idPr=i.idPr ) where p.idPr = " + idPr;
			//System.out.println("takeInstant> " + sql);
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) {
		    	   //Date day = rs.getDate(2);
		    	   //LocalDate lday = day.toLocalDate();
		    	   ins = new Instant(idPr, rs.getInt(1), rs.getDate(2).toLocalDate(), rs.getString(3),
		    			   rs.getFloat(4), rs.getDate(5).toLocalDate(), rs.getFloat(6), rs.getString(7));
		       }
		}
		catch (Exception e) {System.out.println("takeInstant> " + e.getMessage());}
		return ins;
	}
	public Account takeAccount(int idPr, String name) {
		Account ac = null; 
		try{
			sql = "select number, kind from account where idPr = " + idPr + " and name = '" + name + "'";
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) ac = new Account(rs.getString(1), idPr, name, rs.getString(2));
		}
		catch (Exception e) {System.out.println("takeAccount> " + e.getMessage());}
		return ac;
	}
	public Amount takeAmount(String number, LocalDate day) {
		Amount am = null; 
		try{
			sql = "select day, sum from amount as M where M.number = " + number + " and M.day = " + 
		            " (select max(A.day) from amount as A where M.number=A.number and A.day <= '" + day + "')";
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) am = new Amount(number, rs.getDate(1).toLocalDate(), rs.getFloat(2));
		}
		catch (Exception e) {System.out.println("takeAmount> " + e.getMessage());}
		return am;
	}
	public ArrayList <Amount> takeListAmount(String number, LocalDate day) {
		ArrayList<Amount> al = new ArrayList<>(); 
		try{
			sql = "select day, sum from amount as M where M.number = " + number + " and M.day > '" + day + "'";
			//System.out.println("takeListAmount> " + sql);
			s.execute(sql);
			rs = s.getResultSet();
			while((rs!=null) && (rs.next())) {
		       al.add(new Amount(number, rs.getDate(1).toLocalDate(), rs.getFloat(2)));
			}   
		}
		catch (Exception e) {System.out.println("takeListAmount> " + e.getMessage());}
		return al;
	}	
	public Movement takeMovement(int idM) {
		Movement mv = null; 
		try{
			sql = "select sum, numberD, numberC, idOp from movement where idM = " + idM;
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next()))
		    	   mv = new Movement(idM, rs.getFloat(1), rs.getString(2), rs.getString(3), rs.getInt(4));
		}
		catch (Exception e) {System.out.println("takeMovement> " + e.getMessage());}
		return mv;
	}
	public ArrayList <Event> takeListEvent() {
		ArrayList<Event> el = new ArrayList<>(); 
		try{
			sql = "select id,day,product,operation,idCP,sum,rate,end,idC,name from event order by id";
			//System.out.println("takeListAmount> " + sql);
			s.execute(sql);
			rs = s.getResultSet();
			while((rs!=null) && (rs.next())) {
				int idCP=0, idC=0;
				float sum=0,rate=0;
				LocalDate end = LocalDate.of(2000, 1,1);
				String name="";
				if (rs.getObject("idCP")!=null) idCP = rs.getInt("idCP");
				if (rs.getObject("sum")!=null) sum = rs.getFloat("sum");
				if (rs.getObject("rate")!=null) rate = rs.getFloat("rate");
				if (rs.getObject("end")!=null) end = rs.getDate("end").toLocalDate();
				if (rs.getObject("idC")!=null) idC = rs.getInt("idC");
				if (rs.getObject("name")!=null) name = rs.getString("name");
		        el.add(new Event(rs.getInt("id"),rs.getDate("day").toLocalDate(),rs.getString("product"),
		        		         rs.getString("operation"),idCP,sum,rate,end,idC,name));
			}   
		}
		catch (Exception e) {System.out.println("takeListEvent> " + e.getMessage());}
		return el;
	}	
	//-------------- MAIN OPERATION---------------------------
	public boolean beginClient(Client cl, Current cur, Account acc) {
		boolean res = true;
		try {
			conn.setAutoCommit(false);
			try{
				addClient(cl.getIdCl(),cl.getName());
				addProduct(cur.getIdPr(),cur.getIdCl(),"current",Date.valueOf(cur.getBegin()));
				addAccount(acc.getNumber(), acc.getIdPr(), acc.getName(),acc.getKind());
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("beginClientE1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("beginClientE2> " + e.getMessage());}	
		return res;
	}
	public boolean opClient(AccountOp aop, Movement mv, Amount adeb, Amount acre) {
		boolean res = true;
		try {
			conn.setAutoCommit(false);
			try{
				addOperation(aop.getIdOp(),aop.getIdPr(),Date.valueOf(aop.getDay()),"account",aop.getType());
				addAccountOp(aop.getIdOp(),aop.getSum(), 0);
				if(adeb!=null) addAmount(adeb.getNumber(),Date.valueOf(adeb.getDay()),adeb.getSum());
				if(acre!=null) addAmount(acre.getNumber(),Date.valueOf(acre.getDay()),acre.getSum());
				addMovement(mv.getIdM(),mv.getSum(),mv.getNumberD(),mv.getNumberC(),mv.getIdOp(),Date.valueOf(aop.getDay()));
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("opClientE1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("opClientE2> " + e.getMessage());}	
		return res;
	}
	public boolean beginDeposit(Instant ins, DeposOp dop, Account acc, Movement mv, Amount adeb, Amount acre) {
		boolean res = true;
		try {
			conn.setAutoCommit(false);
			try{
				addProduct(ins.getIdPr(),ins.getIdCl(),"time",Date.valueOf(ins.getBegin()));
				addInstant(ins.getIdPr(),ins.getKind(),ins.getSum(),Date.valueOf(ins.getEnd()),ins.getRate(),ins.getState());
				addOperation(dop.getIdOp(),dop.getIdPr(),Date.valueOf(dop.getDay()),"deposit",dop.getType());
				addAccount(acc.getNumber(), acc.getIdPr(), acc.getName(), acc.getKind());
				if(adeb!=null) addAmount(adeb.getNumber(),Date.valueOf(adeb.getDay()),adeb.getSum());
				if(acre!=null) addAmount(acre.getNumber(),Date.valueOf(acre.getDay()),acre.getSum());
				addMovement(mv.getIdM(),mv.getSum(),mv.getNumberD(),mv.getNumberC(),mv.getIdOp(),Date.valueOf(dop.getDay()));
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("begiDepos1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("beginDepos2> " + e.getMessage());}	
		return res;
	}
	public boolean opDeposit(DeposOp dop, Account accCost, Movement mvd, Movement mvp, Amount adep, Amount acur, Amount acost) {
		boolean res = true;
		try {
			conn.setAutoCommit(false);
			try{
				updInstant(dop.getIdPr(),dop.getType());
				addOperation(dop.getIdOp(),dop.getIdPr(),Date.valueOf(dop.getDay()),"deposit",dop.getType());
				if (accCost!=null) addAccount(accCost.getNumber(), accCost.getIdPr(), accCost.getName(), accCost.getKind());
				if(adep!=null) addAmount(adep.getNumber(),Date.valueOf(adep.getDay()),adep.getSum());
				if(acur!=null) addAmount(acur.getNumber(),Date.valueOf(acur.getDay()),acur.getSum());
				if(acost!=null) addAmount(acost.getNumber(),Date.valueOf(acost.getDay()),acost.getSum());
				addMovement(mvd.getIdM(),mvd.getSum(),mvd.getNumberD(),mvd.getNumberC(),mvd.getIdOp(),Date.valueOf(dop.getDay()));
				if (mvp!=null)addMovement(mvp.getIdM(),mvp.getSum(),mvp.getNumberD(),mvp.getNumberC(),mvp.getIdOp(),Date.valueOf(dop.getDay()));
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("opDepos1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("opDepos2> " + e.getMessage());}	
		return res;
	}
	//--------- add with throws Exception ------------------------
	public void addClient(int idCl, String name) throws Exception {
		sql = "insert into client values (" + idCl + ",'" + name + "')";
		//System.out.println("addClient> " + sql);
		s.executeUpdate(sql);
	}
	public void addProduct(int idPr, int idCl, String kind, Date begin) throws Exception {
		sql = "insert into product values (" + idPr + "," + idCl + ",'" + kind + "','" + begin + "')";
		//System.out.println("addProduct> " + sql);
		s.executeUpdate(sql);
	}
	public void addInstant(int idPr, String kind, float sum, Date end, float rate, String state) throws Exception {
		sql = "insert into instant values (" + idPr + ",'" + kind + "'," + sum + ",'" + end + "'," +  rate + ",'" + state + "')";
		//System.out.println("addInstant> " + sql);
		s.executeUpdate(sql);
	}
	public void updInstant(int idPr, String state) throws Exception {
		sql = "update instant set state = '" + state + "' where idPr = " + idPr;
		//System.out.println("updInstant> " + sql);
		s.executeUpdate(sql);
	}
	public void addAccount(String number, int idPr, String name, String kind) throws Exception {
		sql = "insert into account values ('" + number + "'," + idPr + ",'" + name + "','" + kind + "')";
		//System.out.println("addAccount> " + sql);
		s.executeUpdate(sql);
	}	
	public void addOperation(int idOp, int idPr, Date dOp, String kind, String type) throws Exception {
		sql = "insert into operation values (" + idOp + "," + idPr + ",'"  + dOp +"','" + kind + "','" + type + "')";
		//System.out.println("addOperation> " + sql);
		s.executeUpdate(sql);
	}
	public void addAccountOp(int idOp, float sum, int idCl) throws Exception {
		String sIdCl = "NULL";
		if (idCl>0) sIdCl = "" + idCl;
		sql = "insert into accountop values (" + idOp + "," + sum + ","  + sIdCl + ")";
		//System.out.println("addAccountOp> " + sql);
		s.executeUpdate(sql);
	}	
	public void addAmount(String number, Date day, float sum) throws Exception {
		sql = "insert into amount values ('" + number + "','" + day + "',"  + sum + ")";
		s.executeUpdate(sql);
	}
	public void addMovement(int idM, float sum, String numberD, String numberC,  int idOp, Date day) throws Exception {
		sql = "insert into movement values (" + idM + "," + sum + ",'"  + numberD +"','" + numberC + "'," + idOp + ")";
		s.executeUpdate(sql);
		sql = "update amount set sum = sum - " + sum + " where number = '" + numberD + "' and day >= '" + day + "'";
		s.executeUpdate(sql);
		sql = "update amount set sum = sum + " + sum + " where number = '" + numberC + "' and day >= '" + day + "'";
		s.executeUpdate(sql);
	}
	// ---------------------
	public boolean beginClient1(Client cl, Current cr, Account ac) {
		boolean res = true;
		try {
			conn.setAutoCommit(false);
			try{
				int idCl = cl.getIdCl();
				int idPr = cr.getIdPr();
				res = insertClient(idCl, cl.getName());
				if (res) res = insertProduct(idPr, idCl, "current", Date.valueOf(cr.getBegin()));
				if (res) res = insertAccount(ac.getNumber(), idPr, "current", "pas");
				if (res) conn.commit();
				else conn.rollback();
			}
			catch (Exception e) {
				res = false;
				conn.rollback();
				System.out.println("beginClientE1> " + e.getMessage());
			}
			conn.setAutoCommit(true);
		}	
		catch (Exception e) { res=false; System.out.println("beginClientE2> " + e.getMessage());}	
		return res;
	}
	
	// --------------------------------------------------
	public int nextId(String table){
		int i = 0;
		String id;
		switch(table) {
		case "client": id = "idCl"; break;
		case "product": id = "idPr"; break;
		case "movement": id = "idM"; break;
		default: id = "idOp"; break;        //operation
		}
		try{
			sql = "select max(" + id + ") from " + table;
			// System.out.println(sql);
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next()))
		    	   if (rs.getObject(1)!=null) i = rs.getInt(1);
		}
		catch (Exception e) {System.out.println("nextId> " + e.getMessage());}
		return (i+1);
	}	
	public String nextNumber(String what){
		String res = "0000";
		String cl;
		switch(what) {
		case "cash": cl = "1"; break;
		case "current": cl = "2"; break;
		case "deposit": cl = "3"; break;
		case "credit": cl = "4"; break;
		case "out": cl = "5"; break;
		case "income": cl = "7"; break;
		case "costDep": cl = "8"; break;
		case "costLoss": cl = "9"; break;
		default: cl = "6"; break;        // capital
		}
		try{
			sql = "select max(substr(number,3)) from account where substr(number,1,1)= '" + cl + "'" ;
			s.execute(sql);
			rs = s.getResultSet();
			if((rs!=null) && (rs.next())) 
				if (rs.getString(1)!=null) res = rs.getString(1);   // 
		}
		catch (Exception e) {System.out.println("nextId> " + e.getMessage());}
		//System.out.println("cl=" + cl + " res = " + res);
		return evalNextNumber(cl,res);
	}		
	private String evalNextNumber(String cl, String part) {
		int next=1, sumNewPart=0, test=0, v;
		String newPart="";
		for(int i = part.length();i>0;i-=1) {
			int ch = part.charAt(i-1)- '0';
			                                              //System.out.println("i = " + i + " ch = " + ch + " next = " + next );
			ch+=next; next=ch / 10; ch=ch % 10;
			sumNewPart+=ch;	newPart = ch + newPart;
			                                              //System.out.println("i = " + i + " ch = " + ch + " next = " + next + "..." + " newPart = " + newPart);
		}
		sumNewPart += cl.charAt(0)-'0';
		switch (sumNewPart % 10) {
		case 0: test = 1; break;
		case 1: break;
		default: test = 11-sumNewPart % 10;
		}
		return cl+test+newPart;
	}
	
	public Client getClient(int idCl) {
		Client cl = null; 
		try{
			sql = "select name from client where idCl = " + idCl;
			// System.out.println(sql);
			s.execute(sql);
			rs = s.getResultSet();
		       if((rs!=null) && (rs.next())) cl = new Client(idCl,rs.getString(1));
		}
		catch (Exception e) {System.out.println("nextId> " + e.getMessage());}
		return cl;
	}
	
	// ---------------------------------------------------------------------
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String cl="3", part="0000";
		//System.out.println("cl=" + cl + " Part = " + part + " nextNumber =" + evalNextNumber(cl,part));
	}
}
