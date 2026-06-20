=============================================================================== ======================================================== 
-- Author: WAYOU SEPE
-- Project : How to Secure a mobile Application with Spring Security  
=============================================================================== ======================================================== 


drop TABLE IF EXISTS 
	app_user ,
	profiles,
    demand,
    provenance,
    product,
    Product_classification,
    product_hierarchy,
    product_type,
    reference_type,
    regulatory_affairs_validation ,
    purchase_Validation,
    pricing_Validation,
    status_demand,
    marking,
    formulaire,
    traceability,
    supplier,
    tariffs,
    region,
    product_country_of_origin,
    country,
    attachments
CASCADE;

-- Status table
CREATE TABLE status_demand (
    id_status 					        varchar(255) PRIMARY KEY
    ,status_name 			            VARCHAR(255)
);

-- Marquage table
CREATE TABLE marking (
    id_marking 					        INT PRIMARY key,
    marking 				            BOOLEAN, 
    type_of_marking 		    	    VARCHAR(255)
);

-- Validation Pricing table
CREATE TABLE pricing_validation ( 
	pricing_validation 				    VARCHAR(255) PRIMARY key     
);

-- Regulatory Affairs Validation table
CREATE TABLE regulatory_affairs_validation (
    reg_affairs_validation 			    VARCHAR(255) primary key

    
);

-- Validation Achat table
CREATE TABLE purchase_validation (
    purchase_validation 			           VARCHAR(255)  PRIMARY KEY
);

-- Traceability table
CREATE TABLE traceability (
    id_traceability 				           SERIAL PRIMARY KEY,
    traceability_description 		           VARCHAR(255)
);

-- Tarif Douanier table
CREATE TABLE tariffs (
    tariff_code 					            INT PRIMARY KEY,
    tariff_code_description 		            VARCHAR(255)
);

-- Gamme table
CREATE TABLE product_line (
    product_line_id 				            SERIAL PRIMARY KEY,
    product_line_name 				            VARCHAR(255)
);

