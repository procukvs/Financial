package dbWork;

import java.time.LocalDate;

public class DeposOp {
	private int idOp;
	private int idPr;
	private LocalDate day;
	private String type;
	public DeposOp(int idOp, int idPr, LocalDate day, String type) {
		this.idOp = idOp; this.idPr = idPr; this.day = day;	this.type = type; 
	}
	public int getIdOp() {
		return idOp;
	}
	public int getIdPr() {
		return idPr;
	}
	public LocalDate getDay() {
		return day;
	}
	public String getType() {
		return type;
	}
	@Override
	public String toString() {
		return "DeposOp [idOp=" + idOp + ", idPr=" + idPr + ", day=" + day + ", type=" + type + "]";
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
        return true;
	}		
}
