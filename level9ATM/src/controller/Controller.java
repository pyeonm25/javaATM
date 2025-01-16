package controller;

import account.AccountDAO;
import client.ClientDAO;
import utils.Utils;

public class Controller {
	public AccountDAO accDAO;
	public ClientDAO clientDAO;
	private final String BANK_NAME = "더조은뱅크";
	private int log;
	private Utils utils;

	public Controller() {
		accDAO = AccountDAO.getInstance();
		clientDAO = ClientDAO.getInstance();
		utils = Utils.getInstance();
		log = -1;

	}
	// [1]관리자 [2]사용자 [0]종료
	// 관리자
	// [1] 회원목록 [2] 회원수정 [3]회원 삭제 [4]데이터 저장 [5]데이터 불러오기
	// 회원수정 : 회원 아이디로 검색 . 비밀번호 , 이름 수정가능
	// 회원삭제 : 회원 아이디
	// 데이터 저장 : account.txt , client.txt

	// 사용자메뉴
	// [1] 회원가입 [2] 로그인 [0] 뒤로가기
	// 회원가입 : 회원 아이디 중복 확인

	// 로그인메뉴
	// [1] 계좌추가 [2] 계좌삭제 [3] 입금 [4] 출금 [5] 이체 [6]탈퇴 [7]마이페이지 [0]로그아웃

	// 계좌추가 : 숫자4개-숫자4개-숫자4개 일치할때 추가가능 , 중복 계좌번호 불가능 , 계좌 총 3개까지만 생성가능
	// 계좌삭제 : 본인 회원 계좌만 삭제 가능 ,
	// 입금 : account에 계좌가 존재할때만 입금가능 :100이상 입금/이체/출금 : 계좌잔고만큼만 가능
	// 이체 : 잔고 내에서만 이체가능 본인 계좌사이에서 이체 가능.동일계좌 이체불가능
	// 탈퇴 : 비밀번호 다시 입력받아서 탈퇴가능.
	// 마이페이지 : 내 계좌목록/잔고 확인

	private void printBankName() {
		System.out.println("=======" + BANK_NAME + "=======");
	}

	private void printMainMenu() {
		// 초기화면
		System.out.printf("[1]관리자 %n[2]사용자 %n[0]종료%n");
	}

	private void printAdminMenu() { // 관리자메뉴
		System.out.printf("[1] 회원목록 %n[2] 회원수정 %n[3]회원 삭제 %n[4]데이터 저장 %n[5]데이터 불러오기%n");
	}

	private int printUserMenu() { // 사용자 메뉴
		if (log == -1) {
			System.out.printf("[1] 회원가입 %n[2] 로그인 %n[0] 뒤로가기%n");
			return 2;
		} else {
			System.out.printf("[1] 계좌추가 %n[2] 계좌삭제 %n[3] 입금 %n[4] 출금 %n[5]이체 %n[6]탈퇴 %n[7]마이페이지 [0]로그아웃%n");
			return 7;
		}
	}

	private void adminRun() {
		// 관리자
		while (true) {
			printBankName();
			printAdminMenu(); // 관리자 화면출력
			int sel = utils.getValue("메뉴 선택", 0, 5);
			if (sel == 0) {
				return;
			} else if (sel == 1) {
				// 회원목록
				clientDAO.printClientList();
			} else if (sel == 2) {
				// 회원수정
				clientDAO.updateClient();
			} else if (sel == 3) {
				// 회원삭제
				clientDAO.deleteClient(accDAO);
			} else if (sel == 4) {
				// 데이터저장
				clientDAO.saveClientData();
				accDAO.saveAccountData();
			} else if (sel == 5) {
				// 데이터불러오기
				utils.laodFromFile(accDAO, clientDAO);
			}
		}
	}

	private void logoutMenu() {
		while (true) {
			printBankName();
			int end = printUserMenu();
			int sel = utils.getValue("사용자 메뉴 선택", 0, end);
			if (sel == 0) {
				return;
			}
			if (log == -1) {
				if (sel == 1) {
					// 회원가입
					clientDAO.addClient();
				} else if (sel == 2) {
					// 로그인
					log = clientDAO.login();
					if (log == -1) {
						utils.printMsg(" 로그인 먼저 해주세요 ");
					}
					utils.printMsg("로그인 성공 ");
					loginMenu();
					return;
				}
			}
		}
	}

	private void loginMenu() {
		while (true) {
			printBankName();
			int end = printUserMenu();
			int sel = utils.getValue("사용자 메뉴 선택", 0, end);
			if (sel == 1) {
				// 계좌추가
				accDAO.addAccount(clientDAO.getClientNum(log), clientDAO);
			} else if (sel == 2) {
				// 계좌삭제
				accDAO.deleteAccount(clientDAO.getClientNum(log));
			} else if (sel == 3) {
				// 입금
				accDAO.deposit(clientDAO.getClientNum(log));
			} else if (sel == 4) {
				// 출금
				accDAO.withdraw(clientDAO.getClientNum(log));
			} else if (sel == 5) {
				// 이체
				accDAO.transfer(clientDAO.getClientNum(log));
			} else if (sel == 6) {
				// 탈퇴
				log = clientDAO.deleteClient(accDAO, log);
			} else if (sel == 7) {
				// 마이페이지
				clientDAO.myPage(accDAO, log);
			} else {
				log = -1;
				utils.printMsg("로그아웃 완료 ");
				return;
			}
		}
	}

	public void run() { // 주 실행메소드
		utils.laodFromFile(accDAO, clientDAO);
		while (true) {
			printBankName();
			printMainMenu(); // 초기
			System.out.println("log = " + log);
			int sel = utils.getValue("메뉴 선택", 0, 2); // 메뉴 사용자 입력
			if (sel == 0) {
				utils.printMsg("시스템 종료");
				return;
			} else if (sel == 1) { // 관리자 모드
				adminRun();
			} else if (sel == 2 && log == -1) {
				logoutMenu();
			} else if (sel == 2 && log != -1) {
				loginMenu();

			}
		}

	}

}
