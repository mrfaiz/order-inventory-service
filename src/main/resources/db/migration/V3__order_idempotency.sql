alter table orders
add column idempotency_key varchar(64);

create unique index uk_orders_idempotency_key
on orders(idempotency_key)
where idempotency_key is not null;

