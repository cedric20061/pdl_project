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
/* Campagne 1: OPEN (en cours) - promotion 2026 */
INSERT INTO CAMPAIGN (start_date, end_date, status, max_choices, promotion, created_by, modified_by) 
VALUES (DATE '2026-03-01', DATE '2026-03-31', 'OPEN', 3, 2026, 2000, NULL);

/* Campagne 2: PLANNED (prochaine) - promotion 2026 */
INSERT INTO CAMPAIGN (start_date, end_date, status, max_choices, promotion, created_by, modified_by) 
VALUES (DATE '2026-04-01', DATE '2026-04-30', 'PLANNED', 3, 2026, 2001, NULL);

/* Campagne 3: CLOSED (fermée) - promotion 2026 */
INSERT INTO CAMPAIGN (start_date, end_date, status, max_choices, promotion, created_by, modified_by) 
VALUES (DATE '2026-02-01', DATE '2026-02-28', 'CLOSED', 2, 2026, 2002, NULL);


/* ===================================================== */
/* ======================= SESSION ===================== */
/* ===================================================== */

/* ===== CAMPAIGN 1 (OPEN) - Sessions variées avec couverture complète des spécialisations ===== */
/* NOTE: Chevauchements intentionnels d'horaires sur 2026-03-10 matin:
   - Session 1 (08:30-09:00) + Session 2 (08:30-09:30) = CONFLIT HORAIRE
   - Session 2 (08:30-09:30) + Session 3 (09:00-09:30) = CONFLIT HORAIRE
   Ces chevauchements permettent de tester la gestion des conflits d'emploi du temps
*/

/* Session 1 - ESAA (Spé 1) - 2026-03-10 - 08:30-09:00 - 4 places (SESSION REMPLIE) */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('08:30:00', 'HH24:MI:SS'), TO_DATE('09:00:00', 'HH24:MI:SS'), 4, 0, 'A101', 1, 1, 2000, NULL);

/* Session 2 - ICOM (Spé 2) - 2026-03-10 - 08:30-09:30 (CHEVAUCHEMENT avec Session 1) */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('08:30:00', 'HH24:MI:SS'), TO_DATE('09:30:00', 'HH24:MI:SS'), 25, 24, 'A102', 2, 1, 2000, NULL);

/* Session 3 - BDTN (Spé 3) - 2026-03-10 - 09:00-09:30 (CHEVAUCHEMENT avec Session 2) */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('09:00:00', 'HH24:MI:SS'), TO_DATE('09:30:00', 'HH24:MI:SS'), 30, 29, 'B201', 3, 1, 2000, NULL);

/* Session 4 - IA-IR (Spé 4) - 2026-03-10 - 10:00-10:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('10:00:00', 'HH24:MI:SS'), TO_DATE('10:30:00', 'HH24:MI:SS'), 25, 24, 'B202', 4, 1, 2000, NULL);

/* Session 5 - CERT (Spé 5) - 2026-03-10 - 10:30-11:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('10:30:00', 'HH24:MI:SS'), TO_DATE('11:00:00', 'HH24:MI:SS'), 20, 19, 'C301', 5, 1, 2000, NULL);

/* Session 6 - IA-BD (Spé 6) - 2026-03-10 - 11:00-11:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('11:00:00', 'HH24:MI:SS'), TO_DATE('11:30:00', 'HH24:MI:SS'), 28, 27, 'C302', 6, 1, 2000, NULL);

/* Session 7 - ISYMED (Spé 7) - 2026-03-10 - 13:30-14:00 (après midi, pas de conflit) */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('13:30:00', 'HH24:MI:SS'), TO_DATE('14:00:00', 'HH24:MI:SS'), 30, 29, 'D401', 7, 1, 2000, NULL);

