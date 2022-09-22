package Main;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// 고객조회폼
public class SelectCustomerForm extends JFrame{
	private JTextField name; // 이름 입력창
	private JTable jt; // 조회된 결과를 보여주는 테이블
	private String [] s = {"code","name","birth","tel","address","company"}; // 컬럼명
	private Vector<String> colData = new Vector<String>(); // 컬럼명이 들어갈 벡터
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>(); // 데이터가 들어갈 벡터
	private Connection con = Driver_con.Driver_con(); // 데이터베이스 연결
	private Vector<String> vc = new Vector<String>(); // 선택한 행 데이터가 들어갈 벡터
	private int selectrow = -1; // 선택한 행 인덱스
	public SelectCustomerForm() {
		setTitle("고객조회");
		// x버튼을 클릭했을 때 메인폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new MainForm();}
		});
		
		// 테이블 생성, 조회 결과가 나올 부분
		for(int i=0; i<s.length; i++) {colData.add(s[i]);}
		DefaultTableModel model = new DefaultTableModel(rowData,colData);
		jt = new JTable(model);
		JScrollPane jsp = new JScrollPane(jt);
		
		getContentPane().add(new NorthP(), BorderLayout.NORTH);
		getContentPane().add(jsp, BorderLayout.CENTER);
		
		setSize(800,600);
		setVisible(true);
	}
	//상단
	class NorthP extends JPanel{
		String [] btns = {"조회","전체보기","수정","삭제","닫기"}; // 버튼 텍스트 배열
		public NorthP() {
			this.add(new JLabel("성명"));
			name = new JTextField(15);
			this.add(name);
			
			// 버튼 추가
			JButton [] btn = new JButton[btns.length];
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btns[i]);
				btn[i].addActionListener(new BtnListener(i));
				this.add(btn[i]);
			}
		}
	}
	
	// 버튼 이벤트 리스너
	class BtnListener implements ActionListener{
		int i;
		
		public BtnListener(int i) {this.i = i;}
		@Override
		public void actionPerformed(ActionEvent e) {
			switch(i) {
				case 0: // 조회
					if(!name.getText().equals("")) { // 이름 입력창이 공백이 아닐 때 실행
						String sql = "select * from customer where name like '" + name.getText() + "%'"; // 성만 입력해도 관련된 이름 모두 검색, ex) '홍' -> '홍길동', '홍길'->'홍길동'
						Search(sql);
					}
					break;
				case 1: // 전체보기
					String sql = "select * from customer"; // customer 테이블 전체 조회
					Search(sql);
					break;
				case 2: // 수정
					UpdateCustomer();
					break;
				case 3: // 삭제
					DeleteCustomer();
					break;
				case 4: // 닫기
					dispose();
					new MainForm(); // 메인폼으로
					break;
			}
		}
	}
	// 검색 , 전체보기
	private void Search(String sql) {
		rowData.clear(); // 안에 들어있는 데이터 지우기
		try {
			Statement st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				Vector<String> search = new Vector<String>(); // 검색결과를 담을 벡터
				for(int i=0; i<s.length; i++) {search.add(rs.getString(i+1));} // 
				rowData.add(search);
				
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		jt.updateUI();
	}
	// 선택했는지 안헀는지 확인, 선택했으면 선택한 
	private void isSelected() {
		vc.clear(); // 백터 초기화
		selectrow = jt.getSelectedRow();
		if(selectrow == -1) return;
		DefaultTableModel m = (DefaultTableModel) jt.getModel();
		vc = rowData.get(selectrow);
	}
	// 수정 -> 수정폼
	private void UpdateCustomer() {
		isSelected();
		if(vc.size()!=0) { // 데이터값이 들어가 있을 때
			new UpdateCustomerForm(vc);
		}
	}
	// 삭제
	private void DeleteCustomer() {
		isSelected();
		if(vc.size()!=0) {
			DefaultTableModel m = (DefaultTableModel) jt.getModel();
			int result = JOptionPane.showConfirmDialog(null, vc.get(1) + "님을 정말 삭제하시겠습니까?","고객정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				String sql = "delete from customer where code = ?"; // customer 테이블의 code값이 ~ 인 값을 삭제
				try {
					PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
					psmt.setString(1, vc.get(0)); // 코드값을 가져와서 값 넣기
					int re = psmt.executeUpdate(); // sql문 실행
					if(re>0) { // sql문이 실행되서 성공하면
						JOptionPane.showMessageDialog(null, "삭제되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
						m.removeRow(selectrow);
					}else { // sql문이 실패하면
						JOptionPane.showMessageDialog(null, "오류", "메시지", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
