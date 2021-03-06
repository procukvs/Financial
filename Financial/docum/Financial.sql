drop database if exists Financial;
create database Financial;
use Financial;

/*!40101 SET character_set_client = utf8 */;

drop table if exists Amount;
drop table if exists Movement;
drop table if exists Instant;
drop table if exists Account;
drop table if exists CreditOp;
drop table if exists AccountOp;
drop table if exists Operation;
drop table if exists Product;
drop table if exists Client;
drop table if exists Event;

create table Client(
  idCl integer primary key,
  name varchar(25) not null
) engine = InnoDB;

create table Product(
    idPr integer primary key,
    idCl integer,
    kind varchar(7) not null, 	
	begin date not null,
	foreign key (idCl) references Client(idCl)
)engine = InnoDB; 

create table Instant(
    idPr integer primary key, 
    kind varchar(7) not null, 
    sum float not null,
    end date not null, 
    rate float not null,
    state varchar(5) not null, 	
    foreign key (idPr) references Product(idPr)
)engine = InnoDB; 

create table Account(  
	number char(6) primary key,
	idPr integer,
	name varchar(8) not null, 
	kind char(3) not null,
    foreign key (idPr) references Product(idPr)  
) engine = InnoDB;

create table Operation(
	idOp integer primary key, 
	idPr integer not null,
	day date not null,
	kind varchar(7) not null,	
	type varchar(6) not null,	
	foreign key (idPr) references Product(idPr)
) engine = InnoDB;

create table AccountOp(
	idOp integer primary key, 
	sum decimal (12,2) not null,
	idCl integer,
	foreign key (idOp) references Operation(idOp),
	foreign key (idCl) references Client(idCl)
) engine = InnoDB;

create table CreditOp(
	idOp integer primary key, 
	charged decimal (12,2) not null,
	pay decimal (12,2) not null,
	turn decimal (12,2) not null,	
	foreign key (idOp) references Operation(idOp)
) engine = InnoDB;

create table Movement(
	idM integer primary key, 
	sum decimal (12,2) not null,
	numberD char(6) not null,
	numberC char(6)not null, 
	idOp integer not null,
	foreign key (idOp) references Operation(idOp),
	foreign key (numberD) references Account(number),
	foreign key (numberC) references Account(number)
) engine = InnoDB;

create table Amount(
	number char(6),
    day date,	
	sum decimal (12,2) not null,
	primary key (number, day),
	foreign key (number) references Account(number)
) engine = InnoDB;

insert into Account values ('190001', NULL,'cash','act');

create table Event(
	id integer primary key,
	day date,
	product char(1),
	operation char(6),
	idCP integer,
	sum decimal (12,2),  -- sum
	rate float,          -- rate
	end date,            -- end instant (dep/credit) 
	idC integer,         -- id client
	name varchar(25)     -- name client
); 

insert into Event values (1, '2012-02-01', 'A', 'begin', NULL, NULL, NULL, NULL, NULL, 'Gavrosh');
insert into Event values (2, '2012-02-06', 'A', 'put', 1, 341.11, NULL, NULL, NULL, NULL);
insert into Event values (3, '2012-02-09', 'A', 'put', 1, 110, NULL, NULL, NULL, NULL);
insert into Event values (4, '2012-02-05', 'A', 'put', 1, 10, NULL, NULL, NULL, NULL);
insert into Event values (5, '2012-02-06', 'A', 'put', 1, 200, NULL, NULL, NULL, NULL);
insert into Event values (6, '2012-02-08', 'A', 'put', 1, 20, NULL, NULL, NULL, NULL);
insert into Event values (7, '2012-02-08', 'A', 'put', 1, 20, NULL, NULL, NULL, NULL);
insert into Event values (8, '2012-02-06', 'A', 'begin', NULL, NULL, NULL, NULL, NULL, 'Petrenko');
insert into Event values (9, '2012-02-07', 'A', 'move', 1, 300, NULL, NULL, 2, NULL);
insert into Event values (10, '2012-02-08', 'D', 'begin', 2, 300, 30, '2012-02-20', NULL, NULL);
insert into Event values (11, '2012-02-20', 'D', 'close', 3, NULL, NULL, NULL, NULL, NULL);
insert into Event values (12, '2012-02-21', 'D', 'begin', 2, 300, 30, '2012-03-21', NULL, NULL);
insert into Event values (13, '2012-02-28', 'D', 'abort', 4, NULL, NULL, NULL, NULL, NULL);
