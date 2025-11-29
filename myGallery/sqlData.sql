use mygallery;

drop table if exists items;
create table items (
                       id           int auto_increment comment '아이디' primary key,
                       name         varchar(50)                          not null comment '상품 이름',
                       img_path     varchar(100)                         not null comment '상품 사진 경로',
                       price        int                                  not null comment '상품 가격',
                       discount_per int                                  not null comment '상품 할인율',
                       summary      varchar(30)                                   comment '작품 요약',
                       description  text                                     null comment '작품 설명',
                       created      datetime default current_timestamp() not null comment '생성 일시'
) comment '상품';

truncate items;
INSERT INTO items (name, img_path, price, discount_per, summary, description)
VALUES ('야간등반', '/img/001.jpg', 1000000, 5,
        '별빛 아래, 미지의 세계를 탐험하는 고독한 용기.',
        '밤하늘의 별빛을 나침반 삼아 오르는 고독한 여정. 헤드랜턴 불빛 아래 드러난 바위의 질감과 그 위를 수놓는 은하수의 대조가 인상적인 작품입니다. **고요함 속에 울려 퍼지는 심장의 박동 소리만이 유일한 동반자입니다. 극한의 환경에서 오직 자신과 자연만이 존재하는 순간의 경외감을 극대화합니다.** 미지의 세계를 탐험하는 용기를 표현합니다.')
     , ('겨울산', '/img/002.jpg', 2000000, 10,
        '침묵하는 설산의 웅장함, 고결한 아름다움.',
        '침묵하는 설산의 웅장함과 순수함이 느껴지는 풍경입니다. 차가운 공기 속에 뼈대만 남은 나무들과 칼바람을 견뎌낸 봉우리의 고결한 아름다움을 담아냈습니다. **날카롭게 깎인 능선 위로 퍼지는 새벽빛은 고독하면서도 평화로운 에너지를 선사합니다. 모든 소리가 얼어붙은 듯한 정적 속에서 삶의 본질을 되찾는 순간입니다.** 인내와 경외심의 순간.')
     , ('완등후', '/img/003.jpg', 3000000, 15,
        '도전의 끝에서 느끼는 해방감과 평화.',
        '마침내 정상에 섰을 때의 해방감과 성취감을 포착한 작품입니다. 발아래 펼쳐진 세상과 땀방울이 마르는 순간의 희열이 담겨 있습니다. **클라이머의 거친 숨결과 멀리 보이는 수평선이 대비되며, 수많은 시련 끝에 얻어낸 자유의 무게를 가늠하게 합니다. 이제 세상은 그의 발아래에서 숨 쉬기 시작합니다.** 도전의 끝에서 느끼는 평화.')
     , ('절벽등반', '/img/004.jpg', 4000000, 20,
        '수직 절벽을 오르는, 생명을 건 극한의 집중력.',
        '수직으로 깎아지른 아찔한 절벽을 오르는 클라이머의 강인한 의지를 담았습니다. 생명을 건 몸짓과 거대한 자연 앞에서 인간이 보여줄 수 있는 극한의 집중력을 표현합니다. **손끝 하나, 발끝 하나에 모든 신경을 집중하며 중력을 거부하는 듯한 춤을 춥니다. 바위의 미세한 흠집조차 놓치지 않는 예리함과 순간의 긴장감이 폭발하는 역동적인 작품입니다.**')
     , ('멀티등반', '/img/005.jpg', 5000000, 25,
        '신뢰와 협동, 로프 하나에 의지하는 팀워크.',
        '두 명의 클라이머가 한 팀이 되어 여러 피치(Pitch)를 오르는 역동적인 순간을 담았습니다. 신뢰와 협동, 그리고 로프 하나에 의지하는 팀워크의 중요성을 상징하는 작품입니다. **선등자와 후등자 사이를 잇는 로프는 단순한 안전 장치를 넘어선 믿음의 끈입니다. 서로의 움직임을 읽고 호흡을 맞추며 불가능해 보이는 벽을 함께 정복해나가는 인간적인 유대를 포착했습니다.**')
     , ('멀티등반2', '/img/006.jpg', 6000000, 30,
        '장엄한 역광 속, 고전적인 암벽 등반의 미학.',
        '장엄한 스케일의 암벽에서 펼쳐지는 두 번째 피치의 모습입니다. 역광 속에서 실루엣으로 처리된 클라이머의 모습이 더욱 드라마틱하고 고전적인 암벽 등반의 미학을 전달합니다. **황금빛으로 부서지는 햇빛이 암벽의 거친 표면을 비추고, 인간의 형태는 하나의 그림자처럼 승화됩니다. 단순한 등반을 넘어선 자연 속 예술 행위로서의 클라이밍을 표현했습니다.**')
     , ('렛서팬더등반', '/img/007.jpg', 7000000, 45,
        '자연의 유머, 끈기 있는 렛서팬더의 발바닥처럼.',
        '자연의 순수함과 예상치 못한 유머가 결합된 작품입니다. 렛서팬더의 발바닥 패드처럼 끈기 있게 바위를 짚는 등반가의 모습을 익살스럽게 재해석했습니다. **날카로운 바위 대신 포근한 발바닥이 느껴지는 듯한 시각적 착각을 유발하며, 고난도의 등반 기술 속에 숨겨진 해학적인 요소를 강조합니다. 무거운 도전 속 경쾌한 미소를 발견합니다.** 경쾌한 도전 정신.')
     , ('고양이등반', '/img/008.jpg', 2000000, 55,
        '고양이처럼 우아한 유연성, 균형의 미학.',
        '유연하고 우아한 고양이의 움직임처럼, 최소한의 힘으로 바위를 타고 오르는 클라이머의 자세를 표현합니다. 균형과 유연성의 미학, 무중력 상태를 거스르는 듯한 섬세함. **마치 공중에 떠 있는 듯한 완벽한 밸런스와 불필요한 힘을 모두 뺀 움직임에서 동양화의 여백과 같은 절제된 아름다움을 느낄 수 있습니다. 바위와 하나가 된 듯한 경지입니다.**')
     , ('자연속건물', '/img/009.jpg', 3000000, 65,
        '도심 속 구조물에서 발견하는 새로운 형태의 탐험.',
        '자연 암벽이 아닌 도심 속 버려진 구조물을 등반하는 모습을 담았습니다. 현대 건축물과 거친 자연 환경이 만나는 지점에서 새로운 형태의 등반 예술을 발견합니다. **무미건조한 콘크리트 벽과 강철 구조물 사이에서 생명력 있는 인간의 몸짓이 대비됩니다. 도시의 회색빛 속에서 자유와 모험을 갈망하는 현대인의 초상을 담아낸 작품입니다.** 도시 속의 탐험.')
     , ('맨손등반', '/img/010.jpg', 2000000, 80,
        '손끝과 바위의 마찰, 본능적인 도전의 순간.',
        '어떠한 장비 없이 오직 손끝과 발끝의 감각에 의존하는 클라이머의 맨몸을 클로즈업한 작품입니다. 거친 바위와 피부의 마찰이 생생하게 느껴지며, 본능적인 도전을 담아냅니다. **피와 땀이 섞이는 그립(Grip)의 순간을 극한까지 클로즈업하여 인간 본연의 투쟁심을 강조합니다. 자연의 거대한 힘 앞에 오직 순수한 의지만으로 맞서는 장엄한 순간입니다.**')
     , ('자연암벽', '/img/011.jpg', 4000000, 32,
        '억겁의 시간이 빚은 암벽 앞에서 겸손해지는 모습.',
        '억겁의 시간 동안 바람과 물이 조각한 순수한 자연 암벽의 위용을 담았습니다. 자연이 빚어낸 홀드(Hold)와 지형의 아름다움, 그리고 그 앞에서 겸손해지는 인간의 모습을 대조합니다. **클라이머의 작은 실루엣은 자연의 영원함과 위대함을 강조하며, 인간의 삶이 그 속에 잠시 머무는 순간임을 깨닫게 합니다. 숭고한 침묵 속에서 느껴지는 압도적인 아름다움입니다.**')
     , ('붉은암벽', '/img/012.jpg', 5000000, 50,
        '석양의 빛과 그림자가 만드는 강렬한 암벽 드라마.',
        '석양이 질 무렵, 모든 것을 태울 듯 붉게 물든 거대한 암벽의 표정을 포착했습니다. 하루의 마지막 빛과 등반가의 그림자가 만나 만들어내는 강렬한 드라마가 인상적입니다. **바위의 질감이 붉은 색채 속에서 더욱 도드라지며, 클라이머의 땀과 열정이 마치 불꽃처럼 타오르는 듯합니다. 황홀하면서도 위험한 순간의 긴장감을 극대화한 작품입니다.**')
     , ('겨울암벽', '/img/013.jpg', 8000000, 60,
        '얼음과 눈 속, 극한의 환경에서 피어나는 생명력.',
        '차가운 얼음과 눈에 뒤덮인 암벽을 오르는 아이스 클라이밍의 순간입니다. 날카로운 장비와 얼음의 투명함이 대조를 이루며, 극한의 환경에서 피어나는 생명력과 집중력을 표현합니다. **단단하게 얼어붙은 폭포와 암벽 위에서 피켈과 아이젠이 만들어내는 멜로디는 생존을 위한 처절하면서도 아름다운 몸부림입니다. 차가운 미학 속에 숨겨진 뜨거운 열정을 발견합니다.**')
