package dbWork;

import java.sql.Date;
import java.util.*;

public class WorkDB {
	private DbAccess db =null; 
	
	public WorkDB(DbAccess db) {
		this.db = db;
	}
	
	public boolean beginClient(Date begin, String name) {
		boolean res = true;
		ArrayList param = new ArrayList();
		ArrayList sel;
		int idCl=0, idPr=0;
		String numb="";
		sel = db.selectOne("lastClient", param);
		//if(sel.size()==0) res = false; else  
		idCl = (int)sel.get(0);
		sel = db.selectOne("lastProduct", param);
		idPr = (int)sel.get(0);
		param.add("current");
		sel = db.selectOne("lastAccount", param);
		numb = evalNextNumber("current",(String)sel.get(0));
		
		
		return res;
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
		return cl+test+newPart;
	}

}
