create table review(
    review_no number primary key,
    USER_NO NUMBER,
    REVIEW_COMMENT_NO NUMBER,
    REVIEW_CO_COMMENT_NO NUMBER,
    TITLE varchar2(200) NOT NULL,
    CONTENT varchar2(4000) NULL,
    SCORE NUMBER,
    "LIKE" NUMBER,
    DISLIKE NUMBER
);

CREATE TABLE "REPORT"(
    TABLE_NAME VARCHAR2(40) NOT NULL,
    TABLE_NO NUMBER,
    CONTENT varchar2(4000)
);

CREATE TABLE "TAG"(
    TAG_NO NUMBER PRIMARY KEY,
    review_NO NUMBER,
    BOOK_NO NUMBER
);

CREATE TABLE REVIEW_COMMENT(
    REVIEW_COMMENT_NO NUMBER PRIMARY KEY,
    USER_NO NUMBER,
    REVIEW_NO NUMBER,
    CONTENT VARCHAR2(4000)
);

CREATE TABLE REVIEW_CO_COMMENT(
    REVIEW_CO_COMMENT_NO NUMBER PRIMARY KEY,
    USER_NO NUMBER,
    REVIEW_NO NUMBER,
    REVIEW_COMMENT_NO NUMBER,
    CONTENT VARCHAR2(4000)
);

CREATE SEQUENCE REVIEW_SEQ;
CREATE SEQUENCE REPORT_SEQ;
CREATE SEQUENCE REVIEW_COMMENT_SEQ;
CREATE SEQUENCE REVIEW_CO_COMMENT_SEQ;
CREATE SEQUENCE TAG_SEQ;

--REVIEW FK
ALTER TABLE REVIEW ADD CONSTRAINT REVIEW_USER_no_FK
FOREIGN KEY(USER_no) REFERENCES "USER"(USER_NO) ON DELETE SET NULL;

ALTER TABLE REVIEW ADD CONSTRAINT REVIEW_COMMENT_NO_FK
FOREIGN KEY(REVIEW_COMMENT_NO) REFERENCES REVIEW_COMMENT(REVIEW_COMMENT_NO) ON DELETE SET NULL;

ALTER TABLE REVIEW ADD CONSTRAINT REVIEW_CO_COMMENT_NO_FK
FOREIGN KEY(REVIEW_CO_COMMENT_NO) REFERENCES REVIEW_CO_COMMENT(REVIEW_CO_COMMENT_NO) ON DELETE SET NULL;

--//REVIEW FK
--TAG FK

ALTER TABLE "TAG" ADD CONSTRAINT TAG_review_NO_FK
FOREIGN KEY(REVIEW_NO) REFERENCES REVIEW(review_NO) ON DELETE SET NULL;

ALTER TABLE "TAG" ADD CONSTRAINT TAG_book_NO_FK
FOREIGN KEY(book_NO) REFERENCES BOOK(Book_NO) ON DELETE SET NULL;
--//TAG FK
--REVIEW_COMMENT

ALTER TABLE review_comment ADD CONSTRAINT review_comment_user_NO_FK
FOREIGN KEY(user_NO) REFERENCES "USER"(user_no) ON DELETE SET NULL;

ALTER TABLE review_comment ADD CONSTRAINT review_comment_review_NO_FK
FOREIGN KEY(review_NO) REFERENCES review(review_no) ON DELETE SET NULL;

--//review_comment
--review_co_comment
ALTER TABLE review_co_comment ADD CONSTRAINT review_co_comment_user_NO_FK
FOREIGN KEY(user_NO) REFERENCES "USER"(user_no) ON DELETE SET NULL;

ALTER TABLE review_co_comment ADD CONSTRAINT review_co_comment_review_NO_FK
FOREIGN KEY(review_NO) REFERENCES review(review_no) ON DELETE SET NULL;

ALTER TABLE review_co_comment ADD CONSTRAINT rev_comment_NO_FK
FOREIGN KEY(review_comment_NO) REFERENCES review_comment(review_comment_no) ON DELETE SET NULL;
--//review_co_comment

COMMIT;
