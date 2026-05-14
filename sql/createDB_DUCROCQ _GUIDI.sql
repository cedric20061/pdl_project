-- =============================================
-- Suppression de toutes les tables et séquences existantes
-- =============================================
SET SERVEROUTPUT ON

DECLARE
    CURSOR cur_tables IS
        SELECT table_name FROM user_tables;

    CURSOR cur_sequences IS
        SELECT sequence_name
        FROM user_sequences
        WHERE sequence_name NOT LIKE 'ISEQ$$_%';

    sql_stmt VARCHAR2(500);
BEGIN
    -- Suppression des tables
    FOR t IN cur_tables LOOP
        -- Entourer le nom de guillemets si nécessaire
        sql_stmt := 'DROP TABLE "' || t.table_name || '" CASCADE CONSTRAINTS';
        BEGIN
            EXECUTE IMMEDIATE sql_stmt;
            DBMS_OUTPUT.PUT_LINE('Table supprimée : ' || t.table_name);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Erreur suppression table : ' || t.table_name || ' -> ' || SQLERRM);
        END;
    END LOOP;

    -- Suppression des séquences
    FOR s IN cur_sequences LOOP
        sql_stmt := 'DROP SEQUENCE "' || s.sequence_name || '"';
        BEGIN
            EXECUTE IMMEDIATE sql_stmt;
            DBMS_OUTPUT.PUT_LINE('Séquence supprimée : ' || s.sequence_name);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Erreur suppression séquence : ' || s.sequence_name || ' -> ' || SQLERRM);
        END;
    END LOOP;
END;
/

-- =============================================
-- SEQUENCES
-- =============================================
CREATE SEQUENCE SEQ_STUDENT START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE SEQ_ADMINISTRATOR START WITH 2000 INCREMENT BY 1;
CREATE SEQUENCE SEQ_DEPARTMENT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_SPECIALIZATION START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_CAMPAIGN START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_SESSIONS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_NOTIFICATION START WITH 1 INCREMENT BY 1;

-- =============================================
-- TABLE STUDENT
-- =============================================
CREATE TABLE STUDENT (
    student_id NUMBER(10) DEFAULT SEQ_STUDENT.NEXTVAL,
    first_name VARCHAR2(50) CONSTRAINT NN_first_name NOT NULL,
    last_name  VARCHAR2(50) CONSTRAINT NN_last_name NOT NULL,
    email      VARCHAR2(100) CONSTRAINT NN_email NOT NULL,
    password_hash VARCHAR2(255) CONSTRAINT NN_password NOT NULL,
    student_level VARCHAR2(20) CONSTRAINT NN_level NOT NULL,
    promotion  NUMBER(4) CONSTRAINT NN_promotion NOT NULL,
    CONSTRAINT PK_STUDENT PRIMARY KEY (student_id),
    CONSTRAINT UN_email_STUDENT UNIQUE (email)
);

-- =============================================
-- TABLE ADMINISTRATOR
-- =============================================
CREATE TABLE ADMINISTRATOR (
    admin_id NUMBER(10) DEFAULT SEQ_ADMINISTRATOR.NEXTVAL,
    first_name VARCHAR2(50) CONSTRAINT NN_first_name_ADMIN NOT NULL,
    last_name  VARCHAR2(50) CONSTRAINT NN_last_name_ADMIN NOT NULL,
    email      VARCHAR2(100) CONSTRAINT NN_email_ADMIN NOT NULL,
    password_hash VARCHAR2(255) CONSTRAINT NN_password_ADMIN NOT NULL,
    CONSTRAINT PK_ADMINISTRATOR PRIMARY KEY (admin_id),
    CONSTRAINT UN_email_ADMINISTRATOR UNIQUE (email)
);

-- =============================================
-- TABLE DEPARTMENT
-- =============================================
CREATE TABLE DEPARTMENT (
    department_id NUMBER(5) DEFAULT SEQ_DEPARTMENT.NEXTVAL,
    name        VARCHAR2(50) CONSTRAINT NN_name_DEPARTMENT NOT NULL,
    description VARCHAR2(255),
    handleBy    VARCHAR2(50) CONSTRAINT NN_handleBy_DEPARTMENT NOT NULL,
    CONSTRAINT PK_DEPARTMENT PRIMARY KEY (department_id),
    CONSTRAINT UN_name_DEPARTMENT UNIQUE (name)
);

