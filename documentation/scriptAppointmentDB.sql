create table attribute
(
    id          int auto_increment
        primary key,
    name        varchar(100) not null,
    description varchar(200) not null
);

create table email_verification
(
    id                int auto_increment
        primary key,
    email             varchar(255)                                               not null,
    token             varchar(255)                                               not null,
    created_at        timestamp  default current_timestamp()                     null,
    expired_at        timestamp  default (current_timestamp() + interval 1 hour) null,
    is_available      tinyint    default 1                                       null,
    is_reset_password tinyint(1) default 0                                       null
);

create table module
(
    id           int auto_increment
        primary key,
    name         varchar(75)                           not null,
    direction    varchar(100)                          null,
    is_available tinyint   default 1                   null,
    created_at   timestamp default current_timestamp() null
);

create table page
(
    id           int auto_increment
        primary key,
    name         varchar(75)                           not null,
    is_available tinyint   default 1                   null,
    FK_Module    int                                   not null,
    path         varchar(100)                          not null,
    created_at   timestamp default current_timestamp() null,
    constraint page_ibfk_1
        foreign key (FK_Module) references module (id)
);

create index FK_Module
    on page (FK_Module);

create table resource
(
    id    int auto_increment
        primary key,
    name  varchar(100) not null,
    image varchar(255) null
);

create table resource_has_attribute
(
    id           int auto_increment
        primary key,
    FK_Resource  int not null,
    FK_Attribute int not null,
    constraint resource_has_attribute_ibfk_1
        foreign key (FK_Resource) references resource (id),
    constraint resource_has_attribute_ibfk_2
        foreign key (FK_Attribute) references attribute (id)
);

create index FK_Attribute
    on resource_has_attribute (FK_Attribute);

create index FK_Resource
    on resource_has_attribute (FK_Resource);

create table role
(
    id          int auto_increment
        primary key,
    name        varchar(100)                          not null,
    description varchar(255)                          null,
    created_at  timestamp default current_timestamp() null,
    constraint name
        unique (name)
);

create table role_has_page
(
    id         int auto_increment
        primary key,
    FK_Role    int               not null,
    FK_Page    int               not null,
    can_create tinyint default 1 null,
    can_edit   tinyint default 1 null,
    can_delete tinyint default 1 null,
    constraint role_has_page_ibfk_1
        foreign key (FK_Role) references role (id),
    constraint role_has_page_ibfk_2
        foreign key (FK_Page) references page (id)
);

create index FK_Page
    on role_has_page (FK_Page);

create index FK_Role
    on role_has_page (FK_Role);

create table service
(
    id               int auto_increment
        primary key,
    name             varchar(100)                          not null,
    description      varchar(255)                          null,
    price            decimal(10, 2)                        not null,
    page_information text                                  null,
    time_aprox       int                                   null,
    is_available     tinyint   default 1                   null,
    created_at       timestamp default current_timestamp() null,
    constraint name
        unique (name)
);

create table resource_has_service
(
    id          int auto_increment
        primary key,
    FK_Resource int not null,
    FK_Service  int not null,
    constraint resource_has_service_ibfk_1
        foreign key (FK_Resource) references resource (id),
    constraint resource_has_service_ibfk_2
        foreign key (FK_Service) references service (id)
);

create index FK_Resource
    on resource_has_service (FK_Resource);

create index FK_Service
    on resource_has_service (FK_Service);

create table setting_type
(
    id   int auto_increment
        primary key,
    name varchar(255) not null,
    constraint name
        unique (name)
);

create table user
(
    id           int auto_increment
        primary key,
    email        varchar(100)                          not null,
    username     varchar(100)                          not null,
    password     varchar(255)                          not null,
    auth_token   varchar(255)                          null,
    is_activated tinyint   default 0                   null,
    is_verified  tinyint   default 0                   null,
    created_at   timestamp default current_timestamp() null,
    is_2FA       tinyint(1)                            null,
    constraint email
        unique (email),
    constraint username
        unique (username)
);

create table comment
(
    id         int auto_increment
        primary key,
    FK_User    int                                   not null,
    comment    text                                  not null,
    value      tinyint                               not null
        check (`value` between 1 and 5),
    created_at timestamp default current_timestamp() null,
    constraint comment_ibfk_1
        foreign key (FK_User) references user (id)
);

create index FK_User
    on comment (FK_User);

create table employee
(
    id            int auto_increment
        primary key,
    first_name    varchar(100) not null,
    last_name     varchar(100) null,
    date_of_birth date         null,
    FK_User       int          not null,
    constraint employee_ibfk_1
        foreign key (FK_User) references user (id)
);

