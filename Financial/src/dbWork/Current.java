package dbWork;

import java.util.*;

public class Current {
	private int idPr;
	private int idCl;
	private Date begin;
	Current(int idPr, int idCl, Date begin){
		this.idPr = idPr; this.idCl=idCl; this.begin = begin;
	}
	
	public int getIdPr() {
		return idPr;
	}
	
	public int getIdCl() {
		return idCl;
	}
	
	public Date getBegin() {
		return begin;
	}

	@Override
	public String toString() {
		return "Current [idPr=" + idPr + ", idCl=" + idCl + ", begin=" + begin + "]";
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Current other = (Current) obj;
        if(idPr != other.getIdPr())return false;
        if(!begin.equals(other.getBegin())) return false;
        if(idCl != other.getIdCl())return false;
        return true;
	}		
}
