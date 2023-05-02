
CREATE TABLE analiticas
(
	anl_codigo           VARCHAR(200) NOT NULL ,
	anl_descripcion      VARCHAR(2000) NULL ,
	anl_bdt_codigo       VARCHAR2(20) NULL
);



CREATE UNIQUE INDEX XPKanaliticas ON analiticas
(anl_codigo   ASC);



ALTER TABLE analiticas
	ADD CONSTRAINT  XPKanaliticas PRIMARY KEY (anl_codigo);



CREATE TABLE base_datos
(
	bdt_codigo           VARCHAR2(20) NOT NULL ,
	bdt_descripcion      VARCHAR(200) NULL
);



CREATE UNIQUE INDEX XPKbase_datos ON base_datos
(bdt_codigo   ASC);



ALTER TABLE base_datos
	ADD CONSTRAINT  XPKbase_datos PRIMARY KEY (bdt_codigo);



CREATE TABLE comuna
(
	cmn_id               INTEGER NOT NULL ,
	cmn_descripcion      VARCHAR(200) NULL ,
	cmn_rgn_id           INTEGER NULL
);



CREATE UNIQUE INDEX XPKcomuna ON comuna
(cmn_id   ASC);



ALTER TABLE comuna
	ADD CONSTRAINT  XPKcomuna PRIMARY KEY (cmn_id);



CREATE TABLE estaciones
(
	est_id               INTEGER NOT NULL ,
	est_descripcion      VARCHAR(200) NULL ,
	est_rgd_id           INTEGER NULL
);



CREATE UNIQUE INDEX XPKEstaciones ON estaciones
(est_id   ASC);



ALTER TABLE estaciones
	ADD CONSTRAINT  XPKEstaciones PRIMARY KEY (est_id);



CREATE TABLE hoja_de_datos
(
	hdd_tar_codigo       VARCHAR(60) NOT NULL ,
	hdd_nombre           VARCHAR(200) NOT NULL ,
	hdd_encabezado       LONG VARCHAR NULL
);



CREATE UNIQUE INDEX XPKtipo_informacion ON hoja_de_datos
(hdd_tar_codigo   ASC,hdd_nombre   ASC);



ALTER TABLE hoja_de_datos
	ADD CONSTRAINT  XPKtipo_informacion PRIMARY KEY (hdd_tar_codigo,hdd_nombre);



CREATE TABLE parametros
(
	prm_codigo           VARCHAR(200) NOT NULL ,
	prm_descripcion      VARCHAR(200) NULL ,
	prm_bdt_codigo       VARCHAR2(20) NULL ,
	prm_abreviacion      VARCHAR(20) NULL ,
	prm_unidad_medida    VARCHAR(10) NULL ,
	prm_valor_maximo     NUMBER(15,3) NULL ,
	prm_valor_minimo     NUMBER(15,3) NULL
);



CREATE UNIQUE INDEX XPKparametros ON parametros
(prm_codigo   ASC);



ALTER TABLE parametros
	ADD CONSTRAINT  XPKparametros PRIMARY KEY (prm_codigo);



CREATE TABLE region
(
	rgn_id               INTEGER NOT NULL ,
	rgn_descripcion      VARCHAR(200) NULL
);



CREATE UNIQUE INDEX XPKregion ON region
(rgn_id   ASC);



ALTER TABLE region
	ADD CONSTRAINT  XPKregion PRIMARY KEY (rgn_id);



CREATE TABLE regulados
(
	rgd_id               INTEGER NOT NULL ,
	rgd_descripcion      VARCHAR(2000) NULL ,
	rgd_cmn_id           INTEGER NULL
);



CREATE UNIQUE INDEX XPKregulados ON regulados
(rgd_id   ASC);



ALTER TABLE regulados
	ADD CONSTRAINT  XPKregulados PRIMARY KEY (rgd_id);



CREATE TABLE tipo_error
(
	ter_codigo           VARCHAR(60) NOT NULL ,
	ter_descripcion      VARCHAR(200) NULL
);



CREATE UNIQUE INDEX XPKtipo_error ON tipo_error
(ter_codigo   ASC);



ALTER TABLE tipo_error
	ADD CONSTRAINT  XPKtipo_error PRIMARY KEY (ter_codigo);



CREATE TABLE tipoarchivo
(
	tar_codigo           VARCHAR(60) NOT NULL ,
	tar_descripcion      VARCHAR(200) NULL ,
	tar_observaciones    LONG VARCHAR NULL ,
	tar_fec_creacion     DATE NULL ,
	tar_fec_ultmodificacion DATE NULL ,
	tar_usr_id           VARCHAR2(20) NULL ,
	tar_bdt_codigo       VARCHAR2(20) NULL
);



CREATE UNIQUE INDEX XPKtipo_archivo ON tipoarchivo
(tar_codigo   ASC);



ALTER TABLE tipoarchivo
	ADD CONSTRAINT  XPKtipo_archivo PRIMARY KEY (tar_codigo);



CREATE TABLE tipos_validaciones
(
	tva_codigo           VARCHAR2(20) NOT NULL ,
	tva_descripcion      VARCHAR2(200) NULL ,
	tva_es_normativa     CHAR(1) NOT NULL
);



