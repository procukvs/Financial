package dbWork;

import java.time.*;

public class Event {
	private int id;
	private LocalDate day;
	private String product;
	private String operation;
	private int idCP;
	private float sum;
	private float rate;
	private LocalDate end;
	private int idC;
	private String name;
	
	public Event(int id, LocalDate day, String product, String operation, int idCP,
			     float sum, float rate,	LocalDate end, int idC, String name) {
		this.id = id;	this.day = day;	this.product = product;
		this.operation = operation;	this.idCP = idCP; this.sum = sum;
		this.rate = rate; this.end = end; this.idC = idC; this.name = name;
	}

	public int getId() {return id;}
	public LocalDate getDay() {	return day;	}
	public String getProduct() {return product;	}
	public String getOperation() {	return operation;}
	public int getIdCP() {return idCP;}
	public float getSum() {	return sum;	}
	public float getRate() {return rate;}
	public LocalDate getEnd() {	return end;	}
	public int getIdC() {return idC;}
	public String getName() {return name;}

	@Override
	public String toString() {
		return "Event [id=" + id + ", day=" + day + ", product=" + product + 
				       ", operation=" + operation + ", idCP=" + idCP + ", sum=" +
				       sum + ", rate=" + rate + ", end=" + end + ", idC=" + idC + ", name=" + name + "]";
	}
	
}