;


drop table if exists members;

create table members(
id int primary key auto_increment,
name varchar(50) not null,
login_id varchar(50) unique not null,
login_pw varchar(44) not null,
login_pw_salt char(16) NOT NULL comment '로그인 패스워드 솔트',
phone_number varchar(20) null comment '주요 연락처',
created datetime default current_timestamp() not null,
updated datetime not null default current_timestamp ON UPDATE current_timestamp
);


drop table if exists carts;
create table carts (
                       id        int auto_increment comment '아이디' primary key,
                       member_id int                                  not null comment '회원 아이디',
                       item_id   int                                  not null comment '상품 아이디',
                       created   datetime default current_timestamp() not null comment '생성 일시'
) comment '장바구니';


select * from carts;


drop table if exists orders;
create table orders (
                        id          int auto_increment comment '아이디' primary key,
                        member_id   int                                 not null comment '주문자 회원 아이디',
                        name        varchar(50)                         not null comment '주문자명',
                        address     varchar(500)                        not null comment '배송 주소',
                        payment     varchar(10)                         not null comment '결제 수단',
                        card_number varchar(50)                         null comment '카드 번호',
                        amount      bigint                              not null comment '최종 결제 금액',
                        created     datetime default current_timestamp() not null comment '생성 일시'
) comment '주문';

