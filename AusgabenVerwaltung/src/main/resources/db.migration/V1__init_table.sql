create table if not exists gruppe(
    id uuid PRIMARY key,
    name varchar(50) NOT NULL,
    offen boolean default true,
);
create table if not exists ausgabe(
    id BIGINT auto_increment primary key,
    name varchar(50) NOT NULL,
    beschreibung varchar(200),
    betrag numeric(15,2) NOT NULL,
    bezahltVon varchar(50) NOT NULL,
);
create table if not exists beteiligt(
    ausgabeId BIGINT not null,
    githubHandle varchar(50) not null,
    foreign key (ausgabeId) references ausgabe(id)
);
create table if not exists mitglied(
    gruppenId uuid not null,
    githubHandle varchar(50) not null,
    foreign key (gruppenId) references gruppe(id)
);


