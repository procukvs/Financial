package dbWork;

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
