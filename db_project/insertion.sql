-- Inserting into Course Table
INSERT INTO course (course_code, course_title, semester, batch_id, credtis) VALUES('CSE233','Object Oriented Programming Language','2/2',2011,3.00);
INSERT INTO course (course_code, course_title, semester, batch_id, credtis) VALUES('CSE339','Theory of Computation','2/2',2011,2.00);
INSERT INTO course (course_code, course_title, semester, batch_id, credtis) VALUES('CSE373','Computer Graphics and Image Processing','3/2',2010,3.00);
INSERT INTO course (course_code, course_title, semester, batch_id, credtis) VALUES('CSE331','Software Engineering','3/2',2010,3.00);

-- Insert into Teacher Table
insert into teacher (teacher_code,tname,designation,contact_no,email_address,work_status) values('MZI','M. Zafar Iqbal','Professor',00112233,'mzi@sust.edu','working');
insert into teacher (teacher_code,tname,designation,contact_no,email_address,work_status) values('MRS','Md. Reza Selim','Asoc. Professor.',11223344,'mrs@sust.edu','working');
insert into teacher (teacher_code,tname,designation,contact_no,email_address,work_status) values('AN','Abu Naser','Lecturer',22334455,'an@sust.edu','working');
insert into teacher (teacher_code,tname,designation,contact_no,email_address,work_status) values('MM','Md. Masum','Asst. Professor',33445566,'mm@sust.edu','working');
insert into teacher (teacher_code,tname,designation,contact_no,email_address,work_status) values('MK','Md. Khairullah','Asst. Professor.',44556677,'mk@sust.edu','working');

-- Inserting into priority_table
insert into priority_table (teacher_id,priority_no,given_duties,max_duties) values(1,1,0,3);
insert into priority_table (teacher_id,priority_no,given_duties,max_duties) values(2,2,0,5);
insert into priority_table (teacher_id,priority_no,given_duties,max_duties) values(3,4,0,10);
insert into priority_table (teacher_id,priority_no,given_duties,max_duties) values(4,3,0,7);
insert into priority_table (teacher_id,priority_no,given_duties,max_duties) values(5,3,0,7);

-- Insert into exam_roster 
insert into exam_roster (course_id,building_name,room_no,exam_time) values(1,'A',310,'2014-06-06 00:09:30');
insert into exam_roster (course_id,building_name,room_no,exam_time) values(2,'A',310,'2014-06-17 00:09:30');
insert into exam_roster (course_id,building_name,room_no,exam_time) values(3,'A',310,'2014-06-22 00:09:30');
insert into exam_roster (course_id,building_name,room_no,exam_time) values(4,'A',310,'2014-06-28 00:09:30');

-- Insert into teacher_assign
insert into teacher_assign (teacher_id,exam_id) values (1,1);
insert into teacher_assign (teacher_id,exam_id) values (1,2);
insert into teacher_assign (teacher_id,exam_id) values (2,1);
insert into teacher_assign (teacher_id,exam_id) values (2,2);
insert into teacher_assign (teacher_id,exam_id) values (2,3);
insert into teacher_assign (teacher_id,exam_id) values (3,1);
insert into teacher_assign (teacher_id,exam_id) values (3,2);
