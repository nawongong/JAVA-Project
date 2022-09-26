package Main;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// 메뉴 관리폼, 메인폼->관리자 패스워드 입력폼->관리폼-> 메뉴 관리
public class ManageMenuForm extends JFrame{
	private JComboBox cb1; // 메뉴 종류
	private Vector<String> colData; // 테이블 컬럼명, 타이틀명
	private Vector<Vector<Object>> rowData; // 테이블 데이터값 
	private JTable jt;
	private JScrollPane jsp;
	private DefaultTableModel model;
	private Connection con; // 데이터베이스 연결
	public ManageMenuForm() {
		setTitle("메뉴 관리");
		// x버튼을 클릭했을 때 이전폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new ManageForm();}
		});
		
		con = (Connection) Driver_con.Driver_con();
		
		this.add(new NorthP(), BorderLayout.NORTH);
		CenterP();
		this.add(jsp, BorderLayout.CENTER);
		
		setSize(600,600);
		setVisible(true);
	}
	// 상단, 종류 콤보박스 + 버튼들 
	class NorthP extends JPanel{
		private String [] s1 = {"한식", "중식", "일식", "양식"};
		private String [] s2 = {"검색", "수정", "삭제", "오늘의메뉴선정", "닫기"};
		private JButton [] btns; // 버튼들
		public NorthP() {
			// 종류 콤보박스
			JLabel label = new JLabel("종류:");
			cb1 = new JComboBox(s1);
			this.add(label);
			this.add(cb1);
			
			// 버튼들
			btns = new JButton[s2.length];
			for(int i=0; i<s2.length; i++) {
				btns[i] = new JButton(s2[i]);
				btns[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						switch(e.getActionCommand()) {
							case "검색":setMenu(cb1.getSelectedIndex()+1); break;
							case "수정":updateMenu(); break;
							case "삭제": deleteMenu(); break;
							case "오늘의메뉴선정": todaymenu();break;
							case "닫기": new ManageForm(); dispose(); break;
						}
					}
				});
				this.add(btns[i]);
			}
		}
	}
	public void CenterP() {
		colData = new Vector<String>();
		rowData = new Vector<Vector<Object>>();
		
		// 테이블 컬럼명, 타이틀명
		colData.add("□"); // 체크박스
		colData.add("menuName");
		colData.add("price");
		colData.add("maxCount");
		colData.add("todayMeal");
		
		model = new DefaultTableModel(rowData,colData) { // 체크박스 기능 
			public java.lang.Class<?> getColumnClass(int columnIndex) {
				switch(columnIndex) { // 첫번째라서 0
					case 0: return Boolean.class; // true면 체크, false는 체크x
					default: return String.class;
				}
			};
		};
		jt = new JTable(model);
		jsp = new JScrollPane(jt);
		setMenu(cb1.getSelectedIndex()+1); // 처음에는 한식
	}
	// table에 메뉴 종류에 맞게 출력
	public void setMenu(int selectnum) {
		rowData.clear(); // 테이블 데이터값 초기화
		String sql = "select mealName,price,maxCount,todayMeal from meal where cuisineNo=" + selectnum; // 종류에 맞는 음식리스트 뽑아내기
		try {
			Statement st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			Vector<Object> v = null; // 검색했을 때 테이블에 보여질 내용
			while(rs.next()) {
				v = new Vector<Object>(); // 벡터 초기화
				v.add(false); // 체크박스는 기본값 false
				v.add(rs.getString(1)); // 메뉴 이름
				v.add(rs.getInt(2)); // 가격
				v.add(rs.getInt(3)); // 조리 가능 수량
				// 오늘의 메뉴, 1이면 Y, 0이면 N
				if(rs.getInt(4)==1) { v.add("Y");}
				else {v.add("N");} 
				rowData.add(v); // 테이블 데이터 값에 추가
			}
			jt.updateUI(); // 테이블 업데이트
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// 수정
	public void updateMenu() {
		Vector<Object> v = new Vector<Object>(); // 선택한 메뉴 updatemenuform에 넘겨줄 벡터
		int cnt=0; // 체크한 수 
		// 테이블 열만큼
		for(int i=0; i<model.getRowCount(); i++) {
			// 체크한게 있다면
			if((boolean) model.getValueAt(i, 0)) {
				cnt++;
				v.add(cb1.getSelectedIndex()); // 0~3
				v.add(model.getValueAt(i, 1)); // 메뉴 이름
				v.add(model.getValueAt(i, 2)); // 가격
				v.add(model.getValueAt(i, 3)); // 조리가능수량
			}
		}
		switch(cnt) {
			case 0: JOptionPane.showMessageDialog(null, "수정할 메뉴를 선택해주세요.","Message", JOptionPane.ERROR_MESSAGE);break;
			case 1: dispose(); new UpdateMenuForm(v); break;
			default: JOptionPane.showMessageDialog(null, "하나씩 수정가능합니다.","Message",JOptionPane.ERROR_MESSAGE); v.clear();break;
		}
	}
	// 삭제
	public void deleteMenu() {
		int cnt = 0; // 체크한 수
		String delete_menuname = null; // 삭제할 메뉴 이름
		int select_rows=0; // 체크한 메뉴의 열 번호 
		
		// 테이블 열만큼
		for(int i=0; i<model.getRowCount(); i++) {
			// 체크한게 있다면
			if((boolean)model.getValueAt(i, 0)){
				cnt++;
				delete_menuname=(String) model.getValueAt(i, 1); // 체크한 메뉴 이름가져오기
				select_rows = jt.getSelectedRow(); // 체크한 메뉴의 열 번호를 가져옴
			}
		}
		switch(cnt) {
			case 0: JOptionPane.showMessageDialog(null, "삭제할 메뉴를 선택해주세요.","Message", JOptionPane.ERROR_MESSAGE); break;
			case 1: delete(delete_menuname,select_rows);break;
			default: JOptionPane.showMessageDialog(null, "하나씩 삭제가능합니다.","Message", JOptionPane.ERROR_MESSAGE); break;
		}
	}
	// db에 삭제
	public void delete(String delete_menuname,int select_rows) { // 메뉴이름, 체크한 열 번호
		String sql = "delete from meal where mealName='" + delete_menuname + "'"; // meal테이블에서 메뉴이름이 ~ 인것을 지움
		try {
			Statement st = (Statement) con.createStatement();
			int rs = st.executeUpdate(sql); // sql문 실행
			if(rs==1) {
				model.removeRow(select_rows); // 테이블에도 삭제를해야하기 때문에 그 열의 번호에 있는 데이터값을 지움
				JOptionPane.showMessageDialog(null, "삭제되었습니다.","Message", JOptionPane.INFORMATION_MESSAGE);
				jt.updateUI(); // 테이블 업데이트
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// 오늘의 메뉴 선정
	public void todaymenu() {
		Vector<String> v = new Vector<String>(); // 선택한 메뉴 오늘의 메뉴로 수정할 벡터
		int cnt = 0; // 체크한 메뉴 수
		
		// 테이블 열만큼, 타이틀
		for(int i=0; i<model.getRowCount(); i++) {
			if((boolean)model.getValueAt(i, 0)) { // 체크가 되었으면 
				cnt++;
				v.add((String) model.getValueAt(i, 1)); // 메뉴 이름을 추가
			}
		}
		if(cnt<=25) { // 25개 이하 체크 시 
			try {
				for(int i=0; i<v.size(); i++) { // 추가한 벡터 사이즈 만큼
					String sql = "update meal set todayMeal=1 where mealName=?"; // 메뉴이름이 ~ 인것의 오늘의 메뉴를 수정
					PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
					psmt.setString(1, v.get(i)); // 벡터의 i번째꺼를 뽑아옴
					psmt.executeUpdate(); // sql문 실행
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "오늘의 메뉴 선정되었습니다.","Message", JOptionPane.INFORMATION_MESSAGE);
			setMenu(cb1.getSelectedIndex()+1);
		}else { 
			JOptionPane.showMessageDialog(null, "25개를 초과할 수 없습니다.","Message",JOptionPane.ERROR_MESSAGE);
		}
	}
}
