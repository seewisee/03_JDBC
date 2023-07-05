-- 게시글 목록 조회 + 댓글 개수 (상관 서브쿼리 + 스칼라 서브쿼리)
-- BOARD 테이블 : BOARD_NO, BOARD_TITLE, BAOARD_CONTENT, CREATE_DATE, READ_COUNT, MEMBER_NO

 SELECT BOARD_NO, BOARD_TITLE, CREATE_DATE, READ_COUNT, MEMBER_NM,
 (SELECT COUNT(*) FROM REPLY R
    WHERE R.BOARD_NO = B.BOARD_NO) REPLY_COUNT
 FROM BOARD B
 JOIN MEMBER USING(MEMBER_NO)
 ORDER BY BOARD_NO DESC;
 -- 게시판 번호가 크다 == 최신 글이다
 
 -- 댓글 개수 조회(특정 게시글만!)
 SELECT COUNT(*) FROM REPLY
WHERE BOARD_NO = 1;
 
 -- BOARD 테이블 샘플 데이터
INSERT INTO BOARD 
VALUES(SEQ_BOARD_NO.NEXTVAL, '샘플 게시글 1', '샘플1 내용입니다.', DEFAULT, DEFAULT, 1);

INSERT INTO BOARD 
VALUES(SEQ_BOARD_NO.NEXTVAL, '샘플 게시글 2', '샘플2 내용입니다.', DEFAULT, DEFAULT, 1);

INSERT INTO BOARD 
VALUES(SEQ_BOARD_NO.NEXTVAL, '샘플 게시글 3', '샘플3 내용입니다.', DEFAULT, DEFAULT, 1);

COMMIT;

-- 댓글 샘플 데이터 삽입
-- REPLY_NO, REPLY_CONTENT, CREATE_DATE, MEMBER_NO, BOARD_NO
INSERT INTO REPLY
VALUES(SEQ_REPLY_NO.NEXTVAL, '샘플1의 댓글1', DEFAULT, 1, 1);

INSERT INTO REPLY
VALUES(SEQ_REPLY_NO.NEXTVAL, '샘플1의 댓글2', DEFAULT, 1, 1);

INSERT INTO REPLY
VALUES(SEQ_REPLY_NO.NEXTVAL, '샘플1의 댓글3', DEFAULT, 1, 1);

COMMIT;

-- 특정 게시글 상세 조회
SELECT B.* , MEMBER_NM
FROM BOARD B
JOIN MEMBER M ON(B.MEMBER_NO = M.MEMBER_NO)
WHERE BOARD_NO = 2;

-- 특정 게시글의 댓글 목록 조회
SELECT R.*, MEMBER_NM 
FROM REPLY R
JOIN MEMBER M ON (R.MEMBER_NO = M.MEMBER_NO) 
WHERE BOARD_NO = 1
-- ORDER BY REPLY_NO DESC; -- 최근 댓글이 상단
ORDER BY REPLY_NO;-- 최근 댓글이 하단

-- 댓글 목록에서 최근 작성한 글은 제일 위? 제일 아래?
--                            SNS   카페, 커뮤니티

-- 게시글 수 증가
-- 이전 조회수 + 1을 조회수 컬럼에 대입
UPDATE BOARD SET READ_COUNT = READ_COUNT + 1
WHERE BOARD_NO = ?;

DELETE FROM BOARD 
WHERE BOARD_NO = 1;

ROLLBACK;

ALTER TABLE REPLY
DROP CONSTRAINT SYS_C007571;




ALTER TABLE REPLY ADD FOREIGN_KEY REFERENCES BOARD (BOARD_NO) ON DELETE CASCADE;











