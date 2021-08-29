drop table if exists member_authority;
create table member_authority (
    id bigint not null primary key auto_increment,

    member_id bigint not null,

    authority_name varchar(20) not null,

    FOREIGN KEY (member_id) references member (member_id)
                              on update cascade,
    Foreign Key (authority_name) references authority (authority_name)
                              on update cascade

);
drop table if exists recruitment_member;
create table recruitment_member (
      id bigint not null primary key auto_increment,

      member_id bigint not null,

      recruitment_id bigint not null,

      FOREIGN KEY (member_id) references member (member_id)
          on update cascade,
      Foreign Key (recruitment_id) references recruitment (recruitment_id)
          on update cascade

);