package dbWork;

import java.time.*;

public class Amount {
	private String number;
	private LocalDate day;
	private float sum;
	public Amount(String number, LocalDate day, float sum) {
		super(); this.number = number; this.day = day;	this.sum = sum;
	}
	public String getNumber() {
		return number;
	}
	public LocalDate getDay() {
		return day;
	}
	public float getSum() {
		return sum;
	}
	//  якщо Ї залишок по рахунку number на дату day, то null
	//  в ≥ншому випадку повертаЇ залишок на дату day, котрий потр≥бно занести в базу.   
	public static Amount maybeAmount(DbAccess db, String number, LocalDate day) {
		Amount res = null;
		Amount last = db.takeAmount(number, day);
		if(last==null) res = new Amount(number, day, 0); 
		else if (last.getDay().compareTo(day)<0) res = new Amount(number, day, last.getSum());
		return res;
	}
	
	@Override
	public String toString() {
		return "Amount [number=" + number + ", day=" + day + ", sum=" + sum + "]";
	}
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Amount other = (Amount) obj;
        if(!number.equals(other.getNumber())) return false;
        if(!day.equals(other.getDay())) return false;
        if(sum != other.getSum())return false;
        return true;
	}		
}