CREATE UNIQUE INDEX XPKtipos_validaciones ON tipos_validaciones
(tva_codigo   ASC);



ALTER TABLE tipos_validaciones
	ADD CONSTRAINT  XPKtipos_validaciones PRIMARY KEY (tva_codigo);



CREATE TABLE usuario
(
	usr_id               VARCHAR2(20) NOT NULL ,
	usr_password         VARCHAR2(20) NULL ,
	usr_nombre           VARCHAR2(140) NULL ,
	usr_rol_id           INTEGER NULL
);



CREATE UNIQUE INDEX XPKusuario ON usuario
(usr_id   ASC);



ALTER TABLE usuario
	ADD CONSTRAINT  XPKusuario PRIMARY KEY (usr_id);



CREATE TABLE validaciones_normativas
(
	vnv_id               INTEGER NOT NULL ,
	vnv_tar_codigo       VARCHAR(60) NOT NULL ,
	vnv_tva_codigo       VARCHAR2(20) NULL ,
	vnv_datosadicionales LONG VARCHAR NULL
);



CREATE UNIQUE INDEX XPKvlidaciones_normativas ON validaciones_normativas
(vnv_tar_codigo   ASC,vnv_id   ASC);



ALTER TABLE validaciones_normativas
	ADD CONSTRAINT  XPKvlidaciones_normativas PRIMARY KEY (vnv_tar_codigo,vnv_id);



CREATE TABLE validaciones_tipo_archivo
(
	vta_tva_codigo       VARCHAR2(20) NOT NULL ,
	vta_datos_adicionales LONG VARCHAR NULL ,
	vta_hdd_tar_codigo   VARCHAR(60) NOT NULL ,
	vta_hdd_nombre       VARCHAR(200) NOT NULL ,
	vta_id               INTEGER NOT NULL ,
	vta_ter_codigo       VARCHAR(60) NOT NULL
);



CREATE UNIQUE INDEX XPKvalidaciones_tipo_archivo ON validaciones_tipo_archivo
(vta_hdd_tar_codigo   ASC,vta_hdd_nombre   ASC,vta_id   ASC);



ALTER TABLE validaciones_tipo_archivo
	ADD CONSTRAINT  XPKvalidaciones_tipo_archivo PRIMARY KEY (vta_hdd_tar_codigo,vta_hdd_nombre,vta_id);



ALTER TABLE analiticas
	ADD (CONSTRAINT bd_analiticas FOREIGN KEY (anl_bdt_codigo) REFERENCES base_datos (bdt_codigo));



ALTER TABLE comuna
	ADD (CONSTRAINT region_comuna FOREIGN KEY (cmn_rgn_id) REFERENCES region (rgn_id));



ALTER TABLE estaciones
	ADD (CONSTRAINT regulados_estaciones FOREIGN KEY (est_rgd_id) REFERENCES regulados (rgd_id));



ALTER TABLE hoja_de_datos
	ADD (CONSTRAINT tipoarchivo_hojaddatos FOREIGN KEY (hdd_tar_codigo) REFERENCES tipoarchivo (tar_codigo));



ALTER TABLE parametros
	ADD (CONSTRAINT basedatos_parametros FOREIGN KEY (prm_bdt_codigo) REFERENCES base_datos (bdt_codigo));



ALTER TABLE regulados
	ADD (CONSTRAINT comuna_regulado FOREIGN KEY (rgd_cmn_id) REFERENCES comuna (cmn_id));



ALTER TABLE tipoarchivo
	ADD (CONSTRAINT usuario_tipoarchivo FOREIGN KEY (tar_usr_id) REFERENCES usuario (usr_id));



ALTER TABLE tipoarchivo
	ADD (CONSTRAINT basedato_tipoarchivo FOREIGN KEY (tar_bdt_codigo) REFERENCES base_datos (bdt_codigo));



ALTER TABLE usuario
	ADD (CONSTRAINT rol_id_FK FOREIGN KEY (usr_rol_id) REFERENCES rol (rol_id));



ALTER TABLE validaciones_normativas
	ADD (CONSTRAINT tipoarchivo_valnormativas FOREIGN KEY (vnv_tar_codigo) REFERENCES tipoarchivo (tar_codigo));



ALTER TABLE validaciones_normativas
	ADD (CONSTRAINT tipoval_valnormativa FOREIGN KEY (vnv_tva_codigo) REFERENCES tipos_validaciones (tva_codigo));



ALTER TABLE validaciones_tipo_archivo
	ADD (CONSTRAINT tipovalidac_valtipoarchivo FOREIGN KEY (vta_tva_codigo) REFERENCES tipos_validaciones (tva_codigo));



ALTER TABLE validaciones_tipo_archivo
	ADD (CONSTRAINT hdd_validtipoarchivo FOREIGN KEY (vta_hdd_tar_codigo, vta_hdd_nombre) REFERENCES hoja_de_datos (hdd_tar_codigo, hdd_nombre));



ALTER TABLE validaciones_tipo_archivo
	ADD (CONSTRAINT ttipoerror_valtipoarchivo FOREIGN KEY (vta_ter_codigo) REFERENCES tipo_error (ter_codigo));
