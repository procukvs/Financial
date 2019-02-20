package dbWork;

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
