package edu.kh.jdbc.board.model.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.kh.jdbc.board.model.vo.Board;
import edu.kh.jdbc.board.model.vo.Reply;
import edu.kh.jdbc.member.model.vo.Member;

import static edu.kh.jdbc.common.JDBCTemplate.*;

public class BoardDAO {
	
	// JDBC 객체 참조용 변수 선언(Java.sql)
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	// SQL 내용을 저장할 Properties 객체 참조 변수 선언
	private Properties prop;
	
	// 기본 생성자(board-sql.xml 파일 읽어오기(Properties))
	public BoardDAO() {
		
		try {
			prop = new Properties();
			
			// xml파일 읽어오기
			prop.loadFromXML(new FileInputStream("board-sql.xml"));
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}

	/**게시글 목록 조회 DAO
	 * @param conn
	 * @return boardList
	 * throws Exception
	 */
	public List<Board>selectAll(Connection conn) throws Exception{
		
		// 결과 저장용 변수
		List<Board>boardList = new ArrayList<Board>();
		
		try {
			
			// 1) SQL 작성
			
			String sql = prop.getProperty("selectAll"); 
			
			// 2) Statement 생성
			stmt = conn.createStatement();
			
			//3) SQL 수행 후 결과 반환 받기
			rs = stmt.executeQuery(sql);
			
			//4) ResultSet을 한 행씩 모두 접근
			while(rs.next()) {
				
				// 5) 현재 행에서 컬럼명을 이용해서 컬럼 값 얻어오기
				int boardNo = rs.getInt("BOARD_NO");
				String boardTitle = rs.getString("BOARD_TITLE");
				Date createDate = rs.getDate("CREATE_DATE");
				int readCount = rs.getInt("READ_COUNT");
				String memberName = rs.getString("MEMBER_NM");
				int replyCount = rs.getInt("REPLY_COUNT");
				
				
				// 6) Board 객체를 생성하여 컬럼 값 담기
				Board board = new Board(boardNo, boardTitle, createDate, readCount, memberName, replyCount);
				
				
				// 7) Board 객체를 boardList에 추가
				boardList.add(board);
				
				
			}
			
			
			
		}finally {
			close(rs);
			close(stmt);
			
			
		}
		
		
		return boardList;
	}

	/**특정 게시글 상세 조회 DAO
	 * @param conn
	 * @param boardNo
	 * @return board
	 * throws Exception
	 */
	public Board selectOne(Connection conn, int boardNo) throws Exception{
		
		Board board = null; // 결과 저장용 변수
		
		try {
			
			//1) SQL 작성
			String sql = prop.getProperty("selectOne");
					
			//2) PreparedStatement 생성
			pstmt = conn.prepareStatement(sql);
			
					
			//3) 위치 홀더 '?' 알맞은 값 세팅
			pstmt.setInt(1, boardNo);
					
			//4) SQL 수행 후 결과 반환 받기
			rs = pstmt.executeQuery();
					
			//5) 조회된 한 행(if)이 있을 경우 조회된 컬럼 값 얻어오기	
			
			if(rs.next()) {
				//int boardNo = rs.getInt("BOARD_NO");
				// --> 입력 받은 boatdNo와 조회된 BOATD_NO는 같으므로
				// 굳이 DB 조회 결과에서 얻어오지 않아도 된다.
				
				String boardTitle = rs.getString("BOARD_TITLE");
				Date createDate = rs.getDate("CREATE_DATE");
				int readCount = rs.getInt("READ_COUNT");
				String memberName = rs.getString("MEMBER_NM");
				
				String boardContent = rs.getString("BOARD_CONTENT");
				int memberNo = rs.getInt("MEMBER_NO");
				
				
				// 6) Board 객체를 생성하여 컬럼 값 세팅
				board = new Board();
				
				board.setBoardNo(boardNo);
				board.setBoardTitle(boardTitle);
				board.setBoardcontent(boardContent);
				board.setCreateDate(createDate);
				board.setReadCount(readCount);
				board.setMemberName(memberName);
				board.setMemberNo(memberNo);
				
			}
					
					
		}finally {
			//7) 사용한 JDBC 자원 반환
			close(rs);
			close(pstmt);
			
			
		}
		
		// 결과 반환
		return board;
	}

	/**특정 게시글 댓글 목록 조회 DAO
	 * @param conn
	 * @param boardNo
	 * @return replyList
	 * throws Exception
	 */
	public List<Reply> selectReplyList(Connection conn, int boardNo) throws Exception{
		
		List<Reply> replyList = new ArrayList<Reply>(); // 결과 저장용 변수
		
		try {
			// 1) sql 작성
			String sql = prop.getProperty("selectReplyList");
			
			// 2) PreparedStatemtnt 생성
			pstmt = conn.prepareStatement(sql);
			
			//3) 위치 홀더에 알맞은 값 대입
			pstmt.setInt(1, boardNo);
			
			//4) sql 수행 후 결과 반환 받기
			rs = pstmt.executeQuery();
			
			//5) 조회된 결과를 한 행씩 접근 (while(rs.next))
			// -> 각 행 별로 컬럼 값 얻어오기
			
			while (rs.next()) {
				
			int replyNo = rs.getInt("REPLY_NO");
			String replyContent = rs.getString("REPLY_CONTENT");
			Date createDate = rs.getDate("CREATE_DATE");
			int memberNo = rs.getInt("MEMBER_NO");
			String memberName = rs.getString("MEMBER_NM");
			// boardNo 는 매개변수 사용
			
				
			//6) reply 객체를 생성하여 컬럼 값 담기
			Reply reply = new Reply();
			
			reply.setReplyNo(replyNo);
			reply.setReplyContent(replyContent);
			reply.setCreateDate(createDate);
			reply.setMemberNo(memberNo);
			reply.setMemberName(memberName);
			reply.setBoardNo(boardNo);
			
			
			// 7) replyList에 reply 객체 추가
			replyList.add(reply);
			
			}
			
			
			
		}finally {
			// JDBC 객체 자원 반환
			close(rs);
			close(pstmt);
			
		}
		
		// 결과 반환
		return replyList;
	}

	/**게시글 조회수 증가 DAO
	 * @param conn
	 * @param boardNo
	 * @return result
	 * throws Exception
	 */
	public int increaseReadCount(Connection conn, int boardNo) throws Exception {
		
		int result = 0; // 결과 저장용 변수
		
		try {
			
			String sql = prop.getProperty("increaseReadCount");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1,boardNo);
			
			result = pstmt.executeUpdate();
			
			
		}finally {
			close(pstmt);
			
			
		}
		
		return result;
	}

