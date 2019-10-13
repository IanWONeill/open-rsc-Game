include .env
MYSQL_DUMPS_DIR=./backups

go:
	`pwd`/Go-Linux.sh

run:
	`pwd`/scripts/run.sh

run-game:
	`pwd`/scripts/run.sh

combined-install:
	`pwd`/scripts/combined-install.sh

direct-install:
	`pwd`/scripts/direct-install.sh

docker-install:
	`pwd`/scripts/docker-install.sh

get-updates:
	`pwd`/scripts/get-updates.sh

start:
	docker-compose up -d

stop:
	@docker-compose down -v

restart:
	@docker-compose down -v
	docker-compose up -d

ps:
	docker-compose ps

logs:
	@docker-compose logs -f

compile:
	sudo ant -f server/build.xml compile_core
	sudo ant -f server/build.xml compile_plugins
	sudo ant -f client/build.xml compile
	sudo ant -f Launcher/build.xml compile

create-database-openrsc:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database openrsc;"

create-database-cabbage:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database cabbage;"

create-database-preservation:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database preservation;"

create-database-openpk:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database openpk;"

create-database-wk:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database wk;"

create-database-dev:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database dev;"

create-database-website:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database website;"

import-database-openrsc:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openrsc < Databases/openrsc_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openrsc < Databases/openrsc_game_players.sql

import-database-cabbage:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} cabbage < Databases/cabbage_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} cabbage < Databases/cabbage_game_players.sql

import-database-preservation:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} preservation < Databases/openrsc_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} preservation < Databases/openrsc_game_players.sql

import-database-openpk:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openpk < Databases/openpk_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openpk < Databases/openpk_game_players.sql

import-database-wk:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} wk < Databases/wk_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} wk < Databases/wk_game_players.sql

import-database-dev:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} dev < Databases/openrsc_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} dev < Databases/openrsc_game_players.sql

upgrade-database-openrsc:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openrsc < Databases/openrsc_game_server.sql

upgrade-database-cabbage:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} cabbage < Databases/cabbage_game_server.sql

upgrade-database-preservation:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} preservation < Databases/openrsc_game_server.sql

upgrade-database-openpk:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openpk < Databases/openpk_game_server.sql

upgrade-database-wk:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} wk < Databases/wk_game_server.sql

upgrade-database-dev:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} dev < Databases/openrsc_game_server.sql

backup-openrsc:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openrsc --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-openrsc.zip

backup-cabbage:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} cabbage --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-cabbage.zip

backup-preservation:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} preservation --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-preservation.zip

backup-openpk:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openpk --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-openpk.zip

backup-wk:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} wolfkingdom --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-wolfkingdom.zip

backup-dev:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} dev --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-dev.zip

backup-website:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} website --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-website.zip

clear-old-backups:
	sudo find $(MYSQL_DUMPS_DIR)/*.zip -mtime +90 -exec rm -f {} \;
