package dbWork;

import java.time.LocalDate;
import java.util.ArrayList;

public class Client {
	private int idCl;
	private String name;
	Client(int idCl, String name){
		this.idCl=idCl; this.name = name;
	}
	
	public int getIdCl() {
		return idCl;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> iswfPut(DbAccess db, LocalDate day, float sum){
		ArrayList<String> er = new ArrayList<>();
		// � �볺��� idCl ������� ���� ������� - �������� ������� (Current cur).
		Current cur = db.takeCurrent(idCl);
		if (cur !=null) {
			// ���� �������� day �� ������ ���� ����� �볺��� � ������� cur.getBegin()
			if(cur.getBegin().compareTo(day)>0) er.add("Date "+ day+ " less then date bein client " + cur.getBegin());
			// ������� cur (���� ����� cur.getIdPr()) ������� ���� ��"������ �������� ������� (Account acc)
			Account acc = db.takeAccount(cur.getIdPr(), "current");
			if (acc ==null) er.add("Not find account current for product " + cur.getIdPr());
		} else er.add("Not find product Current for client " + idCl);
		return er;
	}	
	public ArrayList<String> iswfTake(DbAccess db, LocalDate day, float sum){
		ArrayList<String> er = new ArrayList<>();
		// � �볺��� idCl ������� ���� ������� - �������� ������� (Current cur).
		Current cur = db.takeCurrent(idCl);
		if (cur !=null) {
			// ���� �������� day �� ������ ���� ����� �볺��� � ������� cur.getBegin()
			if(cur.getBegin().compareTo(day)>0) er.add("Date "+ day+ " less then date bein client " + cur.getBegin());
			// ������� cur (���� ����� cur.getIdPr()) ������� ���� ��"������ �������� ������� (Account acc)
			Account acc = db.takeAccount(cur.getIdPr(), "current");
			// �������� ������� acc ����� ��������� �� ���� sum ����� day 
			if (acc!=null) er.addAll(acc.iswfModify(db,"D", day, sum));
			else er.add("Not find account current for product " + cur.getIdPr());
			Account cash = Account.takeCash();
			// ������� ���� cash ����� ����������� �� ���� sum ����� day 
			er.addAll(cash.iswfModify(db,"C", day, sum));
		} else er.add("Not find product Current for client " + idCl);
		return er;
	}	
	public ArrayList<String> iswfMoveFrom(DbAccess db, LocalDate day, float sum){
		ArrayList<String> er = new ArrayList<>();
		// � �볺��� idCl ������� ���� ������� - �������� ������� (Current cur).
		Current cur = db.takeCurrent(idCl);
		if (cur !=null) {
			// ���� �������� day �� ������ ���� ����� �볺��� � ������� cur.getBegin()
			if(cur.getBegin().compareTo(day)>0) er.add("Date "+ day+ " less then date bein client " + cur.getBegin());
			// ������� cur (���� ����� cur.getIdPr()) ������� ���� ��"������ �������� ������� (Account acc)
			Account acc = db.takeAccount(cur.getIdPr(), "current");
			// �������� ������� acc ����� ��������� �� ���� sum ����� day 
			if (acc!=null) er.addAll(acc.iswfModify(db,"D", day, sum));
			else er.add("Not find account current for product " + cur.getIdPr());
		} else er.add("Not find product Current for client " + idCl);
		return er;
	}
	public ArrayList<String> iswfMoveTo(DbAccess db, LocalDate day, float sum){
		ArrayList<String> er = new ArrayList<>();
		// � �볺��� idCl ������� ���� ������� - �������� ������� (Current cur).
		Current cur = db.takeCurrent(idCl);
		if (cur !=null) {
			// ���� �������� day �� ������ ���� ����� �볺��� � ������� cur.getBegin()
			if(cur.getBegin().compareTo(day)>0) er.add("Date "+ day+ " less then date bein client " + cur.getBegin());
			// ������� cur (���� ����� cur.getIdPr()) ������� ���� ��"������ �������� ������� (Account acc)
			Account acc = db.takeAccount(cur.getIdPr(), "current");
			if (acc ==null) er.add("Not find account current for product " + cur.getIdPr());
		} else er.add("Not find product Current for client " + idCl);
		return er;
	}	
	@Override
	public String toString() {
		return "Client [idCl=" + idCl + ", name=" + name + "]";
	}
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Client other = (Client) obj;
        if(!name.equals(other.getName())) return false;
        if(idCl != other.getIdCl())return false;
        return true;
	}		
}
