# drop table if exists member_authorities;
# create table member_authorities
# (
#     id        bigint      not null primary key auto_increment,
#
#     member_id bigint      not null,
#
#     authority varchar(20) not null,
#
#     FOREIGN KEY (member_id) references member (member_id)
#         on update cascade
#
# );
drop table if exists recruitment_member;
create table recruitment_member
(
    id             bigint not null primary key auto_increment,

    member_id      bigint not null,

    recruitment_id bigint not null,

    FOREIGN KEY (member_id) references member (member_id)
        on update cascade,
    Foreign Key (recruitment_id) references recruitment (recruitment_id)
        on update cascade

);