/* Session 8 - MCTSE (Spé 8) - 2026-03-10 - 14:00-14:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('14:00:00', 'HH24:MI:SS'), TO_DATE('14:30:00', 'HH24:MI:SS'), 22, 21, 'D402', 8, 1, 2000, NULL);

/* Session 9 - DARIA (Spé 9) - 2026-03-10 - 14:30-15:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('14:30:00', 'HH24:MI:SS'), TO_DATE('15:00:00', 'HH24:MI:SS'), 25, 24, 'E501', 9, 1, 2000, NULL);

/* Session 10 - EDD (Spé 10) - 2026-03-10 - 15:00-15:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-10', TO_DATE('15:00:00', 'HH24:MI:SS'), TO_DATE('15:30:00', 'HH24:MI:SS'), 26, 25, 'E502', 10, 1, 2000, NULL);

/* Sessions supplémentaires pour Campaign 1 (jours différents pour éviter conflits) */
/* Session 11 - ESAA (Spé 1) - 2026-03-11 - 09:00-09:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-11', TO_DATE('09:00:00', 'HH24:MI:SS'), TO_DATE('09:30:00', 'HH24:MI:SS'), 30, 29, 'A103', 1, 1, 2001, NULL);

/* Session 12 - ICOM (Spé 2) - 2026-03-11 - 10:00-10:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-11', TO_DATE('10:00:00', 'HH24:MI:SS'), TO_DATE('10:30:00', 'HH24:MI:SS'), 25, 24, 'B203', 2, 1, 2001, NULL);

/* Session 13 - BDTN (Spé 3) - 2026-03-11 - 14:00-14:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-11', TO_DATE('14:00:00', 'HH24:MI:SS'), TO_DATE('14:30:00', 'HH24:MI:SS'), 28, 27, 'B204', 3, 1, 2001, NULL);

/* Session 14 - IA-BD (Spé 6) - 2026-03-12 - 08:30-09:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-12', TO_DATE('08:30:00', 'HH24:MI:SS'), TO_DATE('09:00:00', 'HH24:MI:SS'), 30, 29, 'C303', 6, 1, 2002, NULL);

/* Session 15 - CERT (Spé 5) - 2026-03-12 - 10:30-11:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-03-12', TO_DATE('10:30:00', 'HH24:MI:SS'), TO_DATE('11:00:00', 'HH24:MI:SS'), 24, 23, 'C304', 5, 1, 2002, NULL);


/* ===== CAMPAIGN 2 (PLANNED) - Sessions pour la campagne planifiée ===== */

/* Session 16 - ESAA (Spé 1) - 2026-04-05 - 08:30-09:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-04-05', TO_DATE('08:30:00', 'HH24:MI:SS'), TO_DATE('09:00:00', 'HH24:MI:SS'), 30, 29, 'A201', 1, 2, 2000, NULL);

/* Session 17 - ICOM (Spé 2) - 2026-04-05 - 09:30-10:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-04-05', TO_DATE('09:30:00', 'HH24:MI:SS'), TO_DATE('10:00:00', 'HH24:MI:SS'), 28, 27, 'B301', 2, 2, 2000, NULL);

/* Session 18 - BDTN (Spé 3) - 2026-04-05 - 13:30-14:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-04-05', TO_DATE('13:30:00', 'HH24:MI:SS'), TO_DATE('14:00:00', 'HH24:MI:SS'), 30, 29, 'B302', 3, 2, 2000, NULL);

/* Session 19 - IA-IR (Spé 4) - 2026-04-06 - 10:00-10:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-04-06', TO_DATE('10:00:00', 'HH24:MI:SS'), TO_DATE('10:30:00', 'HH24:MI:SS'), 25, 24, 'C401', 4, 2, 2001, NULL);

/* Session 20 - CERT (Spé 5) - 2026-04-06 - 14:00-14:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-04-06', TO_DATE('14:00:00', 'HH24:MI:SS'), TO_DATE('14:30:00', 'HH24:MI:SS'), 22, 21, 'D501', 5, 2, 2001, NULL);

/* Session 21 - IA-BD (Spé 6) - 2026-04-06 - 15:30-16:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-04-06', TO_DATE('15:30:00', 'HH24:MI:SS'), TO_DATE('16:00:00', 'HH24:MI:SS'), 27, 26, 'E601', 6, 2, 2001, NULL);


/* ===== CAMPAIGN 3 (CLOSED) - Sessions de la campagne fermée (toutes les inscriptions acceptées) ===== */

