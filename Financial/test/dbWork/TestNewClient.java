package dbWork;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

class TestNewClient {
	static DbAccess db;
	static int idCl;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		db = new DbAccess();
		if (db.connectionDb()){
			idCl = db.nextId("client");
			db.beginClient(Main.buildSqlDate("2012-02-01"), "Іванов");
		} 
		else System.out.println("no connection to DB finanbcial");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNewClient() {
		Client test = new Client(idCl,"Іванов");
		Client dbCl = db.getClient(idCl);
		//assertEquals(test.getName(), dbCl.getName(), "new Client");
		assertThat(test,equalTo(dbCl));
		//fail("Not yet implemented");
		System.out.println(" test= " + test.toString());
		System.out.println(" dbClient= " + dbCl.toString());
	}

}
