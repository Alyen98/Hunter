-- Inserindo os Hunters principais
INSERT INTO hunters(id, name, age) VALUES (1, 'Gon Freecss', 12);
INSERT INTO hunters(id, name, age) VALUES (2, 'Killua Zoldyck', 12);
INSERT INTO hunters(id, name, age) VALUES (3, 'Kurapika', 17);
INSERT INTO hunters(id, name, age) VALUES (4, 'Leorio Paradinight', 19);

-- Inserindo as Licenças Hunter e associando ao Hunter
INSERT INTO hunter_licenses(id, licenseNumber, issueDate, hunter_id) VALUES (1, '1189-2887-34', '1999-09-01', 1);
INSERT INTO hunter_licenses(id, licenseNumber, issueDate, hunter_id) VALUES (2, '1190-2887-35', '1999-09-01', 2);
INSERT INTO hunter_licenses(id, licenseNumber, issueDate, hunter_id) VALUES (3, '1191-2887-36', '1999-09-01', 3);
INSERT INTO hunter_licenses(id, licenseNumber, issueDate, hunter_id) VALUES (4, '1192-2887-37', '1999-09-01', 4);

-- Inserindo os Exames e Eventos
INSERT INTO exams(id, name, examYear) VALUES (1, '287th Hunter Exam', 1999);
INSERT INTO exams(id, name, examYear) VALUES (2, 'Heavens Arena', 1999);
INSERT INTO exams(id, name, examYear) VALUES (3, 'Greed Island', 2000);
INSERT INTO exams(id, name, examYear) VALUES (4, '288th Hunter Exam', 2000);

-- Relacionando os Hunters com os Exames/Eventos
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (1, 1);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (1, 2);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (1, 3);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (2, 1);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (2, 2);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (2, 3);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (2, 4);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (3, 1);
INSERT INTO hunter_exam(hunter_id, exam_id) VALUES (4, 1);

-- Inserindo as Cards (Habilidades Nen)
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (1, 'Jajanken', 0, 'Greed Island Arc', 1);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (2, 'Lightning Palm', 4, 'Heavens Arena Arc', 2);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (5, 'Thunderbolt', 4, 'Chimera Ant Arc', 2);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (6, 'Godspeed', 4, 'Chimera Ant Arc', 2);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (3, 'Holy Chain', 3, 'Yorknew City Arc', 3);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (7, 'Chain Jail', 3, 'Yorknew City Arc', 3);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (8, 'Judgment Chain', 3, 'Yorknew City Arc', 3);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (9, 'Emperor Time', 5, 'Yorknew City Arc', 3);
INSERT INTO cards(id, nenAbility, nenType, exam, hunter_id) VALUES (4, 'Warping Punch', 1, 'Chairman Election Arc', 4);

-- ATUALIZA AS SEQUÊNCIAS PARA EVITAR CONFLITOS DE ID
alter sequence hunters_SEQ restart with 10;
alter sequence cards_SEQ restart with 10;
alter sequence exams_SEQ restart with 10;
alter sequence hunter_licenses_SEQ restart with 10;

