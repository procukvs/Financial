package dbWork;

import java.sql.Date;
import java.util.*;

public class WorkDB {
	private DbAccessP db =null; 
	
	public WorkDB(DbAccessP db) {
		boolean res;
		this.db = db;
		if (db.connectionDb()){
			
			res = beginClient("2012-02-03"," ваша");
			System.out.println("res = " + res);	
	        db.disConnect();
		}
		else System.out.println("No connection to DB financial");	
		
	}
	
	// date is in format "yyyy-mm-dd"
	public boolean beginClient(String begin, String name) {
		boolean res = true;
		ArrayList param = new ArrayList();
		ArrayList sel;
		int idCl=0, idPr=0;
		String numb="";
		sel = db.selectOne("lastClient", param);
		//if(sel.size()==0) res = false; else  
		idCl = (int)sel.get(0) + 1;
		sel = db.selectOne("lastProduct", param);
		System.out.println("idPr:"+(int)sel.get(0));
		idPr = (int)sel.get(0)  + 1;
		param.add("current");
		sel = db.selectOne("lastNumber", param);
		System.out.println("part:"+(String)sel.get(0));
		numb = evalNextNumber("current",(String)sel.get(0));
		
		ArrayList precedent = new ArrayList();
		ArrayList step = new ArrayList();
		// insClient: idCl + name ----
		step.add("insClient"); step.add(idCl); step.add(name); 
		precedent.add(step); step = new ArrayList(); //step.clear();
		// insProduct: idPr + idCl + "current" + begin
		step.add("insProduct"); step.add(idPr); step.add(idCl); step.add("current");  
		step.add(Main.buildSqlDate(begin));  
		precedent.add(step); step = new ArrayList(); //step.clear();
		//  insAccount: numb + idPr + "current" + "pas"
		step.add("insAccount"); step.add(numb); step.add(idPr); step.add("current"); step.add("pas");  
		precedent.add(step);
		return db.execPrecedent("beginClient", precedent);
	}
	
	private String evalNextNumber(String kind, String part) {
		int next=1, sumNewPart=0, test=0, v;
		String cl="";
		String newPart="";
		switch(kind) {
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
		System.out.println("cl:" +cl+ ":test:"+test + ":newPart:" + newPart );
		return cl+test+newPart;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//db = new DbAccess();
		WorkDB wk = new WorkDB(new DbAccessP());
		
	}

}
