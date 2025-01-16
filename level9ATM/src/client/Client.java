package client;

public class Client {
	private int clientNo;
	private String id;
	private String pw;
	private String name;
	private static int autoNum= 1001; 
	public static void resetClientNo() {
		autoNum = 1001;
	}
	public Client(int clientNo, String id, String pw, String name) {
		this.clientNo = clientNo;
		this.id = id;
		this.pw = pw;
		this.name = name;
		autoNum++;
	}
	public Client(String id, String pw, String name) {
		this.clientNo = autoNum++;
		this.id = id;
		this.pw = pw;
		this.name = name;
	}
	
	public int getClientNo() {
		return clientNo;
	}


	public String getId() {
		return id;
	}


	public String getPw() {
		return pw;
	}


	public void setPw(String pw) {
		this.pw = pw;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	
	public String setData() {
		return String.format("%s/%s/%s/%s\n", clientNo, id, pw, name);
	}
	@Override
	public String toString() {
		return String.format("%s %s %s %s", clientNo, id, pw, name);
	}
	
	
	
}
