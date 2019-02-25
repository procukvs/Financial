package dbWork;

import java.time.LocalDate;
import java.util.ArrayList;

//import java.util.Date;

public class Account {
	private String number;
	private int idPr;
	private String name;
	private String kind;
	Account(String number, int idPr, String name, String kind){
		this.number = number; this.idPr = idPr; 
		this.name = name; this.kind = kind;
	}
	
	public String getNumber() {
		return number;
	}
	public int getIdPr() {
		return idPr;
	}
	
	public String getName() {
		return name;
	}
	
	public String getKind() {
		return kind;
	}
	// what = "D" .. "C"
	public ArrayList<String> iswfModify(DbAccess db, String what, LocalDate day, float sum){
		ArrayList<String> er = new ArrayList<>();
		if (kind.equals("act") && what.equals("C") || kind.equals("pas") && what.equals("D")) {
			Amount am = db.takeAmount(number, day);
			if (am!=null){
				if ((Math.abs(am.getSum())< sum)) er.add("On day " + day + " on account " + number + " no sum " + sum);
				ArrayList<Amount> al = db.takeListAmount(number, day); 
				for(int i=0; i<al.size();i++) {
					Amount am1 = al.get(i);
					if (Math.abs(am1.getSum())< sum) er.add("On day " + am1.getDay() + " on account " + number + " no sum " + sum);
				};
			} else er.add("On day " + day + " on account " + number + " no sum " + sum);
		}
		return er;
	}	
	public static Account takeCash() {
		return new Account("190001", 0, "cash","act");
	}
	public static String evalClass(String what) {
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
		return cl;
	}
	public static String evalNextNumber(String cl, String part) {
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
	@Override
	public String toString() {
		return "Account [number=" + number + ", idPr=" + idPr + 
				        ", name=" + name + ", kind=" + kind + "]";
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Account other = (Account) obj;
        if(!number.equals(other.getNumber())) return false;
        if(idPr != other.getIdPr())return false;
        if(!name.equals(other.getName())) return false;
        if(!kind.equals(other.getKind())) return false;
        return true;
	}		
}
