drop schema if exists stockDataBase;
create schema stockDataBase;
use stockDataBase;

create table portfolio (
	portfolio_id smallint unsigned not null auto_increment,
    balance double unsigned not null,
    worth double unsigned not null,
    profit double not null,
    profit_percentage double not null,
    primary key (portfolio_id)
) ENGINE=INNODB ;

create table stock (
	stock_id varchar(20) not null, # stock's symbol
    stock_name varchar(20) not null,
    current_price double unsigned not null,
    data_lastupdate TIMESTAMP,
    primary key (stock_id)
) ENGINE=INNODB ;

create table user_transaction (
	stock_id varchar(20) not null,
    portfolio_id smallint unsigned not null,
    quantity smallint unsigned not null,
    avg_buyprice double unsigned not null,
    primary key (stock_id, portfolio_id),
    constraint `fk_user_transaction_stock_id` FOREIGN KEY (stock_id) REFERENCES stock (stock_id) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_user_transaction_portfolio_id` FOREIGN KEY (portfolio_id) REFERENCES portfolio (portfolio_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB ;

create table transaction_log (
	stock_id varchar(20) not null,
    portfolio_id smallint unsigned not null,
    transaction_order int unsigned not null auto_increment,
    quantity smallint unsigned not null,
    buy_price double unsigned not null,
	primary key (transaction_order,stock_id, portfolio_id),
	constraint `fk_transaction_log_stock_id` FOREIGN KEY (stock_id) REFERENCES stock (stock_id) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_transaction_log_portfolio_id` FOREIGN KEY (portfolio_id) REFERENCES portfolio (portfolio_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB ;

create table oneday_data (
	stock_id varchar(20) not null,
    dates TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    dates_price double not null,
    primary key (stock_id, dates),
    constraint `fk_oneday_data_stock_id` FOREIGN KEY (stock_id) REFERENCES stock (stock_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB ;


create table year_data (
	stock_id varchar(20) not null,
    dates TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    dates_price varchar(20) not null,
    primary key (stock_id, dates),
    constraint `fk_year_data_stock_id` FOREIGN KEY (stock_id) REFERENCES stock (stock_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB ;