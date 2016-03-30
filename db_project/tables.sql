create table course
(
    course_id integer not null auto_increment,
	course_code varchar(10) not null,
	course_title varchar(50) not null,
	semester varchar(3) not null,
	batch_id numeric(5,0) not null,
	credtis numeric(5,1) not null,
	primary key (course_id)
);

create table teacher(
  teacher_id integer not null auto_increment,
  teacher_code varchar(5) not null,
  tname varchar(50) not null,
  designation varchar(50) not null,
  contact_no numeric(11,0) not null,
  email_address varchar(50) not null,
  work_status varchar(50) not null,
  primary key (teacher_id)
);


create table priority_table(
  pid integer not null auto_increment,
  teacher_id integer,
  priority_no numeric(3,0) not null,
  given_duties numeric(3,0) not null,
  max_duties numeric(3,0) not null,
  primary key (pid),
  foreign key(teacher_id) references teacher(teacher_id) ON DELETE CASCADE ON UPDATE cascade
);

create table exam_roster(
  exam_id integer not null auto_increment,	
  course_id integer not null,
  building_name varchar(50) not null,
  room_no numeric(10,0) not null,
  exam_time timestamp, 
  primary key (exam_id),
  foreign key(course_id) references course(course_id) ON DELETE CASCADE ON UPDATE cascade
);

create table teacher_assign(
  ta_id integer not null auto_increment,
  teacher_id integer,
  exam_id integer,
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  changed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (ta_id),
  foreign key(teacher_id) references teacher(teacher_id) ON DELETE CASCADE ON UPDATE cascade,
  foreign key (exam_id) references exam_roster(exam_id) ON DELETE CASCADE ON UPDATE cascade
);