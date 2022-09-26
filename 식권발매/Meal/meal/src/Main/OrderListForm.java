package Main;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class OrderListForm extends JFrame{
	private JTextField menuname; // 메뉴명
	private JScrollPane jps; // 테이블이 들어갈 곳
	private JTable jt; // 테이블
	private DefaultTableModel model; // 테이블 모델
	private Vector<String> colData; // 테이블 컬럼명, 타이틀명
	private Vector<Vector<String>> rowData; // 테이블 데이터
	private Connection con; // 데이터베이스 연결
	public OrderListForm() {
		setTitle("결제 조회");
		con = (Connection) Driver_con.Driver_con();
		// x버튼을 클릭했을 때 이전폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new ManageForm();}
		});
		
		CenterP();
		this.add(new NorthP(), BorderLayout.NORTH);
		this.add(jps, BorderLayout.CENTER);
		
		setSize(600,600);
		setVisible(true);
	}
	// 상단
	class NorthP extends JPanel{
		private String [] s = {"조회", "새로고침","인쇄","닫기"};
		private JButton [] btns; // 버튼
		public NorthP() {
			// 메뉴명
			this.add(new JLabel("메뉴명:"));
			menuname = new JTextField(12); // 메뉴이름 입력폼
			this.add(menuname);
			
			// 버튼들 
			btns = new JButton[s.length];
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(s[i]);
				btns[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						switch(e.getActionCommand()) {
							case "조회": select(1);break;
							case "새로고침": select(2); menuname.setText(""); break;
							case "인쇄": printer();break;
							case "닫기": dispose(); new ManageForm(); break;
						}
					}
				});
				this.add(btns[i]);
			}
		}
	}
	// 중간, 테이블이 보일곳 
	public void CenterP() {
		colData = new Vector<String>(); // 테이블 컬럼명
		rowData = new Vector<Vector<String>>(); // 테이블 데이터
		
		// 테이블 컬럼명
		colData.add("종류");
		colData.add("메뉴명");
		colData.add("사원명");
		colData.add("결제수량");
		colData.add("총결제금액");
		colData.add("결제일");
		
		// 테이블 수정 불가 
		model = new DefaultTableModel(rowData,colData) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
					return false;
				}
			};
		jt = new JTable(model);
		jps = new JScrollPane(jt); // 테이블이 들어갈 곳
		
		// 전체조회, 새로고침
		select(2);
	}
	// 전체조회, 새로고침 , 조회
	public void select(int a) {
		rowData.clear(); // 테이블 데이터 초기화
		Vector<String> v; // 조회된 데이터값 
		String sql; // 명령문
		
		// 사원명과 메뉴종류(숫자 아닌것)을 뽑아내기 위해 join 
		// meal테이블의 mealName과 cuisine테이블의 cuisineName 필요, orderlist테이블(cuisineNo, mealNo)과 비교
		if(a!=1) { 
			// 1이 아닐 때 
			sql = "select cuisine.cuisineName, meal.mealName, member.memberName, "
					+ "orderCount, amount, orderDate from orderlist "
					+ "inner join member on orderlist.memberNo = member.memberNo "
					+ "join meal on orderlist.mealNo = meal.mealNo "
					+ "join cuisine on orderlist.cuisineNo = cuisine.cuisineNo;";
		}else {
			// 1이면 조회, 입력받은 메뉴명이 포함된 결과값
			sql = "select cuisine.cuisineName, meal.mealName, member.memberName, "
					+ "orderCount, amount, orderDate from orderlist "
					+ "inner join member on orderlist.memberNo = member.memberNo "
					+ "join meal on orderlist.mealNo = meal.mealNo "
					+ "join cuisine on orderlist.cuisineNo = cuisine.cuisineNo "
					+ "where meal.mealName like '%" + menuname.getText() + "%';";
		}
		
		try {
			Statement st = (Statement) con.createStatement();	
			ResultSet rs = st.executeQuery(sql); // sql문 실행
			while(rs.next()) {
				v = new Vector<String>();
				// 나온 데이터값 벡터에 추가, 뽑아낼 데이터 값이 6개
				for(int i=0; i<6; i++) {v.add(rs.getString(i+1));}	
				rowData.add(v); // 테이블 데이터에 추가
			}
			jt.updateUI(); // 테이블 업데이트
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// 인쇄, 텍스트 파일로 
	public void printer() {
		JTextPane jTextPane = new JTextPane();
		jTextPane.setContentType("text/html");
		
		try {
			boolean done = jTextPane.print();
			if(done) {
				try {
					// 인쇄 버튼 눌렀을때
					FileWriter fout = new FileWriter("txt/orderlist.txt");
					
					// 컬럼명
					for(int i=0; i<colData.size(); i++) {
						fout.write(colData.get(i) + "\t");
					}
					fout.write("\r\n");
					// 데이터의 열만큼 
					for(int i=0 ;i<model.getRowCount(); i++) {
						// 데이터의 컬럼명만큼
						for(int j=0; j<model.getColumnCount(); j++) {
							fout.write(rowData.get(i).get(j)+ "\t");
						}
						fout.write("\r\n");
					}
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				// 닫기 
			}
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
