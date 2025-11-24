use gallery;

drop table if exists items;
create table items (
id           int auto_increment comment '아이디' primary key,
name         varchar(50)                          not null comment '상품 이름',
img_path     varchar(100)                         not null comment '상품 사진 경로',
price        int                                  not null comment '상품 가격',
discount_per int                                  not null comment '상품 할인율',
created      datetime default current_timestamp() not null comment '생성 일시'
) comment '상품';


INSERT INTO items (name, img_path, price, discount_per)
VALUES ('Starry', '/img/001.jpg', 10000000, 5)
     , ('Seascape', '/img/002.jpg', 20000000, 10)
     , ('Arles', '/img/003.jpg', 30000000, 15)
     , ('Mountain', '/img/004.jpg', 40000000, 20)
     , ('Provence', '/img/005.jpg', 50000000, 25)
     , ('Houses', '/img/006.jpg', 60000000, 30);

select * from items;


drop table if exists member;

create table member(
    id int primary key auto_increment,
    name varchar(50) not null,
    login_id varchar(50) unique not null,
    login_pw varchar(100) not null,
    created datetime default current_timestamp() not null
);


insert into member
    (name, login_id, login_pw) values ('홍길동','member01','1234');









