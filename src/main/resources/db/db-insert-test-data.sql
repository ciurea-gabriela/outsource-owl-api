INSERT INTO USER_ROLE(ROLE_TYPE) VALUES ('ROLE_BUYER');
INSERT INTO USER_ROLE(ROLE_TYPE) VALUES ('ROLE_SELLER');

INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('IN_PROGRESS');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('LATE');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('DELIVERED');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('FINISHED');
INSERT INTO PURCHASE_STATUS (STATUS) VALUES('RATED');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('CANCELED');

INSERT INTO CATEGORY (NAME) VALUES ('Business');
INSERT INTO CATEGORY (NAME) VALUES ('Design & Graphics');
INSERT INTO CATEGORY (NAME) VALUES ('Digital Marketing');
INSERT INTO CATEGORY (NAME) VALUES ('Lifestyle');
INSERT INTO CATEGORY (NAME) VALUES ('Music & Audio');
INSERT INTO CATEGORY (NAME) VALUES ('Programming');
INSERT INTO CATEGORY (NAME) VALUES ('Video & Animation');
INSERT INTO CATEGORY (NAME) VALUES ('Writing & Translation');


--buyer user password for test = mirel123;
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('buyer', 1500.0, 5.0, 'buyer@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_BUYER'));

--seller user password for test = mirel123;
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('seller', 50.0, 0.0, 'seller@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));

INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will compose music for your project', 15.0, 10, 5.0, 'I will compose music for your project.','82715dce-2b8f-463f-bc3d-bc183abe13b8-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Music & Audio'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='seller'));

INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE,CATEGORY_ID, SELLER_ID)
    VALUES ('I will compose music for your podcast', 20.00, 15, 4.5, 'I will compose music for your podcast.', '82715dce-2c8f-255f-a35d-bf463c1f32b9-1918371372.jpg', (SELECT ID FROM CATEGORY WHERE NAME='Music & Audio'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='seller'));

-- buyer purchase
-- INSERT INTO PURCHASE(QUANTITY,PRICE, CREATION_DATE, DELIVERY_DATE, DESCRIPTION, STATUS_ID, JOB_ID, CUSTOMER_ID, SELLER_ID)
--     VALUES (1, 5.00,
--     (SELECT NOW()),
-- 	'2020-07-01 21:43:41.156905',
-- 	'I need an intro for my new radio show. The perfect genre would be pop, perhaps something mellow and it should be 15 seconds long.',
--     (SELECT ID FROM PURCHASE_STATUS WHERE STATUS='IN_PROGRESS'),
--     (SELECT ID FROM JOB WHERE SELLER_ID = (SELECT ID FROM USER_ACCOUNT WHERE USERNAME = 'seller') LIMIT 1),
--     (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='buyer' ),
-- 	(SELECT ID FROM USER_ACCOUNT WHERE USERNAME = 'seller'));



--unique users
--jerrysmith
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('jerrysmith', 300.0, 5.0, 'jerry@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('Design a logo for your project', 80.00, 10, 5, 'Design a logo for your project','83751dae-2b8f-223f-bc8d-ec183abe13b8-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Design & Graphics'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='jerrysmith'));
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will design minimal and elegant business cards', 50.00, 7, 5, ' will design minimal and elegant business cards','82715dce-268f-853l-b35d-ba56js1a32b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Design & Graphics'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='jerrysmith'));

--melanyargon
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('melanyargon', 200.0, 5.0, 'melany@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));    
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will create a Website for you', 100.00, 15, 4.5, 'I will create a Website for your needs.','13245dda-3a6e-843f-bc5d-cc563cca31b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Programming'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='melanyargon'));    
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will develop an android app for you', 150.00, 15, 5, 'I will develop an android app for you','82tz15dce-2bap-853f-b35d-na563n1a32b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Programming'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='melanyargon'));  

--ciaraevans
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('ciaraevans', 500.0, 5.0, 'ciara@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));

INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will translate your documentation', 50.00, 10, 4.7, 'I will translate your documentation.','95325dce-2c4e-853f-ad8a-b9563caa31b9-1913331322.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Writing & Translation'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='ciaraevans')); 
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will write your podcast show notes in detail', 70.00, 10, 4.7, 'I will write your podcast show notes in detail.','82715dce-2b8f-5sai-cz5d-ba56jc1a32b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Writing & Translation'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='ciaraevans')); 

--parkerharper
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('parkerharper', 200.0, 5.0, 'parker@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will edit your video', 50.00, 10, 4.7, 'description one','82715dce-2b4f-813f-dc7d-bc465bca61b3-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Video & Animation'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='parkerharper')); 
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will do a motivating inspiring video for your brand', 60.00, 10, 4.7, 'I will do a motivating inspiring video for your brand','827aaace-2e8f-853f-b3zd-ba5c3c1334b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Digital Marketing'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='parkerharper')); 


--kiebarber
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('kiebarber', 300.0, 5.0, 'kiebarber@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will record piano for any song', 40.00, 10, 5, 'You can send me your song idea/recording in any audio form, Even you only have a vocal melody/humming with some lyric or it can be a basic guitar strum idea, piano chords, etc alike.',
    '82715dce-2b8f-853f-b35d-ba563chs351b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Music & Audio'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='parkerharper')); 
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will do a custom greeting card', 20.00, 10, 5, 'I will do a custom greeting card.',
    '827r5oce-2h8f-853f-b35d-ba5b3c1a32b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Lifestyle'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='parkerharper')); 
    
--portiaherman
INSERT INTO USER_ACCOUNT (USERNAME, BALANCE, RATING, EMAIL, PASSWORD, ROLE_ID)
    VALUES ('portiaherman', 600.0, 5.0, 'portiaherman@email.com', '$2a$10$wXDEEhA7IyRLMdl.NqoFReYsfTAiW8OnLzTZ/AkKGMd1qbLGCcCSa',
    (SELECT ID FROM USER_ROLE WHERE ROLE_TYPE = 'ROLE_SELLER'));
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will design a minimalist logo', 30.00, 10, 5, 'I will design a minimalist logo',
    '53241dce-4b8f-853f-ba4d-cc2222ca31b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Design & Graphics'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='portiaherman')); 
    
INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will do concept art for you', 40.00, 10, 5, 'I will do concept art and illustrations for you.',
    'g2715dee-2b8f-853f-bx5d-ba5j3c1aa259-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Design & Graphics'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='portiaherman')); 

INSERT INTO JOB (NAME, PRICE, DAYS_UNTIL_DELIVERY, RATING, DESCRIPTION, PREVIEW_IMAGE, CATEGORY_ID, SELLER_ID)
    VALUES ('I will create art for you', 20.00, 10, 5, 'I will do concept art and illustrations for you.',
    '82tz15dce-2bap-853f-b35d-nakh3n1a32b9-1918371372.jpg',
    (SELECT ID FROM CATEGORY WHERE NAME='Design & Graphics'),
    (SELECT ID FROM USER_ACCOUNT WHERE USERNAME='portiaherman'));


