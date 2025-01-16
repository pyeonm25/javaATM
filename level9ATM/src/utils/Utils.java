package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import account.AccountDAO;
import client.ClientDAO;

public class Utils {

	private static Scanner sc = new Scanner(System.in);
	private final static String CUR_PATH = System.getProperty("user.dir") + "\\src\\" + Utils.class.getPackageName()
			+ "\\";;

	private Utils() {};

	private static Utils instance;

	public static Utils getInstance() {
		if (instance == null)
			instance = new Utils();
		return instance;
	}


	// 숫자 입력
	public int getValue(String msg, int start, int end) {
		while (true) {
			System.out.printf("▶ %s[%d-%d] 입력 :", msg, start, end);
			try {
				int num = sc.nextInt();
				sc.nextLine();
				if (num < start || num > end) {
					System.out.printf("%d - %d 사이 값 입력해주세요 %n", start, end);
					continue;
				}
				return num;
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("숫자값을 입력하세요");
			}
		}

	}

	// 문자열 입력
	public String getValue(String msg) {
		System.out.printf("▶ %s 입력 : ", msg);

		return sc.next();

	}
	public void printMsg(String msg) {
		System.out.printf("[메세지] : %s \n" , msg);
	}

	// 파일 읽기
	private String loadFile(String fileName) {
		String data = "";
		try (FileReader fr = new FileReader(CUR_PATH + fileName); BufferedReader br = new BufferedReader(fr);) {
			String line = "";
			while ((line = br.readLine()) != null) {
				data += line + "\n";
			}
			data = data.substring(0, data.length() - 1);
			System.out.println(fileName + " 데이터 로드 완료");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(fileName + "데이터 로드 실패");
		}

		return data;
	}

	// 파일이 없을때 생성
	private void fileInit(String fileName) {
		File file = new File(CUR_PATH + fileName);
		System.out.println(CUR_PATH);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("파일 생성 실패");
				e.printStackTrace();
			}
		}
	}

	// 파일에서 데이터 읽기
	public void laodFromFile(AccountDAO accDAO, ClientDAO cliDAO) {
		String accData = loadFile("account.txt");
		String cliData = loadFile("client.txt");
		
		
		if(!accData.isBlank()) accDAO.addAccountsFromData(accData);
		if(!cliData.isBlank()) cliDAO.addClientsFromData(cliData);
	}

//파일저장
	public void saveData(String fileName, String data) {
		String filePath  = CUR_PATH + fileName;
		try (FileWriter fw = new FileWriter(filePath);) {
			fw.write(data);
			System.out.println(filePath + "데이터 저장 완료");
		} catch (Exception e) {
			System.out.println(filePath + "데이터 저장 실패");
			e.printStackTrace();
		}
	}

}
