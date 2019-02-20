package dbWork;

import java.util.Date;

public class AccountOp {
	private int idOp;
	private int idPr;
	private Date day;
	private String type;
	private float sum;
	private int idCl;
	public AccountOp(int idOp, int idPr, Date day, String type, float sum, int idCl) {
		super(); this.idOp = idOp; this.idPr = idPr; this.day = day;
		this.type = type; this.sum = sum; this.idCl = idCl;
	}
	public int getIdOp() {
		return idOp;
	}
	public int getIdPr() {
		return idPr;
	}
	public Date getDay() {
		return day;
	}
	public String getType() {
		return type;
	}
	public float getSum() {
		return sum;
	}
	public int getIdCl() {
		return idCl;
	}
	@Override
	public String toString() {
		return "AccountOp [idOp=" + idOp + ", idPr=" + idPr + ", day=" + day + 
				", type=" + type + ", sum=" + sum + ", idCl=" + idCl + "]";
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AccountOp other = (AccountOp) obj;
        if(idOp != other.getIdOp())return false;
        if(idPr != other.getIdPr())return false;
        if(!day.equals(other.getDay())) return false;
        if(!type.equals(other.getType())) return false;
        if(sum != other.getSum())return false;
        if(idCl != other.getIdCl())return false;
        return true;
	}		
}