-- Product Type table
CREATE TABLE product_type (
    product_type_id 				            SERIAL PRIMARY KEY,
    product_type_name 				            VARCHAR(255),
    id_traceability 				            INT NOT NULL,
    tariff_code 					            INT NOT NULL,
    product_line_id 				            INT NOT NULL,
    
    CONSTRAINT fk_traceability FOREIGN KEY(id_traceability)  
    REFERENCES Traceability(id_traceability),
    
    CONSTRAINT fk_code FOREIGN KEY(tariff_code) 
    REFERENCES tariffs (tariff_code),
    
    CONSTRAINT fk_productLine FOREIGN KEY(product_line_id)
    REFERENCES product_line(product_line_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


-- Supplier table
CREATE TABLE supplier (
    id_supplier 					            SERIAL PRIMARY KEY,
    supplier_name 					            VARCHAR(255),
    supplier_reference 				            VARCHAR(255)
);

-- Material Classification table
CREATE TABLE product_classification (
    product_classification_id 		    		SERIAL PRIMARY KEY,
    product_classification_description 			VARCHAR(255),
    product_classification_group 				VARCHAR(255),
    profit_center 					   			VARCHAR(255)
);

-- Product Hierarchy table
CREATE TABLE product_hierarchy (
    product_hierarchy_id 		                SERIAL PRIMARY KEY,
    product_hierarchy_name 		                VARCHAR(255)
);

-- Product table
CREATE TABLE Product (
    product_id					                INT PRIMARY KEY,
    product_name 					            VARCHAR(255) not null,
    weight_and_unit_measure 		            VARCHAR(255),
    length_and_unit_measure 		            VARCHAR(255),
    width_and_unit_measure 			            VARCHAR(255),
    height_and_unit_Measure 		            VARCHAR(255),
    maximal_quantity 				            VARCHAR(255),
    dangerous_product 				            BOOLEAN,
    product_hierarchy_id 		                INT NOT NULL,
    id_supplier 					            INT NULL,
    product_classification_id 		            INT NULL,
    catalog_price 					            numeric,
    product_type_id 				            int not null, 
    
    constraint fk_type_of_product foreign key(product_type_id)
    references product_type(product_type_id),
    
    CONSTRAINT fk_product_classification FOREIGN KEY(product_classification_id) 
    REFERENCES product_classification(product_classification_id),
    
    CONSTRAINT fk_productHierarchy FOREIGN KEY(product_hierarchy_id) 
    REFERENCES product_hierarchy(product_hierarchy_id),
    
    CONSTRAINT fk_supplier FOREIGN KEY(id_supplier) 
    REFERENCES supplier(id_supplier)
);

-- Country Origin table
CREATE TABLE product_country_of_origin (
    product_country_origin_id 	                SERIAL PRIMARY KEY,
    product_country_origin_name                 VARCHAR(255)
);

-- Provenance table
CREATE TABLE provenance (
    product_type_Id	 				            INT,
    product_country_origin_id 		            INT,
    
    primary key(product_country_origin_id,product_type_Id),

    CONSTRAINT fk_produit FOREIGN KEY(product_type_Id) 
    REFERENCES product_type(product_type_Id),
    
    CONSTRAINT fk_country_of_product FOREIGN KEY(product_country_origin_id) 
    REFERENCES product_country_of_origin(product_country_origin_id)
);

-- Reference Type table
CREATE TABLE reference_type (
    reference_type_id 			               VARCHAR(255) PRIMARY KEY,
    type_of_reference 			               VARCHAR(255) not null 
);


-- Country table
CREATE TABLE country (
    id_country 					               SERIAL PRIMARY KEY,
    country_name 				               VARCHAR(255) not null   
);


-- Region table
CREATE TABLE region (
    id_region 					               SERIAL PRIMARY KEY,
    region_name 				               VARCHAR(255) not null,
    id_country 					               INT NOT NULL,
    
    CONSTRAINT fk_country FOREIGN KEY(id_Country)
    REFERENCES country(id_Country)
);

-- Continent table

create table attachments(
	attachment_id 				               int primary key, 
	attachment_content 			               BYTEA not null, 
	attachment_name 			               VARCHAR(255) not null
    ); 



-- Demand table
CREATE TABLE demand (
    pricing_validation 			               VARCHAR(255),
    reg_affairs_validation 		               VARCHAR(255),
    purchase_validation 		               VARCHAR(255),
    id_status 					               VARCHAR(255),
    id_marking 				                   INT NULL,
    reference_type_id 			               VARCHAR(255),
    pricing_comments 			               VARCHAR(255), 
    purchase_comments			               Varchar(255),  
    regAffairs_comments                        Varchar(255), 
    alternative_reference 		               INT, 
    id_country 					               INT NOT NULL,
    to_be_published	 			               boolean, 
    bills_of_material 			               boolean,
    attachment_id 			                   int null, 
    product_id 					               int not null,  
    
    primary key(id_country,product_id),
    
    constraint fk_product foreign key(product_id)
    references product(product_id),
    
    constraint fk_attachments foreign key(attachment_id)
    references attachments(attachment_id),

    CONSTRAINT fk_demand_country FOREIGN KEY(id_country) 
    REFERENCES country(id_country),
    
    CONSTRAINT fk_pricing_validation FOREIGN KEY(pricing_validation) 
    REFERENCES pricing_validation(pricing_validation),
    
    CONSTRAINT fk_reg_aff_validation FOREIGN KEY(reg_affairs_validation) 
    REFERENCES regulatory_affairs_validation(reg_affairs_validation),
    
    CONSTRAINT fk_purchase_validation FOREIGN KEY(purchase_validation) 
    REFERENCES purchase_validation(purchase_validation),
    
    CONSTRAINT fk_status FOREIGN KEY(id_status)  
    REFERENCES status_demand(id_status),
    
    CONSTRAINT fk_references FOREIGN KEY(reference_type_id)
    REFERENCES reference_type(reference_type_id),
    
    CONSTRAINT fk_marking FOREIGN KEY(id_marking)
    REFERENCES marking(id_marking)
);

create table profiles(
	 profile_id 		 		                 serial primary key,
	 profile_description 	 	                 VARCHAR(255),  
	 profile_name 				                 varchar(255)
	) ;


-- App User table
CREATE TABLE app_user (
    email 						                 VARCHAR(255) PRIMARY KEY,
    first_name 					                 VARCHAR(255) NOT NULL,
    last_name 					                 VARCHAR(255) NOT NULL,
    password 					                 VARCHAR(255) not null, 
    profile_id      			                 INT not null, 
    id_country 					                 INT NOT NULL, 
    account_expired				                 boolean,  
    account_locked				                 boolean,  
    account_disable				                 boolean,
    
    CONSTRAINT fk_userCountry FOREIGN KEY (id_country) REFERENCES country(id_country),
    constraint fk_userProfile foreign key (profile_id) references profiles(profile_id)
);



INSERT INTO status_demand (
						   id_status,
                           status_name
                           )     
values                     ('A', 'Draft'),
						   ('B', 'Awaiting validation'),
						   ('C', 'Awaiting creation'),
						   ('D', 'Request finalized'),
						   ('E', 'Request canceled'),  
						   ('F', 'Request rejected');


INSERT INTO marking (
					  id_marking,
					  marking, 
				      type_of_marking
				      ) 
VALUES				  (1,TRUE, 'IVDD'),
					  (2,TRUE, 'IVDR'),
					  (3,FALSE, ''),
					  (4,TRUE, 'MDR'),
 					  (5,TRUE, 'MDD'),   
 					  (6,TRUE, 'CE'),  
 					  (7,TRUE, 'FDA'),  
 					  (8,TRUE, 'UKCA');

-- Validation Pricing
INSERT INTO pricing_validation  (
								pricing_validation
								) 
values                          ('Awaiting'),
								('Approved'),
								('Rejected'),
								('Revision required');

-- Regulatory Affairs Validation
INSERT INTO regulatory_affairs_validation  (
										   reg_affairs_validation
										   )
values          						   ('Compliant'),
										   ('Non-compliant'),
										   ('Under evaluation'),
										   ('Requires additional documentation');

-- Validation Achat
INSERT INTO purchase_validation (
							  purchase_validation
							  )
values						  ('Approved'),
							  ('Awaiting'),
							  ('Rejected'),
							  ('Awaiting supplier');

INSERT INTO traceability (
						  traceability_description
						  ) 
values					  ('Lot number required'), 
						  ('Serial number required'),
						  ('Batch number required'),
						  ('No special traceability');

INSERT INTO tariffs (
					tariff_code, 
					tariff_code_description
					) 
VALUES 				(39269097, 'Other articles of plastics'),
    				(84138100, 'Liquid pumps'),
    				(85044090, 'Other static converters'),
    				(85371090, 'Other electrical control boards'),
    				(90189080, 'Other medical devices');

INSERT INTO product_line (
				   product_line_name
				   ) 
values			   ('Professional Range'),
				   ('Industrial Range'),
				   ('Medical Range'),
				   ('Consumer Range');

INSERT INTO product_type (
						  product_type_name, 
						  id_traceability, 
						  tariff_code, 
						  product_line_id
						  ) 
values					  ('Hydraulic Pump', 3, 84138100, 2),
						  ('Electronic Housing', 2, 85044090, 1),
						  ('Medical Device', 1, 90189080, 3),
						  ('Plastic Component', 4, 39269097, 4);


INSERT INTO supplier (
					  supplier_name, 
					  supplier_reference
					  ) 
values 				  ('PlastTech Inc.', 'SUP-PLAST-001'),
					  ('ElectroComponents Ltd', 'SUP-ELEC-002'),
					  ('MediParts GmbH', 'SUP-MEDI-003'),
					  ('MetalWorks Corp', 'SUP-MET-004');


INSERT into product_classification(
								  product_classification_description, 
								  product_classification_group,
								  profit_center									 
								  ) 
values							  ('Polypropylène', 'Plastiques', 'PC-EUR'),
								  ('Acier inoxydable', 'Métaux', 'PC-US'),
								  ('Circuit imprimé', 'Electronique', 'PC-ASIA'),
								  ('Caoutchouc médical', 'Caoutchoucs', 'PC-EUR');

INSERT INTO product_hierarchy(
							 product_hierarchy_name
							 ) 
values						 ('Mechanical Components'),
							 ('Electronic Devices'),
							 ('Medical Equipment'),
							 ('Finished Products');

INSERT INTO product_country_of_origin(
                                     product_country_origin_name
						             ) 
values 					             ('France'),
						             ('Germany'),
						             ('China'),
						             ('USA'); 



INSERT INTO provenance (
						product_type_id, 
						product_country_origin_id
						) 
values					(1, 1),   
						(2, 2),           
						(3, 3),    
						(4, 4);   

INSERT INTO reference_type (
							reference_type_id, 
							type_of_reference
							) 
values						('A','Internal'),
							('B', 'Customer'),
							('C', 'Supplier'),
							('D','Customs'),  
							('E', 'Promotional'),  
							('F', 'Electronic'),  
							('G', 'After-Sales Service');


INSERT INTO country (
					 country_name
					 ) 
values				 ('France'),
					 ('Germany'),
					 ('Italy'),
					 ('Spain'), 
					 ('Belgium');

INSERT INTO region (
					region_name, 
					id_country
					)
values				('Île-de-France', 1),
					('Auvergne-Rhône-Alpes', 1),
					('Bavaria', 2),
					('Catalonia', 4),
					('Lombardy', 3),
					('Bruxelles-Capitale', 5);  



INSERT INTO Product (
					 product_id,
				     product_name, 
				     weight_and_unit_measure, 
				     length_and_unit_measure, 
				     width_and_unit_measure, 
					 height_and_unit_measure, 
					 maximal_quantity, 
					 dangerous_product, 
					 product_hierarchy_id, 
					 id_supplier, 
					 catalog_price, 
					 product_type_id
					)
values			    (1, 'Pump H100',      '2.5 kg', '30 cm', '15 cm', '20 cm', 1000,  FALSE, 1, 1, 125.00, 1),
                    (2, 'Housing E45',    '0.8 kg', '10 cm', '10 cm', '5 cm',  5000,  FALSE, 2, 2,  45.50, 2),
                    (3, 'Device M200',    '1.2 kg', '25 cm', '10 cm', '8 cm',  200,   FALSE, 3, 3, 320.00, 3),
                    (4, 'Component P78',  '0.3 kg', '8 cm',  '8 cm',  '8 cm',  10000, FALSE, 4, 4,   8.75, 4);

INSERT INTO demand (
                    pricing_validation,
                    reg_affairs_validation,
                    purchase_validation,
                    id_status,
                    id_marking,
                    reference_type_id,
                    pricing_comments, 
                    purchase_comments,  
                    regAffairs_comments, 
                    alternative_reference, 
                    id_country,
                    to_be_published, 
                    bills_of_material,
                    attachment_id, 
                    product_id 
					) 
VALUES              ('Approved', 'Compliant', 'Approved', 'D', 6, 'A', 'correct price' , NULL,  NULL, NULL, 4, TRUE, false,NULL, 1),
                    ('Approved', 'Compliant', 'Approved', 'D', 6, 'F', 'correct', 'correct','correct',NULL, 1, True, false,null,2),
                    ('Approved', 'Non-compliant', 'Approved','B',4, 'A', NULL, NULL, 'Non-compliant with MDR standards', NULL, 2,false, false, Null,3),
                    ('Rejected', 'Non-compliant', 'Rejected', 'F', 6, 'D', 'Incorrect Price','Incorrect Supplier', 'Non-compliant with CE standards', NULL, 5, TRUE, true,null ,4);

INSERT INTO profiles (
					 profile_id, 
					 profile_name, 
					 profile_description
					 ) 
values				 (1, 'ROLE_ADMIN', 'Administrator'),
					 (2, 'ROLE_PRICING', 'Approver pricing'),
					 (3, 'ROLE_LAW', 'Approver regulatory affairs'),
					 (4, 'ROLE_PURCHASE', 'Approver purchasing'),
					 (5, 'ROLE_STOCK', 'Creator of the reference'), 
					 (6 , 'ROLE_GENERAL', 'Any requester');


INSERT INTO app_user (
					  email, 
					  first_name, 
					  last_name, 
					  password, 
					  profile_id, 
					  id_country
					  )
VALUES 				  ('test@mail.com','John','Doe', '$2a$12$noVovvphdPZXGMY9EKSBoekrVwDQAX7U7Es7eCW2tLHjj01k1MCcy', 1, 1 , false, false, false),  
					  ('levi@gmail.com',  'Levi',  'Marc', '$2a$12$16SIfWbTG5N9MVESenhCjuTQaeZb5jgWCap48D3iLTEsgWkkFH9XO',3,3,false, false, false),  
					  ('andrelevi@gmail.com','Wayou','Levi', '$2a$12$53bjZoamk9KKDaf.J4ydQ.s6ZZokCCVz3jNF1jZbBWolosSKMcG6u',4, 4, false, false, false),  
					  ('mrcaalex@mail.com', 'marc', 'alex',  '$2a$12$NMorC1Q1AfAskKC.XeEwzuteSEqtWYRvKHguAzh6ZbtGvZ162blpe', 6,5 , false, false, false);


create table forms(
					id_demand 								int primary key, 
					type_of_reference 						varchar(255),  
					requester_name 						    varchar(255),  
					product_line 							varchar(255),     
					type_of_article 						varchar(255), 
					pricing_validation 						varchar(255), 
					purchase_validation 					varchar(255),  
					ref_affairs_validation 					varchar(255), 
					product_classification_description 		varchar(255),   
					catalog_price 							varchar(255),   
					marking 								boolean,  
					type_of_marking 						varchar(255), 
					id_status								varchar(255),
					id_marking 							    int,  
					id_country 								int , 
					email									Varchar(255), 
					product_id 								int
					);  
insert into forms(
					type_of_reference, 						
					requester_name,						    
					product_line, 							    
					type_of_article, 						
					pricing_validation, 						 
					purchase_validation, 					  
					ref_affairs_validation, 					 
					product_classification_description, 		   
					catalog_price, 							   
					marking, 								  
					type_of_marking, 						 
					id_marking, 							      
					id_country, 								 
					email,									 
					product_id 								
					)
values 				('A', 'Jean', 'plastic','plastic' ,  'Approved', 'Rejected', 'Requires additional documentation', 'Plastic', '222', false, null,  null,  1, 'test@mail.com', 1)
			





-- Returns all forms belonging to the user via user email
create or replace function get_forms_by_email(p_email varchar)
returns setof forms
security definer set search_path = public, pg_temp 
  as $$
BEGIN 
	RETURN QUERY
    select f.*
    from forms f
    join app_user au on au.email = f.email
    where au.email = p_email
    order by f.id_demand DESC;
 END ;  
$$ LANGUAGE plpgsql; 

select * from get_forms_by_email('test@mail.com');

CREATE OR REPLACE FUNCTION get_user_requests_with_marking(p_email VARCHAR)
RETURNS SETOF forms 
security definer set search_path = public, pg_temp 
  as $$
BEGIN
    RETURN QUERY
    SELECT f.* 
    FROM (
    SELECT id_country 
    FROM app_user
    WHERE email = p_email
    ) AS user_data
    INNER JOIN forms f ON user_data.id_country = f.id_country
    WHERE f.marking = true; 
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION get_user_requests_for_purchase(
    p_email VARCHAR,
    a_condition TEXT 
)
RETURNS SETOF forms  
security definer set search_path = public, pg_temp 
as $$
BEGIN
    RETURN QUERY  
        SELECT f.* 
        FROM (
        SELECT id_country 
        FROM app_user
        WHERE email = p_email
        ) AS user_data
        INNER JOIN forms f ON user_data.id_country = f.id_country
        WHERE f.type_of_reference = a_condition;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_user_requests_for_pricing(p_email VARCHAR)
RETURNS SETOF forms  
security definer  set search_path = public, pg_temp AS $$
BEGIN
    RETURN QUERY
    SELECT f.* 
    FROM (
    SELECT id_country 
    FROM app_user
    WHERE email = p_email   
    ) AS approver_pricing
    INNER JOIN forms f ON approver_pricing.id_country = f.id_country
    WHERE f.catalog_price is not null and f.catalog_price <> '0'; 
END;
$$ LANGUAGE plpgsql;


create or replace function get_requests_by_country(p_email varchar) 
returns setof forms 
security definer set search_path = public, pg_temp 
as $$
begin 
	return QUERY  
	WITH mytempTable AS (
    SELECT id_country 
    FROM app_user  
    WHERE email = p_email
    )
	SELECT f.*
	FROM forms f
	INNER JOIN mytempTable ON f.id_country = mytempTable.id_country ;
end;
$$ language plpgsql;  



CREATE OR REPLACE FUNCTION update_column_id_country_from_forms()
RETURNS TRIGGER AS $$
BEGIN
    -- Vérifier si id_country est NULL ou vide dans l'insertion
    IF NEW.id_country IS NULL THEN
        -- Mettre à jour id_country à partir de la table app_users
        SELECT u.id_country INTO NEW.id_country
        FROM app_user u
        WHERE u.email = NEW.email;
        
        -- Si aucun utilisateur trouvé, on peut choisir de laisser NULL
        -- ou définir une valeur par défaut
        IF NEW.id_country IS NULL THEN
            -- Optionnel: définir une valeur par défaut si souhaité
            -- NEW.id_country = 0; -- ou toute autre valeur par défaut
            RAISE NOTICE 'no country found for that email: %', NEW.email;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



CREATE TRIGGER trigger_update_forms_country
BEFORE INSERT ON forms
FOR EACH ROW
EXECUTE FUNCTION update_column_id_country_from_forms();

CREATE INDEX idx_users_email_country ON app_user(email, id_country);  


create or replace function isUserAccountDisableOrExpiredOrInactive(p_email varchar)
returns boolean 
security definer set search_path = public, pg_temp  as $$
begin 
	select account_disable, account_locked, account_expired from app_user 
	where email =  p_email ;
end;
$$ LANGUAGE plpgsql;


create index idx_user_email on app_user(email);
CREATE INDEX idx_forms_email ON forms(email);
create index idx_forms_country on forms(id_country);
create index idx_app_user_country on app_user(id_country);



