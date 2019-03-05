package dbWork;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Execution {
	private DbAccess db;
	String msgError ="";
	public Execution(DbAccess db) {
		this.db = db;
	}
	
	/*
	public Client beginClient1(String name, LocalDate day) {
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
	*/
	// forming new client .....
	public boolean beginClient(String name, LocalDate day) {
		int idCl = db.last("client")+1;
		int idPr = db.last("product")+1;
		String number = db.lastNumber("current");
		number = Account.evalNextNumber(Account.evalClass("current"), number);
		Client cl = new Client(idCl, name); 
		Current cur = new Current(idPr,idCl,day);
		Account acc = new Account(number,idPr, "current", "pas");
		return db.beginClient(cl,cur, acc);
	}
	
	public ArrayList<String> iswfPutClient(LocalDate day, int idCl, float sum){
		ArrayList<String> er = new ArrayList<>();
		Client cl = db.takeClient(idCl);
		if (cl != null) er.addAll(cl.iswfPut(db, day, sum));
		else er.add("Not find client " + idCl);
		return er;
	}
	
	public boolean putClient(LocalDate day, int idCl, float sum) {
		//boolean res = true;
		Current cur = db.takeCurrent(idCl);
		Account acc = db.takeAccount(cur.getIdPr(), "current");
		int idOp = db.last("operation")+1; 
		AccountOp aOp = new AccountOp(idOp, cur.getIdPr(), day, "put", sum, 0);
		Account cash = Account.takeCash();
		System.out.println(" cash = " + cash.toString());
		/*
		Amount acash = db.takeAmount(cash.getNumber(), day);
		Amount adeb = null;
		if(acash==null) adeb = new Amount(cash.getNumber(), day, 0); 
		else if (acash.getDay().compareTo(day)<0) adeb = new Amount(acash.getNumber(), day, acash.getSum());
		*/
		Amount adeb = Amount.maybeAmount(db, cash.getNumber(), day);
		/*
		Amount acur = db.takeAmount(acc.getNumber(), day);
		Amount acre = null;
		if (acur==null) acre = new Amount(acc.getNumber(), day, 0); 
		else if (acur.getDay().compareTo(day)<0) acre = new Amount(acur.getNumber(), day, acur.getSum());
		*/
		Amount acre = Amount.maybeAmount(db, acc.getNumber(), day);
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
		//boolean res = true;
		Current cur = db.takeCurrent(idCl);
		Account acc = db.takeAccount(cur.getIdPr(), "current");
		int idOp = db.last("operation")+1; 
		AccountOp aOp = new AccountOp(idOp, cur.getIdPr(), day, "take", sum, 0);
		/*
		Amount acur = db.takeAmount(acc.getNumber(), day);
		Amount adeb = null;
		if (acur.getDay().compareTo(day)<0) adeb = new Amount(acur.getNumber(), day, acur.getSum());
		*/
		Amount adeb = Amount.maybeAmount(db, acc.getNumber(), day);
		Account cash = Account.takeCash();
		/*
		Amount acash = db.takeAmount(cash.getNumber(), day);
		Amount acre = null;
		if (acash.getDay().compareTo(day)<0) acre = new Amount(acash.getNumber(), day, acash.getSum());
		*/
		Amount acre = Amount.maybeAmount(db, cash.getNumber(), day);
		Movement mv = new Movement(db.last("movement")+1, sum,acc.getNumber(),cash.getNumber(), idOp);
		return db.opClient(aOp, mv, adeb, acre);
	}
	
	public ArrayList<String> iswfMoveClient(LocalDate day, int idCl1, float sum, int idCl2){
		ArrayList<String> er = new ArrayList<>();
		Client cl1 = db.takeClient(idCl1);
		if (cl1 == null) er.add("Not find client " + idCl1);
		Client cl2 = db.takeClient(idCl2);
		if (cl2 == null) er.add("Not find client " + idCl2);
		if ((cl1 != null) && (cl2 != null )) {
			er.addAll(cl1.iswfMoveFrom(db, day, sum));
			er.addAll(cl2.iswfMoveTo(db, day, sum));
		}
		return er;
	}
	public boolean moveClient(LocalDate day, int idCl1, float sum, int idCl2) {
		Current cur1 = db.takeCurrent(idCl1);
		Account acc1 = db.takeAccount(cur1.getIdPr(), "current");
		int idOp = db.last("operation")+1; 
		AccountOp aOp = new AccountOp(idOp, cur1.getIdPr(), day, "move", sum, idCl2);
		Current cur2 = db.takeCurrent(idCl2);
		Account acc2 = db.takeAccount(cur2.getIdPr(), "current");
		Amount adeb = Amount.maybeAmount(db, acc1.getNumber(), day);
		Amount acre = Amount.maybeAmount(db, acc2.getNumber(), day);
		Movement mv = new Movement(db.last("movement")+1, sum,acc1.getNumber(),acc2.getNumber(), idOp);
		return db.opClient(aOp, mv, adeb, acre);
	}	
	// work with DEPOSIT
	//  new deposit 
	//   - client idCl form new deposit on sum/perc from day to end
	public ArrayList<String> iswfBeginDeposit(LocalDate day, int idCl, float sum, float perc, LocalDate end){
		ArrayList<String> er = new ArrayList<>();
		Client cl = db.takeClient(idCl);
		//System.out.println("Client id = " + idCl + " is = " + (cl != null) + " == " + cl.toString() );
		if (cl != null) {
			if(!day.isBefore(end)) er.add("End of deposit " + end + " must be after begin of deposit " + day);
			if((perc<0)||(perc>=100)) er.add("Per cent of deposit must be from 0 to 100, but not " + perc + "%."); 
			er.addAll(cl.iswfMoveFrom(db, day, sum));
		} else er.add("Not find client " + idCl);
		return er;
	}
	public boolean beginDeposit(LocalDate day, int idCl, float sum, float perc, LocalDate end) {
		Current cur = db.takeCurrent(idCl);
		Account acc1 = db.takeAccount(cur.getIdPr(), "current");
		int idOp = db.last("operation")+1; 
		int idPr = db.last("product")+1;
		String number = db.lastNumber("deposit");
		number = Account.evalNextNumber(Account.evalClass("deposit"), number);
		Instant ins = new Instant(idPr,idCl,day,"deposit",sum,end,perc,"work");
		Account acc = new Account(number,idPr, "deposit", "pas");
		DeposOp dOp = new DeposOp(idOp, idPr, day, "begin");
		Amount adeb = Amount.maybeAmount(db, acc1.getNumber(), day);                         
		Amount acre = Amount.maybeAmount(db, number, day);
		Movement mv = new Movement(db.last("movement")+1, sum,acc1.getNumber(),number, idOp);
		return db.beginDeposit(ins, dOp, acc, mv, adeb, acre);
	}
    //  - financial close deposit idPr (day>= end) .. operation work in date day !!!!!!
	public ArrayList<String> iswfCloseDeposit(LocalDate day, int idPr){
		ArrayList<String> er = new ArrayList<>();
		Instant ins = db.takeInstant(idPr);
		if (ins != null) {
		   	if(!ins.getKind().equals("deposit")) er.add("Product " + idPr + " must be deposit, but not " + ins.getKind());
		   	if(!ins.getState().equals("work"))  er.add("Deposit must be work, but not " + ins.getState());
		   	//if(day.isBefore(ins.getEnd())) er.add("Date of operation " + day + " not can be befor end of deposit " + ins.getEnd());
		   	if(!day.equals(ins.getEnd())) er.add("Date of operation " + day + " must be equals end of deposit " + ins.getEnd());
		   	Account acc = db.takeAccount(idPr, "deposit");
		   	if (acc==null) er.add("Not find account deposit for product " + idPr);
			Client cl = db.takeClient(ins.getIdCl());
			//System.out.println("Client id = " + idCl + " is = " + (cl != null) + " == " + cl.toString() );
			if (cl != null) {
				er.addAll(cl.iswfMoveTo(db, day, 0));
			} else er.add("Not find client " + ins.getIdCl());
		} else er.add("Not find deposit (product " + idPr + ")");
		return er;
	}
	public boolean closeDeposit(LocalDate day, int idPr) {
		Instant ins = db.takeInstant(idPr);
		int idCl = ins.getIdCl();
		float sum = ins.getSum();
		Account accDep = db.takeAccount(idPr, "deposit");
		String numberDep = accDep.getNumber();
		Current cur = db.takeCurrent(idCl);
		Account accCur = db.takeAccount(cur.getIdPr(), "current");
		String numberCur = accCur.getNumber();
		String number = db.lastNumber("costDep");
		number = Account.evalNextNumber(Account.evalClass("costDep"), number);
		Account accCost = new Account(number,idPr, "costDep", "act");
		int idOp = db.last("operation")+1; 
		DeposOp dOp = new DeposOp(idOp, idPr, day, "close");
	
		float cost = evalPercent(ins.getEnd(), ins.getBegin(),sum,ins.getRate()); 
		LocalDate dayOp = ins.getEnd();
		int idM = db.last("movement")+1;
		// return deposit
		Amount adep = Amount.maybeAmount(db, numberDep, dayOp);                         
		Amount acur = Amount.maybeAmount(db, numberCur, dayOp);
		Amount acost = Amount.maybeAmount(db, number, dayOp);
		Movement mvs = new Movement(idM, sum, numberDep,numberCur, idOp);
		Movement mvp = new Movement(idM+1, cost, number,numberCur, idOp);
		return db.opDeposit(dOp, accCost, mvs, mvp, adep, acur, acost);
	}
	public static float evalPercent(LocalDate begin, LocalDate end, float sum, float rate) {
		long rlong = (long)(sum * ChronoUnit.DAYS.between(end,begin)*rate);
		return (float) rlong/100;
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
				case "begin": ap = new ArrayList<>();
				              res = beginClient(ev.getName(), ev.getDay()); break;
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
