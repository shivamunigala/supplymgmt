create table users (id varchar2(255) primary key, name varchar2(255), username varchar2(255) unique, password varchar2(255), role varchar2(10));
create table products (pid int primary key,pname varchar(20),qty int);
drop table users;
create table hostels (hid int primary key,hname varchar2(20),phno int,category int);
create table price_category (category int,pid int,price int,primary key(category,pid));

select * from users;
commit;

