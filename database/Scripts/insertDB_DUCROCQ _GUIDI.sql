/* ===================================================== */
/* ================== ADMINISTRATOR ==================== */
/* ===================================================== */

INSERT INTO ADMINISTRATOR (first_name,last_name,email,password_hash)
VALUES ('Alice','Martin','alice.martin@wish.fr', 'alice123');

INSERT INTO ADMINISTRATOR (first_name,last_name,email,password_hash)
VALUES ('Lucas','Bernard','lucas.bernard@wish.fr', 'lucas123');

INSERT INTO ADMINISTRATOR (first_name,last_name,email,password_hash)
VALUES ('Sophie','Durand','sophie.durand@wish.fr', 'sophie123');

INSERT INTO ADMINISTRATOR (first_name,last_name,email,password_hash)
VALUES ('Thomas','Petit','thomas.petit@wish.fr', 'thomas123');


/* ===================================================== */
/* ====================== STUDENT ====================== */
/* ===================================================== */

INSERT INTO STUDENT (first_name,last_name,email,password_hash,student_level,promotion)
VALUES ('Emma','Dupont','emma.dupont@etu.fr', 'pwd123','L3',2026);

INSERT INTO STUDENT (first_name,last_name,email,password_hash,student_level,promotion)
VALUES ('Noah','Lefevre','noah.lefevre@etu.fr', 'pwd123','L3',2026);

INSERT INTO STUDENT (first_name,last_name,email,password_hash,student_level,promotion)
VALUES ('Chloe','Moreau','chloe.moreau@etu.fr', 'pwd123','M1',2026);

INSERT INTO STUDENT (first_name,last_name,email,password_hash,student_level,promotion)
VALUES ('Liam','Simon','liam.simon@etu.fr', 'pwd123','L3',2026);


/* ===================================================== */
/* ==================== DEPARTMENT ===================== */
/* ===================================================== */

INSERT INTO DEPARTMENT (name,description,handleBy)
VALUES ('Électronique et Télécommunications','Systèmes électroniques et télécom','Dr. Telecom');

INSERT INTO DEPARTMENT (name,description,handleBy)
VALUES ('TIC','Technologies de l''information','Dr. IT');

INSERT INTO DEPARTMENT (name,description,handleBy)
VALUES ('Systèmes Embarqués','Systèmes embarqués et instrumentation','Dr. Embedded');

INSERT INTO DEPARTMENT (name,description,handleBy)
VALUES ('Génie Électrique','Énergie et systèmes électriques','Dr. Energy');


/* ===================================================== */
/* ================= SPECIALIZATION ==================== */
/* ===================================================== */

/* --- Département Electronique - Télécom --- */
INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Électronique Automobile - Aéronautique','Systèmes embarqués critiques','ESAA','Resp ESAA',
       (SELECT department_id FROM DEPARTMENT WHERE name='Électronique et Télécommunications'));

INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Ingénierie Télécom','Réseaux et communication','ICOM','Resp ICOM',
       (SELECT department_id FROM DEPARTMENT WHERE name='Électronique et Télécommunications'));

/* --- Département TIC --- */
INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Big Data Transformation Numérique','Data engineering','BDTN','Resp BDTN',
       (SELECT department_id FROM DEPARTMENT WHERE name='TIC'));

INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Ingénieur Affaires IT - Réseaux','Business IT','IA-IR','Resp IAIR',
       (SELECT department_id FROM DEPARTMENT WHERE name='TIC'));

INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Cybersécurité IoT','Sécurité réseaux','CERT','Resp CERT',
       (SELECT department_id FROM DEPARTMENT WHERE name='TIC'));

INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Intelligence Artificielle - Big Data','Machine Learning','IA-BD','Resp IABD',
       (SELECT department_id FROM DEPARTMENT WHERE name='TIC'));

/* --- Département Systèmes embarqués --- */
INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Systèmes Médicaux','Technologies médicales','ISYMED','Resp ISYMED',
       (SELECT department_id FROM DEPARTMENT WHERE name='Systèmes Embarqués'));

INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Mécatronique','Robotique embarquée','MCTSE','Resp MCTSE',
       (SELECT department_id FROM DEPARTMENT WHERE name='Systèmes Embarqués'));

/* --- Département Génie électrique --- */ 
INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Robotique Industrielle IA','Automatisation','DARIA','Resp DARIA',
       (SELECT department_id FROM DEPARTMENT WHERE name='Génie Électrique'));

INSERT INTO SPECIALIZATION (name,description,acronym,handleBy,department_id)
VALUES ('Énergie Durable','Transition énergétique','EDD','Resp EDD',
       (SELECT department_id FROM DEPARTMENT WHERE name='Génie Électrique'));


