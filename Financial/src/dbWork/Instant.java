package dbWork;

import java.time.*;

public class Instant {
	private int idPr;
	private int idCl;
	private float sum;
	private LocalDate begin;
	private String kind;
	private LocalDate end;
	private float rate;
	private String state;
	public Instant(int idPr, int idCl, LocalDate begin, String kind, float sum, LocalDate end, float rate, String state) {
		this.idPr = idPr; this.idCl = idCl; this.sum = sum; this.begin = begin;	this.kind = kind;
		this.end = end; this.rate = rate; this.state = state;
	}
	public int getIdPr() {
		return idPr;
	}
	public int getIdCl() {
		return idCl;
	}
	public float getSum() {
		return sum;
	}
	public LocalDate getBegin() {
		return begin;
	}
	public String getKind() {
		return kind;
	}
	public LocalDate getEnd() {
		return end;
	}
	public float getRate() {
		return rate;
	}
	public String getState() {
		return state;
	}
	@Override
	public String toString() {
		return "Instant [idPr=" + idPr + ", idCl=" + idCl + ", sum=" + sum + ", begin=" + begin + ", kind=" + kind + 
				         ", end=" + end	+ ", rate=" + rate + ", state=" + state + "]";
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Instant other = (Instant) obj;
        if(idPr != other.getIdPr())return false;
        if(!begin.equals(other.getBegin())) return false;
        if(idCl != other.getIdCl())return false;
        if(sum != other.getSum())return false;
        if(!kind.equals(other.getKind())) return false;
        if(!end.equals(other.getEnd())) return false;
        if(rate != other.getRate()) return false;
        if(!state.equals(other.getState())) return false;
        return true;
	}		
}
