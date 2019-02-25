package dbWork;

import java.util.*;
import java.time.*;

public class Current {
	private int idPr;
	private int idCl;
	private LocalDate begin;
	Current(int idPr, int idCl, LocalDate begin){
		this.idPr = idPr; this.idCl=idCl; this.begin = begin;
	}
	
	public int getIdPr() {
		return idPr;
	}
	
	public int getIdCl() {
		return idCl;
	}
	
	public LocalDate getBegin() {
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
