PGDMP                          {            DB_Android_API    14.9    14.9     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16394    DB_Android_API    DATABASE     [   CREATE DATABASE "DB_Android_API" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'C';
     DROP DATABASE "DB_Android_API";
             
   AndroidAPI    false            �           0    0    SCHEMA public    ACL     �   REVOKE ALL ON SCHEMA public FROM postgres;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO "AndroidAPI";
GRANT ALL ON SCHEMA public TO PUBLIC;
                
   AndroidAPI    false    3            �            1259    16438    products    TABLE     5  CREATE TABLE public.products (
    id_product bigint NOT NULL,
    name text NOT NULL,
    description text NOT NULL,
    price money NOT NULL,
    actual_stock integer NOT NULL,
    enabled boolean NOT NULL,
    added_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);
    DROP TABLE public.products;
       public         heap 
   AndroidAPI    false            �            1259    16437    products_id_product_seq    SEQUENCE     �   CREATE SEQUENCE public.products_id_product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.products_id_product_seq;
       public       
   AndroidAPI    false    210            �           0    0    products_id_product_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.products_id_product_seq OWNED BY public.products.id_product;
          public       
   AndroidAPI    false    209            f           2604    16441    products id_product    DEFAULT     z   ALTER TABLE ONLY public.products ALTER COLUMN id_product SET DEFAULT nextval('public.products_id_product_seq'::regclass);
 B   ALTER TABLE public.products ALTER COLUMN id_product DROP DEFAULT;
       public       
   AndroidAPI    false    210    209    210            �          0    16438    products 
   TABLE DATA           u   COPY public.products (id_product, name, description, price, actual_stock, enabled, added_at, updated_at) FROM stdin;
    public       
   AndroidAPI    false    210   �       �           0    0    products_id_product_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.products_id_product_seq', 6, true);
          public       
   AndroidAPI    false    209            h           2606    16445    products products_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id_product);
 @   ALTER TABLE ONLY public.products DROP CONSTRAINT products_pkey;
       public         
   AndroidAPI    false    210            �   �   x�]�=
1��zr�v�a~6Y3���w�	d��l$��1��k9���z8.�Ւ�<�Vr���Vm��=�(@�+0��M��
+Ӿ?�X��&�)�5��t�-}�m�|t���������xSx󤈯��c��l.�     