/* Session 22 - ESAA (Spé 1) - 2026-02-10 - 08:30-09:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-02-10', TO_DATE('08:30:00', 'HH24:MI:SS'), TO_DATE('09:00:00', 'HH24:MI:SS'), 30, 28, 'F101', 1, 3, 2002, NULL);

/* Session 23 - ICOM (Spé 2) - 2026-02-10 - 09:30-10:00 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-02-10', TO_DATE('09:30:00', 'HH24:MI:SS'), TO_DATE('10:00:00', 'HH24:MI:SS'), 25, 24, 'F102', 2, 3, 2002, NULL);

/* Session 24 - BDTN (Spé 3) - 2026-02-10 - 14:00-14:30 */
INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by, modified_by)
VALUES (DATE '2026-02-10', TO_DATE('14:00:00', 'HH24:MI:SS'), TO_DATE('14:30:00', 'HH24:MI:SS'), 28, 27, 'G201', 3, 3, 2002, NULL);


/* ===================================================== */
/* ==================== REGISTRATION =================== */
/* ===================================================== */

/* ===== CAMPAIGN 1 (OPEN) ===== */

/* Session 1 (remplie avec 4 places) - Tous les 4 étudiants inscrits */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 1, 1, 'ACCEPTED');
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 1, 1, 'ACCEPTED');
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 1, 1, 'ACCEPTED');
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 1, 1, 'ACCEPTED');

/* Session 2 - ICOM - Étudiant 1000 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 2, 2, 'PENDING');

/* Session 3 - BDTN - Étudiant 1001 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 3, 2, 'PENDING');

/* Session 4 - IA-IR - Étudiant 1002 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 4, 2, 'PENDING');

/* Session 5 - CERT - Étudiant 1003 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 5, 2, 'PENDING');

/* Session 6 - IA-BD - Étudiant 1000 (13:30, pas de conflit avec 08:30-09:00) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 6, 3, 'ACCEPTED');

/* Session 7 - ISYMED - Étudiant 1001 (13:30) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 7, 3, 'PENDING');

/* Session 8 - MCTSE - Étudiant 1002 (14:00) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 8, 3, 'ACCEPTED');

/* Session 9 - DARIA - Étudiant 1003 (14:30) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 9, 3, 'PENDING');

/* Session 10 - EDD - Étudiant 1000 (15:00) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 10, 1, 'PENDING');

/* Session 11 (2026-03-11) - ESAA - Étudiant 1001 (09:00) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 11, 1, 'ACCEPTED');

/* Session 12 (2026-03-11) - ICOM - Étudiant 1002 (10:00) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 12, 1, 'PENDING');

/* Session 13 (2026-03-11) - BDTN - Étudiant 1003 (14:00) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 13, 1, 'ACCEPTED');

/* Session 14 (2026-03-12) - IA-BD - Étudiant 1000 (08:30) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 14, 2, 'PENDING');

/* Session 15 (2026-03-12) - CERT - Étudiant 1001 (10:30) */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 15, 2, 'ACCEPTED');


/* ===== CAMPAIGN 2 (PLANNED) ===== */

/* Session 16 - ESAA - Étudiant 1002 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 16, 1, 'PENDING');

/* Session 17 - ICOM - Étudiant 1003 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 17, 1, 'PENDING');

/* Session 18 - BDTN - Étudiant 1000 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 18, 1, 'PENDING');

/* Session 19 - IA-IR - Étudiant 1001 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 19, 1, 'PENDING');

/* Session 20 - CERT - Étudiant 1002 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 20, 2, 'PENDING');

/* Session 21 - IA-BD - Étudiant 1003 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 21, 2, 'PENDING');


/* ===== CAMPAIGN 3 (CLOSED) - Toutes les inscriptions acceptées ===== */

/* Session 22 - ESAA - Étudiant 1000 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1000, 22, 1, 'ACCEPTED');

/* Session 23 - ICOM - Étudiant 1001 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1001, 23, 1, 'ACCEPTED');

/* Session 24 - BDTN - Étudiant 1002 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1002, 24, 1, 'ACCEPTED');

/* Inscription supplémentaire Campaign 3 - Étudiant 1003 */
INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) VALUES (1003, 22, 2, 'ACCEPTED');

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