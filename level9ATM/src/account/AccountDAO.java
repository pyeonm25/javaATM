package account;

import java.util.ArrayList;

import client.ClientDAO;
import utils.Utils;

public class AccountDAO {
	public ArrayList<Account> list;
	private Utils utils;

	private AccountDAO() {
		utils = Utils.getInstance();
		list = new ArrayList<Account>();
	}

	private static AccountDAO intstance = new AccountDAO();

	private final int MAX_ACC_CNT = 3;

	public static AccountDAO getInstance() {
		return intstance;
	}

// 데이터로부터 계좌정보를 읽어서 계좌목록에 추가
	public void addAccountsFromData(String accData) {
		list = new ArrayList<Account>();
		String[] temp = accData.split("\n");
		for (int i = 0; i < temp.length; i++) {
			String[] temp2 = temp[i].split("/");
			insertAccout(new Account(Integer.parseInt(temp2[0]), temp2[1], temp2[2], Integer.parseInt(temp2[3])));
		}
	}

	private boolean hasData() {
		if (list.size() == 0) {
			utils.printMsg("계좌 정보가 없습니다");
			return false;
		}
		return true;
	}

	private void insertAccout(Account a) {
		list.add(a);
	}

	public void saveAccountData() {
		if (!hasData())
			return;

		String data = "";
		for (Account a : list)
			data += a;

		data = data.substring(0, data.length() - 1);
		utils.saveData("account.txt", data);
	}

	public void addAccount(int clientNo, ClientDAO clientDAO) {
		// 본인 계좌 갯수 확인
		int cnt = getAccountCnt(clientNo);
		if (cnt >= MAX_ACC_CNT) {
			utils.printMsg("최대 " + MAX_ACC_CNT + "개의 계좌만 개설할 수 있습니다.");
			return;
		}
		String accNum = utils.getValue("계좌번호를 입력하세요");
		// 계좌번호 유효성 확인
		if (matchAccountNumber(accNum)) {
			return;
		}
		if (getAccIdxFromAccNum(accNum) != -1) {
			utils.printMsg("이미 존재하는 계좌번호입니다.");
			return;
		}
		insertAccout(new Account(clientNo, clientDAO.getClientIdByClientNo(clientNo), accNum));

		utils.printMsg("계좌가 생성되었습니다.");

	}

	private int getAccountCnt(int clientNo) {
		int cnt = 0;
		for (Account account : list)
			if (clientNo == account.getClientNo())
				cnt++;
		return cnt;
	}

	public void deleteAccount(int clientNo) {
		if (!hasData())
			return;

		String accNum = utils.getValue("삭제할 계좌번호를 입력하세요");
		int idx = getAccIdxFromAccNum(accNum);
		if (idx == -1) {
			utils.printMsg("해당 계좌가 존재하지 않습니다.");
			return;
		}

		if (!isMyAccount(list.get(idx), clientNo)) {
			utils.printMsg("본인의 계좌만 삭제할 수 있습니다.");
			return;
		}
		removeOneAccout(idx);
		utils.printMsg("계좌가 삭제되었습니다.");

	}

	public void removeOneAccout(int idx) {
		list.remove(idx);
	}

	public void deposit(int clientNo) {
		String accNum = utils.getValue("입금할 계좌번호를 입력하세요");
		int idx = getIdxAndValidationAccountNum(accNum, clientNo, "입금");
        if(idx == -1) return;
		int money = utils.getValue("입금할 금액을 입력하세요", 100, 1000000);

		list.get(idx).setMoney(money);
		System.out.println(list.get(idx));
		utils.printMsg("입금이 완료되었습니다.");
	}

	public void withdraw(int clientNo) {
		String accNum = utils.getValue("출금할 계좌번호를 입력하세요");
		int idx = getIdxAndValidationAccountNum(accNum, clientNo, "출금");
        if(idx == -1) return;
        
		utils.printMsg("현재 잔액은 " + list.get(idx).getMoney() + "원 입니다.");

		int money =  getSaveAccountBalance("출금", idx);

		if(money == -1) return;
		
		list.get(idx).setMoney(money);
		System.out.println(list.get(idx));
		
		utils.printMsg("출금이 완료되었습니다.");
	}

	private int getSaveAccountBalance(String state, int idx) {
		int money = 0;
		
		if(list.get(idx).getMoney() == 0) {
			utils.printMsg("잔액이 0원입니다.");
			return -1;
		}
		
		while (true) {
			money = utils.getValue( state +" 할 금액을 입력하세요", 100, list.get(idx).getMoney());
			if (list.get(idx).getMoney() < money) {
				utils.printMsg("잔액이 부족합니다.");
				continue;
			}
			return money;
		}
	}

	private int getIdxAndValidationAccountNum(String accNum, int clientNo, String state) {
		int idx = getAccIdxFromAccNum(accNum);
		if (idx == -1) {
			utils.printMsg("해당 계좌가 존재하지 않습니다.");
			return -1;
		}
	
		if (!isMyAccount(list.get(idx), clientNo)) {
			utils.printMsg("본인의 계좌만 " + state + "할 수 있습니다.");
			return -1;
		}
		return idx;
	}

	public void transfer(int clientNo) {
        String accNum = utils.getValue("출금할 계좌번호를 입력하세요");
        int idx = getIdxAndValidationAccountNum(accNum, clientNo, "이체");
        if(idx == -1) return;
  
        utils.printMsg("현재 잔액은 " + list.get(idx).getMoney() + "원 입니다.");
    	int money =  getSaveAccountBalance("이체", idx);
    	if(money == -1) return;
    	
        utils.printMsg("이체할 계좌번호를 입력하세요");
        String transferAcc = utils.getValue("계좌번호를 입력하세요");
        int transferIdx = getAccIdxFromAccNum(transferAcc);
        
    	if(idx == transferIdx) {
			utils.printMsg("동일 계좌는 이체 할 수 없습니다");
			return;
		}
        
        
        if( transferIdx == -1) {
            utils.printMsg("해당 계좌가 존재하지 않습니다.");
            return;
        }
        
        list.get(idx).setMoney(money); 
        list.get(transferIdx).setMoney(money); 
        
        System.out.println(list.get(idx));
        utils.printMsg("이체가 완료되었습니다.");

    }

	public void deleteAllAccountsFromAClient(int clientNo) {

		if (getAccountCnt(clientNo) == 0) {
			utils.printMsg("삭제할 계좌가 없습니다.");
			return;
		}
		for(int i =0; i < list.size();i+=1) 
			if(list.get(i).getClientNo() == clientNo) removeOneAccout(i);
			
		 utils.printMsg("계좌 전부 삭제 완료");
		
	}

	private boolean matchAccountNumber(String accNum) { // 계좌확인
		if (!accNum.matches("^\\d{4}-\\d{4}-\\d{4}$")) {
			utils.printMsg("계좌번호 형식은 1234-1234-1234");
			return true;
		}
		return false;
	}

	public String getAccountListFromAClient(int clientNo) {

		String data = "";
		for (Account c : list)
			if (clientNo == c.getClientNo())
				data += c + "\n";
		return data;

	}

	private int getAccIdxFromAccNum(String accNum) {
		int idx = 0;
		for (Account a : list) {
			if (a.getAccNumber().equals(accNum))
				return idx;
			idx += 1;
		}

		return -1;
	}

	private boolean isMyAccount(Account account, int clientNo) {
		if (account.getClientNo() == clientNo) {
			return true;
		}
		return false;
	}

}