	/**게시글 삭제 DAO
	 * @param conn
	 * @param boardNo
	 * @return result
	 * @throws Exception
	 */
	public int deleteBoard(Connection conn, int boardNo) throws Exception {
		int result = 0;
		
		try {
			String sql = prop.getProperty("deleteBoard");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1,boardNo);
			
			result = pstmt.executeUpdate();
			
			
			
		}finally {
			close(pstmt);
			
		}
		return result;
	}

	/** 게시글 수정  DAO
	 * @param conn
	 * @param board
	 * @return result
	 * throws Exception
	 */
	public int updateBoard(Connection conn, Board board) throws Exception {
		
		int result = 0;
		
		
		try {
			
		}finally {
			String sql = prop.getProperty("updateBoard");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, board.getBoardTitle());
			pstmt.setString(2, board.getBoardcontent());
			pstmt.setInt(3, board.getBoardNo());
			
			result = pstmt.executeUpdate();
			
			close(pstmt);
		}
				
		return result;
	}

	/**댓글 삽입 DAO
	 * @param loginMember
	 * @param boardNo
	 * @param reply
	 * @return result
	 * @throws Exception
	 */
	public int insertReply(Connection conn, Reply reply) throws Exception {
		
		int result = 0;
		
		try {
			String sql = prop.getProperty("insertReply");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, reply.getReplyContent());
			pstmt.setInt(2, reply.getMemberNo());
			pstmt.setInt(3, reply.getBoardNo());
			
			result = pstmt.executeUpdate();
			
			
			}finally {
				close(pstmt);
			}
		
		return result;
	}

	/**댓글 수정 DAO
	 * @param conn
	 * @param input
	 * @return result
	 * @throws Exception
	 */
	public int updateReply(Connection conn, Reply reply) throws Exception {
		int result = 0;
		
		try {
			
			String sql = prop.getProperty("updateReply");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, reply.getReplyContent());
			pstmt.setInt(2, reply.getReplyNo());
			
			result = pstmt.executeUpdate();
			
		}finally {
			close(pstmt);
		}
		
		return result;
	}

	/** 댓글 삭제 DAO
	 * @param conn
	 * @param replyNo
	 * @return result
	 * @throws Exception
	 */
	public int deleteReply(Connection conn, Reply reply) throws Exception {
		System.out.println("다오 확인");
		int result = 0;
		
		try {
			String sql = prop.getProperty("deleteReply");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, reply.getReplyNo());
			
			result = pstmt.executeUpdate();
		}finally {
			close(pstmt);
		}
		
		System.out.println("돌아와 다오");
		return result;
	}

	/** 게시글 작성
	 * @param conn
	 * @param board
	 * @return
	 * @throws Exception
	 */
	public int insertBoard(Connection conn, Board board) throws Exception {
		int result = 0;
		
		try {
			
			String sql = prop.getProperty("insertBoard");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, board.getBoardTitle());
			pstmt.setString(2, board.getBoardcontent());
			pstmt.setInt(3, board.getMemberNo());
			
			result = pstmt.executeUpdate();
			
		}finally {
			close(pstmt);
		}
		
		return result;
	}

	/** 게시글 검색 DAO
	 * @param conn
	 * @param menuNum
	 * @param keyWord
	 * @return boardList
	 * @throws Exception
	 */
	public List<Board> searchBoard(Connection conn, int menuNum, String keyWord) throws Exception{
		
		List<Board> boardList = new ArrayList<Board>();
		
		try {
			// sql 작성 (menuNum에 따라서 sql 조합)
			
			String sql = prop.getProperty("searchBoard1") 
										+ prop.getProperty("condition" + menuNum)
										+ prop.getProperty("searchBoard2");
			
			pstmt = conn.prepareStatement(sql);
			
			// 위치 홀더에 알맞은 값 세팅
			// * 주의 *
			// -> 제목 + 내용을 검색하는 조건 (3번) 은 혼자만 위치홀더가 2개이다!!
			
			pstmt.setString(1, keyWord);
			
			if(menuNum == 3) pstmt.setString(2, keyWord);
			
			rs = pstmt.executeQuery(); // select문 수행 후 결과 resultSet 반환
			
			while(rs.next()) {
				
				int boardNo = rs.getInt("BOARD_NO");
				String boardTitle = rs.getString("BOARD_TITLE");
				Date createDate = rs.getDate("CREATE_DATE");
				int readCount = rs.getInt("READ_COUNT");
				String memberName = rs.getString("MEMBER_NM");
				int replyCount = rs.getInt("REPLY_COUNT");
				
				
				Board board = new Board(boardNo, boardTitle, createDate, readCount, memberName, replyCount);
				
				boardList.add(board);
				
			}
			
		}finally {
			close(rs);
			close(pstmt);
			
		}
		
		
		return boardList;
	}

	

}