-- =============================================
-- TABLE SPECIALIZATION 
-- =============================================
CREATE TABLE SPECIALIZATION (
    specialization_id NUMBER(5) DEFAULT SEQ_SPECIALIZATION.NEXTVAL,
    name          VARCHAR2(50) CONSTRAINT NN_name_SPECIALIZATION NOT NULL,
    description   VARCHAR2(255),
    acronym       VARCHAR2(10),
    handleBy      VARCHAR2(50) CONSTRAINT NN_handleBy_SPECIALIZATION NOT NULL,
    department_id NUMBER(5) CONSTRAINT NN_department_id_SPECIALIZATION NOT NULL,
    CONSTRAINT PK_SPECIALIZATION PRIMARY KEY (specialization_id),
    CONSTRAINT FK_department_id_DEPARTMENT_department_id
        FOREIGN KEY (department_id)
        REFERENCES DEPARTMENT(department_id)
);
-- =============================================
-- TABLE CAMPAIGN
-- =============================================
CREATE TABLE CAMPAIGN (
    campaign_id NUMBER(5) DEFAULT SEQ_CAMPAIGN.NEXTVAL,
    start_date  DATE CONSTRAINT NN_start_date NOT NULL,
    end_date    DATE CONSTRAINT NN_end_date NOT NULL,
    status      VARCHAR2(20) CONSTRAINT NN_status NOT NULL,
    max_choices NUMBER(2) CONSTRAINT NN_max_choices NOT NULL,
    promotion   NUMBER(4) CONSTRAINT NN_promotion_CAMPAIGN NOT NULL,
    created_by  NUMBER(10) CONSTRAINT NN_created_by NOT NULL,
    modified_by NUMBER(10),
    
    CONSTRAINT PK_CAMPAIGN PRIMARY KEY (campaign_id),

    CONSTRAINT FK_created_by_ADMINISTRATOR_admin_id
        FOREIGN KEY (created_by)
        REFERENCES ADMINISTRATOR(admin_id),

    CONSTRAINT FK_modified_by_ADMINISTRATOR_admin_id
        FOREIGN KEY (modified_by)
        REFERENCES ADMINISTRATOR(admin_id),

    CONSTRAINT CK_campaign_dates 
        CHECK (end_date >= start_date),

    CONSTRAINT CK_campaign_status 
        CHECK (status IN ('OPEN', 'CLOSED', 'ARCHIVED', 'PLANNED'))
);
-- =============================================
-- TABLE SESSIONS
-- =============================================
CREATE TABLE SESSIONS (
    session_id NUMBER(10) DEFAULT SEQ_SESSIONS.NEXTVAL,
    session_date DATE CONSTRAINT NN_session_date NOT NULL,
    start_time   DATE CONSTRAINT NN_start_time NOT NULL,
    end_time     DATE CONSTRAINT NN_end_time NOT NULL,
    max_capacity NUMBER(3) CONSTRAINT NN_max_capacity NOT NULL,
    remaining_capacity NUMBER(3) CONSTRAINT NN_remaining_capacity NOT NULL,
    room VARCHAR2(10) CONSTRAINT NN_room NOT NULL,
    specialization_id NUMBER(5) CONSTRAINT NN_specialization_id NOT NULL,
    campaign_id       NUMBER(5) CONSTRAINT NN_campaign_id NOT NULL,
    created_by  NUMBER(10) CONSTRAINT NN_created_by_SESSIONS NOT NULL,
    modified_by NUMBER(10),
    CONSTRAINT PK_SESSIONS PRIMARY KEY (session_id),
    CONSTRAINT FK_specialization_id_SPECIALIZATION_specialization_id
        FOREIGN KEY (specialization_id)
        REFERENCES SPECIALIZATION(specialization_id),
    CONSTRAINT FK_campaign_id_CAMPAIGN_campaign_id
        FOREIGN KEY (campaign_id)
        REFERENCES CAMPAIGN(campaign_id),
    CONSTRAINT FK_created_by_SESSIONS_ADMINISTRATOR_admin_id
        FOREIGN KEY (created_by)
        REFERENCES ADMINISTRATOR(admin_id),
    CONSTRAINT FK_modified_by_SESSIONS_ADMINISTRATOR_admin_id
        FOREIGN KEY (modified_by)
        REFERENCES ADMINISTRATOR(admin_id),
    CONSTRAINT CK_session_time CHECK (end_time > start_time)
);
-- =============================================
-- TABLE REGISTRATION
-- =============================================
CREATE TABLE REGISTRATION (
    student_id NUMBER(10),
    session_id NUMBER(10),
    preference_rank NUMBER(2) CONSTRAINT NN_preference_rank NOT NULL,
    status          VARCHAR2(20) CONSTRAINT NN_status_REG NOT NULL,

    CONSTRAINT PK_REGISTRATION PRIMARY KEY (student_id, session_id),

    CONSTRAINT FK_student_id_STUDENT_student_id
        FOREIGN KEY (student_id)
        REFERENCES STUDENT(student_id)
        ON DELETE CASCADE,

    CONSTRAINT FK_session_id_SESSIONS_session_id
        FOREIGN KEY (session_id)
        REFERENCES SESSIONS(session_id)
        ON DELETE CASCADE,

    CONSTRAINT CK_registration_status
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);

-- =============================================
-- TABLE NOTIFICATION
-- =============================================
CREATE TABLE NOTIFICATION (
    notification_id NUMBER(10) DEFAULT SEQ_NOTIFICATION.NEXTVAL,
    content       VARCHAR2(255) CONSTRAINT NN_content NOT NULL,
    type          VARCHAR2(30) CONSTRAINT NN_type NOT NULL,
    creation_date TIMESTAMP CONSTRAINT NN_creation_date NOT NULL,
    is_read       NUMBER(1) CONSTRAINT NN_is_read NOT NULL,
    student_id NUMBER(10),
    admin_id   NUMBER(10),
    CONSTRAINT PK_NOTIFICATION PRIMARY KEY (notification_id),
    CONSTRAINT FK_student_id_NOTIFICATION_STUDENT_student_id
        FOREIGN KEY (student_id)
        REFERENCES STUDENT(student_id)
        ON DELETE SET NULL,
    CONSTRAINT FK_admin_id_NOTIFICATION_ADMINISTRATOR_admin_id
        FOREIGN KEY (admin_id)
        REFERENCES ADMINISTRATOR(admin_id)
        ON DELETE SET NULL
);

COMMIT;