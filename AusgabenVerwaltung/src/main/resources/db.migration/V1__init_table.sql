create table if not exists GRUPPE(
    id uuid PRIMARY key,
    name varchar(50) NOT NULL,
    offen boolean default true
);
create table if not exists AUSGABE(
    id serial primary key,
    gruppenId uuid NOT NULL,
    name varchar(50) NOT NULL,
    beschreibung varchar(200),
    betrag numeric(15,2) NOT NULL,
    bezahltVon varchar(50) NOT NULL,
    foreign key (gruppenId) references GRUPPE(id)
);
create table if not exists BETEILIGT(
    ausgabeId serial not null,
    githubHandle varchar(50) not null,
    foreign key (ausgabeId) references AUSGABE(id)
);
create table if not exists MITGLIED(
    gruppenId uuid not null,
    githubHandle varchar(50) not null,
    foreign key (gruppenId) references GRUPPE(id)
);


