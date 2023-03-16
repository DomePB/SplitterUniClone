create table if not exists GRUPPE
(
    id    uuid primary key,
    name  varchar(50) not null,
    offen boolean default true
);

create table if not exists AUSGABE
(
    id           uuid default random_uuid() primary key,
    gruppenid    uuid           not null,
    name         varchar(50)    not null,
    beschreibung varchar(200),
    betrag       numeric(15, 2) not null,
    bezahlt_von  varchar(50)    not null,
    foreign key (gruppenid) references GRUPPE (id)
);

create table if not exists BETEILIGT
(
    ausgabeid    uuid        not null,
    githubhandle varchar(50) not null,
    foreign key (ausgabeid) references AUSGABE (id)
);

create table if not exists MITGLIED
(
    gruppenid    uuid        not null,
    githubhandle varchar(50) not null,
    foreign key (gruppenid) references GRUPPE (id)
);