-- 2. orders 테이블 샘플 데이터
INSERT INTO orders (member_id, name, address, payment, card_number, amount)
VALUES
    -- 주문 1 (회원 ID 1: 김벽)
    (1, '김벽', '서울특별시 종로구 북한산로 123-1', 'CARD', '1234-xxxx-xxxx-5678', 1900000),

    -- 주문 2 (회원 ID 2: 이산)
    (2, '이산', '경기도 남양주시 팔당호로 45-2', 'BANK', NULL, 5400000),

    -- 주문 3 (회원 ID 1: 김벽)
    (1, '김벽', '서울특별시 종로구 북한산로 123-1', 'CARD', '1234-xxxx-xxxx-5678', 7600000);


drop table if exists order_items;
create table order_items (
                             id       int auto_increment comment '아이디' primary key,
                             order_id int                                  not null comment '주문 아이디',
                             item_id  int                                  not null comment '주문 상품 아이디',
                             created  datetime default current_timestamp() not null comment '생성 일시'
) comment '주문 상품';

-- 3. order_items 테이블 샘플 데이터

INSERT INTO order_items (order_id, item_id)
VALUES
    -- 주문 1 (총 1,900,000원)
    (1, 1), -- 야간등반 (950,000원)
    (1, 1), -- 야간등반 (950,000원)

    -- 주문 2 (총 5,400,000원)
    (2, 2), -- 겨울산 (1,800,000원)
    (2, 4), -- 절벽등반 (3,200,000원)
    (2, 1), -- 야간등반 (950,000원)
    (2, 1), -- 야간등반 (950,000원)

    -- 주문 3 (총 7,600,000원)
    (3, 6), -- 멀티등반2 (4,200,000원)
    (3, 4), -- 절벽등반 (3,200,000원)
    (3, 2), -- 겨울산 (1,800,000원)
    (3, 1); -- 야간등반 (950,000원)



create table blocks (
                        id      int auto_increment comment '아이디' primary key,
                        token   varchar(250)                         not null comment '차단 토큰',
                        created datetime default current_timestamp() not null comment '생성 일시'
) comment '토큰 차단';


-- 테이블 드롭 (테스트용, 기존 테이블이 있다면)
-- DROP TABLE IF EXISTS user_addresses;

CREATE TABLE user_addresses (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id INT NOT NULL COMMENT '회원 ID',
alias VARCHAR(50) NOT NULL COMMENT '주소 별칭 (예: 집, 회사)',
recipient_name VARCHAR(50) COMMENT '수령인 이름',
recipient_phone VARCHAR(20) COMMENT '수령인 연락처',
zip_code VARCHAR(10) NOT NULL COMMENT '우편 번호',
base_address VARCHAR(255) NOT NULL COMMENT '기본 주소 (도로명 또는 지번)',
detail_address VARCHAR(255) COMMENT '상세 주소 (동, 호수)',
is_default BOOLEAN NOT NULL DEFAULT FALSE COMMENT '기본 배송지 여부',

FOREIGN KEY (user_id)
    REFERENCES members(id)
    ON DELETE CASCADE, -- 회원이 삭제되면 등록된 주소도 함께 삭제 (CASCADE)

created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '회원 배송지 목록 테이블';

CREATE INDEX idx_user_default_address ON user_addresses (user_id, is_default);