/* ===================================================== */
/* ===================== CAMPAIGN ====================== */
/* ===================================================== */

INSERT INTO CAMPAIGN (start_date,end_date,status,max_choices,promotion,created_by,modified_by) VALUES (DATE '2026-03-01', DATE '2026-03-31','OPEN',3,2026,2000,NULL);
INSERT INTO CAMPAIGN (start_date,end_date,status,max_choices,promotion,created_by,modified_by) VALUES (DATE '2026-04-01', DATE '2026-04-30','PLANNED',2,2026,2001,NULL);
INSERT INTO CAMPAIGN (start_date,end_date,status,max_choices,promotion,created_by,modified_by) VALUES (DATE '2026-05-01', DATE '2026-05-31','PLANNED',3,2026,2002,NULL);


/* ===================================================== */
/* ======================= SESSION ===================== */
/* ===================================================== */

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (
    DATE '2026-03-10',
    TO_DATE('09:00:00', 'HH24:MI:SS'),
    TO_DATE('11:00:00', 'HH24:MI:SS'),
    30, 30, 'Salle 1', 1, 1, 2000, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-03-11',
    TO_DATE('10:00:00', 'HH24:MI:SS'),
    TO_DATE('12:00:00', 'HH24:MI:SS'),
    25, 25, 'Salle 2', 1, 1, 2000, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-03-12',
    TO_DATE('14:00:00', 'HH24:MI:SS'),
    TO_DATE('16:00:00', 'HH24:MI:SS'),
    20, 20, 'Salle 3', 1, 1, 2001, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-03-13',
    TO_DATE('09:00:00', 'HH24:MI:SS'),
    TO_DATE('11:00:00', 'HH24:MI:SS'),
    30, 30, 'Salle 4', 1, 1, 2001, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-03-14',
    TO_DATE('14:00:00', 'HH24:MI:SS'),
    TO_DATE('16:00:00', 'HH24:MI:SS'),
    25, 25, 'Salle 5', 2, 2, 2002, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-04-06',
    TO_DATE('14:00:00', 'HH24:MI:SS'),
    TO_DATE('16:00:00', 'HH24:MI:SS'),
    25, 25, 'Salle 6', 2, 3, 2003, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-04-07',
    TO_DATE('10:00:00', 'HH24:MI:SS'),
    TO_DATE('12:00:00', 'HH24:MI:SS'),
    20, 20, 'Salle 7', 3, 1, 2000, NULL
);

INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by) VALUES (
    DATE '2026-04-08',
    TO_DATE('09:00:00', 'HH24:MI:SS'),
    TO_DATE('11:00:00', 'HH24:MI:SS'),
    30, 30, 'Salle 8', 3, 2, 2000, NULL
);

INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1000,1,1,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1000,2,2,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1001,3,1,'ACCEPTED');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1001,4,2,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1002,5,1,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1002,6,1,'ACCEPTED');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1003,7,1,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1003,8,1,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1001,8,1,'PENDING');
INSERT INTO REGISTRATION (student_id,session_id,preference_rank,status) VALUES (1000,8,1,'ACCEPTED');

/* ===================================================== */
/* ==================== NOTIFICATION =================== */
/* ===================================================== */

INSERT INTO NOTIFICATION (content,type,creation_date,is_read,admin_id)
VALUES (
    'Nouvelle campagne ouverte',
    'ANNOUNCEMENT',
    SYSTIMESTAMP,
    0,
    (SELECT admin_id FROM ADMINISTRATOR WHERE email='alice.martin@wish.fr')
);

INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Nouvelle campagne ouverte','ANNOUNCEMENT',SYSTIMESTAMP,0,NULL,2000);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES  ('Inscription confirmée','SUCCESS',SYSTIMESTAMP,0,1001,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Session complète','WARNING',SYSTIMESTAMP,0,1002,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Nouvelle session disponible','INFO',SYSTIMESTAMP,0,1003,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Changement de date','INFO',SYSTIMESTAMP,0,1000,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Rappel de session','REMINDER',SYSTIMESTAMP,0,1003,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Session annulée','WARNING',SYSTIMESTAMP,0,1001,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Nouvelle campagne à venir','ANNOUNCEMENT',SYSTIMESTAMP,0,NULL,2001);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Session validée','SUCCESS',SYSTIMESTAMP,0,1001,NULL);
INSERT INTO NOTIFICATION (content,type,creation_date,is_read,student_id,admin_id) VALUES ('Clôture de campagne','INFO',SYSTIMESTAMP,0,NULL,2002);

COMMIT;