--
-- PostgreSQL database cluster dump
--

-- Started on 2023-07-24 16:22:49

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE "Tipomat_api";
ALTER ROLE "Tipomat_api" WITH NOSUPERUSER INHERIT NOCREATEROLE CREATEDB LOGIN NOREPLICATION NOBYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:vMstl5OciAclWzXAjwZlAQ==$DidysFH2QNO3XGU0ziM3tGIlFnUtiDBgV9fnExBNMiE=:YzBiHdC1Hm2d01uiTcRacg5rKFm4wby1KZhX9TkqV1E=';
COMMENT ON ROLE "Tipomat_api" IS 'User to api can access database.';
CREATE ROLE postgres;
ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:koAl3N7zYRwyP2qED8E8/w==$VeAAHTprxF28xPJfqlHROwd4mkNBqOxfKLFAqEl318o=:bSRS0KLcQWk9fGtUfxwfb9NHsn0mts59GAgKwBQGTf0=';

--
-- User Configurations
--






--
-- Tablespaces
--

CREATE TABLESPACE ts_tipomat OWNER "Tipomat_api" LOCATION E'D:\\db\\postgre\\tipomat\\data';


--
-- Databases
--

--
-- Database "template1" dump
--

\connect template1

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2023-07-24 16:22:49

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Completed on 2023-07-24 16:22:50

--
-- PostgreSQL database dump complete
--

--
-- Database "Tipomat" dump
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2023-07-24 16:22:50

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3326 (class 1262 OID 16398)
-- Name: Tipomat; Type: DATABASE; Schema: -; Owner: Tipomat_api
--

CREATE DATABASE "Tipomat" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Portuguese_Portugal.1252' TABLESPACE = ts_tipomat;


ALTER DATABASE "Tipomat" OWNER TO "Tipomat_api";

\connect "Tipomat"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 16400)
-- Name: users; Type: TABLE; Schema: public; Owner: Tipomat_api
--

CREATE TABLE public.users (
    id_user bigint NOT NULL,
    email text NOT NULL,
    name text NOT NULL,
    phone text NOT NULL,
    password text NOT NULL,
    address json NOT NULL,
    permissions json NOT NULL,
    picture text,
    favorites json,
    "emailVerified" boolean DEFAULT false
);


ALTER TABLE public.users OWNER TO "Tipomat_api";

--
-- TOC entry 214 (class 1259 OID 16399)
-- Name: users_id_user_seq; Type: SEQUENCE; Schema: public; Owner: Tipomat_api
--

CREATE SEQUENCE public.users_id_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_user_seq OWNER TO "Tipomat_api";

--
-- TOC entry 3327 (class 0 OID 0)
-- Dependencies: 214
-- Name: users_id_user_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: Tipomat_api
--

ALTER SEQUENCE public.users_id_user_seq OWNED BY public.users.id_user;


--
-- TOC entry 3173 (class 2604 OID 16403)
-- Name: users id_user; Type: DEFAULT; Schema: public; Owner: Tipomat_api
--

ALTER TABLE ONLY public.users ALTER COLUMN id_user SET DEFAULT nextval('public.users_id_user_seq'::regclass);


--
-- TOC entry 3320 (class 0 OID 16400)
-- Dependencies: 215
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: Tipomat_api
--

COPY public.users (id_user, email, name, phone, password, address, permissions, picture, favorites, "emailVerified") FROM stdin;
1	simao.s.jorge@gmail.com	Simão Jorge	963916846	$2b$10$SymJwzd93JgrWnkB7cEIMe/XbhVhHa23LzhdViIZHSDdbF.N9VpJi	{"street": "Rua Teste", "locality": "Teste", "postal-code": "2560-374"}	{"user": true}	default.png	{}	f
2	joaopedrorodrigues741@gmail.com	João Rodrigues	927409837	$2b$10$P.988yVSKaw9AoNo0zFslu8VSfOY0fRcfep/RNzgsgp5eqN/chHuC	{"street":"Rua Teste","locality":"Teste","postal-code":"2560-374"}	{"user":true}	default.png	{}	f
\.


--
-- TOC entry 3328 (class 0 OID 0)
-- Dependencies: 214
-- Name: users_id_user_seq; Type: SEQUENCE SET; Schema: public; Owner: Tipomat_api
--

SELECT pg_catalog.setval('public.users_id_user_seq', 2, true);


--
-- TOC entry 3176 (class 2606 OID 16407)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: Tipomat_api
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


-- Completed on 2023-07-24 16:22:50

--
-- PostgreSQL database dump complete
--

--
-- Database "postgres" dump
--

\connect postgres

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2023-07-24 16:22:50

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 16384)
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- TOC entry 3316 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


-- Completed on 2023-07-24 16:22:50

--
-- PostgreSQL database dump complete
--

-- Completed on 2023-07-24 16:22:50

--
-- PostgreSQL database cluster dump complete
--

