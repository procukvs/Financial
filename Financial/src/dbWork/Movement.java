package dbWork;

import java.util.Date;

public class Movement {
	private int idM;
	private float sum;
	private String numberD;
	private String numberC;
	private int idOp;
	
	public Movement(int idM, float sum, String numberD, String numberC, int idOp) {
		super(); this.idM = idM; this.sum = sum;
		this.numberD = numberD;	this.numberC = numberC;	this.idOp = idOp;
	}
	public int getIdM() {
		return idM;
	}
	public float getSum() {
		return sum;
	}
	public String getNumberD() {
		return numberD;
	}
	public String getNumberC() {
		return numberC;
	}
	public int getIdOp() {
		return idOp;
	}
	@Override
	public String toString() {
		return "Movement [idM=" + idM + ", sum=" + sum + ", numberD=" + numberD + 
				", numberC=" + numberC + ", idOp=" + idOp + "]";
	}
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Movement other = (Movement) obj;
        if(idM != other.getIdM())return false;
        if(sum != other.getSum())return false;
        if(!numberD.equals(other.getNumberD())) return false;
        if(!numberC.equals(other.getNumberC())) return false;
        if(idOp != other.getIdOp())return false;
        return true;
	}		
}
