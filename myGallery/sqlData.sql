use mygallery;

drop table if exists items;
create table items (
                       id           int auto_increment comment '아이디' primary key,
                       name         varchar(50)                          not null comment '상품 이름',
                       img_path     varchar(100)                         not null comment '상품 사진 경로',
                       price        int                                  not null comment '상품 가격',
                       discount_per int                                  not null comment '상품 할인율',
                       created      datetime default current_timestamp() not null comment '생성 일시'
) comment '상품';

truncate items;
INSERT INTO items (name, img_path, price, discount_per)
VALUES ('야간등반', '/img/001.jpg', 1000000, 5)
     , ('겨울산', '/img/002.jpg', 2000000, 10)
     , ('완등후', '/img/003.jpg', 3000000, 15)
     , ('절벽등반', '/img/004.jpg', 4000000, 20)
     , ('멀티등반', '/img/005.jpg', 5000000, 25)
     , ('멀티등반2', '/img/006.jpg', 6000000, 30)
     , ('렛서팬더등반', '/img/007.jpg', 7000000, 45)
, ('고양이등반', '/img/008.jpg', 2000000, 55)
, ('자연속건물', '/img/009.jpg', 3000000, 65)
, ('맨손등반', '/img/010.jpg', 2000000, 80)
     , ('자연암벽', '/img/011.jpg', 4000000, 32)
     , ('붉은암벽', '/img/012.jpg', 5000000, 50)
     , ('겨울암벽', '/img/013.jpg', 8000000, 60)
;

select * from items;


drop table if exists members;

create table members(
                        id int primary key auto_increment,
                        name varchar(50) not null,
                        login_id varchar(50) unique not null,
                        login_pw varchar(100) not null,
                        created datetime default current_timestamp() not null
);


insert into members
(name, login_id, login_pw) values ('홍길동','member01','1234');

insert into members
(name, login_id, login_pw) values ('김첨지','member02','1234');


drop table if exists carts;
create table carts (
                       id        int auto_increment comment '아이디' primary key,
                       member_id int                                  not null comment '회원 아이디',
                       item_id   int                                  not null comment '상품 아이디',
                       created   datetime default current_timestamp() not null comment '생성 일시'
) comment '장바구니';

insert carts (member_id, item_id) VALUES (1, 1);
insert carts (member_id, item_id) VALUES (1, 5);

insert carts (member_id, item_id) VALUES (2, 4);



select * from carts;


drop table if exists orders;
create table orders (
                        id          int auto_increment comment '아이디' primary key,
                        member_id   int                                 not null comment '주문자 회원 아이디',
                        name        varchar(50)                         not null comment '주문자명',
                        address     varchar(500)                        not null comment '배송 주소',
                        payment     varchar(10)                         not null comment '결제 수단',
                        card_number varchar(16)                         null comment '카드 번호',
                        amount      bigint                              not null comment '최종 결제 금액',
                        created     datetime default current_timestamp() not null comment '생성 일시'
) comment '주문';

drop table if exists order_items;
create table order_items (
                             id       int auto_increment comment '아이디' primary key,
                             order_id int                                  not null comment '주문 아이디',
                             item_id  int                                  not null comment '주문 상품 아이디',
                             created  datetime default current_timestamp() not null comment '생성 일시'
) comment '주문 상품';

select * from orders;
select * from order_items;

select * from items;

select * from members;
