package client;

import utils.Utils;

import java.util.ArrayList;

import account.AccountDAO;

public class ClientDAO {
	public ArrayList<Client> list;
	private Utils utils;

	private ClientDAO() {
		utils = Utils.getInstance();
		list = new ArrayList<Client>();
	}

	private static ClientDAO instance = new ClientDAO();

	public static ClientDAO getInstance() {

		return instance;
	}

	public void addClientsFromData(String cliData) {
		list = new ArrayList<Client>();
		Client.resetClientNo();
		String[] temp = cliData.split("\n");
		for (int i = 0; i < temp.length; i++) {
			String[] temp2 = temp[i].split("/");
			insertClient(new Client(Integer.parseInt(temp2[0]), temp2[1], temp2[2], temp2[3]));
		}
	}

	private void insertClient(Client c) {
		list.add(c);

	}

	public String getClientIdByClientNo(int clientNo) {
		return list.get(getCilentIdxByClienetNo(clientNo)).getId();
	}

	private boolean hasData() {
		if (list.size() == 0) {
			utils.printMsg("고객 정보가 없습니다");
			return false;
		}
		return true;
	}

	public void printClientList() {
		if (!hasData())
			return;
		System.out.println("[ 전체 회원 목록 ]");
		for (Client c : list)
			System.out.println(c);
	}

	private int getCilentIdxByClienetNo(int clientNo) {
		int idx = 0;
		for (Client c : list) {
			if (c.getClientNo() == clientNo)
				return idx;
			idx += 1;
		}
		return -1;
	}

	private int getCilentIdxById(String id) {
		int idx = 0;
		for (Client c : list) {
			if (c.getId().equals(id))
				return idx;
			idx += 1;
		}
		return -1;
	}

	public void updateClient() {
		if (!hasData())
			return;
		String id = utils.getValue("수정할 회원 아이디를 입력하세요");
		int idx = getCilentIdxById(id);
		Client c = list.get(idx);
		if (idx == -1) {
			utils.printMsg(" 회원 아이디가 존재하지 않습니다");
			return;
		}
		utils.printMsg(" 수정할 회원 정보를 입력하세요");
		String pw = utils.getValue("비밀번호");
		String name = utils.getValue("이름");

		if (c.getPw().equals(pw)) {
			utils.printMsg("기존과 다른 비밀번호 입력하세요");
			return;
		}
		utils.printMsg("회원정보가 수정되었습니다.");

	}

	public void deleteClient(AccountDAO accDao) {
		if (!hasData())
			return;
		String id = utils.getValue("삭제할 회원 아이디를 입력하세요");
		int idx = getCilentIdxById(id);
		if (idx == -1) {
			utils.printMsg("해당 회원이 존재하지 않습니다.");
			return;
		}
		utils.printMsg("정말로 삭제하시겠습니까? 예 : 0, 아니오 : 1");
		int sel = utils.getValue("선택", 0, 1);
		if (sel == 0) {
			accDao.deleteAllAccountsFromAClient(list.get(idx).getClientNo());
			deleteClient(idx);
			utils.printMsg("회원이 삭제되었습니다.");
		} else {
			utils.printMsg("회원삭제가 취소되었습니다.");
		}
	}

	public void saveClientData() {
		if (!hasData())
			return;

		String data = "";
		for (Client c : list)
			data += c;

		data = data.substring(0, data.length() - 1);
		utils.saveData("client.txt", data);
	}

	public int getClientNum(int idx) {
		return list.get(idx).getClientNo();
	}

	public void addClient() {
		String id = "";

		while (true) {
			id = utils.getValue("아이디");
			if (getCilentIdxById(id) != -1) {
				utils.printMsg("이미 사용한 아이디 입니다");
				continue;
			}
			break;
		}

		String pw = utils.getValue("비밀번호");
		String name = utils.getValue("이름");

		insertClient(new Client(id, pw, name));
		utils.printMsg("회원가입이 완료되었습니다.");
	}

	public int login() {
		if (!hasData())
			return -1;
		String id = utils.getValue("아이디");
		String pw = utils.getValue("비밀번호");
		int idx = getCilentIdxById(id);

		if (idx == -1 || !pw.equals(list.get(idx).getPw())) {
			utils.printMsg("아이디 또는 비밀번호가 일치하지 않습니다.");
			return -1;
		}

		return idx;
	}

	public int deleteClient(AccountDAO accDao, int idx) {
		if (!hasData())
			return -1;

		String pw = utils.getValue("비밀번호 확인");
		if (!pw.equals(list.get(idx).getPw())) {
			utils.printMsg("비밀번호가 일치하지 않습니다.");
			return idx;
		}

		utils.printMsg("정말로 탈퇴하시겠습니까? 예 : 0, 아니오 : 1");
		int sel = utils.getValue("선택", 0, 1);
		if (sel == 0) {
			accDao.deleteAllAccountsFromAClient(list.get(idx).getClientNo());
			deleteClient(idx);
			utils.printMsg("회원이 탈퇴되었습니다.");
			return -1;
		}
		utils.printMsg("회원탈퇴가 취소되었습니다.");

		return idx;
	}

	public void deleteClient(int idx) {
		list.remove(idx);
	}

	public void myPage(AccountDAO accDAO, int idx) {
		System.out.println("===== " + list.get(idx).getName() + "님 정보 ======" );
		Client c = list.get(idx);
		utils.printMsg("아이디 : " + c.getId());
		utils.printMsg("비밀번호 : " + c.getPw());
		utils.printMsg("이름 : " + c.getName());

		System.out.println(accDAO.getAccountListFromAClient(list.get(idx).getClientNo()));
	}

}
