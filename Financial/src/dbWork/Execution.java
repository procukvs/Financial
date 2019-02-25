package dbWork;

import java.time.*;
import java.util.*;

public class Execution {
	private DbAccess db;
	String msgError ="";
	public Execution(DbAccess db) {
		this.db = db;
	}
	
	// forming new client .....
	public Client beginClient(String name, LocalDate day) {
		Client cl = null;
		int idCl = db.last("client");
		int idPr = db.last("product");
		String number = db.lastNumber("current");
		if ((idCl != -1) && (idPr != -1) && (!number.isEmpty())) {
			idCl++; idPr++; number = Account.evalNextNumber(Account.evalClass("current"), number);
			cl = new Client(idCl, name); 
			Current cr = new Current(idPr,idCl,day);
			Account ac = new Account(number,idPr, "current", "pas");
			if(!db.beginClient(cl, cr,ac)) {msgError = "Not beginClient in Db !"; cl = null;}
		} else msgError = "Problem with base components !";
		
		return cl;
	}
	
	public ArrayList<String> iswfPutClient(LocalDate day, int idCl, float sum){
		ArrayList<String> er = new ArrayList<>();
		Client cl = db.takeClient(idCl);
		if (cl != null) er.addAll(cl.iswfPut(db, day, sum));
		else er.add("Not find client " + idCl);
		return er;
	}
	
	public boolean putClient(LocalDate day, int idCl, float sum) {
		boolean res = true;
		Current cur = db.takeCurrent(idCl);
		Account acc = db.takeAccount(cur.getIdPr(), "current");
		int idOp = db.last("operation")+1; 
		AccountOp aOp = new AccountOp(idOp, cur.getIdPr(), day, "put", sum, 0);
		Account cash = Account.takeCash();
		System.out.println(" cash = " + cash.toString());
		Amount acash = db.takeAmount(cash.getNumber(), day);
		//System.out.println(" acash = " + acash.toString());
		Amount adeb = null;
		if(acash==null) adeb = new Amount(cash.getNumber(), day, 0); 
		else if (acash.getDay().compareTo(day)<0) adeb = new Amount(acash.getNumber(), day, acash.getSum());
		Amount acur = db.takeAmount(acc.getNumber(), day);
		Amount acre = null;
		if (acur==null) acre = new Amount(acc.getNumber(), day, 0); 
		else if (acur.getDay().compareTo(day)<0) acre = new Amount(acur.getNumber(), day, acur.getSum());
		Movement mv = new Movement(db.last("movement")+1, sum,cash.getNumber(),acc.getNumber(), idOp);
		return db.opClient(aOp, mv, adeb, acre);
	}
	
	public ArrayList<String> iswfTakeClient(LocalDate day, int idCl, float sum){
		ArrayList<String> er = new ArrayList<>();
		Client cl = db.takeClient(idCl);
		if (cl != null) er.addAll(cl.iswfTake(db, day, sum));
		else er.add("Not find client " + idCl);
		return er;
	}
	
	public boolean takeClient(LocalDate day, int idCl, float sum) {
		boolean res = true;
		Current cur = db.takeCurrent(idCl);
		Account acc = db.takeAccount(cur.getIdPr(), "current");
		int idOp = db.last("operation")+1; 
		AccountOp aOp = new AccountOp(idOp, cur.getIdPr(), day, "take", sum, 0);
		Amount acur = db.takeAmount(acc.getNumber(), day);
		Amount adeb = null;
		if (acur.getDay().compareTo(day)<0) adeb = new Amount(acur.getNumber(), day, acur.getSum());
		Account cash = Account.takeCash();
		Amount acash = db.takeAmount(cash.getNumber(), day);
		Amount acre = null;
		if (acash.getDay().compareTo(day)<0) acre = new Amount(acash.getNumber(), day, acash.getSum());
		Movement mv = new Movement(db.last("movement")+1, sum,acur.getNumber(),acash.getNumber(), idOp);
		return db.opClient(aOp, mv, adeb, acre);
	}
	
	public void initial() {
		ArrayList<Event> el = db.takeListEvent();
		ArrayList<String> ap = null;
		for(int i=0;i<el.size();i++) {
			Event ev = el.get(i);
			System.out.println(ev.toString());
			boolean isExec = true;
			boolean res = false;
			switch(ev.getProduct()) {
			case "A": 
				switch (ev.getOperation()) {
				case "begin": ap = new ArrayList<>(); res = true;
				              beginClient(ev.getName(), ev.getDay()); break;
				case "put": ap = iswfPutClient(ev.getDay(), ev.getIdCP(), ev.getSum());
				            if (ap.isEmpty()) res = putClient(ev.getDay(), ev.getIdCP(), ev.getSum());  break;
				case "take":ap = iswfTakeClient(ev.getDay(), ev.getIdCP(), ev.getSum());
	                        if (ap.isEmpty()) res = takeClient(ev.getDay(), ev.getIdCP(), ev.getSum());  break;
				case "move": isExec = false; break;	
				} break;
			case "D": case "C":	isExec = false; break;	
			}
			if (isExec) {
				if (ap.isEmpty()) System.out.println("Execution event " + ev.getId() + " is " + res);
				else for(int j=0; j<ap.size();j++) System.out.println("  :" + ap.get(j));
			} else 	System.out.println("Execution event " + ev.getId() + ":" + ev.getProduct() + "-" + ev.getOperation() +  " not relize!");   
		}
	}

}