create table appointment
(
    id          int auto_increment
        primary key,
    FK_User     int                                                                   not null,
    FK_Resource int                                                                   null,
    total       decimal(10, 2)                                                        not null,
    state       enum ('PENDING', 'CONFIRMED', 'CANCELED') default 'PENDING'           null,
    created_at  timestamp                                 default current_timestamp() null,
    start_time  timestamp                                 default current_timestamp() null,
    end_time    timestamp                                 default current_timestamp() null,
    FK_Employee int                                                                   null,
    constraint appointment_ibfk_1
        foreign key (FK_User) references user (id),
    constraint appointment_ibfk_2
        foreign key (FK_Resource) references resource (id),
    constraint appointment_ibfk_3
        foreign key (FK_Employee) references employee (id)
);

create index FK_Employee
    on appointment (FK_Employee);

create index FK_Resource
    on appointment (FK_Resource);

create index FK_User
    on appointment (FK_User);

create table appointment_has_service
(
    id             int auto_increment
        primary key,
    FK_Appointment int            not null,
    price          decimal(10, 2) not null,
    FK_Service     int            not null,
    time_aprox     int            not null,
    constraint appointment_has_service_ibfk_1
        foreign key (FK_Appointment) references appointment (id),
    constraint appointment_has_service_ibfk_3
        foreign key (FK_Service) references service (id)
);

create index FK_Appointment
    on appointment_has_service (FK_Appointment);

create index FK_Service
    on appointment_has_service (FK_Service);

create table bill
(
    id             int auto_increment
        primary key,
    appointment_id int                                        not null,
    nit            varchar(20)                                not null,
    name           varchar(50)                                not null,
    address        varchar(100)                               not null,
    description    text                                       not null,
    price          decimal(10, 2)                             not null,
    created_at     timestamp      default current_timestamp() null,
    advancement    decimal(10, 2) default 0.00                null,
    tax            decimal(10, 2) default 0.00                null,
    constraint bill_ibfk_1
        foreign key (appointment_id) references appointment (id)
);

create index appointment_id
    on bill (appointment_id);

create index FK_User
    on employee (FK_User);

create table employee_has_service
(
    id          int auto_increment
        primary key,
    FK_Employee int not null,
    FK_Service  int not null,
    constraint employee_has_service_ibfk_1
        foreign key (FK_Employee) references employee (id),
    constraint employee_has_service_ibfk_2
        foreign key (FK_Service) references service (id)
);

create index FK_Employee
    on employee_has_service (FK_Employee);

create index FK_Service
    on employee_has_service (FK_Service);

create table user_2fa
(
    id           int auto_increment
        primary key,
    secret_key   varchar(255)                                              not null,
    FK_User      int                                                       not null,
    created_at   timestamp default current_timestamp()                     null,
    expired_at   timestamp default (current_timestamp() + interval 1 hour) null,
    is_available tinyint   default 1                                       null,
    constraint user_2fa_ibfk_1
        foreign key (FK_User) references user (id)
);

create index FK_User
    on user_2fa (FK_User);

create table user_has_role
(
    id      int auto_increment
        primary key,
    FK_User int not null,
    FK_Role int not null,
    constraint user_has_role_ibfk_1
        foreign key (FK_User) references user (id),
    constraint user_has_role_ibfk_2
        foreign key (FK_Role) references role (id)
);

create index FK_Role
    on user_has_role (FK_Role);

create index FK_User
    on user_has_role (FK_User);

create table user_information
(
    id            int auto_increment
        primary key,
    nit           varchar(20)             null,
    image_profile varchar(255) default '' null,
    description   varchar(255) default '' null,
    FK_User       int                     not null,
    dpi           varchar(100)            null,
    phone_number  varchar(100)            null,
    constraint dpi
        unique (dpi),
    constraint nit
        unique (nit),
    constraint phone_number
        unique (phone_number),
    constraint tel
        unique (phone_number),
    constraint user_information_ibfk_1
        foreign key (FK_User) references user (id)
);

create index FK_User
    on user_information (FK_User);

create table value_type
(
    id   int auto_increment
        primary key,
    name varchar(255) not null,
    constraint name
        unique (name)
);

create table company_settings
(
    id              int auto_increment
        primary key,
    key_name        varchar(255)            not null,
    key_value       text                    null,
    is_required     tinyint      default 0  null,
    is_available    tinyint      default 0  null,
    FK_Value_type   int                     not null,
    FK_Setting_type int                     not null,
    label_value     varchar(100)            not null,
    help            varchar(100) default '' null,
    constraint key_name
        unique (key_name),
    constraint company_settings_ibfk_1
        foreign key (FK_Value_type) references value_type (id),
    constraint company_settings_ibfk_2
        foreign key (FK_Setting_type) references setting_type (id)
);

create index FK_Setting_type
    on company_settings (FK_Setting_type);

create index FK_Value_type
    on company_settings (FK_Value_type);


