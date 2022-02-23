# Introduction #

START - is an application with backend for diagnostic of children ASD (autism spectrum disorder), primarily in India.

Project consists of 3 main parts:

- Mobile application
- RESTful API
- CMS

Aplication is an most important part of project (not stored at this repository). It allows **social workers** to launch surveys on tablet and save children personal data and survey results.

RESTful API serves as an bridge between mobile application and database, receiving and sending data.

CMS consitst of modules listed below:

- CMS users management module. There are several roles in system:
	* **Super Administrators**
	* **Clinicians** (can view everything, cannot modify or delete information)
	* **Researchers** (cannot see personal info, such as names, parent status, contact information)
- **Social workers** management module (it is application-side users)
- Children/surveys view/management module
- Content module (pictures an multimedia content used for surveys generation)
- Instructions management module (docs or guidelines for **social workers** on how to deal with content and perform surveys)


## Purpose ##

The main puspose of the project is to collect survey data and provide tools for analysis by clinicians and researchers.


## Setup instructions ##

### Summary of set up ###
Standard setup: Clone repository and modify settings.
Clone or copy in /var/www/<your_html> folder

### Prereq
Before running 'composer install', install php7.0-xmlrpc, php7.0xml and composer:
 - sudo apt-get update
 - sudo apt-get install php7.0-xmlrpc
 - for php7.0-xml, take the package directly from https://packages.ubuntu.com/search?keywords=php7.0-xml and use sudo dpkg -i <pkg>
 - sudo apt install composer

### Configuration ###
CMS (backend):

- Open `config/` and `start_cms/app/config/` directories.
- There are files `{config_name}_default.php`. Edit them and rename to `{config_name}.php`.
- Make sure that configs are git-ignored. Delete from cache if necessary.
- Open `start_cms/deps/` and run `composer install`

### Database configuration, apache and php setup###
~~Install mysql if no db present:~~
Note: Install mariadb. Mysql is resistant to nothing being inserted in not null fields. This was causing an issue. In production maria db is installed which is tolerant to this change, so please install maria db. (No need to purge mysql if already present)
 - sudo apt upgrade #this will take time and is not really needed
 ~~- sudo apt install mysql-server~~
 - sudo apt install mariadb-server-10.0 #Note: presently it is "mysql  Ver 15.1 Distrib 10.0.34-MariaDB, for debian-linux-gnu (x86_64) using readline 5.2" in production. Can be seen using "mysql -V". Mariadb works without password for root. To access from commandline just use "sudo mysql -u root". To set password for root use "mysqladmin --user=root password "<password>" " 
 - Insall apache2 if not present:
 	- sudo apt install apache2
	- sudo a2enmod rewrite
	- sudo service apache2 restart
 - enable php if php7.0 files missing in /etc/apache2/mods-enabled/
 	- sudo apt install libapache2-mod.php
	- sudo a2enmod php7.0
	- sudo service apache2 reload
	- NOTE: The php.ini file has been included in this git. Replace any php.ini with this one at /etc/php/7.0/apache2/php.ini or at least change the max upload and file size to 2048 MB. Otherwise large video uploads fail with an incorrect message "assestment type is not specified"
DB dump included. (Can use the latest dump. On production server use 'sudo mysqldump -u <admin> -p <dbname> > tempdel.sql')
 - mysql -u root -p <dbname> < dump_file #to restore in local machine

Not really needed, but steps for phpmyadmin:
 - sudo apt install phpmyadmin
 - In /etc/apache2/apache.conf add at the bottom:
 	- Include /etc/phpmyadmin/apache.conf
 - sudo service apache2 restart

### For enabling ssl on backend:
 - sudo apt-get update
 - sudo apt-get install apache2
 - sudo a2enmod ssl
 - sudo service apache2 restart
 - sudo mkdir /etc/apache2/ssl
 - sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/apache2/ssl/apache.key -out /etc/apache2/ssl/apache.crt
 - sudo nano /etc/apache2/sites-available/default-ssl.conf. Change the following:        
 	ServerAdmin admin@example.com
        ServerName your_domain.com #used localhost
        #ServerAlias www.your_domain.com
        DocumentRoot /var/www/html
        SSLCertificateFile /etc/apache2/ssl/apache.crt
        SSLCertificateKeyFile /etc/apache2/ssl/apache.key

 - sudo a2ensite default-ssl.conf
 - sudo service apache2 restart

### Changes to access backend from local machine
/var/www/html/start_cms/app/config/dirs.php - define('SITE', '//<machine_ip>/'); #or use //localhost/

Apache settings: The next three points were required to access local backend with specified ip /etc/apache2/sites-enabled/default-ssl.conf (if ssl enabled otherwise in 000-default.conf):
    
    	<Directory />
            Options FollowSymLinks
            AllowOverride None
        </Directory>
        <Directory /var/www/html/>
            Options Indexes FollowSymLinks MultiViews
            AllowOverride All
            Order allow,deny
	    allow from all
        </Directory>
	sudo service apache2 restart

### Add user to access start database
Add a user in db to access only the start database. Change its user id and password in
/start_cms/*/*/* php file. Otherwise, login page will not render and show 'dbo' error. Also
update at the original root (/html/config/) config file otherwise app will not be able to access backend.
> **NOTE**
>
> All personal data (children, parents, survey results) is encrypted using AES algorythm.
> That means that direct viewing or editing is impossible. Please update the AES key at the same location

### For enabling xls download for assessments
Install zip extension for php otherwise survey assessment xls will not be downloaded. Restart apache afterwards
- sudo apt install php-zip

### For suppressing php errors
Although not recommended, this has been done on production server. Hence, please change the environment name to "prod"
- define('CMS_ENV', 'prod');

### Dependencies ###
to be defined

### Unit tests ###
There are functional tests implemented, accessible at `{site_name}/test-api/run`

### Deployment instructions ###
There are files not included by repository, such as `./uploads/*`. Deployment via manual FTP upload only. Make sure you did not upload such files as `README.md` or `.gitignore
 - Copy the content from uploads/tests_content folder, otherwise login into the app fails with 'synchronization error'

## Contribution guidelines ##

Contribution by current developer(s).
